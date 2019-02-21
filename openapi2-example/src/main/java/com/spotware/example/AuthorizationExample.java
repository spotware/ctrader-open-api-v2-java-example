package com.spotware.example;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.exception.AuthorizationException;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthRes;

public class AuthorizationExample {
    private static Config config;

    public static void main(String[] args) throws InterruptedException {
        config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
        sendAuthorizationRequest(nettyClient);
    }

    private static void sendAuthorizationRequest(NettyClient nettyClient) throws InterruptedException {
        try {

            AuthHelper authHelper = nettyClient.getAuthHelper();

            MessageLite applicationAuthRes = authHelper.authorizeApplication(config.getClientId(), config.getClientSecret());
            if (applicationAuthRes instanceof ProtoOAApplicationAuthRes) {
                System.out.println("Response ProtoOAApplicationAuthRes received.");
                System.out.println("Response: " + ((ProtoOAApplicationAuthRes) applicationAuthRes).getPayloadType());
            } else {
                System.out.println("Something went wrong");
                System.out.println("Response: " + applicationAuthRes);
                throw new AuthorizationException("application can't be authorize");
            }

            System.out.println();
            MessageLite accountAuthRes = authHelper.authorizeAccount(config.getCtid(), config.getAccessToken());
            if (accountAuthRes instanceof ProtoOAAccountAuthRes) {
                System.out.println("Response ProtoOAAccountAuthRes received.");
                System.out.println("Response: " + accountAuthRes);
            } else {
                System.out.println("Something went wrong.");
                System.out.println("Response: " + accountAuthRes);
            }
        } finally {
            nettyClient.closeConnection();
        }
    }

}
