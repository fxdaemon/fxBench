/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/dialogs/CurrencySubscriptionDialog.java#1 $
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
 * Author: Andre Mermegas
 * Created: Feb 2, 2007 1:18:43 PM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.dialog;

import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.TradingSessionStatus;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.fxbench.BenchApp;
import org.fxbench.desk.Offers;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TSummary;
import org.fxbench.trader.dialog.BaseDialog;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.ui.auxi.BaseTable;
import org.fxbench.ui.auxi.RiverLayout;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

/**
 */
public class CurrencySubscriptionDialog extends BaseDialog {

    /**
     * Constructor.
     *
     * @param aOwner owner frame
     */
    public CurrencySubscriptionDialog(Frame aOwner) {
        super(aOwner);
        setTitle(BenchApp.getInst().getResourceManager().getString("IDS_CCY_SUBSCRIPTION_LIST"));
        setContentPane(getPanel());
        setModal(true);
        pack();
    }

    private JPanel getPanel() {
        ResourceManager rm = BenchApp.getInst().getResourceManager();
        JPanel panel = new JPanel(new RiverLayout());
        final DefaultTableModel dataModel =
                new DefaultTableModel(new Object[][]{}, new Object[]{
                        rm.getString("IDS_ORDER_CURRENCY"), rm.getString("IDS_ORDER_STATUS")}) {
                    @Override
                    public boolean isCellEditable(int aRow, int aColumn) {
                        return aColumn > 0;
                    }

                    @Override
                    public Class getColumnClass(int aColumn) {
                        return getValueAt(0, aColumn).getClass();
                    }
                };
        String symbolList[] = getTradeDesk().getOffers().getSymbolList();
        for (int i = 0; i < symbolList.length; i++) {
            try {
                TradingSessionStatus tss = ((FxcmServerSession)getTradeDesk().getTradingServerSession()).getTradingSessionStatus();
                TradingSecurity ts = tss.getSecurity(symbolList[i]);
                if (IFixDefs.FXCMSUBSCRIPTIONSTATUS_SUBSCRIBE.equals(ts.getFXCMSubscriptionStatus())) {
                    dataModel.addRow(new Object[]{ts.getSymbol(), Boolean.TRUE});
                } else {
                    dataModel.addRow(new Object[]{ts.getSymbol(), Boolean.FALSE});
                }
            } catch (NotDefinedException e) {
                e.printStackTrace();
            }
        }

        JTable table = new BaseTable(dataModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        String title = BenchApp.getInst().getResourceManager().getString("IDS_CCY_SUBSCRIPTION_LIST");
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        table.setRowSelectionInterval(0, 0);
        JButton cancelButton = UIManager.getInst().createButton("Cancel");
        JButton okButton = UIManager.getInst().createButton("OK");
        InputMap inputMap = okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ExitAction");

        okButton.getActionMap().put("ExitAction", new AbstractAction() {
            public void actionPerformed(ActionEvent aEvent) {
                closeDialog(JOptionPane.OK_OPTION);
            }
        });
        okButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                        StringBuffer open = new StringBuffer();
                        Vector dataVector = dataModel.getDataVector();

                        MarketDataRequest sub = new MarketDataRequest();
                        sub.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_BIDASK);
                        sub.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);

                        MarketDataRequest unsub = new MarketDataRequest();
                        unsub.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_BIDASK);
                        unsub.setSubscriptionRequestType(SubscriptionRequestTypeFactory.UNSUBSCRIBE);

                        int selectedCount = 0;
                        for (Object aDataVector : dataVector) {
                            Vector v = (Vector) aDataVector;
                            Boolean enabled = (Boolean) v.get(1);
                            String ccy = (String) v.get(0);
                            if (enabled) {
                                selectedCount++;
                            }
                            System.out.printf("ccy=%s, enabled=%s\n", ccy, enabled);
                            TradingSessionStatus sessionStatus = ((FxcmServerSession)getTradeDesk().getTradingServerSession()).getTradingSessionStatus();
                            TradingSecurity security = sessionStatus.getSecurity(ccy);
                            if (enabled) {
                                sub.addRelatedSymbol(security);
                            } else {
                                unsub.addRelatedSymbol(security);
                            }
                            TSummary summary = getTradeDesk().getSummaries().getSummary(ccy);
                            if (summary != null && !enabled) {
                                open.append(ccy).append(",");
                            }
                        }
//                        if (!TradingServerSession.getInstance().isUnlimitedCcy() && selectedCount > 12) {
                            /*
                            ResourceManager resourceManager = BenchApp.getInst().getResourceManager();
                            JOptionPane.showMessageDialog(CurrencySubscriptionDialog.this,
                                                          "The Maximum number of symbols you can subscribe to is 12.",
                                                          resourceManager.getString("IDS_MAINFRAME_SHORT_TITLE"),
                                                          JOptionPane.ERROR_MESSAGE);
                            return;
                            */
//                        }
                        if (open.length() == 0) {
                            if (sub.getRelatedSymbols().hasMoreElements()) {
                            	((FxcmServerSession)getTradeDesk().getTradingServerSession()).send(sub);
                            }
                            if (unsub.getRelatedSymbols().hasMoreElements()) {
                            	((FxcmServerSession)getTradeDesk().getTradingServerSession()).send(unsub);
                            }
                            closeDialog(JOptionPane.OK_OPTION);
                        } else {
                            ResourceManager resourceManager = BenchApp.getInst().getResourceManager();
                            open.deleteCharAt(open.length() - 1);
                            JOptionPane.showMessageDialog(CurrencySubscriptionDialog.this,
                                                          "You cannot disable currency pair(s) "
                                                          + open
                                                          + " because there is an open position for this pair(s).",
                                                          resourceManager.getString("IDS_MAINFRAME_SHORT_TITLE"),
                                                          JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

        cancelButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                        closeDialog(JOptionPane.CANCEL_OPTION);
                    }
                });

        Utils.setAllToBiggest(new JComponent[]{okButton, cancelButton});
        UIManager.getInst().packColumns(table, 2);
        getRootPane().setDefaultButton(okButton);
        getContentPane().setLayout(new RiverLayout(0, 0));
        getContentPane().add("vfill hfill", scrollPane);
        panel.add("vfill hfill", scrollPane);
        panel.add("br center", okButton);
        panel.add("", cancelButton);
        return panel;
    }

    @Override
    public int showModal() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return 0;
    }
}
