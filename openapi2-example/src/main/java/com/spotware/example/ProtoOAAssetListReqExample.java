package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetListReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetListRes;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;

public class ProtoOAAssetListReqExample {

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());

        try {
            AuthHelper authHelper = nettyClient.getAuthHelper();
            Long ctidTraderAccountId = config.getCtid();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), ctidTraderAccountId, config.getAccessToken());

            sendProtoOAAssetListReq(nettyClient, ctidTraderAccountId);
        } finally {
            nettyClient.closeConnection();
        }
    }

    private static void sendProtoOAAssetListReq(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
        ProtoOAAssetListReq protoOAAssetListReq = ProtoOAAssetListReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAAssetListReq);

        MessageLite messageLite = receiver.waitSingleResult();

        if (messageLite instanceof ProtoOAAssetListRes) {
            ProtoOAAssetListRes response = (ProtoOAAssetListRes) messageLite;
            System.out.println(response);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
}
