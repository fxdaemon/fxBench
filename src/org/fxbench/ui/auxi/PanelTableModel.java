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
 * 10/10/2006   Andre Mermegas: update frame fix
 *
 */
package org.fxbench.ui.auxi;


import java.util.TimeZone;

import javax.swing.table.AbstractTableModel;

import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.Field;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;
import org.fxbench.util.signal.SignalVector;
import org.fxbench.util.signal.Signaler;

/**
 * Abstract class for simplifying writing an inner &lt;&lt;ViewTableModel&gt;&gt; classes<br>
 * Generaly an implementation of ATableFrame need to implements next methods only:
 * <ol>
 * <li>constructor, which should call the superclass constructor</li>
 * <li>getRowCount</li>
 * <li>getValueAt</li>
 * </ol>
 * See the
 * <code>ATableFrame</code> for how to simplify &lt;&lt;ViewFrame&gt;&gt; implementation
 *
 * @Creation date (9/22/2003 12:55 PM)
 */
public abstract class PanelTableModel extends AbstractTableModel implements ISignalListener
{
    /**
     * Array of descriptions of columns.
     */
    private String[] mColDescriptors;
    /**
     * Array of names of columns.
     */
    private String[] mColumnNames;
    
    private ResourceManager mResourceManager;
    private SignalVector rowData = null;

    /**
     * Protected constructor.
     *
     * @param aResourceMan
     * @param aColDescriptors Two directional array, its each member repersents
     * the array of two String. The first from them is ID of resource, the second
     * one is default value
     */
    protected PanelTableModel(ResourceManager aResourceMan, String[] aColDescriptors, SignalVector signalVector) {
        mColDescriptors = aColDescriptors.clone();
        mColumnNames = new String[aColDescriptors.length];
        setColunmNames(aResourceMan, aColDescriptors);
        mResourceManager = aResourceMan;
        rowData = signalVector;
    }
    
    /**
     * sets the column name using Column Descriptors biderectional array and Resource Manager
     */
    private void setColunmNames(ResourceManager aResourceMan, String[] aColDescriptors) {
        if (aResourceMan != null) {
            for (int i = 0; i < aColDescriptors.length; i++) {
                mColumnNames[i] = aResourceMan.getString(aColDescriptors[i]);
            }
        }
    }

    /**
     * Returns columns count as it is defined by JTableModel interface
     */
    public int getColumnCount() {
        return mColDescriptors.length;
    }

    /**
     * Returns column name value as it is defined by JTableModel interface
     */
    public String getColumnName(int aiCol) {
        return mColumnNames[aiCol];
    }

    /**
	 * @return the rowData
	 */
	public SignalVector getRowData() {
		return rowData;
	}

	/**
	 * @param rowData the rowData to set
	 */
	public void setRowData(SignalVector rowData) {
		this.rowData = rowData;
	}

	/**
     * Returns the number of rows in the model.
     */
    public int getRowCount() {
    	if (rowData == null) {
    		return 0;
    	} else {
    		if (rowData.isTotal()) {
    			return rowData.size() + 1;
    		} else {
    			return rowData.size();
    		}
    	}
    }
    
    /**
     * Returns the value in the cell at aiCol and aiRow to aValue.
     *
     * @param aRow number of row
     * @param aCol number of column
     */
    public Object getValueAt(int row, int col) {
    	if (rowData == null) {
    		return null;
    	} else {
    		if (rowData.isTotal() && row == rowData.size()) {
				if (col == 0) {
					return mResourceManager.getString("IDS_SUMMARY_TOTAL");
				} else {
					Field totalField = rowData.getTotal(col);
					if (totalField == null) {
						return null;
					} else {
						return totalField.getFormatText();
					}
				}
    		} else {
    			Field field = rowData.get(row).getField(col);
    			return field.getFormatText();
    		}
    	}
    }
    
    /**
     * This method is called when current locale of the aMan is changed and becomes aLocale.
     *
     * @param aResourceMan resource manager.
     */
    public void onChangeLocale(ResourceManager aResourceMan) {
        //refreshes colunm names of table
        setColunmNames(aResourceMan, mColDescriptors);
        fireTableStructureChanged();
    }

    /**
     * This method is called when signal is fired.
     *
     * @param aSrc source of the signal
     * @param aSignal signal
     */
    public void onSignal(Signaler src, Signal signal) {
        if (signal == null) {
            return;
        }
        if (signal.getType() == SignalType.CHANGE) {
            fireTableRowsUpdated(signal.getIndex(), signal.getIndex());
        } else if (signal.getType() == SignalType.ADD) {
            fireTableRowsInserted(signal.getIndex(), signal.getIndex());
        } else if (signal.getType() == SignalType.REMOVE) {
            fireTableRowsDeleted(signal.getIndex(), signal.getIndex());
        }
    }
}
