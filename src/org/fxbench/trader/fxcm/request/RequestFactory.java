/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/RequestFactory.java#1 $
 *
 * Copyright (c) 2006 FXCM, LLC.
 * 32 Old Slip, New York NY, 10005 USA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.fxbench.trader.fxcm.request;

import java.util.Date;

import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequestFactory;
import org.fxbench.trader.fxcm.request.ChangePasswordRequest;
import org.fxbench.trader.fxcm.request.ClosePositionRequest;
import org.fxbench.trader.fxcm.request.CloseTrueMarketOrderRequest;
import org.fxbench.trader.fxcm.request.CreateEntryOrderRequest;
import org.fxbench.trader.fxcm.request.CreateMarketOrderRequest;
import org.fxbench.trader.fxcm.request.CreateTrueMarketOrderRequest;
import org.fxbench.trader.fxcm.request.GetPriceHistoryRequest;
import org.fxbench.trader.fxcm.request.RFQRequest;
import org.fxbench.trader.fxcm.request.RemoveEntryOrderRequest;
import org.fxbench.trader.fxcm.request.ResetStopLimitRequest;
import org.fxbench.trader.fxcm.request.SetStopLimitOrderRequest;
import org.fxbench.trader.fxcm.request.SetStopLimitRequest;
import org.fxbench.trader.fxcm.request.UpdateEntryOrderRequest;

/**
 * @Creation date (9/5/2003 4:50 PM)
 */
public class RequestFactory implements IRequestFactory {
    public IRequest closePosition(String aTicketID, long aAmount, String aCustomText, int aAtMarket) {
        ClosePositionRequest request = new ClosePositionRequest();
        request.setTicketID(aTicketID);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        request.setAtMarket(aAtMarket);
        return request;
    }

    public IRequest closeTrueMarket(String aTicketID, long aAmount, String aCustomText) {
        CloseTrueMarketOrderRequest request = new CloseTrueMarketOrderRequest();
        request.setTicketID(aTicketID);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        return request;
    }

    public IRequest createEntryOrder(String aCurrency,
                                     String aAccount,
                                     BnS aSide,
                                     double aRate,
                                     long aAmount,
                                     String aCustomText) {
        CreateEntryOrderRequest request = new CreateEntryOrderRequest();
        request.setCurrency(aCurrency);
        request.setAccount(aAccount);
        request.setBS(aSide);
        request.setRate(aRate);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        return request;
    }

    public IRequest createMarketOrder(String aCurrency,
                                      String aAccount,
                                      BnS aSide,
                                      long aAmount,
                                      String aCustomText,
                                      int aAtMarketPoints) {
        CreateMarketOrderRequest request = new CreateMarketOrderRequest();
        request.setCurrency(aCurrency);
        request.setAccount(aAccount);
        request.setBS(aSide);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        request.setAtMarket(aAtMarketPoints);
        return request;
    }

    public IRequest createRequestForQuote(String aAccount, String aCCY, long aAmount) {
        RFQRequest request = new RFQRequest();
        request.setAccount(aAccount);
        request.setCurrency(aCCY);
        request.setAmount(aAmount);
        return request;
    }

    public IRequest changePassword(String aOldPassword, String aNewPassword, String aConfirmNewPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(aOldPassword);
        request.setNewPassword(aNewPassword);
        request.setConfirmNewPassword(aConfirmNewPassword);
        return request;
    }

    public IRequest createTrueMarketOrder(String aCurrency,
                                          String aAccount,
                                          BnS aSide,
                                          long aAmount,
                                          String aCustomText) {
        CreateTrueMarketOrderRequest request = new CreateTrueMarketOrderRequest();
        request.setCurrency(aCurrency);
        request.setAccount(aAccount);
        request.setBS(aSide);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        return request;
    }

    public IRequest removeEntryOrder(String aOrderID) {
        RemoveEntryOrderRequest request = new RemoveEntryOrderRequest();
        request.setOrderID(aOrderID);
        return request;
    }

    public IRequest resetStopLimit(String aTicketID, boolean aResetStop, boolean aResetLimit, int aTrailStop) {
        ResetStopLimitRequest request = new ResetStopLimitRequest();
        request.setTicketID(aTicketID);
        request.setResetStop(aResetStop);
        request.setResetLimit(aResetLimit);
        request.setTrailStop(aTrailStop);
        return request;
    }

    public IRequest setStopLimitOrder(String aOrderID, double aStop, double aLimit, int aTrailStop) {
        SetStopLimitOrderRequest request = new SetStopLimitOrderRequest();
        request.setOrderID(aOrderID);
        request.setStop(aStop);
        request.setLimit(aLimit);
        request.setTrailStop(aTrailStop);
        return request;
    }

    public IRequest setStopLimitPosition(String aTicketID, double aStop, double aLimit, int aTrailStop) {
        SetStopLimitRequest request = new SetStopLimitRequest();
        request.setTicketID(aTicketID);
        request.setStop(aStop);
        request.setLimit(aLimit);
        request.setTrailStop(aTrailStop);
        return request;
    }

    public IRequest updateEntryOrder(String aOrderID, double aRate, long aAmount, String aCustomText) {
        UpdateEntryOrderRequest request = new UpdateEntryOrderRequest();
        request.setOrderID(aOrderID);
        request.setRate(aRate);
        request.setAmount(aAmount);
        request.setCustomText(aCustomText);
        return request;
    }
    
    public IRequest getPriceHistory(String symbol, String interval, Date startDate, Date endDate) {
    	GetPriceHistoryRequest request = new GetPriceHistoryRequest();
        request.setSymbol(symbol);
        request.setInterval(interval);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        return request;
    }
}
