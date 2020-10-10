package org.fxbench.trader.local;

import org.fxbench.BenchApp;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.local.LoginRequest;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.properties.editor.PropertyDialog;
import org.fxbench.util.properties.editor.PropertySheetNode;

public class LoginDialog extends PropertyDialog
{	
	private PropertySheet propertySheet;
	
	public LoginDialog(PropertySheet propertySheet) {
		super(BenchApp.getInst().getMainFrame(), "IDS_TITLE", PropertyManager.getInstance().getResourceManager());
		loadProperties(PropertySheetNode.valueOf(
        		PropertyManager.getInstance().getResourceManager(),
        		propertySheet,
        		PropertyManager.getInstance().getDefaultPropSheet(propertySheet)));
	}
	
	@Override
	protected void setUIComponent() {
		mApplyButton.setVisible(false);
		mResetToDefaultButton.setVisible(false);
	}

	@Override
	public void applyAction() {
	}

	@Override
	public void okAction() {
		PropertySheetNode propSheetNode = propertyMainPanel.getSheetPanel().getSheetNode();
		propertySheet = propSheetNode.getSavePropSheet();
	}

	@Override
	public void setButtonEnableBySetValue(boolean enable) {
		mOkButton.setEnabled(enable);
	}
	
	public IRequester getLoginParameters() {
        return new LoginRequest(propertySheet);
    }
}
