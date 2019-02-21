package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsForConversionReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsForConversionRes;

public class ProtoOASymbolsForConversionReqExample {

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            Long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), ctidTraderAccountId, config.getAccessToken());

            sendProtoOASymbolsForConversionReq(nettyClient, ctidTraderAccountId);
        } finally {
            nettyClient.closeConnection();
        }
    }

    private static void sendProtoOASymbolsForConversionReq(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOASymbolsForConversionReq protoOASymbolByIdReq = ProtoOASymbolsForConversionReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .setFirstAssetId(1)
                .setLastAssetId(2)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOASymbolByIdReq);

        MessageLite messageLite = receiver.waitSingleResult(200L);

        if (messageLite instanceof ProtoOASymbolsForConversionRes) {
            ProtoOASymbolsForConversionRes response = (ProtoOASymbolsForConversionRes) messageLite;
            System.out.println(response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
}
