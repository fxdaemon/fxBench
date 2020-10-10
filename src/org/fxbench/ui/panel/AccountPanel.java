package org.fxbench.ui.panel;

import java.net.URL;
import java.util.List;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.fxbench.desk.Accounts;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TAccount.FieldDef;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.ITableListener;
import org.fxbench.ui.auxi.ITableSelectionListener;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.Utils;


/**
 * A list with the names of Roman gods.
 * 
 * @author Heidi Rakels.
 */
public class AccountPanel<E> extends TablePanel<E> implements ITableSelectionListener
{
	public static final String NAME = "Accounts";
	public static final String TITLE = "Account";
	public static final String TOOLTIP = "Account";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.ACCOUNT_NAME, FieldDef.ACCOUNT_BALANCE, FieldDef.ACCOUNT_EQUITY,
		FieldDef.ACCOUNT_USED_MARGIN, FieldDef.ACCOUNT_USABLE_MARGIN, FieldDef.ACCOUNT_GROSSPL, 
		FieldDef.ACCOUNT_MARGIN_CALL, FieldDef.ACCOUNT_HEDGING};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);
	
    private TAccount mSelectedAccount;

	// Constructors.
	public AccountPanel(BenchFrame mainFrame, ResourceManager aMan) {	
		super(mainFrame, aMan);
        setCurSortColumn(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_NAME));
        
		//sets icon to internal frame
        URL iconUrl = getResourceManager().getResource("ID_ACCOUNTS_FRAME_ICON");
        if (iconUrl != null) {
        //	barIcon = new ImageIcon(getClass().getResource("/org/fxbench/ui/docking/resources/icons/account.png"));
        	barIcon = new ImageIcon(iconUrl);
        }
        
        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);
                
        getTableListener().addSelectionListener(this);
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
            titleBuffer.append(aResourceMan.getString("IDS_ACCOUNTS_TITLE"));
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
        mLogger.debug("AccountsFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    public TAccount getSelectedAccount() {
        if (mSelectedAccount == null) {
            return null;
        } else {
            return mSelectedAccount;
        }
    }
    
    public static TAccount newAccount() {
        return new TAccount(fieldDefStub);
    }
    
    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getAccounts();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new AccountsTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Returns table name.
     */
    @Override
    protected String getTableName() {
        return "account";
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
        getTableListener().removeSelectionListener(this);
        mCheckBoxMap.clear();
    }
    
    public void onTableChangeSelection(ITableListener aTable, int[] aiRow) {
        try {
            Accounts accounts = (Accounts)getSignalVector();
            if (!accounts.isEmpty()) {
                mSelectedAccount = (TAccount) accounts.get(aiRow[0]);
                double contractSize = mSelectedAccount.getBaseUnitSize();
                TimeZone tz = TimeZone.getTimeZone(getTradeDesk().getTradingServerSession().getParameterValue("BASE_TIME_ZONE"));
                mLogger.debug(mSelectedAccount.getAccountName() + " unitsize = " + Utils.format(tz, contractSize));
                //pipcost changes if unitsize changes
            //    TradeDesk.updatePipCosts();
            }
        } catch (Exception e) {
            mSelectedAccount = null;
            //swallow
        }
    }
        
    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class AccountsTableModel extends PanelTableModel
    {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
        AccountsTableModel(ResourceManager aResourceMan) {
            super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }

	
}
