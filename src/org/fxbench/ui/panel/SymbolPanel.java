package org.fxbench.ui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Timer;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.fxbench.desk.Offers;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TOffer.FieldDef;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.action.ActionManager;
import org.fxbench.trader.action.CreateEntryOrderAction;
import org.fxbench.trader.action.CreateMarketOrderAction;
import org.fxbench.trader.action.CurrencySubscriptionAction;
import org.fxbench.trader.action.RequestForQuoteAction;
import org.fxbench.trader.action.TradeAction;
import org.fxbench.trader.action.TradeAction.ActionType;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.ITableListener;
import org.fxbench.ui.auxi.ITableSelectionListener;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.InvokerSetRowHeight;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;


/**
 * A list with the names of Roman gods.
 * 
 * @author yld.
 */
public class SymbolPanel<E> extends TablePanel<E> implements ITableSelectionListener
{
	public static final String NAME = "Symbols";
	public static final String TITLE = "Symbol";
	public static final String TOOLTIP = "Symbol";
	
	protected Color mColorBGDown;
	protected Color mColorBGRaised;
	protected Color mColorBGDownSelected;
	protected Color mColorBGRaisedSelected;
	protected Color mColorFGDown;
	protected Color mColorFGRaised;
	protected Color mColorFGDownSelected;
	protected Color mColorFGRaisedSelected;
	protected Font mFontDown;
	protected Font mFontRaised;
	protected Font mFontDownSelected;
	protected Font mFontRaisedSelected;
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.SYMBOL, FieldDef.OFFER_BID, FieldDef.OFFER_ASK,
		FieldDef.OFFER_SPREAD, FieldDef.OFFER_HIGH, FieldDef.OFFER_LOW, 
		FieldDef.OFFER_INTR_S, FieldDef.OFFER_INTR_B, FieldDef.OFFER_PIP_COST,
		FieldDef.OFFER_TIME};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);
	
    private String mAmtColName;
    private TableColumn mAmtTableColumn;
    private JPopupMenu mBuyPopupMenu;
    private String mSelectedCurrency = "EUR/USD";
    private JPopupMenu mSellPopupMenu;
    
    private final Timer mTimer = new Timer("RatesFrameTimer");

    /**
     * Constructor.
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
    public SymbolPanel(BenchFrame mainFrame, ResourceManager aMan) {
    	super(mainFrame, aMan);
 
    	//sets icon to internal frame
        URL iconUrl = getResourceManager().getResource("ID_RATES_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }
        
        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);
        
        //update title again w/ rates
        setTitle(getLocalizedTitle(aMan));

        //creates a popup menu for sell side
        UIManager uiManager = UIManager.getInst();
        mSellPopupMenu = uiManager.createPopupMenu();

        //first menu item
        Action entryOrderAction = getMainFrame().getAction(ActionType.CREATE_ENTRY_ORDER, BnS.SELL.name());
        uiManager.addAction(entryOrderAction,
                            "IDS_CREATE_ENTRY_ORDER",
                            "ID_ENTRY_ICON",
                            null,
                            "IDS_CREATE_ENTRY_ORDER_DESC",
                            "IDS_CREATE_ENTRY_ORDER_DESC");
        JMenuItem menuItem = uiManager.createMenuItem(entryOrderAction);
        menuItem.setActionCommand(TPosition.BnS.SELL.name());
        mSellPopupMenu.add(menuItem);

        //second menu item
        Action marketOrderAction = getMainFrame().getAction(ActionType.CREATE_MARKET_ORDER, BnS.SELL.name());
        uiManager.addAction(marketOrderAction,
                            "IDS_CREATE_MARKET_ORDER",
                            "ID_MARKET_ORDER_ICON",
                            null,
                            "IDS_CREATE_MARKET_ORDER_DESC",
                            "IDS_CREATE_MARKET_ORDER_DESC");
        menuItem = uiManager.createMenuItem(marketOrderAction);
        menuItem.setActionCommand(TPosition.BnS.SELL.name());
        mSellPopupMenu.add(menuItem);

        //third menu item
        Action rfq = getMainFrame().getAction(ActionType.REQUEST_FOR_QUOTE, null);
        uiManager.addAction(rfq,
                            "IDS_REQUEST_FOR_QUOTE",
                            "ID_MARKET_ORDER_ICON",
                            null,
                            "IDS_REQUEST_FOR_QUOTE_DESC",
                            "IDS_REQUEST_FOR_QUOTE_DESC");
        menuItem = uiManager.createMenuItem(rfq);
        menuItem.setActionCommand("RFQ");
        mSellPopupMenu.add(menuItem);
        mSellPopupMenu.add(getCCYSubsMenu());

        //creates a popup menu for buy syde
        mBuyPopupMenu = UIManager.getInst().createPopupMenu();

        //first menu item
        entryOrderAction = getMainFrame().getAction(ActionType.CREATE_ENTRY_ORDER, BnS.BUY.name());
        uiManager.addAction(entryOrderAction,
                            "IDS_CREATE_ENTRY_ORDER",
                            "ID_ENTRY_ICON",
                            null,
                            "IDS_CREATE_ENTRY_ORDER_DESC",
                            "IDS_CREATE_ENTRY_ORDER_DESC");
        menuItem = uiManager.createMenuItem(entryOrderAction);
        menuItem.setActionCommand(BnS.BUY.name());
        mBuyPopupMenu.add(menuItem);

        //second menu item
        marketOrderAction = getMainFrame().getAction(ActionType.CREATE_MARKET_ORDER, BnS.BUY.name());
        uiManager.addAction(marketOrderAction,
                            "IDS_CREATE_MARKET_ORDER",
                            "ID_MARKET_ORDER_ICON",
                            null,
                            "IDS_CREATE_MARKET_ORDER_DESC",
                            "IDS_CREATE_MARKET_ORDER_DESC");
        menuItem = uiManager.createMenuItem(marketOrderAction);
        menuItem.setActionCommand(TPosition.BnS.BUY.name());
        mBuyPopupMenu.add(menuItem);

        //third menu item
        rfq = getMainFrame().getAction(ActionType.REQUEST_FOR_QUOTE, null);
        uiManager.addAction(rfq,
                            "IDS_REQUEST_FOR_QUOTE",
                            "ID_MARKET_ORDER_ICON",
                            null,
                            "IDS_REQUEST_FOR_QUOTE_DESC",
                            "IDS_REQUEST_FOR_QUOTE_DESC");
        menuItem = uiManager.createMenuItem(rfq);
        menuItem.setActionCommand("RFQ");
        mBuyPopupMenu.add(menuItem);
        mBuyPopupMenu.add(getCCYSubsMenu());

        //adds mouse listener
        getTable().addMouseListener(new MouseAdapter() {
            /**
             * Invoked when a mouse button has been released on a component.
             */
            @Override
            public void mouseReleased(MouseEvent aEvent) {
                JTable table = (JTable) aEvent.getComponent();
                int col = table.columnAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                int row = table.rowAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                table.setRowSelectionInterval(row, row); //selects row
                
                String ccy = (String) table.getModel().getValueAt(row, fieldDefStub.getFieldNo(FieldDef.SYMBOL));
                setSelectedCurrency(ccy);
                
                //gets index of column at model
                int columnIndex = table.getColumnModel().getColumn(col).getModelIndex();
                if (aEvent.getButton() == MouseEvent.BUTTON3 || aEvent.isPopupTrigger()) { //if right button
                    if (columnIndex == fieldDefStub.getFieldNo(FieldDef.OFFER_BID)) {
                        //shows popup menu
                        mSellPopupMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                    } else {
                        //shows popup menu
                        mBuyPopupMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                    }
                } else { //if left button
                    if (columnIndex == fieldDefStub.getFieldNo(FieldDef.SYMBOL)) {
                        Action action = getMainFrame().getAction(ActionType.REQUEST_FOR_QUOTE, null);
                        ActionEvent event = new ActionEvent(this, 0, "RFQ");
                        action.actionPerformed(event);
                    } else if (columnIndex == fieldDefStub.getFieldNo(FieldDef.OFFER_BID)) {
                        Action action = getMainFrame().getAction(ActionType.CREATE_MARKET_ORDER, null);
                        if (action.isEnabled()) {
                            ActionEvent event = new ActionEvent(this, 0, BnS.SELL.toString());
                            action.actionPerformed(event);
                        }
                    } else if (columnIndex == fieldDefStub.getFieldNo(FieldDef.OFFER_ASK)) {
	                    Action action = getMainFrame().getAction(ActionType.CREATE_MARKET_ORDER, null);
	                    if (action.isEnabled()) {
	                        ActionEvent event = new ActionEvent(this, 0, BnS.BUY.toString());
	                        action.actionPerformed(event);
	                    }
                    }
                }
            }
        });

        if (mSelectedCurrency == null) {
            getTable().setRowSelectionInterval(0, 0);
            String ccy = (String) getTable().getModel().getValueAt(0, fieldDefStub.getFieldNo(FieldDef.SYMBOL));
            setSelectedCurrency(ccy);
        } else {
            int rowCount = getTable().getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String ccy = (String) getTable().getModel().getValueAt(i, fieldDefStub.getFieldNo(FieldDef.SYMBOL));
                if (mSelectedCurrency.equals(ccy)) {
                    getTable().setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
        getTableListener().addSelectionListener(this);
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

    /**
     * Returns popup menu for buy side.
     *
     * @return popup menu for buy side.
     */
    public JPopupMenu getBuyPopupMenu() {
        return mBuyPopupMenu;
    }

    protected JMenuItem getCCYSubsMenu() {
        JMenuItem item = UIManager.getInst().createMenuItem("IDS_CCY_SUBSCRIPTION_LIST", null, null,
                                                            "IDS_CCY_SUBSCRIPTION_LIST");
        item.addActionListener(new CurrencySubscriptionAction());
        return item;
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
	
	@Override
	protected void setPreferences() {
		super.setPreferences();
		mColorBGDown = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_down");
		mColorBGRaised = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_raised");
		mColorBGDownSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_down_selected");
		mColorBGRaisedSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_raised_selected");
		mColorFGDown = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_down");
		mColorFGRaised = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_raised");
		mColorFGDownSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_down_selected");
		mColorFGRaisedSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_raised_selected");
		mFontDown = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_down");
		mFontRaised = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_raised");
		mFontDownSelected = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_down_selected");
		mFontRaisedSelected = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_raised_selected");
	}
	
	@Override
	protected DefaultTableCellRenderer getDefaultCellRenderer() {
		return new DefaultTableCellRenderer() {
            //Overriding of DefaultTableCellRenderer`s method
            @Override
            public Component getTableCellRendererComponent(JTable aTable,
                                                           Object aValue,
                                                           boolean aIsSelected,
                                                           boolean aHasFocus,
                                                           int aRow,
                                                           int aColumn) {

            	//gets index of column at model
                TableColumnModel columnModel = aTable.getColumnModel();
                TableColumn tableColumn = columnModel.getColumn(aColumn);
                
                TOffer rate = (TOffer)getSignalVector().get(aRow);
                if (rate == null) {
                    return null;
                }
                JLabel comp = (JLabel) super.getTableCellRendererComponent(aTable,
                                                                           aValue,
                                                                           aIsSelected,
                                                                           aHasFocus,
                                                                           aRow,
                                                                           aColumn);
                setForeground(mColorFGDefault);
                if (aRow % 2 == 0) {
                    setBackground(mColorBGEven);
                } else {
                    setBackground(mColorBGOdd);
                }
                //set font and color of content table
                comp.setFont(mFontContent);
//                if (rate.isTradable()) {
                    if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.OFFER_BID)) {//SELL_COLUMN
//                        if (rate.isSellTradable()) {
                            if (rate.getOldBid() <= rate.getBid()) {
                                comp.setForeground(mColorFGRaised);
                                comp.setBackground(mColorBGRaised);
                                comp.setFont(mFontRaised);
                            } else if (rate.getOldBid() >= rate.getBid()) {
                                comp.setForeground(mColorFGDown);
                                comp.setBackground(mColorBGDown);
                                comp.setFont(mFontDown);
                            }
//                        } else {
//                            comp.setForeground(mColorFGTradable);
//                            comp.setFont(mFontContentTradable);
//                        }
                    } else if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.OFFER_ASK)) {//BUY_COLUMN
//                        if (rate.isBuyTradable()) {
                            if (rate.getOldAsk() <= rate.getAsk()) {
                                comp.setForeground(mColorFGRaised);
                                comp.setBackground(mColorBGRaised);
                                comp.setFont(mFontRaised);
                            } else if (rate.getOldAsk() >= rate.getAsk()) {
                                comp.setForeground(mColorFGDown);
                                comp.setBackground(mColorBGDown);
                                comp.setFont(mFontDown);
                            }
//                        } else {
//                            comp.setForeground(mColorFGTradable);
//                            comp.setFont(mFontContentTradable);
//                        }
                    }
//                } else {
//                    comp.setForeground(mColorFGTradable);
//                    comp.setFont(mFontContentTradable);
//                }

                //sets color and fonts of selected row
                if (aTable.getSelectedRow() == aRow) {
                    setBackground(mColorBGSelected);
                    setForeground(mColorFGSelected);
                    comp.setFont(mFontSelected);
                    if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.OFFER_BID)) {//SELL_COLUMN
//                        if (rate.isSellTradable()) {
                            if (rate.getOldBid() <= rate.getBid()) {
                                comp.setForeground(mColorFGRaisedSelected);
                                comp.setBackground(mColorBGRaisedSelected);
                                comp.setFont(mFontRaisedSelected);
                            } else if (rate.getOldBid() >= rate.getBid()) {
                                comp.setForeground(mColorFGDownSelected);
                                comp.setBackground(mColorBGDownSelected);
                                comp.setFont(mFontDownSelected);
                            }
//                        } else {
//                            comp.setForeground(mColorFGTradable);
//                            comp.setFont(mFontContentTradable);
//                        }
                    } else if (tableColumn.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.OFFER_ASK)) {//BUY_COLUMN
//                        if (rate.isBuyTradable()) {
                            if (rate.getOldAsk() <= rate.getAsk()) {
                                comp.setForeground(mColorFGRaisedSelected);
                                comp.setBackground(mColorBGRaisedSelected);
                                comp.setFont(mFontRaisedSelected);
                            } else if (rate.getOldAsk() >= rate.getAsk()) {
                                comp.setForeground(mColorFGDownSelected);
                                comp.setBackground(mColorBGDownSelected);
                                comp.setFont(mFontDownSelected);
                            }
//                        } else {
//                            comp.setForeground(mColorFGTradable);
//                            comp.setFont(mFontContentTradable);
//                        }
                    }
