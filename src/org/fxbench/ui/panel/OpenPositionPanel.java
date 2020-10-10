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
 *
 * $History: $
 * 9/20/2003 Created by USHIK
 * 12/9/2004    Andre Mermegas  -- updated to show in sizes same as classic TS
 * 05/05/2006   Andre Mermegas: fix for NPE when clicking in totals row
 * 07/05/2006   Andre Mermegas: update to allow for totals row not be sorted
 * 05/11/2007   Andre Mermegas: default sort by first column, newest on top
 */
package org.fxbench.ui.panel;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

import org.fxbench.desk.Positions;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TPosition.FieldDef;
import org.fxbench.trader.action.TradeAction.ActionType;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

/**
 * Frames is destined for showing of table with open positions.
 * <br>
 * Creation date (9/20/2003 6:38 PM)
 */
public class OpenPositionPanel<E> extends TablePanel<E>
{
    public static final String NAME = "OpenPositions";
    public static final String TITLE = "OpenPosition";
	public static final String TOOLTIP = "OpenPosition";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.TRADE_ID, FieldDef.ACCOUNT_ID, FieldDef.SYMBOL,
		FieldDef.TRADE_AMOUNT, FieldDef.TRADE_BS, FieldDef.TRADE_OPEN, 
		FieldDef.TRADE_CLOSE,  FieldDef.TRADE_STOP, FieldDef.TRADE_UNT_TRL_MOVE,
		FieldDef.TRADE_LIMIT, FieldDef.TRADE_PL, FieldDef.TRADE_GROSS_PL, 
		FieldDef.TRADE_COM, FieldDef.TRADE_INTEREST, FieldDef.TRADE_OPEN_TIME,
		/*FieldDef.TRADE_CQ_TXT*/};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);

    private static final String CLOSE = "CLOSE";
    private static final String STOP = "STOP";
    private static final String LIMIT = "LIMIT";
   
    /**
     * Popup menu for limit.
     */
    private JPopupMenu mLimitPopupMenu;
    private Color mOddColor;
    /**
     * Popup menu for stop column.
     */
    private JPopupMenu mStopPopupMenu;
    private Color mSummaryBGTotalColor;
    private Color mSummaryFGTotalColor;
    private Font mTotalFont;
    private Font mTradableSelectedFont;

    /**
     * Constructor.
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
    public OpenPositionPanel(BenchFrame mainFrame, ResourceManager aMan) {
        super(mainFrame, aMan);
        setCurSortColumn(fieldDefStub.getFieldNo(FieldDef.TRADE_ID));

    	//sets icon to internal frame
        URL iconUrl = getResourceManager().getResource("ID_OPENPOSITIONS_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }
        
        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);
        
        UIManager uim = UIManager.getInst();

        //creates a popup menu
        mStopPopupMenu = uim.createPopupMenu();

        //first menu item
        Action stopLimitOrderAction = getMainFrame().getAction(ActionType.SET_STOP_LIMIT, STOP);
        uim.addAction(
                stopLimitOrderAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        JMenuItem menuItem = uim.createMenuItem(stopLimitOrderAction);
        menuItem.setActionCommand(STOP);
        mStopPopupMenu.add(menuItem);

        //second menu item
        Action action = getMainFrame().getAction(ActionType.CLOSE_POSITION, null);
        uim.addAction(action, "IDS_CLOSE", "ID_CLOSE_ICON", null, "IDS_CLOSE_DESC", "IDS_CLOSE_DESC");
        menuItem = uim.createMenuItem(action);
        menuItem.setActionCommand(CLOSE);
        mStopPopupMenu.add(menuItem);

        //creates a popup menu
        mLimitPopupMenu = UIManager.getInst().createPopupMenu();
        //first menu item
        stopLimitOrderAction = getMainFrame().getAction(ActionType.SET_STOP_LIMIT, LIMIT);
        uim.addAction(
                stopLimitOrderAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        menuItem = uim.createMenuItem(stopLimitOrderAction);
        menuItem.setActionCommand(LIMIT);
        mLimitPopupMenu.add(menuItem);

        //second menu item
        action = getMainFrame().getAction(ActionType.CLOSE_POSITION, null);
        uim.addAction(action, "IDS_CLOSE", "ID_CLOSE_ICON", null, "IDS_CLOSE_DESC", "IDS_CLOSE_DESC");
        menuItem = uim.createMenuItem(action);
        mLimitPopupMenu.add(menuItem);
        JTable table = getTable();
        table.addKeyListener(
            new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent aE) {
                    if (KeyEvent.VK_DELETE == aE.getKeyCode()) {
                        Action closePositionAction = getMainFrame().getAction(ActionType.CLOSE_POSITION, null);
                        if (closePositionAction.isEnabled()) {
                            ActionEvent event = new ActionEvent(this, 0, "");
                            closePositionAction.actionPerformed(event);
                        }
                    }
                }
            });
        
        //adds mouse listener
        table.addMouseListener(
            new MouseAdapter() {
                private int mCurrentRow = -1;
                private int mCurrentColumn = -1;
                @Override
                public void mouseReleased(MouseEvent aEvent) {
                    JTable jTable = (JTable) aEvent.getComponent();
                    mCurrentColumn = jTable.columnAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                    mCurrentRow = jTable.rowAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                    if (mCurrentRow == jTable.getRowCount() - 1) { //0 based
                        //ignore processing in totals row
                        return;
                    }

                    //gets index of column at model
                    TableColumn tableColumn = jTable.getColumnModel().getColumn(mCurrentColumn);
                    if (aEvent.getButton() == MouseEvent.BUTTON3 || aEvent.isPopupTrigger()) {
                        boolean selected = false;
                        for (int selectedRow : jTable.getSelectedRows()) {
                            if (selectedRow == mCurrentRow) {
                                selected = true;
                            }
                        }
                        if (!selected) {
                            jTable.getSelectionModel().setSelectionInterval(mCurrentRow, mCurrentRow);
                        }
                        Positions openPositions = getTradeDesk().getOpenPositions();
                        if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.TRADE_LIMIT)) {
                            //shows popup menu
                            mLimitPopupMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                        } else {
                            //shows popup menu
                            JMenuItem stopLimitMenuItem = UIManager.getInst().createMenuItem();
                            JMenuItem closeMenuItem = UIManager.getInst().createMenuItem();
                            for (Component component : mStopPopupMenu.getComponents()) {
                                if (component instanceof JMenuItem) {
                                    JMenuItem item = (JMenuItem) component;
                                    if (STOP.equals(item.getActionCommand())) {
                                        stopLimitMenuItem = item;
                                    } else if (CLOSE.equals(item.getActionCommand())) {
                                        closeMenuItem = item;
                                    }
                                }
                            }
                            boolean showCloseMenu = true;
                            String tickedID = (String) jTable.getModel().getValueAt(mCurrentRow, fieldDefStub.getFieldNo(FieldDef.TRADE_ID));
                            TPosition position = openPositions.getPosition(tickedID);
                            TOffer rate = getTradeDesk().getOffers().getOffer(position.getSymbol());
                            if (rate != null && !rate.isTradable()) {
                                showCloseMenu = false;
                            }
                            if (showCloseMenu) {
                                closeMenuItem.setEnabled(true);
                                stopLimitMenuItem.setEnabled(true);
                            } else {
                                closeMenuItem.setEnabled(false);
                                stopLimitMenuItem.setEnabled(false);
                            }
                            if (jTable.getSelectedRows().length > 1) {
                                stopLimitMenuItem.setEnabled(false);
                            }
                            mStopPopupMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                        }
                    } else {
                        if (jTable.getSelectedRows().length == 1) {
                            if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE)) {
                               Action a = getMainFrame().getAction(ActionType.CLOSE_POSITION, null);
                                if (a.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, CLOSE);
                                    a.actionPerformed(event);
                                }                                
                            } else if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.TRADE_STOP)) {
                                Action a = getMainFrame().getAction(ActionType.SET_STOP_LIMIT, null);
                                if (a.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, STOP);
                                    a.actionPerformed(event);
                                }
                            } else if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.TRADE_LIMIT)) {
                                Action a = getMainFrame().getAction(ActionType.SET_STOP_LIMIT, null);
                                if (a.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, LIMIT);
                                    a.actionPerformed(event);
                                }
                            }
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent aEvent) {
                    mCurrentColumn = -1;
                    mCurrentRow = -1;
                }

                @Override
                public void mouseEntered(MouseEvent aEvent) {
                    mCurrentColumn = -1;
                    mCurrentRow = -1;
                }
            });

        fireSorting();
    }
    
    @Override
	protected String id() {
		return NAME;
	}

	@Override
	protected String title() {
		return TITLE;
	}

	@Override
	protected String tooltip() {
		return TOOLTIP;
	}

    public static FieldDefStub<FieldDef> getFieldDefStub() {
		return fieldDefStub;
	}
    
    @Override
	protected FieldType getColumnType(int column) {
		return column >=0 && column < FIELDS_DEF.length ?
				FIELDS_DEF[column].getFieldType() : FieldType.STRING;
	}
    
    @Override
	protected int getColumnAlignment(int column) {
		return column >=0 && column < FIELDS_DEF.length ?
				FIELDS_DEF[column].getFiledAlignment() : SwingConstants.LEFT;
	}
    
    @Override
	public void propertyUpdated(List<PropertySheet> aChangings) {
		super.propertyUpdated(aChangings);
		setPreferences();
	}
    
    /**
     * Returns localized title.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    protected String getLocalizedTitle(ResourceManager aResourceMan) {
        if (aResourceMan != null) {
//            UserPreferences pref = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//            String mode = pref.getString(IClickModel.TRADING_MODE);
            int positionSize = getTradeDesk().getOpenPositions().size();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_POSITIONS_TITLE"));
            if (positionSize > 0) {
                titleBuffer.append(" (").append(positionSize).append(")");
            }
//            if (IClickModel.SINGLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ ONE CLICK TRADING ~~~~~~");
//            } else if (IClickModel.DOUBLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ DOUBLE CLICK TRADING ~~~~~~");
//            }
            return titleBuffer.toString();
        }
        mLogger.debug("Error: OpenPositionsFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getOpenPositions();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new OpenPostionsTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Returns table name.
     */
    @Override
    protected String getTableName() {
        return "openposition";
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    /**
     * Invoked on frame closing.
     *
     * @param aEvent event
     */
    @Override
    public void onClose() {
    	super.onClose();
        mLimitPopupMenu.removeAll();
        mLimitPopupMenu.removeNotify();
        mLimitPopupMenu = null;
        mStopPopupMenu.removeAll();
        mStopPopupMenu.removeNotify();
        mStopPopupMenu = null;
        removeAll();
        removeNotify();
    }
    
    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class OpenPostionsTableModel extends PanelTableModel {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
        OpenPostionsTableModel(ResourceManager aResourceMan) {
            super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }
}
