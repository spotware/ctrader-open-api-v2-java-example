package com.spotware.connect.netty.handler.util;

import com.spotware.connect.protocol.ChannelMessage;
import com.xtrader.protocol.proto.commons.ProtoMessage;

public class CodecUtils {

    public static void setProtoMessageCommonFields(ProtoMessage.Builder protoMessageBuilder, ChannelMessage<?> channelMessage) {
        if (channelMessage.getClientRequestId() != null) {
            protoMessageBuilder.setClientMsgId(channelMessage.getClientRequestId());
        }
    }

    public static void setChannelMessageCommonFields(ChannelMessage<?> message, ProtoMessage protoMessage) {
        if (protoMessage.hasClientMsgId()) {
            message.setClientRequestId(protoMessage.getClientMsgId());
        }
    }
}
