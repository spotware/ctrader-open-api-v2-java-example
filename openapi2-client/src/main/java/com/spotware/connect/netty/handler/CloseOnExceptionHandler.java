package com.spotware.connect.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.ssl.NotSslRecordException;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.UnresolvedAddressException;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.spotware.connect.netty.handler.util.MessageFormat;
import com.spotware.connect.protocol.ChannelMessage;
import com.xtrader.protocol.proto.commons.ProtoErrorRes;
import com.xtrader.protocol.proto.commons.model.ProtoErrorCode;

@Sharable
public class CloseOnExceptionHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseOnExceptionHandler.class);
    private static final ProtoErrorRes FRAME_TOO_LONG_ERROR_RES;
    private static final ProtoErrorRes CORRUPTED_FRAME_ERROR_RES;
    public static final CloseOnExceptionHandler DEFAULT;

    private CloseOnExceptionHandler() {
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        if (cause instanceof CorruptedFrameException) {
            if (channel.isActive()) {
                channel.writeAndFlush(new ChannelMessage<>(CORRUPTED_FRAME_ERROR_RES)).addListener(ChannelFutureListener.CLOSE);
                LOGGER.warn("Caught CorruptedFrameException({}). Sending error response and closing channel={}", cause.getMessage(), channel);
            } else {
                LOGGER.warn("Caught CorruptedFrameException({}). Channel={} already closed", cause.getMessage(), channel);
            }

            LOGGER.trace("Cause:", cause);
        } else if (cause instanceof TooLongFrameException) {
            channel.writeAndFlush(new ChannelMessage<>(FRAME_TOO_LONG_ERROR_RES)).addListener(ChannelFutureListener.CLOSE);
            LOGGER.warn("Caught TooLongFrameException. Sending error response and closing channel={}", channel);
        } else if (cause instanceof NotSslRecordException) {
            LOGGER.warn("NotSslRecordException caught in channel {}. Cause: {}. Closing channel.", channel, cause.getMessage());
            channel.close();
        } else if (cause instanceof InvalidProtocolBufferException || cause.getCause() != null && cause.getCause() instanceof InvalidProtocolBufferException) {
            LOGGER.warn("InvalidProtocolBufferException caught in channel {}. Cause: {}. Closing channel.", channel, cause.getMessage());
            channel.close();
        } else if (!this.handleSSLException(channel, cause)) {
            if (cause instanceof ConnectException) {
                LOGGER.warn("Exception caught in channel " + channel + ": Unable to connect to " + channel.remoteAddress() + ": " + cause.getMessage() + ". Closing channel.");
            } else if (cause instanceof ClosedChannelException) {
                LOGGER.warn("Exception caught in channel " + channel + ": attempt to write to closed channel " + channel.remoteAddress());
            } else if (cause.getClass() == IOException.class) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(MessageFormat.format("Exception caught in channel {}. Closing channel.", channel), cause);
                } else {
                    LOGGER.warn("Exception caught in channel {}: {} {}. Closing channel.", channel, cause.getClass(), cause.getMessage());
                }

            } else if (cause instanceof WebSocketHandshakeException) {
                LOGGER.error("Exception caught in channel " + channel + ": attempt to handshake failed. " + channel.remoteAddress());
            } else if (cause instanceof UnresolvedAddressException) {
                LOGGER.warn("UnresolvedAddressException caught in channel " + channel + ": Unable to connect to " + channel.remoteAddress() + ": " + cause.getMessage() + ". Closing channel.");
            } else if (cause instanceof IllegalArgumentException && cause.getMessage() != null && cause.getMessage().contains("unsupported message type")) {
                LOGGER.warn("The message can't be written to the channel {}: {}; The channel will be closed", channel, cause.getMessage());
                channel.close();
            } else {
                LOGGER.error(MessageFormat.format("Exception caught in channel {}. Closing channel.", channel), cause);
                channel.close();
            }
        }
    }

    private boolean handleSSLException(Channel channel, Throwable cause) {
        Throwable rootCause = cause;
        if (cause instanceof DecoderException && cause.getCause() != null) {
            rootCause = cause.getCause();
        }

        if (rootCause instanceof SSLException) {
            String message = rootCause.getMessage();
            if (message.contains("close_notify")) {
                LOGGER.warn("SSLException caught in channel {}. Cause: {}", channel, message);
                return true;
            } else {
                LOGGER.warn("SSLException caught in channel {}. Cause: {}. Closing channel.", channel, message);
                channel.close();
                return true;
            }
        } else {
            return false;
        }
    }

    static {
        FRAME_TOO_LONG_ERROR_RES = ProtoErrorRes.newBuilder().setErrorCode(ProtoErrorCode.FRAME_TOO_LONG.name()).setDescription("Frame size exceeds allowed limit.").build();
        CORRUPTED_FRAME_ERROR_RES = ProtoErrorRes.newBuilder().setErrorCode(ProtoErrorCode.UNKNOWN_ERROR.name()).setDescription("Corrupted frame.").build();
        DEFAULT = new CloseOnExceptionHandler();
    }
}

