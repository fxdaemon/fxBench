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


import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.fxbench.util.ILocaleListener;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.Property;
import org.fxbench.util.properties.PropertySheet;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Main panel of preferences dialog.
 * Creation date (24/09/2003 13:36 )
 */
public class PropertyMainPanel extends JSplitPane
{
    protected PropertySheetPanel mSheetPanel;	//Right part of screen
    protected PropertyTreePanel mTreePanel;		//Left part of screen
    protected PropertyMainPanel.TreeSelectionListener mTreeSelectionListener;
    protected PropertyDialog propertyDialog;
        
    /**
     * Constructor.
     *
     * @param aUserName username
     */
    public PropertyMainPanel(PropertyDialog parentDialog, DefaultMutableTreeNode[] treeList) {
        this.propertyDialog = parentDialog;
        mTreePanel = new PropertyTreePanel(this, treeList);
        mSheetPanel = new PropertySheetPanel(this);
        //sets split panel
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        setLeftComponent(mTreePanel);
        setRightComponent(mSheetPanel);
        setDividerLocation(150);
        setOneTouchExpandable(true);
    }
    
    public PropertyMainPanel(PropertyDialog parentDialog, PropertySheetNode sheet) {
    	this.propertyDialog = parentDialog;
    	mTreePanel = null;
    	mSheetPanel = new PropertySheetPanel(this);
    	mSheetPanel.setSheetNode(sheet);
    	//sets split panel
    	setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    	setLeftComponent(mSheetPanel);
    	setRightComponent(mTreePanel);
    	setDividerLocation(0);
    }

    public PropertyDialog getPropertyDialog() {
    	return propertyDialog;
    }
    
    public PropertySheetPanel getSheetPanel() { 
    	return mSheetPanel;
    }
    
    public void setEscape() {
    	//Prepare to used Escape key
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        mSheetPanel.getTable().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "ExitAction");
        SwingUtilities.getUIActionMap(mSheetPanel.getTable()).put(
        		"ExitAction", getPropertyDialog().getCancelButtonAction());
        if (mTreePanel != null) {
        	mTreePanel.getTree().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, "ExitAction");
        	SwingUtilities.getUIActionMap(mTreePanel.getTree()).put(
        		"ExitAction", getPropertyDialog().getCancelButtonAction());
        }
    }
    
    /**
     * @return allow stop editing
     */
    public boolean allowStopEditing() {
        return mSheetPanel.allowStopEditing();
    }
    
    public void saveProperties() {
		if (mSheetPanel.getSheetNode().getSavePropSheet() != null) {
			mSheetPanel.getSheetNode().getSavePropSheet().copyValue(
    				mSheetPanel.getSheetNode().getEditPropSheet());
		}
    	if (mTreePanel != null) {
    		List<PropertySheetNode> propSheetNodeList = mTreePanel.getAllPropSheetNode();
    		for (PropertySheetNode propSheetNode : propSheetNodeList) {
    			if (propSheetNode.getSavePropSheet() != null) {
    				propSheetNode.getSavePropSheet().copyValue(propSheetNode.getEditPropSheet());
    			}
    		}
    	}
    }
    
    public void resetProperties() {
    	if (mSheetPanel.getSheetNode().getEditPropSheet() != null) {
			mSheetPanel.getSheetNode().getEditPropSheet().copyValue(
    				mSheetPanel.getSheetNode().getDefaultPropSheet());
			mSheetPanel.refresh();
		}
    	if (mTreePanel != null) {
    		List<PropertySheetNode> propSheetNodeList = mTreePanel.getAllPropSheetNode();
    		for (PropertySheetNode propSheetNode : propSheetNodeList) {
    			if (propSheetNode.getEditPropSheet() != null) {
    				propSheetNode.getEditPropSheet().copyValue(propSheetNode.getDefaultPropSheet());
    			}
    		}
    	}
    }
    
    public void cancelEditing() {
        mSheetPanel.cancelEditing();
    }

    /**
     * expandTree
     * Call to display all nodes first level with hidden null-level root node.
     */
    public void expandTree() {
    	if (mTreePanel != null) {
	        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) mTreePanel.getTree().getModel().getRoot();
	        TreePath stealth = new TreePath(rootNode);
	        mTreePanel.getTree().expandPath(stealth);
    	}
    }

    public void removeTreeListener() {
    	if (mTreePanel != null) {
    		mTreePanel.getTree().removeTreeSelectionListener(mTreeSelectionListener);
    	}
    }
    
    public void setTreeListener() {
    	if (mTreePanel != null) {
	        mTreeSelectionListener = new TreeSelectionListener(mTreePanel.getTree());
	        mTreePanel.getTree().addTreeSelectionListener(mTreeSelectionListener);
    	}
    }

    private class TreeSelectionListener implements javax.swing.event.TreeSelectionListener {
        private JTree mTree;

        TreeSelectionListener(JTree aTree) {
            mTree = aTree;
        }

        public void valueChanged(TreeSelectionEvent aEvent) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) aEvent.getPath().getLastPathComponent();
            if (node == null) {
                if (!allowStopEditing()) {
                    mTree.setSelectionPath(aEvent.getOldLeadSelectionPath());
                }
                return;
            }
            PropertySheetNode propSheetNode = (PropertySheetNode) node.getUserObject();
            if (propSheetNode.getEditPropSheet() != null && 
            	(mSheetPanel.getSheetNode() == null ||            	
            	propSheetNode.getEditPropSheet() != mSheetPanel.getSheetNode().getEditPropSheet())) {
                if (!allowStopEditing()) {
                    mTree.setSelectionPath(aEvent.getOldLeadSelectionPath());
                    return;
                }
                mSheetPanel.setSheetNode(propSheetNode);
                mTreePanel.requestFocus();
            }
        }
    }
}
