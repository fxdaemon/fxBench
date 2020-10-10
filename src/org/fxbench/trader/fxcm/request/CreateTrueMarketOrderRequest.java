/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/CreateTrueMarketOrderRequest.java#1 $
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
 * Author: Andre Mermegas
 * Created: Apr 27, 2007 12:55:22 PM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.ISide;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.trade.OrderSingle;

import org.fxbench.entity.TAccount;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;

public class CreateTrueMarketOrderRequest extends BaseRequest implements IRequester {
    private String mAccount;
    private long mAmount;
    private String mCurrency;
    private String mCustomText;
    private BnS bs;

    /**
     * Executes the request
     *
     * @return Status Ready if successful null else
     */
    public LiaisonStatus doIt() throws LiaisonException {
        if (getTradeDesk().getLiaison().getSessionID() == null) {
            throw new TradingAPIException(null, "IDS_SESSION_ISNOT_LOGGED");
        }
        try {
        	FxcmServerSession ts = (FxcmServerSession)getTradeDesk().getTradingServerSession();
            TAccount account = getTradeDesk().getAccounts().getAccount(mAccount);
            ISide side;
            if (bs == BnS.BUY) {
                side = SideFactory.BUY;
            } else {
                side = SideFactory.SELL;
            }
            OrderSingle orderSingle = MessageGenerator.generateMarketOrder(account.getAccountName(),
                                                                           mAmount,
                                                                           side,
                                                                           mCurrency,
                                                                           mCustomText);
            ts.send(orderSingle);
            return LiaisonStatus.READY;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TradingAPIException(e, "IDS_INVALID_REQUEST_FIELD");
        }
    }

    /**
     * Return account.
     */
    public String getAccount() {
        return mAccount;
    }

    /**
     * Sets account.
     */
    public void setAccount(String aAccount) {
        mAccount = aAccount;
    }

    /**
     * Returns contract size.
     */
    public long getAmount() {
        return mAmount;
    }

    /**
     * Sets contract size.
     */
    public void setAmount(long aAmount) {
        mAmount = aAmount;
    }

    /**
     * Returns market order currency pair.
     */
    public String getCurrency() {
        return mCurrency;
    }

    /**
     * Sets market order currency pair.
     */
    public void setCurrency(String aCurrency) {
        mCurrency = aCurrency;
    }

    /**
     * Returns parent batch request
     */
    public IRequest getRequest() {
        return this;
    }

    /**
     * Returns next request of batch request
     */
    public IRequester getSibling() {
        return null;
    }

    /**
     * Returns side (buy or sell)
     */
    public TPosition.BnS getBS() {
        return bs;
    }

    /**
     * Sets side (buy or sell)
     */
    public void setBS(TPosition.BnS aSide) {
        bs = aSide;
    }

    public void setCustomText(String aCustomText) {
        if (aCustomText != null && !"".equals(aCustomText.trim())) {
            mCustomText = aCustomText;
        }
    }

    /**
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
