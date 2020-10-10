/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/core/Accounts.java#1 $
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

import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TPosition;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;

/**
 * A collection of Account objects.
 */
public class Accounts extends SignalVector implements ISignalListener
{
	private TradeDesk tradeDesk;
    private double mTotalBalance;
    private double mTotalEquity;
    private double mTotalGrossPnL;
    private double mTotalUsableMargin;
    private double mTotalUsedMargin;

    public Accounts(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    }
    
    public double getTotalBalance() {
        return mTotalBalance;
    }

    public void setTotalBalance(double aTotalBalance) {
        mTotalBalance = aTotalBalance;
    }

    public double getTotalEquity() {
        return mTotalEquity;
    }

    public void setTotalEquity(double aTotalEquity) {
        mTotalEquity = aTotalEquity;
    }

    public double getTotalGrossPnL() {
        return mTotalGrossPnL;
    }

    public void setTotalGrossPnL(double aTotalGrossPnL) {
        mTotalGrossPnL = aTotalGrossPnL;
    }

    public double getTotalUsableMargin() {
        return mTotalUsableMargin;
    }

    public void setTotalUsableMargin(double aTotalUsableMargin) {
        mTotalUsableMargin = aTotalUsableMargin;
    }

    public double getTotalUsedMargin() {
        return mTotalUsedMargin;
    }

    public void setTotalUsedMargin(double aTotalUsedMargin) {
        mTotalUsedMargin = aTotalUsedMargin;
    }
    
    public TAccount getAccount(String accountId) {
        return (TAccount)get(accountId);
    }
    
    @Override
    public void add(BaseEntity entity) {
        TAccount account = (TAccount)entity;
//		if (account.isInvisible()) {
			super.add(entity);
			update(account);
//		}
    }
    
    @Override
    public BaseEntity set(int index, BaseEntity entity) {
        BaseEntity oldEntity = super.set(index, entity);
        if (oldEntity instanceof TAccount) {
            update((TAccount)oldEntity);
        }
        return oldEntity;
    }

    @Override
    public void clear() {
        super.clear();
        updateTotals();
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
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Accounts");
        sb.append(" --- begin superclass toString ");
        sb.append(super.toString());
        sb.append(" --- end superclass toString ");
        sb.append("{mTotalBalance=").append(mTotalBalance);
        sb.append(", mTotalEquity=").append(mTotalEquity);
        sb.append(", mTotalGrossPnL=").append(mTotalGrossPnL);
        sb.append(", mTotalUsableMargin=").append(mTotalUsableMargin);
        sb.append(", mTotalUsedMargin=").append(mTotalUsedMargin);
        sb.append('}');
        return sb.toString();
    }

    public void update(String aAccount) {
        update((TAccount)get(aAccount));
    }

    private TAccount update(TAccount aAccount) {
        double dblGrossPnLSum = tradeDesk.getOpenPositions().getGrossPLSum(aAccount.getAccountId());
        aAccount.setGrossPL(dblGrossPnLSum);
        aAccount.setEquity(aAccount.getBalance() + dblGrossPnLSum);
        aAccount.setUsableMargin(aAccount.getEquity() - aAccount.getUsedMargin());
        updateTotals();
        return aAccount;
    }

    private void updateTotals() {
        double totalBalance = 0;
        double totalEquity = 0;
        double totalUsedMargin = 0;
        double totalGrossPnL = 0;
        double totalUsableMargin = 0;
        synchronized (listEntity) {
	        for (int i = 0; i < listEntity.size(); i++) {
	            TAccount account = (TAccount) listEntity.get(i);
	            totalBalance += account.getBalance();
	            totalEquity += account.getEquity();
	            totalUsedMargin += account.getUsedMargin();
	            totalGrossPnL += account.getGrossPL();
	            totalUsableMargin += account.getUsableMargin();
	        }
        }
        mTotalBalance = totalBalance;
        mTotalEquity = totalEquity;
        mTotalUsedMargin = totalUsedMargin;
        mTotalGrossPnL = totalGrossPnL;
        mTotalUsableMargin = totalUsableMargin;
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
		case 3:
		case 4:
		case 5:
			return getFieldTotal(fieldNo);
		default:
			return null;
		}
    }
    
    @Override
    public void onSignal(Signaler aSrc, Signal signal) {
    	if (signal != null) {
	        Object obj = signal.getElement();
	        if (obj instanceof TPosition) {
	        	update(getAccount(((TPosition)obj).getAccountId()));
	        }
    	}
    }
}
