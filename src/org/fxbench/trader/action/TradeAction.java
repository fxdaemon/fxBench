/*
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
 */
package org.fxbench.trader.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.ui.auxi.WeakPropertyChangeListener;

import javax.swing.AbstractAction;
import java.beans.PropertyChangeListener;

/**
 * Base abstract class for trade actions.
 */
public abstract class TradeAction extends AbstractAction
{
    protected final Log mLogger = LogFactory.getLog(TradeAction.class);

    /**
     * Name of the trade action.
     */
    private String mName;

    /**
     * Protected constructor with trade action name as argument.
     *
     * @param aName name of action
     */
    protected TradeAction(String aName) {
        mName = aName;
    }
    
    protected TradeDesk getTradeDesk() {
    	return BenchApp.getInst().getTradeDesk();
    }

    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        super.addPropertyChangeListener(new WeakPropertyChangeListener(aListener, this));
    }

    /**
     * This action is called to notify the action if the application in appropriate state to performs trade actions.
     * Note that even if abCanAct is true, action specific logic can deny action execution.
     */
    public abstract void canAct(boolean aCanAct);

    /**
     * Returns name of the trade action.
     */
    public String getName() {
        return mName;
    }
    
    public enum ActionType {
    	CLOSE_POSITION,
    	CREATE_ENTRY_ORDER,
    	CREATE_MARKET_ORDER,
        REQUEST_FOR_QUOTE,
        LOGIN,
        REMOVE_ENTRY_ORDER,
        REPORT,
        SET_STOP_LIMIT,
        SET_STOP_LIMIT_ORDER,
        UPDATE_ENTRY_ORDER,
        GET_PRICE_HISTORY;
    }
}
