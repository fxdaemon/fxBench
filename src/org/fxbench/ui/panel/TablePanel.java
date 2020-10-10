/**
 * 
 */
package org.fxbench.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.BaseTable;
import org.fxbench.ui.auxi.ITableListener;
import org.fxbench.ui.auxi.ITableSelectionListener;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ILocaleListener;
import org.fxbench.util.PreferencesDialog;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.IPropertyListener;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;



/**
 * @author yld
 *
 */
public abstract class TablePanel<E> extends BasePanel 
	implements ILocaleListener, IPropertyListener, Observer, ISignalListener
{	
	protected Color mColorBGHeader;
	protected Color mColorBGSelected;
	protected Color mColorBGEven;
	protected Color mColorBGOdd;	
	protected Color mColorBGMargin;
	protected Color mColorBGClosed;
	protected Color mColorBGTotal;	
	
	protected Color mColorFGHeader;
	protected Color mColorFGSelected;
	protected Color mColorFGDefault;
	protected Color mColorFGMargin;
	protected Color mColorFGClosed;
	protected Color mColorFGTotal;

	protected Font mFontContent;
	protected Font mFontHeader;
	protected Font mFontMargin;
	protected Font mFontSelected;
	protected Font mFontClosed;
	protected Font mFontTotal;
    
	/**
     * Initial height of table.
     */
    protected final int initialRowHeight = UIManager.getInst()
            .createLabel()
            .getFontMetrics(new Font("dialog", 0, 12))
            .getHeight() + 2;
    
    /**
     * Cell renderer of columns of table.
     */
    protected DefaultTableCellRenderer mCellRenderer;
    /**
     * Map of checkboxes
     */
    protected Map<String, JCheckBoxMenuItem> mCheckBoxMap = new HashMap<String, JCheckBoxMenuItem>();

    private ComparatorFactory mComparatorFactory;

    /**
     * Current sorting column.
     */
    private int mCurSortColumn = -1;
    /**
     * Is Descending mode now?
     */
    protected boolean mDescendingMode = true;
    /**
     * Image icon for anscending mode of sorting.
     */
    protected ImageIcon mDownSortIcon;
    /**
     * ITable-based instance associated with this frame.
     */
    private TableListener tableListener;
    /**
     * Header renderer of columns of table.
     */
    protected DefaultTableCellRenderer mHeaderRenderer;
    /**
     * Localized resource manager
     */
    protected ResourceManager mResourceManager;
    /**
     * Scroll panel.
     */
    protected JScrollPane mScrollPane;

    private final Object mSDFMutex = new Object();
    /**
     * Formater for localization of date format
     */
    private SimpleDateFormat mSimpleDateFormat;
    private SimpleDateFormat mSimpleTimeFormat;
    /**
     * Column for that popup menu was opened.
     */
    protected int mSortMenuColumn = -1;
    /**
     * Popup menu for selecting of sorting mode.
     */
    private SortPopupMenu mSortPopupMenu;
    private final Object mSTFMutex = new Object();
    /**
     * Sorted table's model.
     */
    //protected SortedTableModel mSortedTableModel;
    /**
     * Table instance.
     */
    private JTable mTable;
    /**
     * Table's model.
     */
    protected PanelTableModel mTableModel;
    private int mTitleBarHeight;
    /**
     * Image icon for descending mode of sorting.
     */
    protected ImageIcon mUpSortIcon;
    private boolean mColumnsUnset = true;
		
	/**
     * Protected constructor.
     * Does next:
     * <ul>
     * <li> sets title of frame;</li>
     * <li> creates table model and creates a table;</li>
     * <li> creates ITable implementation and registers it in Table Manager;</li>
     * <li> creates Scrolling pane and adds iself to Scrolling pane;</li>
     * <li> adds iself to ResourceManager to listen the locale changing;</li>
     * <li> adds Internal Frame Listener to superclass;</li>
     * </ul>
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
    protected TablePanel(BenchFrame mainFrame, ResourceManager aMan) {
    	super(mainFrame);
        mResourceManager = aMan;
        restoreDockable = true;
        mComparatorFactory = new ComparatorFactory<E>(this);
        setTitle(getLocalizedTitle(mResourceManager));
        mTableModel = getTableModel(mResourceManager);
//        UserPreferences.getUserPreferences(getTradeDesk().getUserName()).addObserver(this);
        PropertyManager.getInstance().addObserver(this);
        PreferencesDialog.addPreferencesListener(this);
//        PreferencesManager.getPreferencesManager(getTradeDesk().getUserName()).addPreferencesListener(this);

        mTable = new BaseTable(mTableModel);
        mTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        mTable.setRowHeight(initialRowHeight);
        JTableHeader tableHeader = new JTableHeader(mTable.getColumnModel());
        tableHeader.setReorderingAllowed(false);
        mTable.setTableHeader(tableHeader);

        tableListener = new TableListener(getTableName());
        setName(getTableName());
        ListSelectionModel listSelectionModel = mTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listSelectionModel.addListSelectionListener(tableListener);

        //creates the scroll pane and add the table to it.
        mScrollPane = new JScrollPane(mTable);
        //adds the scroll pane to this panel.
        add(mScrollPane, BorderLayout.CENTER);
        mResourceManager.addLocaleListener(this);

        //initializes sort icons
        URL iconUrl = mResourceManager.getResource("ID_UP_SORT_ICON");
        if (iconUrl != null) {
            mUpSortIcon = new ImageIcon(iconUrl);
        }
        URL iconUrl2 = mResourceManager.getResource("ID_DOWN_SORT_ICON");
        if (iconUrl2 != null) {
            mDownSortIcon = new ImageIcon(iconUrl2);
        }
        mSortPopupMenu = new SortPopupMenu();
        mTable.setColumnSelectionAllowed(false);
        addMouseListenerToHeaderInTable(mTable);
        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        mSimpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = getTradeDesk().getTradingServerSession().getTimeZone();
        if (tz != null) {
            mSimpleDateFormat.setTimeZone(tz);
            mSimpleTimeFormat.setTimeZone(tz);
        }
        
        getSignalVector().subscribe(this, SignalType.ADD);
        getSignalVector().subscribe(this, SignalType.REMOVE);
        getSignalVector().subscribe(this, SignalType.CHANGE);
    }
    
    protected String getPropSheetPath () {
    	return "preferences.panels." + getTableName() + ".";
    }
    
    protected void setPreferences() {
//    	UserPreferences uiPrefs = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//        JLabel label = new JLabel();
        mColorBGSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_selected"); 
    	mColorBGEven = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_default_even");
    	mColorBGOdd = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_default_odd");
    	mColorBGMargin = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_undermargincall");
    	mColorBGClosed = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_closed");
    	mColorBGTotal = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "background_total");
    	mColorFGHeader = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_header");
    	mColorFGSelected = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_selected");
    	mColorFGDefault = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_default");
    	mColorFGMargin = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_undermargincall");
    	mColorFGClosed = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_closed");
    	mColorFGTotal = PropertyManager.getInstance().getColorVal(getPropSheetPath() + "foreground_total");
    	mFontContent = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_content");
    	mFontHeader = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_header");
    	mFontMargin = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_undermargincall");
    	mFontSelected = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_selected");
    	mFontClosed = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_closed");
    	mFontTotal = PropertyManager.getInstance().getFontVal(getPropSheetPath() + "font_total");
    }
    
    protected DefaultTableCellRenderer getDefaultCellRenderer() {
    	return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable aTable,
                                                           Object aValue,
                                                           boolean aIsSelected,
                                                           boolean aHasFocus,
                                                           int aRow,
                                                           int aColumn) {
            	SignalVector signalVector = ((PanelTableModel)aTable.getModel()).getRowData();
//            	Object valueAt = aTable.getModel().getValueAt(aRow, 0);
            	if (signalVector == null /*|| valueAt == null*/) {
            		return null;
            	}

                JLabel comp = (JLabel) super.getTableCellRendererComponent(aTable,
                                                                           aValue,
                                                                           aIsSelected,
                                                                           aHasFocus,
                                                                           aRow,
                                                                           aColumn);
                
                if (signalVector.isTotal() && aRow == signalVector.size()) {
                	if (aIsSelected) {
                		comp.setBackground(mColorBGSelected);
                		comp.setForeground(mColorFGSelected);
                        comp.setFont(mFontSelected);
                    } else {
                    	comp.setBackground(mColorBGTotal);
                    	comp.setForeground(mColorFGTotal);
                        comp.setFont(mFontTotal);
                    }
                } else {
                	comp.setFont(mFontContent);
                	comp.setForeground(mColorFGDefault);
                    if (aRow % 2 == 0) {
                    	comp.setBackground(mColorBGEven);
                    } else {
                    	comp.setBackground(mColorBGOdd);
                    }
                    if (aTable.getSelectedRow() == aRow) {
                    	comp.setBackground(mColorBGSelected);
                    	comp.setForeground(mColorFGSelected);
                        comp.setFont(mFontSelected);
                    }
                }
                
                setOpaque(true);
                comp.setHorizontalAlignment(getColumnAlignment(aColumn));
