/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/actions/RequestForQuoteAction.java#1 $
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
package org.fxbench.trader.action;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.fxbench.BenchApp;
import org.fxbench.desk.Accounts;
import org.fxbench.desk.Offers;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TOffer;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequestFactory;
import org.fxbench.trader.Liaison;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.dialog.RequestForQuoteDialog;
import org.fxbench.ui.auxi.MessageBoxRunnable;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.Signaler;
import org.fxbench.util.signal.WeakListener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

/**
 * An action of creating market order.
 * The action is enabled when:<br>
 * <ol>
 * <li> there is account not under margin call;</li>
 * <li> if Rates table is registered:
 * <ul>
 * <li>there is selected rate;</li>
 * <li>it's tradable;</li></ul></li>
 * <li>OR if rates table is not registered and
 * <ul>
 * <li>there is at least one rate with trade available;</li></li></ul>
 * </ol>
 * <br>
 * Note. The instance of that class should be created after initialization
 * of core component, TradeDesk especially, but before creating of ITable instances
 * Creation date (9/29/2003 9:16 AM)
 */
public class RequestForQuoteAction extends TradeAction implements ISignalListener
{
    private static final String ACTION_NAME = "RequestForQuoteAction";
    /**
     * Singeleton of this class
     */
    private static final RequestForQuoteAction REQUEST_FOR_QUOTE_ACTION = new RequestForQuoteAction();
    /**
     * Account. Is determined by the current account in Accounts table (if registered).
     * If table is not registered (or no such account) then it's first account
     * (may be under margin call!);
     */
    private String mAccountID;
    /**
     * Stores the index of the current row in the Accounts table. It is changed
     * in AccountSelectionListener.onTableChangeSelection method
     */
    private int mAccountsCurRow;
    /**
     * Flag of enabling action that is set by Action manager.
     */
    private boolean mCanAct;

    /**
     * Dialog
     */
    private RequestForQuoteDialog mDlg;
    /**
     * Flag of enabling action.
     */
    private boolean mEnabled;
    /**
     * Inner Actions
     */
    private final WeakListener<Action> mInnerActions = new WeakListener<Action>();

