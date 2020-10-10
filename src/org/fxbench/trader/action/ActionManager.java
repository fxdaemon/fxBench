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

//import com.fxcm.fix.IFixDefs;
import java.util.Enumeration;
import java.util.Hashtable;

import org.fxbench.BenchApp;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonListener;
import org.fxbench.trader.LiaisonListenerStub;

/**
 * A class, which singleton instance is used to manage all trade actions.
 */
public class ActionManager extends LiaisonListener
{
    /**
     * The one and only instance of the trade action manager.
     */
    private static ActionManager cInst;
    /**
     * Actions hashtable. Key is action name.
     */
    private Hashtable<String, TradeAction> mActions = new Hashtable<String, TradeAction>();
    /**
     * This variable dependents on the current state of the liaison and determines if a trade action can be performed in this state.
     */
    private boolean mbCanAct;

    /**
     * Private constructor
     */
    private ActionManager() {
        BenchApp.getInst().getTradeDesk().getLiaison().addLiaisonListener(new LiaisonListenerStub(this));
        cInst = this;
    }

    /**
     * Returns enumeration of all actions in the manager.
     */
    public Enumeration<TradeAction> actions() {
        return mActions.elements();
    }

    /**
     * Adds new action to the manager.
     */
    public void add(TradeAction aAction) {
        if (aAction != null && aAction.getName() != null) {
            mActions.put(aAction.getName(), aAction);
        }
    }

    /**
     * Returns true if the application is in appropriate state to perform trade action.
     */
    public boolean canAct() {
        return mbCanAct;
    }

    /**
     * Returns the one and only instance of the trade action manager.
     */
    public static ActionManager getInst() {
        return cInst != null ? cInst : new ActionManager();
    }

    /**
     * This method is called when status of liaison has changed.
     */
    public void onLiaisonStatus(LiaisonStatus aStatus) {
        if (aStatus == LiaisonStatus.READY || aStatus == LiaisonStatus.RECEIVING) {
            if (!mbCanAct /*TODO && TradeDesk.getInst().getTradingServerSession().getUserKind() != IFixDefs.FXCM_SESSION_TYPE_CUSTOMER*/) {
                mbCanAct = true;
                synchronized (mActions) {
                    for (Enumeration<TradeAction> e = actions(); e.hasMoreElements();) {
                        (e.nextElement()).canAct(mbCanAct);
                    }
                }
            }
        } else {
            if (mbCanAct) {
                mbCanAct = false;
                synchronized (mActions) {
                    for (Enumeration<TradeAction> e = actions(); e.hasMoreElements();) {
                        (e.nextElement()).canAct(mbCanAct);
                    }
                }
            }
        }
    }

    /**
     * Removes the action from the manager.
     */
    public void remove(TradeAction aAction) {
        if (aAction != null) {
            mActions.remove(aAction.getName());
        }
    }
}
