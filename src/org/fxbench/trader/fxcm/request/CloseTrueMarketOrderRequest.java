/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/CloseTrueMarketOrderRequest.java#1 $
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
 * Created: Jun 4, 2007 3:33:22 PM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.ISide;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.trade.OrderSingle;

import org.fxbench.entity.TOffer;
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

/**
 */
public class CloseTrueMarketOrderRequest extends BaseRequest implements IRequester  {
    private String mCustomText;
    private long mAmount;
    private String mTicketID;

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
            TPosition position = getTradeDesk().getOpenPosition(mTicketID);
            TOffer rate = getTradeDesk().getOffer(position.getSymbol());
            ISide side;
            if (position.getBS() == BnS.SELL) {
                side = SideFactory.SELL;
            } else {
                side = SideFactory.BUY;
            }
            OrderSingle delOrder = MessageGenerator.generateCloseMarketOrder(
                    mTicketID,
                    position.getAccountId(),
                    mAmount,
                    side,
                    rate.getSymbol(),
                    mCustomText);
            ts.send(delOrder);
            return LiaisonStatus.READY;
        } catch (Exception e) {
            e.printStackTrace();
            // it can be in case of position being closed by server in
            // asynchronous process
            return LiaisonStatus.READY;
        }
    }

    /**
     * Returns amount
     */
    public long getAmount() {
        return mAmount;
    }

    public String getCustomText() {
        return mCustomText;
    }

    public void setCustomText(String aCustomText) {
        if (aCustomText != null && !"".equals(aCustomText.trim())) {
            mCustomText = aCustomText;
        }
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
     * Returns ticket id
     */
    public String getTicketID() {
        return mTicketID;
    }

    public long getlAmount() {
        return mAmount;
    }

    public String getsTicketID() {
        return mTicketID;
    }

    /**
     * Sets amount
     */
    public void setAmount(long aAmount) {
        mAmount = aAmount;
    }

    /**
     * Sets ticket id
     */
    public void setTicketID(String aTicketID) {
        mTicketID = aTicketID;
    }

    public void setlAmount(long aAmount) {
        mAmount = aAmount;
    }

    public void setsTicketID(String aTicketID) {
        mTicketID = aTicketID;
    }

    /**
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
