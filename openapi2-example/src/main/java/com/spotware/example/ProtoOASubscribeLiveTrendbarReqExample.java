package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOASpotEvent;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsRes;
import com.xtrader.protocol.openapi.v2.model.ProtoOATrendbarPeriod;

public class ProtoOASubscribeLiveTrendbarReqExample {
    private static final long symbolId = 1; // Most likely it is EURUSD

    private static Config config;

    public static void main(String[] args) throws InterruptedException {
        config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            Long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), ctidTraderAccountId, config.getAccessToken());

            addSpotListener(nettyClient, ctidTraderAccountId);
            subscribeSymbolSpot(nettyClient, ctidTraderAccountId, symbolId);
            subscribeSymbolTrendbar(nettyClient, ctidTraderAccountId, symbolId);

            Thread.sleep(60000);

            unsubscribeSymbolTrendbar(nettyClient, ctidTraderAccountId, symbolId);
            unsubscribeSymbolSpot(nettyClient, ctidTraderAccountId, symbolId);
        } finally {
        	nettyClient.closeConnection();
        }
    }

    private static void addSpotListener(NettyClient nettyClient, long ctidTraderAccountId) {
        nettyClient.addListener(message -> {
            MessageLite messageLite = message.getMessage();

            if (messageLite instanceof ProtoOASpotEvent) {
                ProtoOASpotEvent event = (ProtoOASpotEvent) messageLite;
                if (event.getCtidTraderAccountId() != ctidTraderAccountId) {
                    return;
                }
                System.out.println("Received "+ messageLite.getClass().getSimpleName() + ":\n" + messageLite);
            }
        });
    }

    private static void subscribeSymbolSpot(NettyClient nettyClient, long ctidTraderAccountId, long symbolId) throws InterruptedException {
        ProtoOASubscribeSpotsReq protoOASubscribeSpotsReq = ProtoOASubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASubscribeSpotsReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }

    private static void subscribeSymbolTrendbar(NettyClient nettyClient, long ctidTraderAccountId, long symbolId) throws InterruptedException {
        ProtoOASubscribeLiveTrendbarReq protoOASubscribeSpotsReq1 = ProtoOASubscribeLiveTrendbarReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .setPeriod(ProtoOATrendbarPeriod.M1)
                .setSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASubscribeSpotsReq1);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }

    private static void unsubscribeSymbolSpot(NettyClient nettyClient, long ctidTraderAccountId, long symbolId) throws InterruptedException {
        ProtoOAUnsubscribeSpotsReq protoOAUnsubscribeSpotsReq = ProtoOAUnsubscribeSpotsReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(symbolId)
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

    private static void unsubscribeSymbolTrendbar(NettyClient nettyClient, long ctidTraderAccountId, long symbolId) throws InterruptedException {
        ProtoOAUnsubscribeLiveTrendbarReq protoOAUnsubscribeLiveTrendbarReq = ProtoOAUnsubscribeLiveTrendbarReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .setPeriod(ProtoOATrendbarPeriod.M1)
                .setSymbolId(symbolId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAUnsubscribeLiveTrendbarReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
}
