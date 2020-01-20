package com.spotware.connect.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.spotware.connect.netty.handler.util.CodecUtils;
import com.spotware.connect.protocol.ProtoMessageFactory;
import com.spotware.connect.netty.exception.IncorrectMessageException;
import com.spotware.connect.protocol.ChannelMessage;
import com.xtrader.protocol.proto.commons.ProtoMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class ProtoMessageToChannelMessageDecoder extends SimpleChannelInboundHandler<ProtoMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoMessageToChannelMessageDecoder.class);


    private final ProtoMessageFactory protoMessageFactory;

    public ProtoMessageToChannelMessageDecoder(ProtoMessageFactory protoMessageFactory) {
        assert protoMessageFactory != null : "undefined protoMessageFactory";
        this.protoMessageFactory = protoMessageFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage protoMessage) {
        Channel channel = ctx.channel();
        LOGGER.trace("Decoding ProtoMessage message in channel={}", channel);

        int payloadType = protoMessage.getPayloadType();
        MessageLite protoObjectPrototype = protoMessageFactory.getMessageByPayloadType(payloadType);
        if (protoObjectPrototype == null) {
            throw new IncorrectMessageException("protoObjectPrototype is null for payloadType=" + payloadType);
        }

        ByteString protoObjectBytes = protoMessage.getPayload();
        MessageLite protoObject;
        if (protoObjectBytes == null) {
            protoObject = protoObjectPrototype.getDefaultInstanceForType();
        } else {
            try {
                protoObject = protoObjectPrototype.getParserForType().parseFrom(protoObjectBytes);
            } catch (InvalidProtocolBufferException e) {
                throw new IncorrectMessageException(e);
            }
        }

        ChannelMessage<MessageLite> message = createMessageFromProto(protoMessage, protoObject);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Decoded message {} in channel={}", message.toAbbreviatedString(), channel);
        }

        ctx.fireChannelRead(message);
    }

    protected ChannelMessage<MessageLite> createMessageFromProto(ProtoMessage protoMessage, MessageLite payload) {
        ChannelMessage<MessageLite> message = new ChannelMessage<>(payload);
        CodecUtils.setChannelMessageCommonFields(message, protoMessage);
        return message;
    }
}
