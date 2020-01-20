package com.spotware.connect.protocol;

import java.util.Arrays;

import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountDisconnectEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountLogoutReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountLogoutRes;
import com.xtrader.protocol.openapi.v2.ProtoOAAccountsTokenInvalidatedEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAAmendOrderReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAmendPositionSLTPReq;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthReq;
import com.xtrader.protocol.openapi.v2.ProtoOAApplicationAuthRes;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetClassListReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetClassListRes;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetListReq;
import com.xtrader.protocol.openapi.v2.ProtoOAAssetListRes;
import com.xtrader.protocol.openapi.v2.ProtoOACancelOrderReq;
import com.xtrader.protocol.openapi.v2.ProtoOACashFlowHistoryListReq;
import com.xtrader.protocol.openapi.v2.ProtoOACashFlowHistoryListRes;
import com.xtrader.protocol.openapi.v2.ProtoOAClientDisconnectEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAClosePositionReq;
import com.xtrader.protocol.openapi.v2.ProtoOADealListReq;
import com.xtrader.protocol.openapi.v2.ProtoOADealListRes;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOAExecutionEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAExpectedMarginReq;
import com.xtrader.protocol.openapi.v2.ProtoOAExpectedMarginRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetAccountListByAccessTokenReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetAccountListByAccessTokenRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetCtidProfileByTokenReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetCtidProfileByTokenRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTickDataReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTickDataRes;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTrendbarsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAGetTrendbarsRes;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallListReq;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallListRes;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallTriggerEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallUpdateEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallUpdateReq;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginCallUpdateRes;
import com.xtrader.protocol.openapi.v2.ProtoOAMarginChangedEvent;
import com.xtrader.protocol.openapi.v2.ProtoOANewOrderReq;
import com.xtrader.protocol.openapi.v2.ProtoOAOrderErrorEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAReconcileReq;
import com.xtrader.protocol.openapi.v2.ProtoOAReconcileRes;
import com.xtrader.protocol.openapi.v2.ProtoOARefreshTokenReq;
import com.xtrader.protocol.openapi.v2.ProtoOARefreshTokenRes;
import com.xtrader.protocol.openapi.v2.ProtoOASpotEvent;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeLiveTrendbarRes;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOASubscribeSpotsRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolByIdReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolByIdRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolCategoryListReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolCategoryListRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolChangedEvent;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsForConversionReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsForConversionRes;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsListReq;
import com.xtrader.protocol.openapi.v2.ProtoOASymbolsListRes;
import com.xtrader.protocol.openapi.v2.ProtoOATraderReq;
import com.xtrader.protocol.openapi.v2.ProtoOATraderRes;
import com.xtrader.protocol.openapi.v2.ProtoOATraderUpdatedEvent;
import com.xtrader.protocol.openapi.v2.ProtoOATrailingSLChangedEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeLiveTrendbarReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeLiveTrendbarRes;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsReq;
import com.xtrader.protocol.openapi.v2.ProtoOAUnsubscribeSpotsRes;
import com.xtrader.protocol.openapi.v2.ProtoOAVersionReq;
import com.xtrader.protocol.openapi.v2.ProtoOAVersionRes;
import com.xtrader.protocol.proto.commons.ProtoHeartbeatEvent;

public class OA2ProtoMessageFactory extends ProtoMessageFactory {

