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
package org.fxbench.trader.local;

import java.util.Date;

import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequestFactory;

/**
 * @Creation date (9/5/2003 4:50 PM)
 */
public class RequestFactory implements IRequestFactory
{
	String dataPath;
	
	public RequestFactory(String path) {
		this.dataPath = path;
	}
	
    public IRequest closePosition(String aTicketID, long aAmount, String aCustomText, int aAtMarket) {        
        return null;
    }

    public IRequest closeTrueMarket(String aTicketID, long aAmount, String aCustomText) {
        return null;
    }

    public IRequest createEntryOrder(String aCurrency,
                                     String aAccount,
                                     BnS aSide,
                                     double aRate,
                                     long aAmount,
                                     String aCustomText) {
        return null;
    }

    public IRequest createMarketOrder(String aCurrency,
                                      String aAccount,
                                      BnS aSide,
                                      long aAmount,
                                      String aCustomText,
                                      int aAtMarketPoints) {
        return null;
    }

    public IRequest createRequestForQuote(String aAccount, String aCCY, long aAmount) {
        return null;
    }

    public IRequest changePassword(String aOldPassword, String aNewPassword, String aConfirmNewPassword) {
        return null;
    }

    public IRequest createTrueMarketOrder(String aCurrency,
                                          String aAccount,
                                          BnS aSide,
                                          long aAmount,
                                          String aCustomText) {
        return null;
    }

    public IRequest removeEntryOrder(String aOrderID) {
        return null;
    }

    public IRequest resetStopLimit(String aTicketID, boolean aResetStop, boolean aResetLimit, int aTrailStop) {
        return null;
    }

    public IRequest setStopLimitOrder(String aOrderID, double aStop, double aLimit, int aTrailStop) {
        return null;
    }

    public IRequest setStopLimitPosition(String aTicketID, double aStop, double aLimit, int aTrailStop) {
        return null;
    }

    public IRequest updateEntryOrder(String aOrderID, double aRate, long aAmount, String aCustomText) {
        return null;
    }
    
    public IRequest getPriceHistory(String symbol, String period, Date startDate, Date endDate) {
    	GetPriceHistoryRequest request = new GetPriceHistoryRequest();
        request.setSymbol(symbol);
        request.setPeriod(period);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setDataPath(dataPath);
        return request;
    }
}
