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

import org.fxbench.BenchApp;
import org.fxbench.chart.ta.Indicator;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.editor.PropertyDialog;
import org.fxbench.util.properties.editor.PropertySheetNode;

public class IndicatorChangeDialog extends PropertyDialog
{
	private Indicator indicator;
	
	public IndicatorChangeDialog(Frame parent, String title, ResourceManager resourceManager) {
		super(parent, title, resourceManager);
	}
	
	@Override
	protected void setUIComponent() {
	}

	@Override
	public void applyAction() {
		okAction();
	}

	@Override
	public void okAction() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PropertySheetNode propSheetNode = propertyMainPanel.getSheetPanel().getSheetNode();
				if (propSheetNode != null) {
					indicator.setPropertySheet(propSheetNode.getSavePropSheet());
					indicator.update();
					indicator.repaint();
				}
			}
		});
	}

	@Override
	public void setButtonEnableBySetValue(boolean enable) {
		mApplyButton.setEnabled(enable);
		mOkButton.setEnabled(enable);
		mResetToDefaultButton.setEnabled(enable);
	}

	public static void createAndShowDialog(Indicator indicator) {
		ResourceManager resManager = PropertyManager.getInstance().getResourceManager();
		IndicatorChangeDialog dlg = new IndicatorChangeDialog(
        		BenchApp.getInst().getMainFrame(), "IDS_TITLE", resManager);
		dlg.indicator = indicator;
        dlg.loadProperties(PropertySheetNode.valueOf(
        		PropertyManager.getInstance().getResourceManager(),
        		indicator.getPropertySheet(),
        		PropertyManager.getInstance().getDefaultPropSheet(indicator.getPropertySheet())));
        dlg.showModal();
	}
}
