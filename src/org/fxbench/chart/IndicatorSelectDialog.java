/*
* Copyright 2020 FXDaemon
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.fxbench.chart;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fxbench.BenchApp;
import org.fxbench.ui.panel.ChartPanel;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.TemplateManager;
import org.fxbench.util.properties.editor.PropertyDialog;
import org.fxbench.util.properties.editor.PropertySheetNode;

public class IndicatorSelectDialog extends PropertyDialog
{
	private ChartPanel chartPanel;
	
	public IndicatorSelectDialog(Frame parent, String title, ResourceManager resourceManager) {
		super(parent, title, resourceManager);
	}
	
	@Override
	protected void setUIComponent() {
		mApplyButton.setEnabled(false);
	}

	@Override
	public void applyAction() {
	}

	@Override
	public void okAction() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PropertySheetNode propSheetNode = propertyMainPanel.getSheetPanel().getSheetNode();
				if (propSheetNode != null) {
					chartPanel.addIndicator(propSheetNode.getSavePropSheet());
				}
			}
		});
	}

	@Override
	public void setButtonEnableBySetValue(boolean enable) {
		mOkButton.setEnabled(enable);
		mResetToDefaultButton.setEnabled(enable);
	}

	public static void createAndShowDialog(ChartPanel chartPanel) {
		ResourceManager resManager = PropertyManager.getInstance().getResourceManager();
		IndicatorSelectDialog dlg = new IndicatorSelectDialog(
        		BenchApp.getInst().getMainFrame(), "IDS_TITLE", resManager);
		dlg.chartPanel = chartPanel;
        
       	DefaultMutableTreeNode[] treeList = new DefaultMutableTreeNode[2];
       	treeList[0] = PropertySheetNode.newNode(resManager, "OverLays");
       	PropertySheetNode.addNode(resManager, treeList[0], TemplateManager.getInstance().getOverlayPropSheetsA());
        treeList[1] = PropertySheetNode.newNode(resManager, "Indicators");
        PropertySheetNode.addNode(resManager, treeList[1], TemplateManager.getInstance().getIndicatorPropSheetsA());
                
        dlg.loadProperties(treeList);
        dlg.showModal();
	}
}
