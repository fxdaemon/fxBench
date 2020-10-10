/*
 * $Header:$
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
 * 12/8/2004    Andre Mermegas  -- updated to send out FXCMRequest DasMessages
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.ISide;
import com.fxcm.fix.OrdTypeFactory;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.trade.OrderCancelReplaceRequest;
import com.fxcm.fix.trade.OrderSingle;

import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TOrder;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;

/**
 * Class SetStopLimitRequest.<br>
 * <br>
 * It is responsible for creating and sending to server instances of one or
 * both extension of the class com.fxcm.fxtrade.common.datatypes.StopLimitOrder:
 * StopOrder or/and LimitOrder:.<br>
 * <br>
 * Creation date (9/10/2003 12:36 PM)
 */
public class SetStopLimitOrderRequest extends SetStopLimitRequest {
    private class SetRequest implements IRequester {
        private SetRequest mNext;

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
                TOrder order = getTradeDesk().getOrder(getOrderID());
                TradeDesk tradeDesk = getTradeDesk();
                FxcmServerSession ts = (FxcmServerSession)getTradeDesk().getTradingServerSession();
                TAccount account = tradeDesk.getAccounts().getAccount(order.getAccountId());
                ISide buySide;
                if (order.getBS() == BnS.BUY) {
                    buySide = SideFactory.BUY;
                } else {
                    buySide = SideFactory.SELL;
                }
                if (getLimit() != -1.0 && order.getLimit() == 0) {
                    OrderSingle stopLimit = MessageGenerator.generateStopLimitClose(getLimit(),
                                                                                    order.getTradeId(),
                                                                                    OrdTypeFactory.LIMIT,
                                                                                    account.getAccountName(),
                                                                                    order.getAmount(),
                                                                                    buySide,
                                                                                    order.getSymbol(),
                                                                                    null,
                                                                                    0);
                    ts.send(stopLimit);
                    return LiaisonStatus.READY;
                } else if (getStop() != -1.0 && order.getStop() == 0) {
                    OrderSingle stopLimit =
                            MessageGenerator.generateStopLimitClose(getStop(),
                                                                    order.getTradeId(),
                                                                    OrdTypeFactory.STOP,
                                                                    account.getAccountName(),
                                                                    order.getAmount(),
                                                                    buySide,
                                                                    order.getSymbol(),
                                                                    null,
                                                                    getTrailStop());
                    ts.send(stopLimit);
                    return LiaisonStatus.READY;
                } else if (getLimit() != -1.0 && order.getLimit() > 0) {
                    String limitOrderID = order.getLimitOrderID();
                    OrderCancelReplaceRequest stopLimit =
                            MessageGenerator.generateOrderReplaceRequest(null,
                                                                         limitOrderID,
                                                                         buySide,
                                                                         OrdTypeFactory.LIMIT,
                                                                         getLimit(),
                                                                         0,
                                                                         account.getAccountName());
                    ts.send(stopLimit);
                    return LiaisonStatus.READY;
                } else if (getStop() != -1.0 && order.getStop() > 0) {
                    String stopOrderID = order.getStopOrderID();
                    OrderCancelReplaceRequest stopLimit =
                            MessageGenerator.generateOrderReplaceRequest(null,
                                                                         stopOrderID,
                                                                         buySide,
                                                                         OrdTypeFactory.STOP,
                                                                         getStop(),
                                                                         getTrailStop(),
                                                                         account.getAccountName());
                    ts.send(stopLimit);
                    return LiaisonStatus.READY;
                } else {
                    return LiaisonStatus.READY;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // it can be in case of position being closed by server in
                // asynchronous process
                throw new TradingAPIException(e, "IDS_INVALID_REQUEST_FIELD");
            }
        }

        /**
         * Returns parent batch request
         */
        public IRequest getRequest() {
            return SetStopLimitOrderRequest.this;
        }

        /**
         * Returns next request of batch request
         */
        public IRequester getSibling() {
            return mNext;
        }

        public void setLink(SetRequest aNext) {
            mNext = aNext;
        }

        /**
         * does nothing because is called never.
         */
        public void toQueue(IReqCollection aQueue) {
        }
    }

    private String mOrderID;

    public String getOrderID() {
        return mOrderID;
    }

    public void setOrderID(String aOrderID) {
        mOrderID = aOrderID;
    }

    /**
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    @Override
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(new SetRequest());
    }
}
