package com.spotware.connect.netty.handler;

import javax.net.ssl.SSLException;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslHandler;

public interface SslEngineFactory {

    SslHandler newHandler(Channel channel) throws SSLException;
}
