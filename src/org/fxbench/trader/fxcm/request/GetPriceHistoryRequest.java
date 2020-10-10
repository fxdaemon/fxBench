/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/ClosePositionRequest.java#1 $
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
 * 4/26/2004 Ushik fixed bug of Wrong Side passing and Account Balance is not being changed
 * 4/26/2004 Elana to fully fix bug of Wrong Side, need to use the Bank's perspective on the price.
 * So if the client sends a close order on a BUY position, need to use BUY side, SELL price.
 * On a SELL position, need to use SELL side, BUY price.
 */
package org.fxbench.trader.fxcm.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fxcm.fix.FXCMTimingIntervalFactory;
import com.fxcm.fix.IFXCMTimingInterval;
import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.UTCDate;
import com.fxcm.fix.UTCTimeOnly;
import com.fxcm.fix.pretrade.MarketDataRequest;

import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;

/**
 * Class ClosePositionRequest.<br>
 * <br>
 * It is responsible for creating and sending to server object of class com.fxcm.fxtrade.common.datatypes.GSOrder
 * with the type of ClosePosition.<br>
 * <br>
 * Creation date (9/10/2003 11:44 AM)
 */
public class GetPriceHistoryRequest extends BaseRequest implements IRequester {
	public static Map<String, IFXCMTimingInterval> fxcmIntervalMap;
	static {
		fxcmIntervalMap = new HashMap<String, IFXCMTimingInterval>();
		fxcmIntervalMap.put(Interval.T.name(), FXCMTimingIntervalFactory.TICK);
		fxcmIntervalMap.put(Interval.m1.name(), FXCMTimingIntervalFactory.MIN1);
		fxcmIntervalMap.put(Interval.m5.name(), FXCMTimingIntervalFactory.MIN5);
		fxcmIntervalMap.put(Interval.m15.name(), FXCMTimingIntervalFactory.MIN15);
		fxcmIntervalMap.put(Interval.m30.name(), FXCMTimingIntervalFactory.MIN30);
		fxcmIntervalMap.put(Interval.H1.name(), FXCMTimingIntervalFactory.HOUR1);
		fxcmIntervalMap.put(Interval.H2.name(), FXCMTimingIntervalFactory.HOUR2);
		fxcmIntervalMap.put(Interval.H3.name(), FXCMTimingIntervalFactory.HOUR3);
		fxcmIntervalMap.put(Interval.H4.name(), FXCMTimingIntervalFactory.HOUR4);
		fxcmIntervalMap.put(Interval.H6.name(), FXCMTimingIntervalFactory.HOUR6);
		fxcmIntervalMap.put(Interval.H8.name(), FXCMTimingIntervalFactory.HOUR8);		
		fxcmIntervalMap.put(Interval.D1.name(), FXCMTimingIntervalFactory.DAY1);
		fxcmIntervalMap.put(Interval.W1.name(), FXCMTimingIntervalFactory.WEEK1);
		fxcmIntervalMap.put(Interval.M1.name(), FXCMTimingIntervalFactory.MONTH1);
	}
	
    private String symbol;
    private String interval;
    private Date startDate;
    private Date endDate;

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
            
            // create a new market data request
            MarketDataRequest mdr = new MarketDataRequest();
            // set the subscription type to ask for only a snapshot of the history
            mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SNAPSHOT);
            // request the response to be formated FXCM style
            mdr.setResponseFormat(IFixDefs.MSGTYPE_FXCMRESPONSE);
            // set the intervale of the data candles
            mdr.setFXCMTimingInterval(fxcmIntervalMap.get(interval));
            // set the type set for the data candles
            mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);
            // set the dates and times for the market data request
            mdr.setFXCMStartDate(new UTCDate(startDate));
            mdr.setFXCMStartTime(new UTCTimeOnly(startDate));
            mdr.setFXCMEndDate(new UTCDate(endDate));
            mdr.setFXCMEndTime(new UTCTimeOnly(endDate));
            // set the instrument on which the we want the historical data
            mdr.addRelatedSymbol(ts.getTradingSessionStatus().getSecurity(symbol));
            
            String requestId = ts.send(mdr);
            if (requestId != null) {
            	ts.addRequestId(requestId, interval);
            }
            return LiaisonStatus.READY;
            
        } catch (Exception e) {
            e.printStackTrace();
            // it can be in case of position being closed by server in
            // asynchronous process
            return LiaisonStatus.READY;
        }
    }

    public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
     * Adds itself or other objects implementing IRequester interface to
     * IReqCollection implementation passed as parameter.
     */
    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
