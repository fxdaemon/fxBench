package org.fxbench.util;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.fxbench.BenchApp;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.IPropertyListener;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.properties.editor.PropertyDialog;
import org.fxbench.util.properties.editor.PropertySheetNode;

public class PreferencesDialog extends PropertyDialog
{
	private static List<IPropertyListener> propListenerList;
	
	public PreferencesDialog(Frame parent, String title, ResourceManager resourceManager) {
		super(parent, title, resourceManager);
	}

	public static void addPreferencesListener(IPropertyListener listener) {
		if (propListenerList == null) {
			propListenerList = new ArrayList<IPropertyListener>();
		}
		propListenerList.add(listener);
    }
	
	public static void removePreferencesListener(IPropertyListener listener) {
		if (propListenerList != null) {
			propListenerList.remove(listener);
		}
    }
	
	private void firePreferencesUpdated(List<PropertySheet> updates) {
		if (propListenerList != null) {
	        for (Object listenersList : propListenerList) {
	            ((IPropertyListener) listenersList).propertyUpdated(updates);
	        }
		}
    }
	
	@Override
	protected void setUIComponent() {
	}

	@Override
	protected void applyAction() {
		okAction();
	}

	@Override
	protected void okAction() {
		firePreferencesUpdated(null);
	}

	@Override
	protected void setButtonEnableBySetValue(boolean enable) {
		mApplyButton.setEnabled(enable);
		mOkButton.setEnabled(enable);
		mResetToDefaultButton.setEnabled(enable);
	}
	
	public static void createAndShowDialog() {
		ResourceManager resManager = PropertyManager.getInstance().getResourceManager();
		PreferencesDialog dlg = new PreferencesDialog(
        		BenchApp.getInst().getMainFrame(), "IDS_TITLE", resManager);
        
        DefaultMutableTreeNode[] treeList = null;
        if (PropertyManager.getInstance().getAppPropSheets() == null) {
        	treeList = new DefaultMutableTreeNode[1];
        } else {
        	treeList = new DefaultMutableTreeNode[2];
        	treeList[1] = PropertySheetNode.newNode(resManager, "Preferences");
            PropertySheetNode.addNode(resManager, treeList[1], PropertyManager.getInstance().getAppPropSheets());
            DefaultMutableTreeNode panelTreeNode = PropertySheetNode.newNode(resManager, "Panels");
            treeList[1].add(panelTreeNode);
            PropertySheetNode.addNode(resManager, panelTreeNode, PropertyManager.getInstance().getPanelPropSheets());
        }
        treeList[0] = PropertySheetNode.newNode(resManager, "Server");
        PropertySheetNode.addNode(resManager, treeList[0], PropertyManager.getInstance().getServerPropSheet());
        PropertySheetNode.addNode(resManager, treeList[0], PropertyManager.getInstance().getProxyPropSheet());        
        
        dlg.loadProperties(treeList);
        dlg.showModal();
	}
}
