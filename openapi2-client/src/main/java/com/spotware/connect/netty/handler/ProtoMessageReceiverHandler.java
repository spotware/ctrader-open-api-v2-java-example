package com.spotware.connect.netty.handler;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.protobuf.MessageLite;
import com.spotware.connect.protocol.ChannelMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProtoMessageReceiverHandler extends ChannelInboundHandlerAdapter {
    public static final String NAME = "MESSAGE_RECEIVER";
    private static final long EXPIRATION_REQUEST_TIMEOUT = 5 * 60 * 1000L;// 5 min

    private Map<String, LinkedBlockingQueue<MessageLite>> messagesByRequestIds = new ConcurrentHashMap<>();
    private Map<String, Long> lastTimeUsingRequestId = new ConcurrentHashMap<>();

    private final Set<MessageListener> messageListeners = new HashSet<>();
    private ReadWriteLock messageListenersLock = new ReentrantReadWriteLock();

    public ProtoMessageReceiverHandler() {
        ScheduledExecutorService scheduleExecutor = Executors.newScheduledThreadPool(1,
                (Runnable runnable) -> {
                    Thread th = new Thread(runnable);
                    th.setDaemon(true);
                    th.setName("cleaner-expired-messages");
                    return th;
                }
        );
        scheduleExecutor.scheduleAtFixedRate(this::cleanOldMessages, EXPIRATION_REQUEST_TIMEOUT, EXPIRATION_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ChannelMessage) {
            ChannelMessage channelMessage = (ChannelMessage) msg;
            messageListenersLock.readLock().lock();
            try {
                for (MessageListener messageListener : messageListeners) {
                    messageListener.onMessageReceived(channelMessage);
                }
            } finally {
                messageListenersLock.readLock().unlock();
            }

            String clientRequestId = channelMessage.getClientRequestId();
            if (clientRequestId != null && !clientRequestId.isEmpty()) {
                getMessagesByRequestId(clientRequestId).add(channelMessage.getMessage());
                lastTimeUsingRequestId.put(clientRequestId, System.currentTimeMillis());
            }
        }
        ctx.fireChannelRead(msg);
    }

    private BlockingQueue<MessageLite> getMessagesByRequestId(String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            throw new IllegalArgumentException("clientRequestId can't be null or empty");
        }
        messagesByRequestIds.putIfAbsent(requestId, new LinkedBlockingQueue<>(5));
        return messagesByRequestIds.get(requestId);
    }

    public ProtoMessageReceiver getProtoMessageReceiver(String clientRequestId) {
        return new ProtoMessageReceiver(getMessagesByRequestId(clientRequestId));
    }

    public void addMessageListener(MessageListener listener) {
        messageListenersLock.writeLock().lock();
        try {
            messageListeners.add(listener);
        } finally {
            messageListenersLock.writeLock().unlock();
        }
    }

    public void removeMessageListener(MessageListener listener) {
        messageListenersLock.writeLock().lock();
        try {
            messageListeners.remove(listener);
        } finally {
            messageListenersLock.writeLock().unlock();
        }
    }

    private void cleanOldMessages() {
        for (Map.Entry<String, Long> entry : lastTimeUsingRequestId.entrySet()) {
            String requestId = entry.getKey();
            if (EXPIRATION_REQUEST_TIMEOUT < System.currentTimeMillis() - entry.getValue()) {
                lastTimeUsingRequestId.remove(requestId);
                messagesByRequestIds.remove(requestId);
            }
        }
    }

    public interface MessageListener {
        void onMessageReceived(ChannelMessage message);
    }

}
