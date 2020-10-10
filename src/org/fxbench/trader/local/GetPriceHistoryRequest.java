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
package org.fxbench.trader.local;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.fxbench.BenchApp;
import org.fxbench.entity.TPriceBar;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;

/**
 * Class ClosePositionRequest.<br>
 * <br>
 * It is responsible for creating and sending to server object of class com.fxcm.fxtrade.common.datatypes.GSOrder
 * with the type of ClosePosition.<br>
 * <br>
 * Creation date (9/10/2003 11:44 AM)
 */
public class GetPriceHistoryRequest extends BaseRequest implements IRequester
{
    private String symbol;
    private String period;
    private Date startDate;
    private Date endDate;
    private String dataPath;

    /**
     * Executes the request
     *
     * @return Status Ready if successful null else
     */
    public LiaisonStatus doIt() throws LiaisonException {
    	try {
    		List<TPriceBar> totalPriceBarList = new ArrayList<TPriceBar>();
    		Interval interval = Interval.valueOf(period); 
    		for (Date nextStartDate = startDate; nextStartDate != null;) {
	    		List<TPriceBar> priceBarList = DataReader.read(dataPath, nextStartDate, symbol, interval);
	    		if (priceBarList.size() > 0) {
//	    			System.out.println("GetPriceHistoryRequest.srartDate: " + nextStartDate.toString());
//	    			System.out.println("GetPriceHistoryRequest.loadHead: " + priceBarList.get(0).getStartDate().toString());
//	    			System.out.println("GetPriceHistoryRequest.loadTail: " + priceBarList.get(priceBarList.size() - 1).getStartDate().toString());
	    			totalPriceBarList.addAll(priceBarList);
	    			TPriceBar tail = priceBarList.get(priceBarList.size() - 1);
	    			Date loadToDate = new Date(tail.getStartDate().getTime() - Calendar.getInstance().get(Calendar.ZONE_OFFSET));
	    			if (loadToDate.compareTo(endDate) < 0) {
	    				nextStartDate = DataReader.addStartDate(loadToDate, period, 1);
	    			} else {
	    				nextStartDate = null;
	    			}
	    		} else {
	    			nextStartDate = null;
	    		}
    		}
    		if (totalPriceBarList.size() > 0) {
    			BenchApp.getInst().getTradeDesk().getPriceBars().add(totalPriceBarList);
    		}
        } catch (Exception e) {
            e.printStackTrace();
            // it can be in case of position being closed by server in
            // asynchronous process
        }
        return LiaisonStatus.READY;
    }

    public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getPreiod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
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
