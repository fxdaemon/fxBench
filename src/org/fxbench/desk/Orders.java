/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/core/Orders.java#3 $
 *
 * Copyright (c) 2010 FXCM, LLC.
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
 * 09/10/2003   ID   Created
 * 12/8/2004    Andre Mermegas  added method to get order by TradeID
 */
package org.fxbench.desk;

import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TOrder;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of Order objects.
 */
public class Orders extends SignalVector implements ISignalListener
{
	private TradeDesk tradeDesk;
    private Map<String, TOrder> mTradeIdMap;

    public Orders(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    	mTradeIdMap = new HashMap<String, TOrder>();
    }
    
    @Override
    public void add(BaseEntity entity) {
    	TOrder order = (TOrder)entity;
        if (order.getRate() == 0.0) {
            String currency = order.getSymbol();
            TOffer rate = tradeDesk.getOffers().getOffer(currency);
            double offerRate = order.getBS() == BnS.BUY ? rate.getAsk() : rate.getBid();
            order.setRate(offerRate);
        }
        mTradeIdMap.put(order.getTradeId(), order);
        super.add(order);
    }

    /**
     * Finds the order by order id, returns null if not found.
     *
     * @param aOrderID id of order
     */
    public TOrder getOrder(String aOrderID) {
        return (TOrder) get(aOrderID);
    }

    public TOrder getOrderByTradeId(String aTradeId) {
        return (TOrder) mTradeIdMap.get(aTradeId);
    }

    @Override
    public BaseEntity remove(BaseEntity entity) {
    	TOrder order = (TOrder)entity;
        mTradeIdMap.remove(order.getTradeId());
        return super.remove(entity);
    }

    @Override
    public BaseEntity remove(int index) {
    	BaseEntity entity = super.remove(index);
        if (entity instanceof TOrder) {
            TOrder order = (TOrder) entity;
            mTradeIdMap.remove(order.getTradeId());
        }
        return entity;
    }

    @Override
    public BaseEntity set(int index, BaseEntity entity) {
        BaseEntity oldEntity = super.set(index, entity);
        TOrder incoming = (TOrder) entity;
        TOrder outgoing = (TOrder) oldEntity;
        if (!incoming.getTradeId().equals(outgoing.getTradeId())) {
            mTradeIdMap.remove(outgoing.getTradeId());
            mTradeIdMap.put(incoming.getTradeId(), incoming);
        }
        return oldEntity;
    }
    
    public int getEntryOrderSize() {
    	int entryOrdersCount = 0;
    	synchronized (listEntity) {
    		for (BaseEntity entity : listEntity) {
    			TOrder order = (TOrder)entity;
    			if (order.isEntryOrder()) {
    				entryOrdersCount++;
    			}
    		}
    	}
    	return entryOrdersCount;
    }
    
    public void enableRecalc(boolean aEnable) {
        if (aEnable) {
            tradeDesk.getOffers().subscribe(this, SignalType.CHANGE);
        } else {
            tradeDesk.getOffers().unsubscribe(this, SignalType.CHANGE);
        }
    }
    
    @Override
    public boolean isTotal() {
    	return false;
    }
    
    @Override
    public Field getTotal(int fieldNo) {
		return null;
    }
    
    @Override
    public void onSignal(Signaler src, Signal signal) {
        if (signal != null && signal.getType() == SignalType.CHANGE) {
            TOffer rate = (TOffer)signal.getNewElement();
            List<Integer> indexList = new ArrayList<Integer>();
            for (int i = 0; i < size(); i++) {
                TOrder order = (TOrder)get(i);
                if (rate.getSymbol().equals(order.getSymbol())) {
                    if (order.getBS() == BnS.BUY) {
                        if (order.getRate() != rate.getAsk()) {
                            order.setRate(rate.getAsk());
                            indexList.add(i);
                        }
                    } else {
                        if (order.getRate() != rate.getBid()) {
                            order.setRate(rate.getBid());
                            indexList.add(i);
                        }
                    }
                }
            }
            elementChanged(indexList);
        }
    }
}
