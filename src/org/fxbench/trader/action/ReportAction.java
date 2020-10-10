/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/actions/ReportAction.java#1 $
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
import org.fxbench.entity.TAccount;
import org.fxbench.trader.dialog.ReportDialog;
import org.fxbench.trader.fxcm.FxcmLiaison;
import org.fxbench.util.BrowserLauncher;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.Signaler;
import org.fxbench.util.signal.WeakListener;

import java.awt.event.ActionEvent;

/**
 * This action is used to handle action for creating of the report.
 */
public class ReportAction extends TradeAction implements ISignalListener
{
    private static final String ACTION_NAME = "ReportAction";
    /**
     * Instance.
     */
    private static final ReportAction REPORT_ACTION = new ReportAction();
    /**
     * Account. Is determined by the current account in Accounts table (if registered).
     * If table is not registered (or no such account) then it's first account
     * (may be under margin call!);
     */
    private String mAccountID;
    /**
     * Flag of enabling action that is set by Action manager:
     */
    private boolean mCanAct;

    /**
     * Dialog
     */
    private ReportDialog mDlg;
    /**
     * Flag of enabling action The action is enabled when:
     * 1)   there is some account;
     */
    private boolean mEnabled;
    /**
     * Inner Actions
     */
    private final WeakListener<Action> mInnerActions = new WeakListener<Action>();

    /**
     * Constructor of Report action.
     * Note: It adds action to ActionManager, a creator shouldn't take care of adding and removing it.
     */
    private ReportAction() {
        super(ACTION_NAME);
        try {
            ActionManager.getInst().add(this);
//            TableManager.getInst().addListener(new TableListener());
            getTradeDesk().getAccounts().subscribe(this, SignalType.ADD);
            getTradeDesk().getAccounts().subscribe(this, SignalType.CHANGE);
            getTradeDesk().getAccounts().subscribe(this, SignalType.REMOVE);
            setInitialEnable();
            checkActionEnabled();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Invoked when an action occurs
     */
    public void actionPerformed(ActionEvent aEvent) {
        if (mEnabled && mCanAct) {
            mDlg = new ReportDialog(BenchApp.getInst().getMainFrame());
            if (mAccountID != null) {
                mDlg.setAccountID(mAccountID);
            }
            int res = BenchApp.getInst().getMainFrame().showDialog(mDlg);
            if (res == JOptionPane.OK_OPTION) {
                //creates url
            	FxcmLiaison liaison = (FxcmLiaison)getTradeDesk().getLiaison();
                TAccount account = getTradeDesk().getAccounts().getAccount(mDlg.getAccount());
                String sURL = liaison.getReportURL(account.getAccount(), mDlg.getDateFrom(), mDlg.getDateTo());
                //open web browser
                if (!BrowserLauncher.showDocument(sURL)) {  // * USHIK 4/27/2004
                    //System.out.println("Not launched by JNLP");
                    try {
                        BrowserLauncher.openURL(sURL);
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                        //System.out.println("Not launched by BrowserLauncher");
                    }
                }
            }
            mDlg = null;
        }
    }

    /**
     * Sets Flag of enabling action.Called by Action manager.
     */
    @Override
    public void canAct(boolean aCanAct) {
        mCanAct = aCanAct;
        checkActionEnabled();
        if (mDlg != null) {
            mDlg.enableDialog(aCanAct);
        }
    }

    /**
     * Checking action for enable.
     */
    public void checkActionEnabled() {
        for (Action action : mInnerActions) {
            action.setEnabled(mEnabled && mCanAct);
        }
    }

    /**
     * Retuns Inner class instances implements Actions which are added to user interface controls.
     *
     * @param aCommandString action command string
     */
    public static Action newAction(String aCommandString) {
        Action action = REPORT_ACTION.new SingleReportAction();
        if (aCommandString != null) {
            action.putValue(ACTION_COMMAND_KEY, aCommandString);
        }
        REPORT_ACTION.mInnerActions.add(action);
        REPORT_ACTION.checkActionEnabled();
        return action;
    }

    /**
     * This method is called when signal is fired.
     *
     * @param aSrc source of the signal
     * @param aSignal signal
     */
    public void onSignal(Signaler aSrc, Signal aSignal) {
        if (aSignal.getType() == SignalType.ADD || aSignal.getType() == SignalType.REMOVE) {
            setInitialEnable();
            checkActionEnabled();
        }
    }

    /**
     * Sets AccountId by specified index of acount.
     *
     * @param aIndex index of account
     */
    private void setAccountByIndex(int aIndex) {
        try {
            Accounts accounts = getTradeDesk().getAccounts();
            mEnabled = !accounts.isEmpty() /*|| !accounts.getInvisbleAccounts().isEmpty()*/;
            if (aIndex < 0) {
                mAccountID = null;
            } else {
                if (accounts.size() > aIndex) {
                    TAccount account = (TAccount) accounts.get(aIndex);
                    if (account != null) {
                        mAccountID = account.getAccount();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Is called when the accounts table hasn't been added
     */
    private void setInitialEnable() {
        try {
            Accounts accounts = getTradeDesk().getAccounts();
            mEnabled = accounts != null && (!accounts.isEmpty() /*|| !accounts.getInvisbleAccounts().isEmpty()*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This class implements listener from the selection of accounts table row.
     */
//    private class AccountSelectionListener implements ITableSelectionListener {
//        /**
//         * This method is called when selection is changed.
//         *
//         * @param aTable table which row was changed
//         * @param aRow changed row
//         */
//        public void onTableChangeSelection(ITable aTable, int[] aRow) {
//            if (aRow.length > 0 && aRow[0] <= getTradeDesk().getAccounts().size()) {
//                setAccountByIndex(aRow[0]);
//            } else {
//                setAccountByIndex(-1);
//            }
//            checkActionEnabled();
//        }
//    }

    /**
     * Inner class instances implements Actions which are added to
     * user interface controls.
     */
    private class SingleReportAction extends AbstractAction {
        /**
         * Invoked when an action occurs
         */
        public void actionPerformed(ActionEvent aEvent) {
            ReportAction.this.actionPerformed(aEvent);
        }
    }

    /**
     * Implementation of Table Listener interface.
     */
//    private class TableListener implements ITableListener {
//        /**
//         * Instance implementing the interface ITableSelectionListener. It watches changing value of
//         * current Account
//         */
//        private ITableSelectionListener mAccountsTableListener;
//
//        /**
//         * This method is called when new table is added.
//         *
//         * @param aTable table that was added
//         */
//        public void onAddTable(ITable aTable) {
//            try {
//                if (AccountsFrame.NAME.equals(aTable.getName()) && mAccountsTableListener == null) {
//                    mAccountsTableListener = new AccountSelectionListener();
//                    aTable.addSelectionListener(mAccountsTableListener);
//                    setAccountByIndex(-1);
//                    checkActionEnabled();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * This method is called when new table is removed.
//         *
//         * @param aTable table that was removed
//         */
//        public void onRemoveTable(ITable aTable) {
//            try {
//                if (AccountsFrame.NAME.equals(aTable.getName())) {
//                    ITableSelectionListener tmp = mAccountsTableListener;
//                    mAccountsTableListener = null;
//                    if (tmp != null) {
//                        aTable.removeSelectionListener(tmp);
//                    }
//                    setAccountByIndex(-1);
//                    checkActionEnabled();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
}
