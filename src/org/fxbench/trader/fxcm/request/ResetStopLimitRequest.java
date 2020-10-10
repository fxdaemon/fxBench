/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/ResetStopLimitRequest.java#1 $
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
 * 12/8/2004    Andre Mermegas  -- updated to send FCXMRequest DasMessage
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.ISide;
import com.fxcm.fix.Instrument;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.trade.OrderCancelRequest;

import org.fxbench.entity.TOrder;
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
 * Class ResetStopLimitRequest.<br>
 * <br>
 * It is responsible for creating and sending to server instances of one or
 * both extension of the class com.fxcm.fxtrade.common.datatypes.StopLimitOrder:
 * ResetStopOrder or/and ResetLimitOrder:.<br>
 * <br>
 * Creation date (9/10/2003 1:13 PM)
 */
public class ResetStopLimitRequest extends BaseRequest implements IRequester {
    /**
     * Flag to reset limit
     */
    private boolean mResetLimit;
    /**
     * Flag to reset stop-loss
     */
    private boolean mResetStop;
    /**
     * Ticket id.
     */
    private String mTicketID;

    private int mTrailStop;

    /**
     * Does nothing, because it's not stored in queue.
     */
    public LiaisonStatus doIt() throws LiaisonException {
        mLogger.error(this, new Exception("doIt of this may not called"));
        return LiaisonStatus.READY;
    }

    /**
     * Returns parent batch request
     */
    public IRequest getRequest() {
        return this;
    }

    /**
     * Returns flag to reset limit
     * @return limit
     */
    public boolean getResetLimit() {
        return mResetLimit;
    }

    /**
     * Sets flag to reset limit
     * @param aResetLimit limit
     */
    public void setResetLimit(boolean aResetLimit) {
        mResetLimit = aResetLimit;
    }

    /**
     * Returns flag to reset stop-loss
     * @return stop
     */
    public boolean getResetStop() {
        return mResetStop;
    }

    /**
     * Sets flag to reset stop-loss
     * @param aResetStop stop
     */
    public void setResetStop(boolean aResetStop) {
        mResetStop = aResetStop;
    }

    /**
     * Returns next request of batch request
     */
    public IRequester getSibling() {
        return null;
    }

    /**
     * Returns ticket id.
     * @return ticketid
     */
    public String getTicketID() {
        return mTicketID;
    }

    /**
     * Sets ticket id.
     * @param aTicketID ticketid
     */
    public void setTicketID(String aTicketID) {
        mTicketID = aTicketID;
    }

    public int getTrailStop() {
        return mTrailStop;
    }

    public void setTrailStop(int aTrailStop) {
        mTrailStop = aTrailStop;
    }

    /**
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(new SetRequest());
    }

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
                TPosition position = getTradeDesk().getOpenPosition(getTicketID());
                TOrder order = getTradeDesk().getOrder(getTicketID());
                FxcmServerSession ts = (FxcmServerSession)getTradeDesk().getTradingServerSession();
                BnS side = null;
                String limitOrderID = null;
                String stopOrderID = null;
                if (position != null) {
                    side = position.getBS();
                    stopOrderID = position.getStopOrderID();
                    limitOrderID = position.getLimitOrderID();
                } else if (order != null) {
                    side = order.getBS();
//                    stopOrderID = order.getStopOrderID();
//                    limitOrderID = order.getLimitOrderID();
                }
                String orderID = null;
                if (getResetLimit() && side == BnS.BUY) {
                    orderID = limitOrderID;
                } else if (getResetStop() && side == BnS.BUY) {
                    orderID = stopOrderID;
                } else if (!getResetLimit() && side == BnS.SELL) {
                    orderID = stopOrderID;
                } else if (!getResetStop() && side == BnS.SELL) {
                    orderID = limitOrderID;
                }
                if (orderID != null) {
                    order = getTradeDesk().getTradingServerSession().getStopLimitOrderMap().get(orderID);
                }
                ISide orderSide;
                if (side == BnS.BUY) {
                    orderSide = SideFactory.BUY;
                } else {
                    orderSide = SideFactory.SELL;
                }
                OrderCancelRequest ocr = MessageGenerator.generateOrderCancelRequest(null,
                                                                                     orderID,
                                                                                     orderSide,
                                                                                     order.getAccountId());
                ocr.setOrderQty(order.getAmount());
                ocr.setInstrument(new Instrument(order.getSymbol()));
                ts.send(ocr);
                return LiaisonStatus.READY;
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
            return ResetStopLimitRequest.this;
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
}