//                if (aColumn == 0) {
//                    comp.setHorizontalAlignment(SwingConstants.LEFT);
//                } else {
//                    comp.setHorizontalAlignment(SwingConstants.RIGHT);
//                }
//                int iHeight = comp.getFontMetrics(comp.getFont()).getHeight() + 2;
//                if (iHeight > initialRowHeight && aTable.getRowHeight(aRow) < iHeight) {
//                    EventQueue.invokeLater(new InvokerSetRowHeight(aTable, aRow, iHeight));
//                }
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
    
    protected DefaultTableCellRenderer getDefaultHeaderRenderer() {
    	return new DefaultTableCellRenderer() {
            //Overriding of DefaultTableCellRenderer`s method
            @Override
            public Component getTableCellRendererComponent(JTable aTable,
                                                           Object aValue,
                                                           boolean aIsSelected,
                                                           boolean aHasFocus,
                                                           int aRow,
                                                           int aColumn) {
                JLabel comp = (JLabel) super.getTableCellRendererComponent(aTable,
                                                                           aValue,
                                                                           aIsSelected,
                                                                           aHasFocus,
                                                                           aRow,
                                                                           aColumn);
                setBackground(mColorBGHeader);
                setForeground(mColorFGHeader);
                comp.setFont(mFontHeader);
                //sets opaque mode
                setOpaque(true);
                //sets border
                comp.setBorder(BorderFactory.createEtchedBorder());
                //adds icon
                if (aColumn == getCurSortColumn()) {
                    if (mDescendingMode) {
                        comp.setIcon(mUpSortIcon);
                    } else {
                        comp.setIcon(mDownSortIcon);
                    }
                } else {
                    comp.setIcon(null);
                }
                comp.setHorizontalAlignment(SwingConstants.CENTER);
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
    
    @Override
    protected void paintComponent(Graphics aGraphics) {
        if (UIManager.getInst().isAAEnabled()) {
            Graphics2D g2d = (Graphics2D) aGraphics;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        super.paintComponent(aGraphics);
    }

    /**
     * Adds mouse listener to header of table.
     */
    private void addMouseListenerToHeaderInTable(final JTable aTable) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent aEvent) {
                TableColumnModel columnModel = aTable.getColumnModel();
                int currentColumn = columnModel.getColumnIndexAtX(aEvent.getX());
//                if (getTableModel().getColumnType(currentColumn) != PanelTableModel.ColumnType.NOT_SORTABLE_COLUMN) {
                if (getColumnType(currentColumn) != FieldType.NOT_SORTABLE) {	
                    if (aEvent.getButton() == MouseEvent.BUTTON3 || aEvent.isPopupTrigger()) {
                        //sets checked item at menu
                        if (getCurSortColumn() == currentColumn) {
                            mSortPopupMenu.setSortMode(mDescendingMode
                                                       ? SortPopupMenu.DESCENT
                                                       : SortPopupMenu.ASCENT);
                        }
                        mSortMenuColumn = currentColumn;
                        mSortPopupMenu.show(aEvent.getComponent(), aEvent.getX(), aEvent.getY());
                        revalidate();
                        repaint();
                    } else {
                        if (isSortable()) {
                            if (getCurSortColumn() == currentColumn) {
                                mDescendingMode = !mDescendingMode;
                            } else {
                                mDescendingMode = true;
                            }
                            setCurSortColumn(currentColumn);
                            fireSorting();
                        }
                    }
                }
            }
        };
        aTable.getTableHeader().addMouseListener(mouseAdapter);

        aTable.getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent aEvent) {
                JTableHeader header = (JTableHeader) aEvent.getSource();
                TableColumnModel columnModel = aTable.getColumnModel();
                int colIndex = columnModel.getColumnIndexAtX(aEvent.getX());

                // Return if not clicked on any column header
                if (colIndex >= 0) {
                    TableColumn col = columnModel.getColumn(colIndex);
                    header.setToolTipText(String.valueOf(col.getIdentifier()));
                }
            }
        });

        aTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent aEvent) {
                BaseTable table = (BaseTable) aEvent.getSource();
                int row = table.rowAtPoint(aEvent.getPoint());
                int col = table.columnAtPoint(aEvent.getPoint());
                JComponent comp = (JComponent) table.getCellRenderer(row, col).getTableCellRendererComponent(aTable,
                                                                                               null,
                                                                                               false,
                                                                                               false,
                                                                                               row,
                                                                                               col);
                if (comp != null) {
                    Object valueAt = table.getValueAt(row, col);
                    if (valueAt != null) {
                        comp.setToolTipText(valueAt.toString());
                    } else {
                        comp.setToolTipText(null);
                    }
                }
            }
        });
    }

    /**
     * Fires sorting of the table.
     */
    protected void fireSorting() {
        List selected = null;
        int[] rows = mTable.getSelectedRows();
        for (int row : rows) {
            if (row < getSignalVector().size()) {
                if (selected == null) {
                    selected = new ArrayList();
                }
                selected.add(getSignalVector().get(row));
            }
        }

        sort();
        revalidate();
        repaint();

        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < selected.size(); i++) {
                int index = getSignalVector().indexOf(selected.get(i));
                if (index != -1) {
                    if (i == 0) {
                        mTable.setRowSelectionInterval(index, index);
                    } else {
                        mTable.addRowSelectionInterval(index, index);
                    }
                }
            }
        }
    }

    protected Object getColumnValue(int column, Object object) {
        BaseEntity entity = (BaseEntity)object;
        return entity.getField(column);
    }
    
    protected abstract FieldType getColumnType(int column);
    protected abstract int getColumnAlignment(int column);

    private Comparator getComparator() {
//        if (mTableModel.getColumnType(mCurSortColumn) == PanelTableModel.ColumnType.STRING_COLUMN) {
//            return mComparatorFactory.getStringComparator();
//        } else if (mTableModel.getColumnType(mCurSortColumn) == PanelTableModel.ColumnType.INT_COLUMN) {
//            return mComparatorFactory.getIntComparator();
//        } else if (mTableModel.getColumnType(mCurSortColumn) == PanelTableModel.ColumnType.DOUBLE_COLUMN) {
//            return mComparatorFactory.getDoubleComparator();
//        } else if (mTableModel.getColumnType(mCurSortColumn) == PanelTableModel.ColumnType.DATE_COLUMN) {
//            return mComparatorFactory.getDateComparator();
//        } else {
//            return mComparatorFactory.getStringComparator();
//        }
        if (getColumnType(mCurSortColumn) == FieldType.STRING) {
            return mComparatorFactory.getStringComparator();
        } else if (getColumnType(mCurSortColumn) == FieldType.INT) {
            return mComparatorFactory.getIntComparator();
        } else if (getColumnType(mCurSortColumn) == FieldType.DOUBLE) {
            return mComparatorFactory.getDoubleComparator();
        } else if (getColumnType(mCurSortColumn) == FieldType.DATE) {
            return mComparatorFactory.getDateComparator();
        } else {
            return mComparatorFactory.getStringComparator();
        }
    }

    protected Object getComparatorValue(Object aObj) {
        TableColumn column = mTable.getColumnModel().getColumn(mCurSortColumn);
        return getColumnValue(column.getModelIndex(), aObj);
    }

    public int getCurSortColumn() {
        return mCurSortColumn;
    }

    public void setCurSortColumn(int aCurSortColumn) {
        mCurSortColumn = aCurSortColumn;
    }

    /**
     * Returns itable-based instance associated with this frame.
     */
    public ITableListener getTableListener() {
        return tableListener;
    }

    /**
     * Method is called for getting Localized Frame Title.<br>
     */
    protected abstract String getLocalizedTitle(ResourceManager aResourceMan);

    /**
     * Returns localized resource manager
     */
    public ResourceManager getResourceManager() {
        return mResourceManager;
    }

    /**
     * Sets localized resource manager.
     *
     * @param aMan resource manager
     */
    public void setResourceManager(ResourceManager aMan) {
        mResourceManager = aMan;
    }

    /**
     * Returns table instance.
     */
    public JScrollPane getScrollPane() {
        return mScrollPane;
    }

    protected abstract SignalVector getSignalVector();

    /**
     * Returns table instance.
     */
    public JTable getTable() {
        return mTable;
    }

    /**
     * Returns table's model.
     */
    public abstract PanelTableModel getTableModel(ResourceManager aResourceMan);

    /**
     * Returns table's model.
     */
    public PanelTableModel getTableModel() {
        return mTableModel;
    }

    /**
     * Method is called for getting internal table name, which identifies a table
     * into the table manager.<br>
     */
    protected abstract String getTableName();

