package com.spotware.connect.protocol;


import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class ChannelMessage<M extends MessageLite> {
    private M message;
    private Long commandId;
    private Long sessionId;
    private Long clientId;
    private String clientRequestId;
    private ChannelMessageTimestamp timestamp;

    public ChannelMessage(M message) {
        assert message != null : "Constructor parameter (message) cannot be null.";

        this.message = message;
    }

    public ChannelMessage(M message, ChannelMessage<?> originalRequest) {
        this(message);
        this.clientRequestId = originalRequest.clientRequestId;
        this.commandId = originalRequest.commandId;
        this.sessionId = originalRequest.sessionId;
        this.clientId = originalRequest.clientId;
        this.timestamp = originalRequest.timestamp;
    }

    public ChannelMessage(M message, String clientRequestId) {
        this(message);
        this.clientRequestId = clientRequestId;
    }

    public ChannelMessage(M message, String clientRequestId, long clientId) {
        this(message, clientRequestId);
        this.clientId = clientId;
    }

    public String getClientRequestId() {
        return this.clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }

    public M getMessage() {
        return this.message;
    }

    public void setMessage(M message) {
        this.message = message;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCommandId() {
        return this.commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public ChannelMessageTimestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(ChannelMessageTimestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.appendCommonToStringContent(builder);
        builder.append(", message=[");
        if (this.message instanceof Message) {
            builder.append(TextFormat.shortDebugString((Message)this.message));
        } else {
            builder.append(this.message.toString());
        }

        builder.append("]]");
        return builder.toString();
    }

    public String toAbbreviatedString() {
        StringBuilder builder = new StringBuilder();
        this.appendCommonToStringContent(builder);
        builder.append(", message=[");
        if (this.message instanceof Message) {
            builder.append(StringUtils.abbreviate(TextFormat.shortDebugString((Message)this.message), 5000));
        } else {
            builder.append(StringUtils.abbreviate(this.message.toString(), 5000));
        }

        builder.append("]]");
        return builder.toString();
    }

    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        this.appendCommonToStringContent(builder);
        builder.append("]");
        return builder.toString();
    }

    private StringBuilder appendCommonToStringContent(StringBuilder builder) {
        builder.append(this.getClass().getSimpleName());
        builder.append("[");
        builder.append("messageClass=").append(this.message.getClass().getSimpleName());
        if (this.clientRequestId != null) {
            builder.append(", clientRequestId=").append(this.clientRequestId);
        }

        if (this.sessionId != null) {
            builder.append(", sessionId=").append(this.sessionId);
        }

        if (this.commandId != null) {
            builder.append(", commandId=").append(this.commandId);
        }

        if (this.clientId != null) {
            builder.append(", clientId=").append(this.clientId);
        }

        return builder;
    }

    public int hashCode() {
        return Objects.hash(this.message, this.clientRequestId, this.sessionId, this.commandId);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ChannelMessage)) {
            return false;
        } else {
            ChannelMessage<?> other = (ChannelMessage)obj;
            return Objects.equals(this.message, other.message) && Objects.equals(this.clientRequestId, other.clientRequestId) && Objects.equals(this.sessionId, other.sessionId) && Objects.equals(this.commandId, other.commandId);
        }
    }
}

