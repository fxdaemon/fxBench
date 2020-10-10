/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/core/OpenPositions.java#2 $
 *
 * Copyright 2006 FXCM LLC
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
 * 05/05/2006   Andre Mermegas: fix for stopmove,pippl
 * 07/18/2006   Andre Mermegas: performance update
 * 01/16/2006   Andre Mermegas: bugfix in pnl
 */
package org.fxbench.desk;

import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.util.Utils;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of Position objects for all open positions.
 */
public class Positions extends SignalVector implements ISignalListener
{
	private TradeDesk tradeDesk;
    private double mTotalPipPL;
    private long mTotalAmount;
    private double mTotalCommision;
    private double mTotalGrossPnL;
    private double mTotalInterest;
    private double mTotalNetPnL;
    private double mTotalUsedMargin;
    
    public Positions(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    }
    
    @Override
    public void clear() {
    	super.clear();
    	updateTotals();
    }
    
    /**
     * Finds the open position by ticket id, returns null if not found.
     * asTicketID   id of position
     * @param aTicketID ticket
     * @return position
     */
    public TPosition getPosition(String aTicketID) {
    	return (TPosition)get(aTicketID);
    }
    
    @Override
    public void add(BaseEntity entity) {
   		TPosition position = (TPosition)entity;
		if (position.getStage() == TPosition.Stage.Open && position.getClose() == 0) {
            TOffer rate = tradeDesk.getOffers().getOffer(position.getSymbol());
            position.setClose(rate.getClosePrice(position.getBS()));
            fillPosition(position, rate);
        }
		super.add(entity);
		updateTotals();
    }
    
    @Override
    public BaseEntity remove(int aIndex) {
    	BaseEntity removedEntity = super.remove(aIndex);
    	updateTotals();
    	return removedEntity;
    }

    @Override
    public BaseEntity set(int index, BaseEntity entity) {
    	BaseEntity oldEntity = super.set(index, entity);
    	TPosition position = (TPosition) entity;
        fillPosition(position, tradeDesk.getOffers().getOffer(position.getSymbol()));
    	updateTotals();
        return oldEntity;
    }
    
	public double getTotalPipPL() {
        return mTotalPipPL;
    }

    public void setTotalPipPL(double aPipPL) {
        mTotalPipPL = aPipPL;
    }

    public long getTotalAmount() {
        return mTotalAmount;
    }

    public void setTotalAmount(long aTotalAmount) {
        mTotalAmount = aTotalAmount;
    }

    public double getTotalCommision() {
        return mTotalCommision;
    }

    public void setTotalCommision(double aTotalCommision) {
        mTotalCommision = aTotalCommision;
    }

    public double getTotalGrossPnL() {
        return mTotalGrossPnL;
    }

    public void setTotalGrossPnL(double aTotalGrossPnL) {
        mTotalGrossPnL = aTotalGrossPnL;
    }

    public double getTotalInterest() {
        return mTotalInterest;
    }

    public void setTotalInterest(double aTotalInterest) {
        mTotalInterest = aTotalInterest;
    }

    public double getTotalNetPnL() {
        return mTotalNetPnL;
    }

    public void setTotalNetPnL(double aTotalNetPnL) {
        mTotalNetPnL = aTotalNetPnL;
    }

    public double getTotalUsedMargin() {
        return mTotalUsedMargin;
    }

    public void setTotalUsedMargin(double aTotalUsedMargin) {
        mTotalUsedMargin = aTotalUsedMargin;
    }

    public double getGrossPLSum(String accountId) {
    	double dblGrossPLSum = 0.0;
        // calculating sum of all positions for the account
    	synchronized (listEntity) {
	    	for (BaseEntity entity : listEntity) {
	    		TPosition position = (TPosition)entity;
	    		if (accountId.equals(position.getAccountId())) {
	                dblGrossPLSum += position.getGrossPL();
	            }
	    	}
    	}
        return dblGrossPLSum;
    }
    
    private void fillPosition(TPosition aOpenPos, TOffer aRate) {
        double pipsPrice = tradeDesk.getTradingServerSession().getPointSize(aOpenPos.getSymbol());
        if (aOpenPos.getTrailStop() > 0 && aOpenPos.getClose() != 0) {
            if (BnS.BUY == aOpenPos.getBS()) {
                double diff = (aOpenPos.getTrailingRate() + pipsPrice * aOpenPos.getTrailStop()) - aRate.getBid();
                double stopmove = Utils.round(diff, pipsPrice) / pipsPrice;                
                aOpenPos.setUntTrlMove(stopmove);
            } else {
            	double diff = aRate.getAsk() - (aOpenPos.getTrailingRate() - pipsPrice * aOpenPos.getTrailStop());
                double stopmove = Utils.round(diff, pipsPrice) / pipsPrice;                
                aOpenPos.setUntTrlMove(stopmove);
            }
        }
        if (aOpenPos.getOpen() != 0 && aOpenPos.getClose() != 0) {
            if (BnS.BUY == aOpenPos.getBS()) {
            	double diff = aOpenPos.getClose() - aOpenPos.getOpen();
            	double pipPl = Utils.round(diff, pipsPrice) / pipsPrice;
                aOpenPos.setPl(pipPl);
            } else {
            	double diff = aOpenPos.getOpen() - aOpenPos.getClose();
            	double pipPl = Utils.round(diff, pipsPrice) / pipsPrice;
                aOpenPos.setPl(pipPl);
            }
        }
        if (aOpenPos.getClose() != 0) {
            aOpenPos.setGrossPL(aOpenPos.getAmount() * aOpenPos.getPl() * aRate.getPipCost());
            aOpenPos.setNetPL(aOpenPos.getGrossPL() - aOpenPos.getCom() + aOpenPos.getInterest());
        }
    }
    