    public OA2ProtoMessageFactory() {
        super(Arrays.asList(
                // General
                ProtoHeartbeatEvent.getDefaultInstance(),
                ProtoOAErrorRes.getDefaultInstance(),

                // Auth
                ProtoOAApplicationAuthReq.getDefaultInstance(), ProtoOAApplicationAuthRes.getDefaultInstance(),
                ProtoOAAccountAuthReq.getDefaultInstance(), ProtoOAAccountAuthRes.getDefaultInstance(),
                ProtoOAVersionReq.getDefaultInstance(), ProtoOAVersionRes.getDefaultInstance(),
                ProtoOAGetAccountListByAccessTokenReq.getDefaultInstance(), ProtoOAGetAccountListByAccessTokenRes.getDefaultInstance(),
                ProtoOAGetCtidProfileByTokenReq.getDefaultInstance(), ProtoOAGetCtidProfileByTokenRes.getDefaultInstance(),
                ProtoOAAccountLogoutReq.getDefaultInstance(), ProtoOAAccountLogoutRes.getDefaultInstance(),
                ProtoOARefreshTokenReq.getDefaultInstance(), ProtoOARefreshTokenRes.getDefaultInstance(),

                ProtoOAAccountsTokenInvalidatedEvent.getDefaultInstance(),
                ProtoOAClientDisconnectEvent.getDefaultInstance(),
                ProtoOAAccountDisconnectEvent.getDefaultInstance(),

                // Orders
                ProtoOANewOrderReq.getDefaultInstance(), ProtoOATrailingSLChangedEvent.getDefaultInstance(), ProtoOACancelOrderReq.getDefaultInstance(),
                ProtoOAAmendOrderReq.getDefaultInstance(), ProtoOAAmendPositionSLTPReq.getDefaultInstance(), ProtoOAClosePositionReq.getDefaultInstance(),
                ProtoOAExecutionEvent.getDefaultInstance(), ProtoOAOrderErrorEvent.getDefaultInstance(),

                // Domain objects - lists of symbols, assets etc.
                ProtoOAReconcileReq.getDefaultInstance(), ProtoOAReconcileRes.getDefaultInstance(),
                ProtoOAAssetListReq.getDefaultInstance(), ProtoOAAssetListRes.getDefaultInstance(),
                ProtoOAAssetClassListReq.getDefaultInstance(), ProtoOAAssetClassListRes.getDefaultInstance(),
                ProtoOASymbolCategoryListReq.getDefaultInstance(), ProtoOASymbolCategoryListRes.getDefaultInstance(),
                ProtoOASymbolsListReq.getDefaultInstance(), ProtoOASymbolsListRes.getDefaultInstance(),
                ProtoOASymbolByIdReq.getDefaultInstance(), ProtoOASymbolByIdRes.getDefaultInstance(),
                ProtoOASymbolsForConversionReq.getDefaultInstance(), ProtoOASymbolsForConversionRes.getDefaultInstance(),
                ProtoOADealListReq.getDefaultInstance(), ProtoOADealListRes.getDefaultInstance(),
                ProtoOATraderReq.getDefaultInstance(), ProtoOATraderRes.getDefaultInstance(),

                ProtoOASymbolChangedEvent.getDefaultInstance(),
                ProtoOATraderUpdatedEvent.getDefaultInstance(),

                // Spots, depth, trendbars subscription
                ProtoOASubscribeSpotsReq.getDefaultInstance(), ProtoOASubscribeSpotsRes.getDefaultInstance(),
                ProtoOAUnsubscribeSpotsReq.getDefaultInstance(), ProtoOAUnsubscribeSpotsRes.getDefaultInstance(),
                ProtoOASubscribeLiveTrendbarReq.getDefaultInstance(), ProtoOASubscribeLiveTrendbarRes.getDefaultInstance(),
                ProtoOAUnsubscribeLiveTrendbarReq.getDefaultInstance(), ProtoOAUnsubscribeLiveTrendbarRes.getDefaultInstance(),
                ProtoOAGetTrendbarsReq.getDefaultInstance(), ProtoOAGetTrendbarsRes.getDefaultInstance(),
                ProtoOAGetTickDataReq.getDefaultInstance(), ProtoOAGetTickDataRes.getDefaultInstance(),

                ProtoOASpotEvent.getDefaultInstance(),

                // Margin, balance etc.
                ProtoOACashFlowHistoryListReq.getDefaultInstance(), ProtoOACashFlowHistoryListRes.getDefaultInstance(),
                ProtoOAExpectedMarginReq.getDefaultInstance(), ProtoOAExpectedMarginRes.getDefaultInstance(),
                ProtoOAMarginCallListReq.getDefaultInstance(), ProtoOAMarginCallListRes.getDefaultInstance(),
                ProtoOAMarginCallUpdateReq.getDefaultInstance(), ProtoOAMarginCallUpdateRes.getDefaultInstance(),
                ProtoOAMarginCallUpdateEvent.getDefaultInstance(), ProtoOAMarginCallTriggerEvent.getDefaultInstance(),

                ProtoOAMarginChangedEvent.getDefaultInstance()
                ));
    }
}


