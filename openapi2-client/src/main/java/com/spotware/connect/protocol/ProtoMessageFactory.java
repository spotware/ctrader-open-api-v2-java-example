package com.spotware.connect.protocol;

import static java.util.Objects.requireNonNull;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.xtrader.protocol.proto.commons.ProtoMessage;

public class ProtoMessageFactory {
    private static final String PAYLOAD_TYPE = "payloadType";
    private final Map<Integer, MessageLite> messageByPayloadTypeMap;
    private final HashMap<Class<? extends MessageLite>, Integer> payloadTypeByMessageClassMap;

    ProtoMessageFactory(Collection<? extends MessageLite> messagesDefaultInstances) {
        requireNonNull(messagesDefaultInstances, "Constructor parameter cannot be null.");
        this.messageByPayloadTypeMap = new HashMap<>();
        this.payloadTypeByMessageClassMap = new HashMap<>(messagesDefaultInstances.size());

        for (MessageLite message : messagesDefaultInstances) {
            int payloadType = getProtoPayloadType(message);
            MessageLite prevValue = this.messageByPayloadTypeMap.put(payloadType, message);
            payloadTypeByMessageClassMap.put(message.getClass(), payloadType);
            if (prevValue != null) {
                throw new IllegalStateException("Messages " + message.getClass() + " and " + prevValue.getClass() + " have the same payloadType : "
                        + payloadType);
            }
        }
    }

    public MessageLite getMessageByPayloadType(int payloadType) {
        return messageByPayloadTypeMap.get(payloadType);
    }

    public Integer getPayloadTypeByMessageClass(Class<? extends MessageLite> clazz) {
        return payloadTypeByMessageClassMap.get(clazz);
    }

    private static int getProtoPayloadType(MessageLite messageLite) {
        try {
            assert messageLite instanceof Message;
            Message message = (Message) messageLite;

            Descriptors.FieldDescriptor payloadType = message.getDescriptorForType().findFieldByName(PAYLOAD_TYPE);

            return ((Descriptors.EnumValueDescriptor) message.getField(payloadType)).getNumber();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private ProtoMessage createMessage(int payloadType, ByteString payload, String clientMsgId) {
        ProtoMessage.Builder protoMsg = ProtoMessage.newBuilder();
        protoMsg.setPayloadType(payloadType);
        if (payload != null) {
            protoMsg.setPayload(payload);
        }
        if (clientMsgId != null) {
            protoMsg.setClientMsgId(clientMsgId);
        }

        return protoMsg.build();
    }

    public final ProtoMessage createMessage(MessageLite messageLite) {
        Message message = (Message) messageLite;
        int payloadType = getProtoPayloadType(messageLite);
        ByteString payload = message.toByteString();
        String clientMsgId = randomAlphanumeric(12);
        return createMessage(payloadType, payload, clientMsgId);
    }
}
