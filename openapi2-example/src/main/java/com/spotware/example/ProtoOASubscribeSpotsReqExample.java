package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOASpotEvent;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsRes;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsRes;

public class ProtoOASubscribeSpotsReqExample {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            Long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), ctidTraderAccountId, config.getAccessToken());

            subscribeSymbol(nettyClient, ctidTraderAccountId);
        } finally {
            nettyClient.closeConnection();
        }
    }

    private static void subscribeSymbol(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        addSpotListener(nettyClient, ctidTraderAccountId);

        ProtoOASubscribeSpotsReq protoOASubscribeSpotsReq = ProtoOASubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(1)
                .addSymbolId(2)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOASubscribeSpotsRes) {
            ProtoOASubscribeSpotsRes response = (ProtoOASubscribeSpotsRes) messageLite;
            System.out.println("ProtoOASubscribeSpotsRes: " + response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }

        Thread.sleep(15000);

        unsubscribeSymbol(nettyClient, ctidTraderAccountId);
    }

    private static void addSpotListener(NettyClient nettyClient, long ctidTraderAccountId) {
        nettyClient.addListener(message -> {
            MessageLite messageLite = message.getMessage();
            if (!(message.getMessage() instanceof ProtoOASpotEvent)) {
                return;
            }
            if (messageLite instanceof ProtoOASpotEvent) {
                ProtoOASpotEvent event = (ProtoOASpotEvent) messageLite;
                if (event.getCtidTraderAccountId() != ctidTraderAccountId) {
                    return;
                }
                // PLACE FOR CODE
                System.out.println("Received spot event:");
                System.out.println(event);
            }
        });
    }

    private static void unsubscribeSymbol(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOAUnsubscribeSpotsReq protoOAUnsubscribeSpotsReq = ProtoOAUnsubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(1)
                .addSymbolId(2)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAUnsubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAUnsubscribeSpotsRes) {
            ProtoOAUnsubscribeSpotsRes response = (ProtoOAUnsubscribeSpotsRes) messageLite;
            System.out.println("ProtoOAUnsubscribeSpotsRes: " + response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
}