    /**
     * Constructor of Create Market Order action.
     * Note: It adds action to ActionManager, a creator shouldn't take care of adding and removing it.
     */
    private RequestForQuoteAction() {
        super(ACTION_NAME);
        try {
            ActionManager.getInst().add(this);
//            TableManager.getInst().addListener(new TableListener());
            //getTradeDesk().getAccounts().subscribe(accountsSignalListener, SignalType.ADD);
            getTradeDesk().getAccounts().subscribe(this, SignalType.CHANGE);
            //getTradeDesk().getAccounts().subscribe(accountsSignalListener, SignalType.REMOVE);

            //getTradeDesk().getRates().subscribe(ratesSignalListener, SignalType.ADD);
            getTradeDesk().getOffers().subscribe(this, SignalType.CHANGE);
            //getTradeDesk().getRates().subscribe(ratesSignalListener, SignalType.REMOVE);
            setInitialEnable();
            setAccountByIndex(0);
            checkActionEnabled();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Invoked when an action occurs
     */
    public void actionPerformed(ActionEvent aEvent) {
        TOffer rate = getTradeDesk().getOffers().getOffer(BenchApp.getInst().getMainFrame().getSymbolPanel().getSelectedCurrency());
        if (rate != null && rate.isTradable()) {
            mEnabled = rate.isTradable();
            checkActionEnabled();
            if (mEnabled && mCanAct) {
                mDlg = new RequestForQuoteDialog(BenchApp.getInst().getMainFrame());
                if (mAccountID == null) {
                    try {
                        Accounts accounts = getTradeDesk().getAccounts();
                        if (!accounts.isEmpty()) {
                            mAccountID = ((TAccount) accounts.get(0)).getAccount();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                mDlg.setAccount(mAccountID);
                mDlg.setCurrency(BenchApp.getInst().getMainFrame().getSymbolPanel().getSelectedCurrency());
                int res = BenchApp.getInst().getMainFrame().showDialog(mDlg);
                if (res == JOptionPane.OK_OPTION) {
                    Liaison liaison = getTradeDesk().getLiaison();
                    IRequestFactory requestFactory = liaison.getRequestFactory();
                    IRequest request = requestFactory.createRequestForQuote(mDlg.getAccount(),
                                                                            mDlg.getCurrency(),
                                                                            mDlg.getAmount());
                    try {
                        liaison.sendRequest(request);
                    } catch (LiaisonException aEx) {
                        EventQueue.invokeLater(new MessageBoxRunnable(aEx));
                    }
                }
                mDlg = null;
            }
        }
    }

    /**
     * Sets Flag of enabling action.Called by Action manager:
     */
    @Override
    public void canAct(boolean aCanAct) {
        //mCanAct = aCanAct;
        mCanAct = false;
        checkActionEnabled();
        if (mDlg != null) {
            mDlg.enableDialog(aCanAct);
        }
    }

    /**
     * Dispatches enabling flag to inner action objects
     */
    public void checkActionEnabled() {
        for (Action action : mInnerActions) {
            action.setEnabled(false);
        }
    }

    /**
     * Returns Inner class instances that implements Actions, which are added to user interface controls.
     *
     * @param aCommandString Action kommand key: "SELL" or "BUY" or null
     *
     * @return Inner class instance that implements Action.
     */
    public static Action newAction(String aCommandString) {
        Action action = REQUEST_FOR_QUOTE_ACTION.new RFQAction();
        if (aCommandString != null) {
            action.putValue(ACTION_COMMAND_KEY, aCommandString);
        }
        REQUEST_FOR_QUOTE_ACTION.mInnerActions.add(action);
        REQUEST_FOR_QUOTE_ACTION.checkActionEnabled();
        return action;
    }

    /**
     * This method is called when signal is fired.
     *
     * @param aSrc source of the signal
     * @param aSignal signal
     */
    public void onSignal(Signaler aSrc, Signal aSignal) {
        if (aSrc instanceof Accounts) {
            try {
                setAccountByIndex(mAccountsCurRow);
                checkActionEnabled();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (aSrc instanceof Offers) {
            if (aSignal.getType() == SignalType.CHANGE) {
                checkActionEnabled();
            }
        }
    }

    /**
     * Sets account by the index in Account table.
     *
     * @param aIndex index of the account
     */
    private void setAccountByIndex(int aIndex) {
        try {
            Accounts accounts = getTradeDesk().getAccounts();
            if (aIndex < 0) {
                mAccountID = null;
            } else {
                if (accounts != null
                    && accounts.size() > aIndex) {
                    TAccount account = (TAccount) accounts.get(aIndex);
                    if (account != null) {
                        mAccountID = account.getAccount();
                    }
                }
            }
            if (mEnabled) {
                mEnabled = false;
                for (int i = 0; i < accounts.size(); i++) {
                    if (!((TAccount) accounts.get(i)).isUnderMarginCall()) {
                        mEnabled = true;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets initional state of enable flag. Is called when the currency table hasn't been added
     */
    private void setInitialEnable() {
        try {
            Offers rates = getTradeDesk().getOffers();
            if (rates == null) {
                mEnabled = false;
                return;
            }
            if (mEnabled) {
                mEnabled = false;
                for (int i = 0; i < rates.size(); i++) {
                    if (((TOffer) rates.get(i)).isTradable()) {
                        mEnabled = true;
                        break;
                    }
                }
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }

    /**
     * An instance of this class listens changing selected position on Accounts table
     */
//    private class AccountSelectionListener implements ITableSelectionListener {
//        /**
//         * This method is called when selection is changed.
//         *
//         * @param aTable table which row was changed
//         * @param aRow changed row
//         */
//        public void onTableChangeSelection(ITable aTable, int[] aRow) {
//            if (aRow.length > 0) {
//                mAccountsCurRow = aRow[0];
//                setAccountByIndex(aRow[0]);
//            } else {
//                mAccountsCurRow = -1;
//                setAccountByIndex(-1);
//            }
//            checkActionEnabled();
//        }
//    }

    /**
     * Inner class instances implements Actions which are added to user
     * interface controls
     */
    private class RFQAction extends AbstractAction {
        /**
         * Invoked when an action occurs
         */
        public void actionPerformed(ActionEvent aEvent) {
            RequestForQuoteAction.this.actionPerformed(aEvent);
        }
    }

    /**
     * An instance of this class listens adding and removing tables to add and
     * remove its selection listeners
     */
//    private class TableListener implements ITableListener {
//        /**
//         * Instance implementing the interface ITableSelectionListener. It watches changing value of
//         * current Account
//         */
//        private ITableSelectionListener mAccountsTableListener;
//
//        /**
//         * Is called when a table has been added.
//         *
//         * @param aTable table that was added
//         */
//        public void onAddTable(ITable aTable) {
//            try {
//                if (AccountsFrame.NAME.equals(aTable.getName()) && mAccountsTableListener == null) {
//                    mAccountsCurRow = 0;
//                    mAccountsTableListener = new RequestForQuoteAction.AccountSelectionListener();
//                    aTable.addSelectionListener(mAccountsTableListener);
//                    setAccountByIndex(0);
//                    checkActionEnabled();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * Is called when a table has been removed
//         *
//         * @param aTable table that was removed
//         */
//        public void onRemoveTable(ITable aTable) {
//            try {
//                if (AccountsFrame.NAME.equals(aTable.getName())) {
//                    mAccountsCurRow = -1;
//                    ITableSelectionListener tmp = mAccountsTableListener;
//                    mAccountsTableListener = null;
//                    if (tmp != null) {
//                        aTable.removeSelectionListener(tmp);
//                    }
//                    setAccountByIndex(0);
//                    checkActionEnabled();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
}
