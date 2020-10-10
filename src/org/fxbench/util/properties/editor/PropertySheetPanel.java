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
 */
package org.fxbench.util.properties.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.ui.auxi.BaseTable;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ILocaleListener;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.Property;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

public class PropertySheetPanel extends JScrollPane implements ILocaleListener
{
	private final Log mLogger = LogFactory.getLog(PropertySheetPanel.class);
    private static int cInitialHeight = UIManager.getInst()
            .createLabel()
            .getFontMetrics(new Font("dialog", 0, 12))
            .getHeight() + 2;
    private int cDirtyCount;

    private PropertyTableCellEditor mEditor;
    private JTable mTable;
    private PrefTableModel mTableModel;
    private PropertyMainPanel propertyMainPanel;
    private PropertySheetNode propSheetNode;
    private PropertySheet editPropSheet;

    /**
     * Constructor PreferencesSheetPanel.
     *
     * @param aUserName parent dialog included this panel.
     */
    public PropertySheetPanel(PropertyMainPanel mainPanle) {
    	propertyMainPanel = mainPanle;
    	
        //Define table
        mTableModel = new PrefTableModel();
        mTable = new BaseTable(mTableModel);
        mTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        PropertyTableCellRenderer renderer = new PropertyTableCellRenderer();
        mTable.setDefaultRenderer(AbstractEditorPanel.class, renderer);
        mEditor = new PropertyTableCellEditor();
        mTable.setDefaultEditor(AbstractEditorPanel.class, mEditor);
        //Do not change columns order
        mTable.getTableHeader().setReorderingAllowed(false);
        //Assign exterior a table
        mTable.setBorder(new EtchedBorder());
        super.setViewportView(mTable);
    }

    public ResourceManager getResourceManager() {
    	return propertyMainPanel.getPropertyDialog().getResourceManager();
    }
    
    public PropertyDialog getPropertyDialog() {
    	return propertyMainPanel.getPropertyDialog();
    }
    
    /**
     * @return stop editing
     */
    public boolean allowStopEditing() {
        return !mEditor.isEditing() || mEditor.stopCellEditing();
    }

    /**
     * cancel editing
     */
    public void cancelEditing() {
        if (mEditor.isEditing()) {
            mEditor.cancelCellEditing();
        }
    }

    /**
     * @return sheet
     */
    public PropertySheetNode getSheetNode() {
        return propSheetNode;
    }

    /**
     * @return table
     */
    public JTable getTable() {
        return mTable;
    }

    /**
     * init table
     */
    public void initTable() {
        int width = -1;
        TableColumnModel columnModel = mTable.getColumnModel();
        if (columnModel.getColumnCount() > 1) {
            width = columnModel.getColumn(0).getPreferredWidth();
        }
        mTableModel.init();
        mTableModel.fireTableStructureChanged();
        if (width >= 0) {
            columnModel = mTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(width);
        }
    }

    /**
     * @return dirty status
     */
    public boolean isDirty() {
        return cDirtyCount > 0;
    }

    /**
     * onChangeLocale
     * Called when current locale is changed.
     *
     * @param aMan resource manager.
     */
    public void onChangeLocale(ResourceManager aMan) {
        mTableModel.fireTableStructureChanged();
    }

    /**
     * @param aPrefProperty pref property
     * @param aDirtyFlag dirtyflag
     */
    public void setPropertyDirty(Property aPrefProperty, boolean aDirtyFlag) {
        if (aDirtyFlag) {
            if (!aPrefProperty.isChanged()) {
                cDirtyCount++;
                aPrefProperty.setChanged(true);
            }
        } else {
            if (aPrefProperty.isChanged()) {
                if (isDirty()) {
                    cDirtyCount--;
                }
                aPrefProperty.setChanged(false);
            }
        }
    }

    /**
     * setSheet.
     *
     * @param aSheet set sheet of data for this panel and displayed it.
     */
    public void setSheetNode(PropertySheetNode sheetNode) {
    	propSheetNode = sheetNode;
    	editPropSheet = new PropertySheet();
    	if (sheetNode.getEditPropSheet() != null) {
	    	for (Property property : sheetNode.getEditPropSheet().getPropertyList()) {
	    		if (property.isVisible()) {
	    			editPropSheet.addProperty(property);
	    		}
	    	}
    	}
        initTable();
    }
    
    public void refresh() {
    	editPropSheet = new PropertySheet();
    	if (propSheetNode.getEditPropSheet() != null) {
	    	for (Property property : propSheetNode.getEditPropSheet().getPropertyList()) {
	    		if (property.isVisible()) {
	    			editPropSheet.addProperty(property);
	    		}
	    	}
    	}
    	initTable();
    }

