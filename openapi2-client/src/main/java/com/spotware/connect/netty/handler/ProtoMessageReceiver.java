package com.spotware.connect.netty.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.MessageLite;

public class ProtoMessageReceiver {

    private static final int TIMEOUT_WAITING_MSG = 2000;
    private BlockingQueue<MessageLite> queue;

    ProtoMessageReceiver(BlockingQueue<MessageLite> queue) {
        this.queue = queue;
    }

    public MessageLite waitSingleResult() throws InterruptedException {
        return queue.poll(TIMEOUT_WAITING_MSG, TimeUnit.MILLISECONDS);
    }

    public MessageLite waitSingleResult(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    public List<MessageLite> getAllNoWait(){
        List<MessageLite> result =new ArrayList<>();
        queue.drainTo(result);
        return result;
    }
}
