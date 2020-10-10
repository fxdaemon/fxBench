/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/core/Summaries.java#2 $
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
 */
package org.fxbench.desk;

import org.fxbench.entity.Field;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.entity.TSummary;
import org.fxbench.ui.panel.SummaryPanel;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;

/**
 * A collection of Summary objects.
 */
public class Summaries extends SignalVector implements ISignalListener
{
	private TradeDesk tradeDesk;
	
    /**
     * Total Gross profit-loss
     */
    private double mGrossTotalPnL;
    /**
     * Total Net profit-loss
     */
    private double mNetTotalPnL;
    /**
     * Total contract size for the currency
     */
    private long mTotalAmount;
    /**
     * Total contract size for all open buy positions
     */
    private long mTotalAmountBuy;
    /**
     * Total contract size for all open sell positions
     */
    private long mTotalAmountSell;
    /**
     * Total buy profit-loss
     */
    private double mTotalBuyPnL;
    /**
     * Total open position count for the currency.
     */
    private int mTotalPositionCount;
    /**
     * Total sell profit-loss
     */
    private double mTotalSellPnL;

    public Summaries(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    }
    
    @Override
    public void clear() {
        super.clear();
        mTotalAmount = 0;
        mTotalAmountBuy = 0;
        mTotalAmountSell = 0;
        mTotalBuyPnL = 0;
        mGrossTotalPnL = 0;
        mNetTotalPnL = 0;
        mTotalPositionCount = 0;
        mTotalSellPnL = 0;
    }

    /**
     * Get total Profit-Loss for Total row.
     */
    public double getGrossTotalPnL() {
        return mGrossTotalPnL;
    }

    /**
     * Set total Profit-Loss for Total row.
     */
    public void setGrossTotalPnL(double aVal) {
        mGrossTotalPnL = aVal;
    }

    public double getNetTotalPnL() {
        return mNetTotalPnL;
    }

    public void setNetTotalPnL(double aNetTotalPnL) {
        mNetTotalPnL = aNetTotalPnL;
    }

    /**
     * Finds the summary by currency, returns null if not found.
     */
    public TSummary getSummary(String aCurrency) {
        return (TSummary) get(aCurrency);
    }

    /**
     * Get Amount value for Total row.
     */
    public long getTotalAmount() {
        return mTotalAmount;
    }

    /**
     * Set Amount value for Total row.
     */
    public void setTotalAmount(long aVal) {
        mTotalAmount = aVal;
    }

    /**
     * Get Amount Buy value for Total row.
     */
    public long getTotalAmountBuy() {
        return mTotalAmountBuy;
    }

    /**
     * Set Amount Buy value for Total row.
     */
    public void setTotalAmountBuy(long aVal) {
        mTotalAmountBuy = aVal;
    }

    /**
     * Get Amount Sell value for Total row.
     */
    public long getTotalAmountSell() {
        return mTotalAmountSell;
    }

    /**
     * Set Amount Sell value for Total row.
     */
    public void setTotalAmountSell(long aVal) {
        mTotalAmountSell = aVal;
    }

    /**
     * Get total buy Profit-Loss for Total row.
     */
    public double getTotalBuyPnL() {
        return mTotalBuyPnL;
    }

    /**
     * Set total buy Profit-Loss for Total row.
     */
    public void setTotalBuyPnL(double aVal) {
        mTotalBuyPnL = aVal;
    }

    /**
     * Get total position count for Total row.
     */
    public int getTotalPositionCount() {
        return mTotalPositionCount;
    }

    /**
     * Set total position count for Total row.
     */
    public void setTotalPositionCount(int aVal) {
        mTotalPositionCount = aVal;
    }

    /**
     * Get total sell Profit-Loss for Total row.
     */
    public double getTotalSellPnL() {
        return mTotalSellPnL;
    }

    /**
     * Set total sell Profit-Loss for Total row.
     */
    public void setTotalSellPnL(double aVal) {
        mTotalSellPnL = aVal;
    }
    
    public void enableRecalc(boolean enable) {
        if (enable) {
        	tradeDesk.getOpenPositions().subscribe(this, SignalType.ADD);
        	tradeDesk.getOpenPositions().subscribe(this, SignalType.CHANGE);
        	tradeDesk.getOpenPositions().subscribe(this, SignalType.REMOVE);
        } else {
        	tradeDesk.getOpenPositions().unsubscribe(this, SignalType.ADD);
        	tradeDesk.getOpenPositions().unsubscribe(this, SignalType.CHANGE);
        	tradeDesk.getOpenPositions().unsubscribe(this, SignalType.REMOVE);
        }
    }
    
    @Override
    public boolean isTotal() {
    	return true;
    }
    
