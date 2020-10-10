/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/core/Rates.java#1 $
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
 * 04/19/2007   Andre Mermegas: sort rates by preferred order, id.
 */
package org.fxbench.desk;

import java.util.Comparator;

import org.fxbench.entity.Field;
import org.fxbench.entity.TOffer;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;


/**
 * A collection of Rate objects.
 */
public class Offers extends SignalVector implements ISignalListener
{
	private TradeDesk tradeDesk;
	
    public Offers(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    	subscribe(this, SignalType.CHANGE);
    	setComparator(new Comparator<TOffer>()  {
        	public int compare(TOffer aObj1, TOffer aObj2) {
                Long rate1 = Long.valueOf(aObj1.getOfferId());
                Long rate2 = Long.valueOf(aObj2.getOfferId());
                return rate1.compareTo(rate2);
            }
        });
    }
    public TOffer getOffer(String symbol) {
        return (TOffer) get(symbol);
    }
    
    public String[] getSymbolList() {
    	String[] s = new String[size()];
    	synchronized (listEntity) {
	    	for (int i = 0; i < s.length; i++) {
	    		s[i] = ((TOffer)listEntity.get(i)).getSymbol();
	    	}
    	}
    	return s;
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
        	Object obj = signal.getNewElement();
            if (obj instanceof TOffer) {
            	TOffer offer = (TOffer)obj;
            	offer.setPipCost(tradeDesk.calcPipCost(offer));
            }
        }
    }
}
