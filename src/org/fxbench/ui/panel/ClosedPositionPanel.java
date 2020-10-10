/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/frames/ClosedPositionsFrame.java#1 $
 *
 * Copyright (c) 2009 FXCM, LLC.
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
 * 07/05/2006   Andre Mermegas: update to allow for totals row not be sorted
 * 06/1/2007   Andre Mermegas: default sort by first column, newest on top
 */
package org.fxbench.ui.panel;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TPosition.FieldDef;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;

import java.net.URL;
import java.util.List;

/**
 * Frames is destined for showing of table with open positions.
 * <br>
 * Creation date (9/20/2003 6:38 PM)
 */
public class ClosedPositionPanel<E> extends TablePanel<E>
{
    public static final String NAME = "ClosedPositions";
	public static final String TITLE = "ClosedPosition";
	public static final String TOOLTIP = "ClosedPosition";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.TRADE_ID, FieldDef.ACCOUNT_ID, FieldDef.SYMBOL,
		FieldDef.TRADE_AMOUNT, FieldDef.TRADE_BS, FieldDef.TRADE_OPEN, 
		FieldDef.TRADE_CLOSE, FieldDef.TRADE_PL, FieldDef.TRADE_GROSS_PL,
		FieldDef.TRADE_COM, FieldDef.TRADE_INTEREST, FieldDef.TRADE_NET_PL, 
		FieldDef.TRADE_OPEN_TIME, FieldDef.TRADE_CLOSE_TIME/*, FieldDef.TRADE_CQ_TXT,
		FieldDef.TRADE_OQ_TXT*/};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);

    /**
     * Constructor.
     *
     * @param aMan resource manager
     * @param aMainFrame main frame
     */
    public ClosedPositionPanel(BenchFrame mainFrame, ResourceManager aMan) {
        super(mainFrame, aMan);
        setCurSortColumn(fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE_TIME));
        
    	//sets icon to internal frame
        URL iconUrl = getResourceManager().getResource("ID_OPENPOSITIONS_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }
        
        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);
        
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
            int positionSize = getSignalVector().size();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_CLOSED_POSITIONS_TITLE"));
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
        mLogger.debug("ClosedPositionsFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getClosedPositions();
    }

    /**
     * Returns table's model.
     *
     * @param aResourceMan current resource manager
     */
    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new ClosedPostionsTableModel(aResourceMan);
        }
        return mTableModel;
    }

    /**
     * Returns table name.
     */
    @Override
    protected String getTableName() {
        return "closedposition";
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
    }

    /**
     * Concrete implementation of AFrameTableModel.
     * This class responds for data filling of table.
     */
    private class ClosedPostionsTableModel extends PanelTableModel {
        /**
         * Constructor.
         *
         * @param aResourceMan current resource manager
         */
        ClosedPostionsTableModel(ResourceManager aResourceMan) {
            super(aResourceMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }
}
