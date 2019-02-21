package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolByIdReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolByIdRes;

public class ProtoOASymbolByIdReqExample {

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            Long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), ctidTraderAccountId, config.getAccessToken());

            sendProtoOASymbolByIdReq(nettyClient, ctidTraderAccountId);
        } finally {
            nettyClient.closeConnection();
        }
    }

    private static void sendProtoOASymbolByIdReq(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOASymbolByIdReq protoOASymbolByIdReq = ProtoOASymbolByIdReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .addSymbolId(1)
                .addSymbolId(2)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASymbolByIdReq);

        MessageLite messageLite = receiver.waitSingleResult(200L);

        if (messageLite instanceof ProtoOASymbolByIdRes) {
            ProtoOASymbolByIdRes response = (ProtoOASymbolByIdRes) messageLite;
            System.out.println(response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
}