    @Override
    public Field getTotal(int fieldNo) {
    	switch (fieldNo) {
		case 1:
		case 2:
		case 4:
		case 5:
		case 10:
		case 11:
		case 12:
			return getFieldTotal(fieldNo);
		default:
			return null;
		}

    }

    public void update(String aCurrency, SignalType aSignalType) {
        double grossPnlBuy = 0;
        double grossPnlSell = 0;
        double netPnlBuy = 0;
        double netPnlSell = 0;
        long amountBuy = 0;
        long amountSell = 0;
        double sumOfMultBuyByBuyAmount = 0;
        double sumOfMultSellBySellAmount = 0;
        int numOfPositions = 0;
        long totalBuyAmount = 0;
        long totalSellAmount = 0;
        double totalGrossBuyPnL = 0;
        double totalGrossSellPnL = 0;
        double totalNetBuyPnL = 0;
        double totalNetSellPnL = 0;
        Positions positions = tradeDesk.getOpenPositions();
        for (int i = 0; i < positions.size(); i++) {
            TPosition position = (TPosition) positions.get(i);
            if (aCurrency.equals(position.getSymbol())) {
                if (position.getBS() == BnS.BUY) {
                    amountBuy += position.getAmount();
                    grossPnlBuy += position.getGrossPL();
                    netPnlBuy += position.getNetPnL();
                    sumOfMultBuyByBuyAmount += position.getAmount() * position.getOpen();
                } else {
                    amountSell += position.getAmount();
                    grossPnlSell += position.getGrossPL();
                    netPnlSell += position.getNetPnL();
                    sumOfMultSellBySellAmount += position.getAmount() * position.getOpen();
                }
                numOfPositions++;
            } else {
                if (position.getBS() == TPosition.BnS.BUY) {
                    totalBuyAmount += position.getAmount();
                    totalGrossBuyPnL += position.getGrossPL();
                    totalNetBuyPnL += position.getNetPnL();
                } else {
                    totalSellAmount += position.getAmount();
                    totalGrossSellPnL += position.getGrossPL();
                    totalNetSellPnL += position.getNetPnL();
                }
            }
        }

        totalBuyAmount += amountBuy;
        totalGrossBuyPnL += grossPnlBuy;
        totalNetBuyPnL += netPnlBuy;
        totalSellAmount += amountSell;
        totalGrossSellPnL += grossPnlSell;
        totalNetSellPnL += netPnlSell;
        long totalAmount = totalBuyAmount + totalSellAmount;
        double totalGrossPnL = totalGrossBuyPnL + totalGrossSellPnL;
        double totalNetPnL = totalNetBuyPnL + totalNetSellPnL;

        mTotalSellPnL = totalGrossSellPnL;
        mTotalBuyPnL = totalGrossBuyPnL;
        mTotalAmountSell = totalSellAmount;
        mTotalAmountBuy = totalBuyAmount;
        mTotalAmount = totalAmount;
        mGrossTotalPnL = totalGrossPnL;
        mNetTotalPnL = totalNetPnL;
        mTotalPositionCount = positions.size();
        // looking for summary with specified currency
        TSummary summary = (TSummary) get(aCurrency);
        int index = -1;
        if (summary == null) {
            summary = new TSummary(SummaryPanel.getFieldDefStub());
            summary.setSymbol(aCurrency);
        } else {
        	index = indexOf(summary);
        }

        // filling summary
        summary.setSellPnL(grossPnlSell);
        if (amountSell > 0) {
            summary.setAvgSellRate(sumOfMultSellBySellAmount / amountSell);
        } else {
            summary.setAvgSellRate(0);
        }
        summary.setAmountSell(amountSell);
        summary.setBuyPnL(grossPnlBuy);

        if (amountBuy > 0) {
            summary.setAvgBuyRate(sumOfMultBuyByBuyAmount / amountBuy);
        } else {
            summary.setAvgBuyRate(0);
        }
        summary.setAmountBuy(amountBuy);

        summary.setPositionsCount(numOfPositions);
        summary.setTotalAmount(amountSell + amountBuy);
        summary.setGrossTotalPnL(grossPnlSell + grossPnlBuy);
        summary.setNetTotalPnL(netPnlBuy + netPnlSell);
        // if new summary was created, add it to Summaries
        if (index == -1) {
            add(summary);
        } else {
            if (aSignalType == SignalType.REMOVE && numOfPositions == 0) {
                remove(summary);
            } else {
                set(index, summary);
            }
        }
    }
    
    @Override
    public void onSignal(Signaler src, Signal signal) {
//        if (signal.getType() == SignalType.ADD || signal.getType() == SignalType.REMOVE) {
        	TPosition position = (TPosition)signal.getElement();
        	if (position != null) {
        		update(position.getSymbol(), signal.getType());
        	}
//        }
    }
}