    public void enableRecalc(boolean aEnable) {
        if (aEnable) {
            tradeDesk.getOffers().subscribe(this, SignalType.CHANGE);
        } else {
            tradeDesk.getOffers().unsubscribe(this, SignalType.CHANGE);
        }
    }
    
    public List<TPosition> getVisiblePositions(Date fromDate, Date toDate) {
    	List<TPosition> positionList = new ArrayList<TPosition>();
    	synchronized (listEntity) {
	        for (int i = 0; i < listEntity.size(); i++) {
	        	boolean isAdd = false;
	        	TPosition position = (TPosition)listEntity.get(i);
	        	if (position.getOpenTime().compareTo(fromDate) >= 0 &&
	        		position.getOpenTime().compareTo(toDate) <= 0) {
	        		positionList.add(position);
	        		isAdd = true;
	        	}
        		if (!isAdd && position.getCloseTime() != null &&
        			position.getCloseTime().compareTo(fromDate) >= 0 &&
	        		position.getCloseTime().compareTo(toDate) <= 0) {
	        		positionList.add(position);
	        	}
	        }
    	}
    	return positionList;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("OpenPositions");
        sb.append(" --- begin superclass toString ");
        sb.append(super.toString());
        sb.append(" --- end superclass toString ");
        sb.append("{mTotalPipPL=").append(mTotalPipPL);
        sb.append(", mTotalAmount=").append(mTotalAmount);
        sb.append(", mTotalCommision=").append(mTotalCommision);
        sb.append(", mTotalGrossPnL=").append(mTotalGrossPnL);
        sb.append(", mTotalInterest=").append(mTotalInterest);
        sb.append(", mTotalNetPnL=").append(mTotalNetPnL);
        sb.append(", mTotalUsedMargin=").append(mTotalUsedMargin);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public boolean isTotal() {
    	return true;
    }
    
    @Override
    public Field getTotal(int fieldNo) {
    	switch (fieldNo) {
		case 3:
		case 10:
			return getFieldTotal(fieldNo);
		default:
			return null;
		}
    }

    private void updateTotals() {
        mTotalAmount = 0;
        mTotalPipPL = 0;
        mTotalGrossPnL = 0;
        mTotalNetPnL = 0;
        mTotalCommision = 0;
        mTotalInterest = 0;
        mTotalUsedMargin = 0;
        synchronized (listEntity) {
	        for (int i = 0; i < listEntity.size(); i++) {
	            TPosition openPos = (TPosition) listEntity.get(i);
	            mTotalGrossPnL += openPos.getGrossPL();
	            mTotalNetPnL += openPos.getNetPL();
	            mTotalCommision += openPos.getCom();
	            mTotalInterest += openPos.getInterest();
	            mTotalUsedMargin += openPos.getUsedMargin();
	            mTotalAmount += openPos.getAmount();
	            mTotalPipPL += openPos.getPl();
	        }
        }
    }
    
    @Override
    public void onSignal(Signaler src, Signal signal) {
    	if (signal == null) {
    		return;
    	}
    	if (signal.getType() == SignalType.CHANGE) {
            TOffer rate = (TOffer)signal.getNewElement();
            // For all open positions for this currency
            mTotalAmount = 0;
            mTotalPipPL = 0;
            mTotalGrossPnL = 0;
            mTotalNetPnL = 0;
            mTotalCommision = 0;
            mTotalInterest = 0;
            mTotalUsedMargin = 0;
            Set<String> ccySet = new HashSet<String>();
//            Set<String> accountSet = new HashSet<String>();
            List<Integer> indexList = new ArrayList<Integer>();
            synchronized (listEntity) {
	            for (int i = 0; i < listEntity.size(); i++) {
	                TPosition position = (TPosition)listEntity.get(i);
	                if (rate.getSymbol().equals(position.getSymbol())) {
	                    double closePrice = rate.getClosePrice(position.getBS());
	                    if (position.getClose() != closePrice) {
	                    	position.setClose(closePrice);
	                        fillPosition(position, rate);
	                        ////xxx change all values before calling elementchanged later
	                        if (ccySet.add(position.getSymbol())) {
	                        	indexList.add(i);
	                        }
	//                        accountSet.add(position.getAccountName());
	                    }                    	
	                }
	                mTotalGrossPnL += position.getGrossPL();
	                mTotalNetPnL += position.getNetPL();
	                mTotalCommision += position.getCom();
	                mTotalInterest += position.getInterest();
	                mTotalUsedMargin += position.getUsedMargin();
	                mTotalAmount += position.getAmount();
	                mTotalPipPL += position.getPl();
	            }
	            elementChanged(indexList);
            }
            //this is done here for performance reasons. only update these once per cycle
//            for (String ccy : ccySet) {
//                Summaries summaries = tradeDesk.getSummaries();
//                summaries.update(ccy, signal.getType());
//            }
//            for (String acc : accountSet) {
//                Accounts accounts = tradeDesk.getAccounts();
//                accounts.update(acc);
//            }
    	} else {
    		updateTotals();
    	}
    }
}
