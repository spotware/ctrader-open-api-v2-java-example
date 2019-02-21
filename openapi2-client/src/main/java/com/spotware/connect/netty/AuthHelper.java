package com.spotware.connect.netty;

import com.google.protobuf.MessageLite;
import com.spotware.connect.netty.exception.AuthorizationException;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthRes;

public class AuthHelper {

    private NettyClient nettyClient;

    AuthHelper(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    public MessageLite authorizeApplication(String clientId, String clientSecret) throws InterruptedException {
        ProtoOAApplicationAuthReq appAuthReq = createAuthorizationRequest(clientId, clientSecret);
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(appAuthReq);
        return receiver.waitSingleResult();
    }

    public MessageLite authorizeAccount(long ctidTraderAccountId, String accessToken) throws InterruptedException {
        ProtoOAAccountAuthReq accountAuthorizationRequest = createAccountAuthorizationRequest(accessToken, ctidTraderAccountId);
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(accountAuthorizationRequest);
        return receiver.waitSingleResult();
    }

    public void authorizeOnlyOneTrader(String clientId, String clientSecret,long ctidTraderAccountId, String accessToken) throws InterruptedException {
        MessageLite applicationAuthRes = authorizeApplication(clientId, clientSecret);
        if (!(applicationAuthRes instanceof ProtoOAApplicationAuthRes)) {
            throw new AuthorizationException(applicationAuthRes.toString());
        }
        MessageLite accountAuthRes = authorizeAccount(ctidTraderAccountId, accessToken);
        if (!(accountAuthRes instanceof ProtoOAAccountAuthRes)) {
            throw new AuthorizationException(accountAuthRes.toString());
        }
    }

    private ProtoOAApplicationAuthReq createAuthorizationRequest(String clientId, String clientSecret) {
        return ProtoOAApplicationAuthReq.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    private ProtoOAAccountAuthReq createAccountAuthorizationRequest(String accessToken, long ctidTraderAccountId) {
        return ProtoOAAccountAuthReq.newBuilder()
                .setAccessToken(accessToken)
                .setCtidTraderAccountId(ctidTraderAccountId)
                .build();
    }
}
