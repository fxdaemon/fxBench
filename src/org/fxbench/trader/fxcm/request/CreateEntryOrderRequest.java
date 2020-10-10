/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/CreateEntryOrderRequest.java#2 $
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
 * 9/10/2003 created by Ushik
 * 12/8/2004    Andre Mermegas  -- updated to handle entry order requests in the FCXM Msg format
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.IOrdType;
import com.fxcm.fix.ISide;
import com.fxcm.fix.OrdTypeFactory;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.trade.OrderSingle;

import org.fxbench.entity.TAccount;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;


/**
 * Class CreateEntryOrderRequest.<br>
 * <br>
 * It is responsible for creating and sending to server object of class
 * com.fxcm.fxtrade.common.datatypes.EntryNewOrder.<br>
 * <br>
 * Creation date (9/10/2003 2:27 PM)
 */
public class CreateEntryOrderRequest extends BaseRequest implements IRequester {
    private String mAccount;
    private long mAmount;
    private String mCurrency;
    private String mCustomText;
    private double mRate;
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
            TOffer rate = getTradeDesk().getOffer(mCurrency);
            FxcmServerSession ts = (FxcmServerSession)getTradeDesk().getTradingServerSession();
            TAccount account = getTradeDesk().getAccounts().getAccount(mAccount);
            IOrdType entryType = null;
            ISide side = null;
            if (bs == BnS.BUY) {
                side = SideFactory.BUY;
                if (mRate - rate.getAsk() >= 0) {
                    entryType = OrdTypeFactory.STOP;
                } else {
                    entryType = OrdTypeFactory.LIMIT;
                }
            } else if (bs == BnS.SELL) {
                side = SideFactory.SELL;
                if (mRate - rate.getBid() >= 0) {
                    entryType = OrdTypeFactory.LIMIT;
                } else {
                    entryType = OrdTypeFactory.STOP;
                }
            }
            OrderSingle entryOrder = MessageGenerator.generateStopLimitEntry(mRate,
                                                                             entryType,
                                                                             account.getAccountName(),
                                                                             mAmount,
                                                                             side,
                                                                             rate.getSymbol(),
                                                                             mCustomText);
            ts.send(entryOrder);
            return LiaisonStatus.READY;
        } catch (Exception e) {
            e.printStackTrace();
            // it can be in case of position being closed by server in
            // asynchronous process
            throw new TradingAPIException(e, "IDS_INVALID_REQUEST_FIELD");
        }
    }

    /**
     * Returns account.
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
     * Returns currency pair.
     */
    public String getCurrency() {
        return mCurrency;
    }

    /**
     * Sets currency pair.
     */
    public void setCurrency(String aCurrency) {
        mCurrency = aCurrency;
    }

    /**
     * Returns entry order rate.
     */
    public double getRate() {
        return mRate;
    }

    /**
     * Sets entry order rate.
     */
    public void setRate(double aRate) {
        mRate = aRate;
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
     * Returns contract side (buy or sell).
     */
    public BnS getBS() {
        return bs;
    }

    /**
     * Sets contract side (buy or sell).
     */
    public void setBS(BnS aSide) {
        bs = aSide;
    }

    public void setCustomText(String aCustomText) {
        mCustomText = aCustomText;
    }

    /**
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
