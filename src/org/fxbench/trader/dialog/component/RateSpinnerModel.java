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
 */
package org.fxbench.trader.dialog.component;


import javax.swing.AbstractSpinnerModel;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;

/**
 * @author Andre Mermegas
 *         Date: May 23, 2006
 *         Time: 1:08:08 PM
 */
public class RateSpinnerModel extends AbstractSpinnerModel {
    private String mCurrency;
    private Number mPrice;

    public Object getNextValue() {
        double pip = BenchApp.getInst().getTradeDesk().getTradingServerSession().getPointSize(mCurrency);
        String priceUp = BenchApp.getInst().getTradeDesk().getRateFormat(mCurrency).format(mPrice.doubleValue() + pip); 
        setValue(priceUp);
        return priceUp;
    }

    public void setValue(Object aValue) {
        if (aValue != null) {
            if (aValue instanceof Number) {
                mPrice = (Number) aValue;
            } else {
                String priceDown = BenchApp.getInst().getTradeDesk().getRateFormat(mCurrency).format(
                		Double.valueOf(aValue.toString()).doubleValue());  
                mPrice = Double.valueOf(priceDown);
            }
            fireStateChanged();
        }
    }

    public Object getPreviousValue() {
        double pip = BenchApp.getInst().getTradeDesk().getTradingServerSession().getPointSize(mCurrency);
        String priceDown = BenchApp.getInst().getTradeDesk().getRateFormat(mCurrency).format(mPrice.doubleValue() - pip); 
        setValue(priceDown);
        return priceDown;
    }

    public Object getValue() {
        return mPrice;
    }

    public void setCurrency(String aCurrency) {
        mCurrency = aCurrency;
    }
}
