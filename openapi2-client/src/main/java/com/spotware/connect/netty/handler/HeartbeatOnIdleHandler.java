package com.spotware.connect.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotware.connect.protocol.ChannelMessage;
import com.xtrader.protocol.proto.commons.ProtoHeartbeatEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatOnIdleHandler extends ChannelDuplexHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatOnIdleHandler.class);
    private static final ChannelMessage<ProtoHeartbeatEvent> HEARTBEAT_MSG = new ChannelMessage<>(ProtoHeartbeatEvent.getDefaultInstance());
    public static final HeartbeatOnIdleHandler DEFAULT = new HeartbeatOnIdleHandler();

    private HeartbeatOnIdleHandler() {
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent)evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                Channel channel = ctx.channel();
                if (channel.isActive()) {
                    LOGGER.trace("Writing heartbeat message to channel {}", channel);
                    channel.writeAndFlush(HEARTBEAT_MSG, channel.voidPromise());
                }
            }
        }

        super.userEventTriggered(ctx, evt);
    }
}