//                    if (!rate.isTradable()) {
//                        comp.setFont(mFontContentTradableSelected);
//                        comp.setForeground(mColorFGTradableSelected);
//                    }
                }
                setOpaque(true);
                comp.setHorizontalAlignment(getColumnAlignment(aColumn));
                int height = comp.getFontMetrics(comp.getFont()).getHeight() + 2;
                if (height > initialRowHeight && aTable.getRowHeight(aRow) < height) {
                    EventQueue.invokeLater(new InvokerSetRowHeight(aTable, aRow, height));
                }
                return comp;
            }

            @Override
            protected void paintComponent(Graphics aGraphics) {
                if (UIManager.getInst().isAAEnabled()) {
                    Graphics2D g2d = (Graphics2D) aGraphics;
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }
                super.paintComponent(aGraphics);
            }
        };
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
            int ratesLength = getSignalVector().size();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_RATES_TITLE"));
            if (ratesLength > 0) {
                titleBuffer.append(" (").append(ratesLength).append(")");
            }
            return titleBuffer.toString();
        }
        mLogger.debug("Error: RatesFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    /**
     * @return Selected Currency
     */
    public String getSelectedCurrency() {
        return mSelectedCurrency;
    }

    /**
     * Returns popup menu for sell side.
     *
     * @return popup menu for sell side.
     */
    public JPopupMenu getSellPopupMenu() {
        return mSellPopupMenu;
    }

    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getOffers();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new OffersTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Returns frames name.
     */
    @Override
    protected String getTableName() {
        return "symbol";
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    /**
     * Method is called by Internal Frame Listener when frame has been closed.
     *
     * @param aEvent event
     */
    @Override
    public void onClose() {
    	super.onClose();
        mBuyPopupMenu.removeAll();
        mBuyPopupMenu.removeNotify();
        mBuyPopupMenu = null;
        mSellPopupMenu.removeAll();
        mSellPopupMenu.removeNotify();
        mSellPopupMenu = null;
        removeAll();
        removeNotify();
    }

    @Override
    public void onSignal(Signaler aSrc, Signal aSignal) {
        super.onSignal(aSrc, aSignal);
        if (aSignal != null && aSignal.getType() == SignalType.CHANGE) {
            TOffer rate = (TOffer) aSignal.getNewElement();
            if (rate.isSubscribed() && getSignalVector().indexOf(rate) == -1) {
            	getSignalVector().add(rate);
                refresh();
            } else if (!rate.isSubscribed() && getSignalVector().indexOf(rate) != -1) {
            	getSignalVector().remove(rate);
                refresh();
            }
        }
    }

    protected void refresh() {
        setTitle(getLocalizedTitle(mResourceManager));
        getTable().revalidate();
        getTable().repaint();
    }

    /**
     * @param aSelectedCurrency Selected Currency
     */
    public void setSelectedCurrency(String aSelectedCurrency) {
        TOffer rate = ((Offers)getSignalVector()).getOffer(aSelectedCurrency);
        Enumeration actions = ActionManager.getInst().actions();
        while (actions.hasMoreElements()) {
            TradeAction action = (TradeAction) actions.nextElement();
            if (rate != null) {
                if (action instanceof CreateMarketOrderAction
                    || action instanceof CreateEntryOrderAction
                    || action instanceof RequestForQuoteAction) {
                    action.canAct(rate.isTradable());
                    action.setEnabled(rate.isTradable());
                }
            }
        }
        mSelectedCurrency = aSelectedCurrency;
        int rowCount = getTable().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String ccy = (String) getTable().getModel().getValueAt(i, fieldDefStub.getFieldNo(FieldDef.SYMBOL));
            if (mSelectedCurrency.equals(ccy)) {
                getTable().setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    @Override
    public void update(Observable aObservable, Object arg) {
        super.update(aObservable, arg);
//        if (arg.toString().endsWith(IClickModel.TRADING_MODE)) {
//            String username = getTradeDesk().getUserName();
//            UserPreferences prefs = UserPreferences.getUserPreferences(username);
//            String mode = prefs.getString(IClickModel.TRADING_MODE);
//            if (IClickModel.DOUBLE_CLICK.equals(mode) || IClickModel.SINGLE_CLICK.equals(mode)) {
//                JCheckBoxMenuItem item = mCheckBoxMap.get(mAmtColName);
//                item.setEnabled(true);
//                item.setSelected(true);
//                getTable().addColumn(mAmtTableColumn);
//                reOrderColumns();
//            } else {
//                JCheckBoxMenuItem item = mCheckBoxMap.get(mAmtColName);
//                item.setEnabled(false);
//                item.setSelected(false);
//                getTable().removeColumn(mAmtTableColumn);
//            }
//        }
    }

    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class OffersTableModel extends PanelTableModel {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
    	OffersTableModel(ResourceManager aResourceMan) {
            super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }

	@Override
	public void onTableChangeSelection(ITableListener aTable, int[] aiRow) {
		TableModel model = getTable().getModel();
        String ccy = (String) model.getValueAt(aTable.getSelectedRow(), fieldDefStub.getFieldNo(FieldDef.SYMBOL));
        setSelectedCurrency(ccy);
	}
}