    /**
     * PrefTableModel
     * <br>Class tuned AbstractTableModel for really task.
     * <br>
     * <br>
     * Creation date (19.12.2003 20:36)
     */
    private class PrefTableModel extends AbstractTableModel {
        /**
         * Array stored renderer panels
         */
        private AbstractEditorPanel[] mPanels;
        private Hashtable mParameters;

        /**
         * getColumnClass.
         *
         * @param aCol of column.
         *
         * @return for column 1 Class string, for column 2 Class AValueEditorPanel.
         */
        public Class getColumnClass(int aCol) {
            if (aCol == 0) {
                return super.getColumnClass(aCol);
            }
            return AbstractEditorPanel.class;
        }

        /**
         * getColumnCount.
         *
         * @return really amount column in the table (2)
         */
        public int getColumnCount() {
            if (editPropSheet == null) {
                return 0;
            } else {
                return 2;
            }
        }

        /**
         * getColumnName.
         *
         * @param aCol of column.
         *
         * @return rendered name of column
         */
        public String getColumnName(int aCol) {
            if (aCol < 1) {
                return getResourceManager().getString("IDS_PROPERTY_HEADER");
            } else {
                return getResourceManager().getString("IDS_VALUE_HEADER");
            }
        }

        /**
         * getRowCount.
         *
         * @return number lines of current table in the sheet.
         */
        public int getRowCount() {
            if (editPropSheet == null) {
                return 0;
            } else {
                return editPropSheet.size();
            }
        }

        /**
         * getValueAt.
         * If called cell is empty, then adds in it from array,
         * and return contents in any case.
         *
         * @param aRow and Column, where getting value
         *
         * @return Object stored in this cell.
         */
        public Object getValueAt(int aRow, int aCol) {
            if (aCol != 1) {
            	String label = editPropSheet.getProperty(aRow).getLabel();
                return " " + getResourceManager().getString(label, label);
            } else if (mPanels[aRow] != null) {
                if (mPanels[aRow].isInvalid()) {
                    mPanels[aRow].setInvalid(false);
                    if (!isDirty()) {
                        mPanels[aRow].getParentDialog().setApplyButtonEnable(false);
//                        if (UserPreferences.getUserPreferences(mUserName).isDefault()) {
                        if (PropertyManager.getInstance().isDefault()) {
                            mPanels[aRow].getParentDialog().setResetButtonEnable(false);
                        }
                    }
                    //Escape key is pressed by user (refusal of entering)
                    mPanels[aRow].setValue(editPropSheet.getProperty(aRow).getValue());
                    mPanels[aRow].refreshControls();
                }
                return mPanels[aRow];
            } else {
                Object oValue = editPropSheet.getProperty(aRow).getValue();
                mPanels[aRow] = editPropSheet.getProperty(aRow).getType().getRenderer(oValue);
                if (mPanels[aRow] == null) {
                    Exception exception = new Exception("-" + aRow + "-" + oValue.getClass().getName());
                    exception.printStackTrace();
                    mPanels[aRow] = null;
                }
                mPanels[aRow].setParameters(mParameters);
                mParameters.put(mPanels[aRow], aRow);
                mPanels[aRow].setValue(oValue);
                //indicate where to report on completion of input for complex types
                mPanels[aRow].setParentDialog(getPropertyDialog());
                //remember what property connect with this panel
                mPanels[aRow].setProperty(editPropSheet.getProperty(aRow));
                return mPanels[aRow];
            }
        }

        /**
         * init
         * Create new array determine dimension.
         */
        void init() {
            mTable.setRowHeight(cInitialHeight);
            mPanels = new AbstractEditorPanel[getRowCount()];
            mParameters = new Hashtable();
            mParameters.put("table", mTable);
        }

        /**
         * Editable only second column
         */
        public boolean isCellEditable(int aRow, int aCol) {
            return aCol == 1;
        }

        /**
         * setValueAt.
         * Method called by model if in cell is changed value.
         *
         * @param aValue what is setting
         * @param aRow and Column where
         */
        public void setValueAt(Object aValue, int aRow, int aCol) {
            if (aCol != 1) {
                return;
            }
            if (aRow < 0 || aRow >= editPropSheet.size()) {
                return;
            }
            if (aValue != null) {
                Property property = editPropSheet.getProperty(aRow);
                //for complex type avoid (values already equals) but for simple as once get
                if (!property.getValue().equals(aValue)) {
                    //Value is changed. Check this for further work
                    property.setValue(aValue);
                    setPropertyDirty(property, true);
                    getPropertyDialog().setButtonEnableBySetValue(true);
                }
            }
        }
    }
}