//    private void initColumnWidths(UserPreferences aPreferences) {
    private void initColumnWidths() {
        //setting of column width
//        String tableColumnWidthes = aPreferences.getString("childframe.table." + getTableName());
        String tableColumnWidthes = PropertyManager.getInstance().getStrVal(getPropSheetPath() + "col_widthes");
        if (tableColumnWidthes != null) {
            TableColumnModel columnModel = mTable.getColumnModel();
            StringTokenizer st = new StringTokenizer(tableColumnWidthes, ";", false);
            for (int i = 0; st.hasMoreTokens(); i++) {
                try {
                    int width = Integer.parseInt(st.nextToken());
                    int columnCount = columnModel.getColumnCount();
                    if (i < columnCount) {
                        TableColumn column = columnModel.getColumn(i);
                        column.setPreferredWidth(width);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isDescendingMode() {
        return mDescendingMode;
    }

    public abstract boolean isSortable();

    public void loadSettings() {
//    	UserPreferences preferences;
//        try {
//            preferences = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//        } catch (Exception e) {
//            e.printStackTrace();
//            super.loadSettings();
//            return;
//        }
        reOrderColumns();
//        initColumnWidths(preferences);
        initColumnWidths();
        super.loadSettings();
    }

    /**
     * This method is called when current locale of the aMan is changed and becomes aLocale.
     *
     * @param aMan resource manager.
     */
    public void onChangeLocale(ResourceManager aMan) {
        mResourceManager = aMan;
        synchronized (mSDFMutex) {
            mSimpleDateFormat = new SimpleDateFormat();
        }

        //sets title
        setTitle(getLocalizedTitle(aMan));
        if (mTableModel != null) {
            int iRowSaved = mTable.getSelectedRow();
            mTableModel.onChangeLocale(aMan);
            mTable.changeSelection(iRowSaved, -1, false, false);
        }

        //sets renderer
        if (mCellRenderer != null || mHeaderRenderer != null) {
            setRenderers(mCellRenderer, mHeaderRenderer);
        }
        mTable.setRowHeight(initialRowHeight);
        mTable.revalidate();
        mTable.repaint();
    }

    /**
     * Method is called by Internal Frame Listener when frame has been closed.<br>
     * The implementation should unregister itself from Signaler and do other destruction work
     *
     * @param aE
     */
    @Override
    public void onClose() {
        mTable.removeAll();
        mTable.removeNotify();
        mResourceManager.removeLocaleListener(this);
        mCheckBoxMap.clear();
        tableListener.clear();
        mScrollPane.removeAll();
        mScrollPane.removeNotify();
        removeAll();
        removeNotify();
//        UserPreferences.getUserPreferences(getTradeDesk().getUserName()).deleteObserver(this);
        PropertyManager.getInstance().deleteObserver(this);
//        PreferencesManager.getPreferencesManager(getTradeDesk().getUserName()).removePreferencesListener(this);
        PreferencesDialog.removePreferencesListener(this);
        
        getSignalVector().unsubscribe(this, SignalType.ADD);
    	getSignalVector().unsubscribe(this, SignalType.REMOVE);
    	getSignalVector().unsubscribe(this, SignalType.CHANGE);
    }

    public void onSignal(Signaler src, Signal aSignal) {
        mTableModel.onSignal(src, aSignal);
        if (mCurSortColumn != -1 && aSignal.getType() == SignalType.CHANGE) {
            Comparator comparator = getComparator();
            if (comparator != null) {
                int aIndex = aSignal.getIndex();
                SignalVector data = getSignalVector();
                Object updated = data.get(aIndex);

                Object before = aIndex <= 0 && aIndex <= data.size() - 1 ? null : data.get(aIndex - 1);
                boolean changed = false;
                if (before != null) { //1st level test
                    changed = comparator.compare(before, updated) > 0;
                }

                if (!changed) { //2nd level test
                    Object after = aIndex >= data.size() - 1 ? null : data.get(aIndex + 1);
                    if (after != null) {
                        changed = comparator.compare(after, updated) < 0;
                    }
                }

                if (changed) {
                    fireSorting();
                }
            }
        } else if (aSignal.getType() == SignalType.REMOVE || aSignal.getType() == SignalType.ADD) {
            List selected = null;
            int[] selectedRows = mTable.getSelectedRows();
            for (int row : selectedRows) {
                if (row < getSignalVector().size()) {
                    if (selected == null) {
                        selected = new ArrayList();
                    }
                    selected.add(getSignalVector().get(row));
                }
            }
            if (selected != null && !selected.isEmpty()) {
                for (int i = 0; i < selected.size(); i++) {
                    int index = getSignalVector().indexOf(selected.get(i));
                    if (index != -1) {
                        // old index selected row value before insert into bottom or top of table
                        if (index == 0) {
                            if (mDescendingMode) {
                                index++;
                            } else {
                                index--;
                            }
                        }
                        if (i == 0) {
                            mTable.setRowSelectionInterval(index, index);
                        } else {
                            mTable.addRowSelectionInterval(index, index);
                        }
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Invoked on updating of preferences.
     *
     * @param aChangings vector that containts changed values
     */
    public void propertyUpdated(List<PropertySheet> aChangings) {
        //This code was added for correct appling of header`s height.
        mTable.setRowHeight(initialRowHeight);
        mTable.revalidate();
        mTable.repaint();
        Rectangle rec = getBounds();
        reshape(rec.x, rec.y, rec.width + 1, rec.height);
        reshape(rec.x, rec.y, rec.width, rec.height);
    }

    protected void reOrderColumns() {
        TreeMap<Integer, TableColumn> map = new TreeMap<Integer, TableColumn>();
        TableColumnModel columnModel = mTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn tableColumn = columnModel.getColumn(i);
            map.put(tableColumn.getModelIndex(), tableColumn);
        }
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn tableColumn = columnModel.getColumn(i);
            mTable.removeColumn(tableColumn);
        }
        for (TableColumn tableColumn : map.values()) {
            mTable.removeColumn(tableColumn);
            mTable.addColumn(tableColumn);
        }
    }

    /* Saves settings to the use preferences. */
    @Override
    public void saveSettings() {
        //calls method of the base class
        super.saveSettings();
        //Get PersistenceStorage
//        UserPreferences preferences;
//        try {
//            preferences = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
        TableColumnModel columnModel = mTable.getColumnModel();
        //saving of column width
        Enumeration enumeration = columnModel.getColumns();
        String storageStr = "";
        for (int i = 0; enumeration.hasMoreElements(); i++) {
            TableColumn tc = (TableColumn) enumeration.nextElement();
            if (i != 0) {
                storageStr += ";";
            }
            storageStr += tc.getPreferredWidth();
        }
//        preferences.set("childframe.table." + getTableName(), storageStr);
        PropertyManager.getInstance().setProperty(getPropSheetPath() + "col_widthes", storageStr);

        //saving of column order
        Enumeration enumeration1 = columnModel.getColumns();
        String storageStr1 = "";
        for (int i = 0; enumeration1.hasMoreElements(); i++) {
            TableColumn tc = (TableColumn) enumeration1.nextElement();
            if (i != 0) {
                storageStr1 += ";";
            }
            storageStr1 += tc.getModelIndex();
        }
//        preferences.set("childframe.table." + getTableName() + ".order", storageStr1);
        PropertyManager.getInstance().setProperty(getPropSheetPath() + "order", storageStr);
    }

    /**
     * Sets renderers to all columns.
     */
    protected void setRenderers(DefaultTableCellRenderer aCellRender, DefaultTableCellRenderer aHeaderRender) {
        JTable table = mTable;
        TableColumnModel columnModel = table.getColumnModel();
        for (Enumeration enumeration = columnModel.getColumns(); enumeration.hasMoreElements();) {
            TableColumn column = (TableColumn) enumeration.nextElement();
            if (aCellRender != null) {
                column.setCellRenderer(aCellRender);
            }
            if (aHeaderRender != null) {
                column.setHeaderRenderer(aHeaderRender);
            }
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
//        if (aFlag && mColumnsUnset) {
//            mColumnsUnset = false;
//            UserPreferences prefs = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//            int columnCount = mTable.getColumnModel().getColumnCount();
//            List<TableColumn> removals = new ArrayList<TableColumn>();
//            for (int i = 0; i < columnCount; i++) {
//                TableColumn tableColumn = mTable.getColumnModel().getColumn(i);
//                boolean show = true;
//                String key = "childframe.table." + getTableName() + ".col." + i;
//                if (prefs.getString(key) != null) {
//                    show = prefs.getBoolean(key);
//                }
//                if (!show) {
//                    removals.add(tableColumn);
//                }
//            }
//            TableColumn tc = null;
//            if (mCurSortColumn != -1) {
//                tc = mTable.getColumnModel().getColumn(mCurSortColumn);
//            }
//            for (TableColumn column : removals) {
//                mTable.removeColumn(column);
//            }
//            //reset the sort column if count changed
//            if (tc != null) {
//                try {
//                    mCurSortColumn = mTable.getColumnModel().getColumnIndex(tc.getIdentifier());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            revalidate();
//            repaint();
//        }
    }

    protected void sort() {
        if (isSortable()) {
            getSignalVector().setComparator(getComparator());
        }
    }

    /**
     * @param aObservable
     * @param arg
     */
    public void update(Observable aObservable, Object arg) {
//        if (arg.toString().endsWith(IClickModel.TRADING_MODE)) {
//            updateTitle();
//        }
    }

    public void updateTitle() {
        setTitle(getLocalizedTitle(mResourceManager));
    }

    /**
     * Popup menu for selecting of type of sorting of column.
     * This popup menu appears by right boutton of mouse on column header.
     */
    private class SortPopupMenu extends JPopupMenu {
        /**
         * Ascent sorting.
         */
        private static final int ASCENT = 1;
        /**
         * Descent sorting.
         */
        private static final int DESCENT = 2;
        /**
         * Ascend mode menu item.
         */
        private JCheckBoxMenuItem mAscentItem;
        /**
         * Descend mode menu item.
         */
        private JCheckBoxMenuItem mDescentItem;

        /**
         * Constructor.
         */
        SortPopupMenu() {
            UIManager oMan = UIManager.getInst();
            if (isSortable()) {
                //first menu item
                mAscentItem = oMan.createCheckBoxMenuItem("IDS_ASCEND_MODE", null, null, "IDS_ASCEND_MODE_DESC");
                mAscentItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent aE) {
                        setCurSortColumn(mSortMenuColumn);
                        mDescendingMode = false;
                        fireSorting();
                        getTable().getTableHeader().revalidate();
                        getTable().getTableHeader().repaint();
                    }
                });
                add(mAscentItem);
            }

            if (isSortable()) {
                //second menu item
                mDescentItem = oMan.createCheckBoxMenuItem("IDS_DESCEND_MODE", null, null, "IDS_DESCEND_MODE_DESC");
                mDescentItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent aE) {
                        setCurSortColumn(mSortMenuColumn);
                        mDescendingMode = true;
                        fireSorting();
                        getTable().getTableHeader().revalidate();
                        getTable().getTableHeader().repaint();
                    }
                });
                add(mDescentItem);
            }

            add(UIManager.getInst().createMenuItem(new AbstractAction("Reset Columns") {
                public void actionPerformed(ActionEvent aEvent) {
                    UIManager.getInst().packColumns(mTable, 10);
                }
            }));
            addSeparator();
//            final UserPreferences prefs = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//            int columnCount = mTable.getColumnCount();
//            for (int i = 0; i < columnCount; i++) {
//                final String name = mTable.getColumnName(i);
//                final TableColumn tableColumn = mTable.getColumn(name);
//                final String key = "childframe.table." + getTableName() + ".col." + i;
//                Action action = new AbstractAction(name) {
//                    public void actionPerformed(ActionEvent aEvent) {
//                        JCheckBoxMenuItem source = (JCheckBoxMenuItem) aEvent.getSource();
//                        TableColumn sortedTC  = null;
//                        if (source.isSelected()) {
//                            mTable.addColumn(tableColumn);
//                            reOrderColumns();
//                            if (mTable.getColumnCount() == 2) {
//                                mCheckBoxMap.get(mTable.getColumnName(0)).setEnabled(true);
//                                mCheckBoxMap.get(mTable.getColumnName(1)).setEnabled(true);
//                            }
//                        } else {
//                            if (isSortable()) {
//                                sortedTC = mTable.getColumnModel().getColumn(getCurSortColumn());
//                            }
//                            mTable.removeColumn(tableColumn);
//                            if (mTable.getColumnCount() == 1) {
//                                mCheckBoxMap.get(mTable.getColumnName(0)).setEnabled(false);
//                            }
//                        }
//
//                        prefs.set(key, source.isSelected());
//                        if (sortedTC != null) {
//                            try {
//                                setCurSortColumn(mTable.getColumnModel().getColumnIndex(sortedTC.getIdentifier()));
//                            } catch (Exception e) {
//                                setCurSortColumn(0);
//                            }
//                            fireSorting();
//                        }
//                    }
//                };
//                JCheckBoxMenuItem item = new JCheckBoxMenuItem(action) {
//                    @Override
//                    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
//                        return new WeakActionPropertyChangeListener(this, a);
//                    }
//
//                    @Override
//                    public void addPropertyChangeListener(PropertyChangeListener aListener) {
//                        super.addPropertyChangeListener(new WeakPropertyChangeListener(aListener, this));
//                    }
//                };
//                mCheckBoxMap.put(name, item);
//                boolean show = true;
//                if (prefs.getString(key) != null) {
//                    show = prefs.getBoolean(key);
//                }
//                item.setSelected(show);
//                add(item);
//            }
        }

        @Override
        protected JMenuItem createActionComponent(Action aAction) {
            JMenuItem mi = UIManager.getInst().createMenuItem();
            mi.setText((String) aAction.getValue(Action.NAME));
            mi.setIcon((Icon) aAction.getValue(Action.SMALL_ICON));
            mi.setHorizontalTextPosition(JButton.TRAILING);
            mi.setVerticalTextPosition(JButton.CENTER);
            mi.setEnabled(aAction.isEnabled());
            return mi;
        }

        /**
         * Sets sort mode.
         *
         * @param aMode mode of sorting
         */
        public void setSortMode(int aMode) {
            if (aMode == ASCENT) {
                mAscentItem.setState(true);
                mDescentItem.setState(false);
            } else if (aMode == DESCENT) {
                mAscentItem.setState(false);
                mDescentItem.setState(true);
            }
        }
    }

	private class ComparatorFactory<E> {
	    private Comparator mDateComparator;
	    private Comparator mDoubleComparator;
	    private Comparator mIntComparator;
	    private Comparator mStringComparator;
	    private TablePanel tablePanel;

	    public ComparatorFactory(final TablePanel tablePanel) {
	        this.tablePanel = tablePanel;
	        mStringComparator = new Comparator<E>() {
	            public boolean isDescendingMode() {
	                return tablePanel.isDescendingMode();
	            }

	            public Object getValue(E aElement) {
	                return tablePanel.getComparatorValue(aElement);
	            }
	            
	            public int compare(E aObject1, E aObject2) {
	                Object value1 = getValue(aObject1);
	                Object value2 = getValue(aObject2);
	                int mode = isDescendingMode() ? -1 : 1;
	                if (value1 instanceof String && value2 instanceof String) {
	                    if (tablePanel.getResourceManager().getString("IDS_SUMMARY_TOTAL").equals(value1)) {
	                        return 1;
	                    } else if (tablePanel.getResourceManager().getString("IDS_SUMMARY_TOTAL").equals(value2)) {
	                        return 0;
	                    } else {
	                        return value1.toString().compareTo(value2.toString()) * mode;
	                    }
	                } else {
	                    return 0;
	                }
	            }
	        };

	        mIntComparator = new Comparator<E>() {
	            public boolean isDescendingMode() {
	                return tablePanel.isDescendingMode();
	            }

	            public Object getValue(E aElement) {
	                return tablePanel.getComparatorValue(aElement);
	            }
	            
	            public int compare(E aObject1, E aObject2) {
	                Object value1 = getValue(aObject1);
	                Object value2 = getValue(aObject2);
	                if (value1 instanceof String && value2 instanceof String) {
	                    int mode = isDescendingMode() ? -1 : 1;
	                    if ("".equals(value1)) {
	                        value1 = "0";
	                    }
	                    if ("".equals(value2)) {
	                        value2 = "0";
	                    }
	                    Integer i1 = Integer.parseInt(value1.toString());
	                    Integer i2 = Integer.parseInt(value2.toString());
	                    return i1.compareTo(i2) * mode;
	                } else if (value1 instanceof Integer && value2 instanceof Integer) {
	                    Integer i1 = (Integer) value1;
	                    Integer i2 = (Integer) value2;
	                    return i1.compareTo(i2);
	                } else {
	                    return 0;
	                }
	            }
	        };

	        mDoubleComparator = new Comparator<E>() {
	            public boolean isDescendingMode() {
	                return tablePanel.isDescendingMode();
	            }

	            public Object getValue(E aElement) {
	                return tablePanel.getComparatorValue(aElement);
	            }
	            
	            public int compare(E aObject1, E aObject2) {
	                Object value1 = getValue(aObject1);
	                Object value2 = getValue(aObject2);
	                if (value1 instanceof String && value2 instanceof String) {
	                    int mode = isDescendingMode() ? -1 : 1;
	                    if ("".equals(value1)) {
	                        value1 = "0";
	                    }
	                    if ("".equals(value2)) {
	                        value2 = "0";
	                    }
	                    double d1 = Double.parseDouble(value1.toString());
	                    double d2 = Double.parseDouble(value2.toString());
	                    return Double.compare(d1, d2) * mode;
	                } else if (value1 instanceof Double && value2 instanceof Double) {
	                    Double d1 = (Double) value1;
	                    Double d2 = (Double) value2;
	                    return d1.compareTo(d2);
	                } else {
	                    return 0;
	                }
	            }
	        };

	        mDateComparator = new Comparator<E>() {
	            public boolean isDescendingMode() {
	                return tablePanel.isDescendingMode();
	            }

	            public Object getValue(E aElement) {
	                return tablePanel.getComparatorValue(aElement);
	            }
	            
	            public int compare(E aObject1, E aObject2) {
	                Object value1 = getValue(aObject1);
	                Object value2 = getValue(aObject2);
	                int mode = isDescendingMode() ? -1 : 1;
	                if (value1 instanceof String && value2 instanceof String) {
	                    return value1.toString().compareTo(value2.toString()) * mode;
	                } else if (value1 instanceof Date && value2 instanceof Date) {
	                    return ((Date) value1).compareTo((Date) value2) * mode;
	                } else {
	                    return 0;
	                }
	            }
	        };
	    }

	    public Comparator getDateComparator() {
	        return mDateComparator;
	    }

	    public Comparator getDoubleComparator() {
	        return mDoubleComparator;
	    }

	    public Comparator getIntComparator() {
	        return mIntComparator;
	    }

	    public Comparator getStringComparator() {
	        return mStringComparator;
	    }
	}
	
	/**
     * An inner class which implements ITable interface for TableManager.<br>
     * Notifies the TableManager about selection changing<br>
     */
    private class TableListener implements ITableListener, ListSelectionListener {
    	/**
         * Vector with selection listeners.
         */
        private Vector mSelectionListeners = new Vector();
        /**
         * Name of the table.
         */
        private String msName;
        
        /**
         * Constructor is protected and takes name of the table.
         */
    	TableListener(String aName) {
    		msName = aName;
        }

    	/**
         * Adds selection listener.
         * It will be notified when selection in the table is changed.
         *
         * @param aListener selection listener
         */
        public void addSelectionListener(ITableSelectionListener aListener) {
            if (aListener != null) {
                mSelectionListeners.add(aListener);
            }
        }
        
        /**
         * Notifies all listeners about changing of selection.
         *
         * @param aRow number of row
         */
        protected void fireChangeSelection(int[] aRow) {
            for (Object selectionListener : mSelectionListeners) {
                ITableSelectionListener tsl = (ITableSelectionListener) selectionListener;
                try {
                    tsl.onTableChangeSelection(this, aRow);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void clear() {
            mSelectionListeners.clear();
        }

        /**
         * Returns name of table.
         */
        public String getName() {
            return msName;
        }

        /**
         * Removes selection listener.
         *
         * @param aListener selection listener
         */
        public void removeSelectionListener(ITableSelectionListener aListener) {
            mSelectionListeners.remove(aListener);
        }
        /**
         * Returns selected (current) row.
         */
        public int getSelectedRow() {
            return getTable().getSelectedRow();
        }

        public void setSelectedRow(int aRow) {
            fireChangeSelection(new int[]{aRow});
        }

        /**
         * Method notifies the TableManager about selection changing.
         */
        public void valueChanged(ListSelectionEvent aEvent) {
            if (!aEvent.getValueIsAdjusting()) {
                fireChangeSelection(getTable().getSelectedRows());
            }
        }
    }
}
