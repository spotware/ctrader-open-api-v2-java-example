package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolChangedEvent;

public class ProtoOASymbolChangedEventExample {

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            authHelper.authorizeApplication(config.getClientId(), config.getClientSecret());

            long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeAccount(ctidTraderAccountId, config.getAccessToken());

            catchProtoOASymbolChangedEvent(nettyClient, ctidTraderAccountId);
        } finally {
            nettyClient.closeConnection();
        }
    }

    private static void catchProtoOASymbolChangedEvent(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {

        nettyClient.addListener(message -> {
            MessageLite messageLite = message.getMessage();
            if (!(message.getMessage() instanceof ProtoOASymbolChangedEvent)) {
                return;
            }
            if (messageLite instanceof ProtoOASymbolChangedEvent) {
                ProtoOASymbolChangedEvent event = (ProtoOASymbolChangedEvent) messageLite;
                if (event.getCtidTraderAccountId() != ctidTraderAccountId) {
                    return;
                }
                // PLACE FOR CODE
                System.out.println(event);
            }
        });

    }
}
