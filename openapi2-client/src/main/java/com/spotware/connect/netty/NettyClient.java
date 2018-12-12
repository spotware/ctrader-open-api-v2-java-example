package com.spotware.connect.netty;


import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import com.google.protobuf.MessageLite;
import com.spotware.connect.netty.handler.ChannelMessageToProtoMessageEncoder;
import com.spotware.connect.netty.handler.ClientSslEngineFactory;
import com.spotware.connect.netty.handler.CloseOnExceptionHandler;
import com.spotware.connect.netty.handler.HeartbeatOnIdleHandler;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.spotware.connect.netty.handler.ProtoMessageReceiverHandler;
import com.spotware.connect.netty.handler.ProtoMessageToChannelMessageDecoder;
import com.spotware.connect.netty.handler.SslEngineFactory;
import com.spotware.connect.protocol.OA2ProtoMessageFactory;
import com.spotware.connect.protocol.ProtoMessageFactory;
import com.xtrader.protocol.proto.commons.ProtoMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {
    private static final long INACTIVITY_READ_MILLIS = 60000;
    private static final long PING_INTERVAL_MILLIS = 30000;
    private static final int MAX_FRAME_LENGTH = 1024 * 1024 * 10;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private final ChannelHandler protobufDecoder = new ProtobufDecoder(ProtoMessage.getDefaultInstance());
    private final ChannelHandler protobufEncoder = new ProtobufEncoder();
    private final ChannelHandler lengthFieldPrepender = new LengthFieldPrepender(LENGTH_FIELD_LENGTH);
    private final ChannelHandler protoChannelMessageDecoder;
    private final ChannelHandler protoChannelMessageEncoder;
    private final ProtoMessageReceiverHandler protoMessageReceiverHandler;
    private final ProtoMessageFactory msgFactory;

    private AuthHelper authHelper;
    private ChannelFuture channelFuture;
    private EventLoopGroup workerGroup;
    private String host;
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.msgFactory = new OA2ProtoMessageFactory();
        this.protoChannelMessageDecoder = new ProtoMessageToChannelMessageDecoder(msgFactory);
        this.protoChannelMessageEncoder = new ChannelMessageToProtoMessageEncoder(msgFactory);
        this.protoMessageReceiverHandler = new ProtoMessageReceiverHandler();
        authHelper = new AuthHelper(this);
        connect();
    }

    public AuthHelper getAuthHelper() {
        return authHelper;
    }

    public void connect() {
        workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    initPipelineForChannel(ch);
                }
            });
            // Start the client.
            channelFuture = b.connect(host, port).sync();

        } catch (Exception ex) {
            closeConnection();
        }
    }

    private void initPipelineForChannel(Channel ch) throws SSLException {
        ChannelPipeline pipeline = ch.pipeline();
        SslEngineFactory sslEngineFactory = new ClientSslEngineFactory();
        pipeline.addLast("ssl", sslEngineFactory.newHandler(ch));

        pipeline.addLast("idleState", new IdleStateHandler(INACTIVITY_READ_MILLIS,
                PING_INTERVAL_MILLIS, 0, TimeUnit.MILLISECONDS));

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, LENGTH_FIELD_LENGTH, 0,
                LENGTH_FIELD_LENGTH));
        pipeline.addLast("protobufDecoder", protobufDecoder);
        pipeline.addLast("protoChannelMessageDecoder", protoChannelMessageDecoder);
        pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
        pipeline.addLast("protobufEncoder", protobufEncoder);
        pipeline.addLast("protoChannelMessageEncoder", protoChannelMessageEncoder);
        pipeline.addLast("heartbeatOnIdle", HeartbeatOnIdleHandler.DEFAULT);
        pipeline.addLast(ProtoMessageReceiverHandler.NAME, protoMessageReceiverHandler);
        pipeline.addLast("closeOnException", CloseOnExceptionHandler.DEFAULT);
    }

    public void addListener(ProtoMessageReceiverHandler.MessageListener listener) {
        protoMessageReceiverHandler.addMessageListener(listener);
    }

    public void removeListener(ProtoMessageReceiverHandler.MessageListener listener) {
        protoMessageReceiverHandler.removeMessageListener(listener);
    }

    public ProtoMessageReceiver writeAndFlush(MessageLite msg) {
        return writeAndFlush(msgFactory.createMessage(msg));
    }

    public ProtoMessageReceiver writeAndFlush(ProtoMessage msg) {
        ProtoMessageReceiver receiver = protoMessageReceiverHandler.getProtoMessageReceiver(msg.getClientMsgId());
        channelFuture.channel().writeAndFlush(msg);
        return receiver;
    }

    public void closeConnection() {
        workerGroup.shutdownGracefully();
    }

}
