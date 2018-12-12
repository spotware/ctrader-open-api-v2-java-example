package com.spotware.connect.netty.handler.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

public class MessageFormat {
    public static String format(String message, Object... params) {
        return MessageFormatter.arrayFormat(message, params).getMessage();
    }

    public static String obfuscateAuthToken(String token) {
        if (token == null) {
            return null;
        } else {
            int start = Math.min(token.length() / 2, 4);
            return StringUtils.overlay(token, "****", start, token.length());
        }
    }

    public static String obfuscatePasswordHash(String passwordHash) {
        return passwordHash == null ? null : StringUtils.overlay(passwordHash, "*********", 16, passwordHash.length() - 8);
    }

    private MessageFormat() {
    }
}
