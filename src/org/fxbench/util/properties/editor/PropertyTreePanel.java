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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.Property;
import org.fxbench.util.properties.PropertySheet;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * PreferencesTreePanel is a part of PreferencesDialog allows to client
 * to select needed property.<br>
 * <ul>
 * <li> .</li>
 * <li> .</li>
 * </ul>
 * <br>
 *
 * @Creation date (27/10/2003 13:36 )
 */
public class PropertyTreePanel extends JScrollPane implements KeyListener
{	
    private JTree mTree;
    private PropertyMainPanel propertyMainPanel;

    /**
     * Constructor PreferencesTreePanel
     * <br>
     * <br>
     */
    public PropertyTreePanel(PropertyMainPanel mainPanle, DefaultMutableTreeNode[] treeList) {
    	propertyMainPanel = mainPanle;
        DefaultMutableTreeNode mRootNode = new DefaultMutableTreeNode("...");
        mTree = UIManager.getInst().createTree(mRootNode);
        mTree.setRootVisible(false);
        mTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mTree.setToggleClickCount(1);
        //To use standard L&F UI keyboard controls
        mTree.addKeyListener(this);
        super.setViewportView(mTree);

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)mTree.getModel().getRoot();
        for (int i = 0; i < treeList.length; i++) {
        	rootNode.add(treeList[i]);
        }
    }

    /**
     * @return tree
     */
    public JTree getTree() {
        return mTree;
    }
    
    public ResourceManager getResourceManager() {
    	return propertyMainPanel.getPropertyDialog().getResourceManager();
    }

    public List<PropertySheetNode> getAllPropSheetNode() {
    	List<PropertySheetNode> propSheetList = new ArrayList<PropertySheetNode>();
    	getAllPropSheetNode((DefaultMutableTreeNode)mTree.getModel().getRoot(), propSheetList);
    	return propSheetList;
    }
    
    private void getAllPropSheetNode(DefaultMutableTreeNode parentTreeNode, List<PropertySheetNode> propSheetList) {
        DefaultMutableTreeNode childTreeNode;
        PropertySheetNode childPropNode;
        for (int i = 0; i < parentTreeNode.getChildCount(); i++) {
        	childTreeNode = (DefaultMutableTreeNode)parentTreeNode.getChildAt(i);
            childPropNode = (PropertySheetNode)childTreeNode.getUserObject();
            if (childPropNode != null) {
            	propSheetList.add(childPropNode);
            }
            getAllPropSheetNode(childTreeNode, propSheetList);
        }
    }
    
    /**
     * @param aEvent KeyEvent
     */
    public void keyPressed(KeyEvent aEvent) {
    }

    /**
     * @param aEvent KeyEvent
     */
    public void keyReleased(KeyEvent aEvent) {
    }

    /**
     * @param aEvent KeyEvent
     */
    public void keyTyped(KeyEvent aEvent) {
    }

    public void requestFocus() {
        mTree.requestFocus();
    }
}