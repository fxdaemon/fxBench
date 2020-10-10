/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/RFQRequest.java#1 $
 *
 * Copyright (c) 2008 FXCM, LLC.
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
 * $History: $
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.fix.pretrade.QuoteRequest;

import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;

/**
 * @author Andre Mermegas
 *         Date: Apr 3, 2006
 *         Time: 3:46:20 PM
 */
public class RFQRequest extends BaseRequest implements IRequester {
    private String mAccount;
    private double mAmount;
    private String mCurrency;

    public LiaisonStatus doIt() throws LiaisonException {
        if (getTradeDesk().getLiaison().getSessionID() == null) {
            throw new TradingAPIException(null, "IDS_SESSION_ISNOT_LOGGED");
        }
        try {
        	FxcmServerSession ts = (FxcmServerSession)getTradeDesk().getTradingServerSession();
            QuoteRequest qr = new QuoteRequest();
            qr.setAccount(mAccount);
            qr.setOrderQty2(mAmount);
            qr.setInstrument(ts.getTradingSessionStatus().getSecurity(mCurrency));
            ts.send(qr);
            return LiaisonStatus.READY;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TradingAPIException(e, "IDS_INVALID_REQUEST_FIELD");
        }
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String aAccount) {
        mAccount = aAccount;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double aAmount) {
        mAmount = aAmount;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String aCurrency) {
        mCurrency = aCurrency;
    }

    public IRequest getRequest() {
        return this;
    }

    public IRequester getSibling() {
        return null;
    }

    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
