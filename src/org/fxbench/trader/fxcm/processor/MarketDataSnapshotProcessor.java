/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/processors/MarketDataSnapshotProcessor.java#4 $
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
 * Created: Nov 13, 2006 11:11:00 AM
 *
 * $History: $
 * 05/01/2007   Andre Mermegas: added MMR
 */
package org.fxbench.trader.fxcm.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fxcm.fix.FXCMTimingIntervalFactory;
import com.fxcm.fix.IFXCMTimingInterval;
import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.UTCTimestamp;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.messaging.ITransportable;

import org.fxbench.BenchApp;
import org.fxbench.chart.GPriceBar;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPriceBar;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.trader.TradingServerSession;
import org.fxbench.trader.fxcm.FxcmServerSession;

/**
 *
 */
public class MarketDataSnapshotProcessor implements IProcessor {
	private static Map<String, SortedSet<TPriceBar>> priceBarMap = new HashMap<String, SortedSet<TPriceBar>>();
	public static Map<IFXCMTimingInterval, Interval> fxcmIntervalMap;
	static {
		fxcmIntervalMap = new HashMap<IFXCMTimingInterval, Interval>();
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.TICK, Interval.T);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.MIN1, Interval.m1);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.MIN5, Interval.m5);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.MIN15, Interval.m15);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.MIN30, Interval.m30);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR1, Interval.H1);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR2, Interval.H2);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR3, Interval.H3);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR4, Interval.H4);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR6, Interval.H6);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.HOUR8, Interval.H8);		
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.DAY1, Interval.D1);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.WEEK1, Interval.W1);
		fxcmIntervalMap.put(FXCMTimingIntervalFactory.MONTH1, Interval.M1);
	}
	
    public void process(ITransportable aTransportable) {
    	TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
        TradingServerSession session = tradeDesk.getTradingServerSession();
        MarketDataSnapshot aMds = (MarketDataSnapshot) aTransportable;
        
        boolean rmRequestId = false;
        session.adjustStatus();
        if (aMds.getRequestID() != null && session.containRequestId(aMds.getRequestID())) {
            if (aMds.getFXCMContinuousFlag() == IFixDefs.FXCMCONTINUOUS_END) {
            	rmRequestId = true;
                session.doneProcessing();
            }
//            tradeDesk.syncServerTime(aMds.getOpenTimestamp().toDate(), false);
        } else {
    		tradeDesk.syncServerTime(aMds.getOpenTimestamp().toDate(), true);
        }
        
        String symbol = null;
        try {
            symbol = aMds.getInstrument().getSymbol();
        } catch (NotDefinedException e) {
            e.printStackTrace();
        }
        
        Interval interval = fxcmIntervalMap.get(aMds.getFXCMTimingInterval());
        if (interval == null) {
        	
        } else if (interval == Interval.T) {
            TOffer rate = tradeDesk.getOffer(symbol);
            if (rate != null && symbol != null) {
                rate.setAsk(aMds.getAskClose()); //1
                rate.setBid(aMds.getBidClose()); //0
//                if (rate.getOpenAsk() == 0) {
//                    rate.setOpenAsk(aMds.getAskClose());
//                }
//                if (rate.getOpenBid() == 0) {
//                    rate.setOpenBid(aMds.getBidClose());
//                }
                rate.setTime(aMds.getOpenTimestamp().toDate());
                rate.setTradable(aMds.isTradeable());
                rate.setHigh(aMds.getHigh());
                rate.setLow(aMds.getLow());
                rate.setAskTradable("A".equals(aMds.getAskQuoteCondition()) && aMds.getAskQuoteType() == 1);
                rate.setBidTradable("A".equals(aMds.getBidQuoteCondition()) && aMds.getBidQuoteType() == 1);
                rate.setOfferId(String.valueOf(aMds.getInstrument().getFXCMSymID()));
//                rate.setQuoteID(aMds.getQuoteID());
                TradingSecurity security = ((FxcmServerSession)session).getTradingSessionStatus().getSecurity(symbol);
                rate.setIntrB(security.getFXCMSymInterestBuy());
                rate.setIntrS(security.getFXCMSymInterestSell());
                tradeDesk.updateOffer(rate);
            }
            
        } else {
        	if (aMds.getRequestID() != null) {
	        	TPriceBar priceBar = new TPriceBar(GPriceBar.getFielddefStub());
	        	priceBar.setSymbol(symbol);

	        	Interval reqInterval = TPriceBar.IntervalValueOfName(session.getRequestValue(aMds.getRequestID()));
	        	if (reqInterval == Interval.un) {
	        		priceBar.setInterval(interval);
	        	} else {
	        		priceBar.setInterval(reqInterval);
	        	}
	        	priceBar.setStartDate(aMds.getDate().toDate());
	        	priceBar.setAskOpen(aMds.getAskOpen());
	        	priceBar.setAskHigh(aMds.getAskHigh());
	        	priceBar.setAskLow(aMds.getAskLow());
	        	priceBar.setAskClose(aMds.getAskClose());
	        	priceBar.setBidOpen(aMds.getBidOpen());
	        	priceBar.setBidHigh(aMds.getBidHigh());
	        	priceBar.setBidLow(aMds.getBidLow());
	        	priceBar.setBidClose(aMds.getBidClose());
	        	
	        	SortedSet<TPriceBar> priceBarSet = priceBarMap.get(aMds.getRequestID());
	        	if (priceBarSet == null) {
	        		priceBarSet = new TreeSet<TPriceBar>();
	        		priceBarMap.put(aMds.getRequestID(), priceBarSet);
	        	}
	        	if (priceBarSet.size() > 0 && reqInterval == Interval.un) {
	        		priceBar.setInterval(priceBarSet.first().getInterval());
	        	}
	        	priceBarSet.add(priceBar);
	        	
	        	if (aMds.getFXCMContinuousFlag() == IFixDefs.FXCMCONTINUOUS_END) {
	        		tradeDesk.getPriceBars().add(new ArrayList<TPriceBar>(priceBarSet));
	        		priceBarMap.remove(aMds.getRequestID());
	        	}
        	}
        }

        if (rmRequestId) {
        	session.removeRequestId(aMds.getRequestID());
        }
        session.adjustStatus();
    }
    
}
