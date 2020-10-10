package org.fxbench.ui.panel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TOrder.FieldDef;
import org.fxbench.trader.action.TradeAction.ActionType;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;


/**
 * A list with the names of Roman gods.
 * 
 * @author Heidi Rakels.
 */
public class OrderPanel<E> extends TablePanel<E>
{
	public static final String NAME = "Orders";
	public static final String TITLE = "Order";
	public static final String TOOLTIP = "Order";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.ORDER_ID, FieldDef.ACCOUNT_ID, FieldDef.ORDER_TYPE,
		FieldDef.ORDER_STATUS, FieldDef.SYMBOL, FieldDef.ORDER_AMOUNT, 
		FieldDef.ORDER_SELL_PRICE, FieldDef.ORDER_BUY_PRICE, FieldDef.ORDER_STOP,
		FieldDef.ORDER_LIMIT, FieldDef.ORDER_TIME, FieldDef.ORDER_Q_TXT};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);
	
    private static final String STOP = "STOP";
    private static final String LIMIT = "LIMIT";
    private static final String UPDATE_ENTRY_ORDER = "UPDATE_ENTRY_ORDER";

    /**
     * Popup menu.
     */
    private JPopupMenu mPopupLimitMenu;
    /**
     * Popup menu.
     */
    private JPopupMenu mPopupStopMenu;
	
    /**
     * Constructor.
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
	public OrderPanel(BenchFrame mainFrame, ResourceManager aMan) {
		super(mainFrame, aMan);
        setCurSortColumn(fieldDefStub.getFieldNo(FieldDef.ORDER_ID));
		
		//sets icon to internal frame
		URL iconUrl = getResourceManager().getResource("ID_ORDERS_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }

        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);

        //creates a popup menu
        UIManager uim = UIManager.getInst();
        mPopupStopMenu = UIManager.getInst().createPopupMenu();
        mPopupLimitMenu = UIManager.getInst().createPopupMenu();
        Action updateEntryOrderAction = getMainFrame().getAction(ActionType.UPDATE_ENTRY_ORDER, null);
        uim.addAction(
                updateEntryOrderAction,
                "IDS_UPDATE_ENTRY_ORDER",
                "ID_ENTRY_ICON",
                null,
                "IDS_UPDATE_ENTRY_ORDER_DESC",
                "IDS_UPDATE_ENTRY_ORDER_DESC");
        JMenuItem menuItem = uim.createMenuItem(updateEntryOrderAction);
        menuItem.setActionCommand(UPDATE_ENTRY_ORDER);
        mPopupStopMenu.add(menuItem);
        updateEntryOrderAction = getMainFrame().getAction(ActionType.UPDATE_ENTRY_ORDER, null);
        uim.addAction(
                updateEntryOrderAction,
                "IDS_UPDATE_ENTRY_ORDER",
                "ID_ENTRY_ICON",
                null,
                "IDS_UPDATE_ENTRY_ORDER_DESC",
                "IDS_UPDATE_ENTRY_ORDER_DESC");
        menuItem = uim.createMenuItem(updateEntryOrderAction);
        menuItem.setActionCommand(UPDATE_ENTRY_ORDER);
        mPopupLimitMenu.add(menuItem);
        Action stopLimitOrderAction = getMainFrame().getAction(ActionType.SET_STOP_LIMIT_ORDER, STOP);
        uim.addAction(
                stopLimitOrderAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        menuItem = uim.createMenuItem(stopLimitOrderAction);
        menuItem.setActionCommand(STOP);
        mPopupStopMenu.add(menuItem);
        stopLimitOrderAction = getMainFrame().getAction(ActionType.SET_STOP_LIMIT_ORDER, LIMIT);
        uim.addAction(
                stopLimitOrderAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        menuItem = uim.createMenuItem(stopLimitOrderAction);
        menuItem.setActionCommand(LIMIT);
        mPopupLimitMenu.add(menuItem);
        Action removeEntryOrderAction = getMainFrame().getAction(ActionType.REMOVE_ENTRY_ORDER, null);
        uim.addAction(
                removeEntryOrderAction,
                "IDS_REMOVE_ORDER",
                "ID_CLOSE_ICON",
                null,
                "IDS_REMOVE_ORDER_DESC",
                "IDS_REMOVE_ORDER_DESC");
        menuItem = uim.createMenuItem(removeEntryOrderAction);
        menuItem.setActionCommand("REMOVE_ORDER");
        mPopupStopMenu.add(menuItem);
        removeEntryOrderAction = getMainFrame().getAction(ActionType.REMOVE_ENTRY_ORDER, null);
        uim.addAction(
                removeEntryOrderAction,
                "IDS_REMOVE_ORDER",
                "ID_CLOSE_ICON",
                null,
                "IDS_REMOVE_ORDER_DESC",
                "IDS_REMOVE_ORDER_DESC");
        menuItem = uim.createMenuItem(removeEntryOrderAction);
        menuItem.setActionCommand("REMOVE_ORDER");
        mPopupLimitMenu.add(menuItem);
        getTable().addKeyListener(
        	new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent aKeyEvent) {
	                if (KeyEvent.VK_DELETE == aKeyEvent.getKeyCode()) {
	                    Action action = getMainFrame().getAction(ActionType.REMOVE_ENTRY_ORDER, null);
	                    if (action.isEnabled()) {
	                        ActionEvent event = new ActionEvent(this, 0, "");
	                        action.actionPerformed(event);
	                    }
	                }
	            }
	        });

        //adds mouse listener
        getTable().addMouseListener(
            new MouseAdapter() {
                private int mCurrentRow = -1;
                private int mCurrentColumn = -1;

                @Override
                public void mouseReleased(MouseEvent aEvent) {
                    JTable table = (JTable) aEvent.getComponent();
                    mCurrentColumn = table.columnAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                    mCurrentRow = table.rowAtPoint(new Point(aEvent.getX(), aEvent.getY()));

                    //gets index of column at model
                    TableColumn column = table.getColumnModel().getColumn(mCurrentColumn);
                    if (aEvent.getButton() == MouseEvent.BUTTON3 || aEvent.isPopupTrigger()) {
                        boolean selected = false;
                        for (int selectedRow : table.getSelectedRows()) {
                            if (selectedRow == mCurrentRow) {
                                selected = true;
                            }
                        }
                        if (!selected) {
                            table.getSelectionModel().setSelectionInterval(mCurrentRow, mCurrentRow);
                        }

                        if (column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_LIMIT)) {
                            //shows popup menu
                            mPopupLimitMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                        } else if (column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_STOP)) {
                            //shows popup menu
                            mPopupStopMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                        }
                    } else {
                        if (table.getSelectedRows().length == 1) {
                            if (column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_STOP)) {
                                Action action = getMainFrame().getAction(ActionType.SET_STOP_LIMIT_ORDER, null);
                                if (action.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, STOP);
                                    action.actionPerformed(event);
                                }
                            } else if (column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_LIMIT)) {
                                Action action = getMainFrame().getAction(ActionType.SET_STOP_LIMIT_ORDER, null);
                                if (action.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, LIMIT);
                                    action.actionPerformed(event);
                                }
                            } else if (column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_BUY_PRICE)
                                       || column.getModelIndex() == fieldDefStub.getFieldNo(FieldDef.ORDER_SELL_PRICE)) {
                                Action action = getMainFrame().getAction(ActionType.UPDATE_ENTRY_ORDER, null);
                                if (action.isEnabled()) {
                                    ActionEvent event = new ActionEvent(this, 0, "UPDATE_ENTRY");
                                    action.actionPerformed(event);
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
            int orderSize = getSignalVector().size();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_ORDERS_TITLE"));
            if (orderSize > 0) {
                titleBuffer.append(" (").append(orderSize).append(")");
            }
//            if (IClickModel.SINGLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ ONE CLICK TRADING ~~~~~~");
//            } else if (IClickModel.DOUBLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ DOUBLE CLICK TRADING ~~~~~~");
//            }
            return titleBuffer.toString();
        }
        mLogger.debug("Error: OrdersFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }


    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getOrders();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new OrdersTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Returns table name.
     */
    @Override
    protected String getTableName() {
        return "order";
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
        mPopupLimitMenu.removeAll();
        mPopupLimitMenu.removeNotify();
        mPopupLimitMenu = null;
        mPopupStopMenu.removeAll();
        mPopupStopMenu.removeNotify();
        mPopupStopMenu = null;
        removeAll();
        removeNotify();
    }


    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class OrdersTableModel extends PanelTableModel {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
        OrdersTableModel(ResourceManager aResourceMan) {
        	super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }
}
