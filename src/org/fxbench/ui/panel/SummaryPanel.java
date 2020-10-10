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
 * 12/8/2004    Andre Mermegas  -- updated to be in units of 100K like classic TS not lots
 * 12/9/2004    Andre Mermegas  -- updated to show in sizes same as classic TS
 * 07/05/2006   Andre Mermegas: update to allow for totals row not be sorted, also to show values for avg buy/avg sell even if 0.0
 * 03/30/2007   Andre Mermeags: update frame title to use price formatter
 */
package org.fxbench.ui.panel;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.fxbench.desk.Summaries;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TSummary.FieldDef;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;

import java.net.URL;
import java.util.List;
import java.util.TimeZone;

/**
 * This frame shows summary info for all open positions of the user (for all accounts).<br>
 * The name of the frame and table associated with the frame is Summary.<br>
 * The table shows contents of the Summaries table.<br>
 * Content pane of the frame consists of the JTable with following  columns.<br>
 * <br>
 * Creation date (9/20/2003 6:38 PM)
 */
public class SummaryPanel<E> extends TablePanel<E>
{
    public static final String NAME = "Summary";
	public static final String TITLE = "Summary";
	public static final String TOOLTIP = "Summary";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.SYMBOL, FieldDef.SUMMARY_PNL_SELL, FieldDef.SUMMARY_AMOUNT_SELL,
		FieldDef.SUMMARY_AVG_SELL, FieldDef.SUMMARY_PNL_BUY, FieldDef.SUMMARY_AMOUNT_BUY, 
		FieldDef.SUMMARY_AVG_BUY, FieldDef.SUMMARY_SELL_RATE, FieldDef.SUMMARY_BUY_RATE,
		FieldDef.SUMMARY_POSITIONS_COUNT, FieldDef.SUMMARY_AMOUNT, FieldDef.SUMMARY_GROSS_PL,
		FieldDef.SUMMARY_NET_PL};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);
	
    /**
     * Constructor.
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
    public SummaryPanel(BenchFrame mainFrame, ResourceManager aMan) {
    	super(mainFrame, aMan);

		//sets icon to internal frame
		URL iconUrl = getResourceManager().getResource("ID_SUMMARY_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }
        
        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);
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
     * Gets Localized Title of Frame.
     * An implementation of abstract method of fxts.stations.trader.ui.frames.ATableFrame class
     *
     * @param aResourceMan Current Resource manager that is used for localistion
     *
     * @return localized Title
     */
    @Override
    protected String getLocalizedTitle(ResourceManager aResourceMan) {
        if (aResourceMan != null) {
//            UserPreferences pref = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//            String mode = pref.getString(IClickModel.TRADING_MODE);
            Summaries summaries = (Summaries)getSignalVector();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_SUMMARYS_TITLE"));
            if (!summaries.isEmpty()) {
            	TimeZone tz = TimeZone.getTimeZone(getTradeDesk().getTradingServerSession().getParameterValue("BASE_TIME_ZONE"));
                titleBuffer.append(" (").append(Utils.format(tz, summaries.getNetTotalPnL())).append(")");
            }
//            if (IClickModel.SINGLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ ONE CLICK TRADING ~~~~~~");
//            } else if (IClickModel.DOUBLE_CLICK.equals(mode)) {
//                titleBuffer.append(" ~~~~~~ DOUBLE CLICK TRADING ~~~~~~");
//            }
            return titleBuffer.toString();
        }
        mLogger.debug("Error: SummaryFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getSummaries();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan Current Resource manager that is used for localistion
     *
     * @return table model
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new SummariesTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Gets name of table .
     * An implementation of abstract method of fxts.stations.trader.ui.frames.ATableFrame class
     */
    @Override
    protected String getTableName() {
        return "summary";
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    /**
     * Invoked when user attempts to close window
     */
    @Override
    public void onClose() {
    	super.onClose();
    }


    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class SummariesTableModel extends PanelTableModel {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
        SummariesTableModel(ResourceManager aResourceMan) {
            super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }
}
