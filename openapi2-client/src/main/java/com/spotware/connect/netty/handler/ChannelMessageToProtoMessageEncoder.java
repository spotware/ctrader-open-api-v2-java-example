package com.spotware.connect.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import com.spotware.connect.netty.handler.util.CodecUtils;
import com.spotware.connect.protocol.ProtoMessageFactory;
import com.spotware.connect.protocol.ChannelMessage;
import com.xtrader.protocol.proto.commons.ProtoMessage;

@Sharable
public class ChannelMessageToProtoMessageEncoder extends MessageToMessageEncoder<ChannelMessage<? extends MessageLite>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelMessageToProtoMessageEncoder.class);


    private final ProtoMessageFactory protoMessageFactory;

    public ChannelMessageToProtoMessageEncoder(ProtoMessageFactory protoMessageFactory) {
        assert protoMessageFactory != null : "undefined protoMessageFactory";
        this.protoMessageFactory = protoMessageFactory;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ChannelMessage<? extends MessageLite> msg, List<Object> out) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.trace("Encoding ChannelMessage message in channel {}", channel);

        MessageLite message = msg.getMessage();
        Class<? extends MessageLite> messageClass = message.getClass();
        Integer payloadType = protoMessageFactory.getPayloadTypeByMessageClass(messageClass);
        if (payloadType == null) {
            LOGGER.error("Attempt to encode a message with unsupported class {}. Closing channel {}.", messageClass, channel);
            channel.close();
            return;
        }

        ProtoMessage.Builder builder = ProtoMessage.newBuilder();
        builder.setPayloadType(payloadType);
        builder.setPayload(message.toByteString());
        CodecUtils.setProtoMessageCommonFields(builder, msg);

        ProtoMessage protoMessage = builder.build();
        out.add(protoMessage);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Encoded message ProtoMessage[{}] in channel {}", StringUtils.abbreviate(TextFormat.shortDebugString(protoMessage), 5000), channel);
        }
    }

}
