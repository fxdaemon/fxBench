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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

import javax.swing.tree.DefaultMutableTreeNode;

public class PropertySheetNode {
    private final Log mLogger = LogFactory.getLog(PropertySheetNode.class);

    private String treeNodeName;
    private DefaultMutableTreeNode treeNode;
    private PropertySheet defaultPropSheet;
    private PropertySheet savePropSheet;
    private PropertySheet editPropSheet;

    public PropertySheetNode(String name) {
    	this.treeNodeName = name;
    }
    
    public String getName() {
    	return treeNodeName;
    }

    public PropertySheet getDefaultPropSheet() {
		return defaultPropSheet;
	}

	public void setDefaultPropSheet(PropertySheet defaultPropSheet) {
		this.defaultPropSheet = defaultPropSheet;
	}

	public PropertySheet getSavePropSheet() {
		return savePropSheet;
	}

	public void setSavePropSheet(PropertySheet savePropSheet) {
		this.savePropSheet = savePropSheet;
	}

	public PropertySheet getEditPropSheet() {
		return editPropSheet;
	}

	public void setEditPropSheet(PropertySheet editPropSheet) {
		this.editPropSheet = editPropSheet;
	}

	public DefaultMutableTreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(DefaultMutableTreeNode aTreeNode) {
        treeNode = aTreeNode;
    }

    public String toString() {
        return getName();
    }
    
    public static PropertySheetNode valueOf(ResourceManager resManager, PropertySheet propSheet, PropertySheet defaultPropSheet) {
    	PropertySheetNode propNode = new PropertySheetNode(
    			resManager.getString(propSheet.getTitle(), propSheet.getTitle()));    	
    	propNode.savePropSheet = propSheet;
    	propNode.editPropSheet = propSheet.clone();
    	propNode.defaultPropSheet = defaultPropSheet;
    	propNode.setTreeNode(new DefaultMutableTreeNode());
    	propNode.getTreeNode().setUserObject(propNode);
    	return propNode;
    }
        
    public static DefaultMutableTreeNode newNode(ResourceManager resManager, String newTreeNodeName) {
    	PropertySheetNode propNode = new PropertySheetNode(
    			resManager.getString(newTreeNodeName, newTreeNodeName));
    	propNode.setTreeNode(new DefaultMutableTreeNode());
    	propNode.getTreeNode().setUserObject(propNode);
    	return propNode.getTreeNode();
    }
        
    public static void addNode(ResourceManager resManager, DefaultMutableTreeNode parentTreeNode, PropertySheet propSheet) {
    	if (propSheet != null && propSheet.isVisible()) {
    		PropertySheet defaultPropSheet = PropertyManager.getInstance().getDefaultPropSheet(propSheet);
    		parentTreeNode.add(valueOf(resManager, propSheet, defaultPropSheet).getTreeNode());
    	}
    }
    
    public static void addNode(ResourceManager resManager, DefaultMutableTreeNode parentTreeNode, List<PropertySheet> propSheets) {
    	if (propSheets == null) {
    		return;
    	}
    	for(PropertySheet propSheet : propSheets) {
    		if (propSheet.isVisible()) {
    			PropertySheet defaultPropSheet = PropertyManager.getInstance().getDefaultPropSheet(propSheet);
    			parentTreeNode.add(valueOf(resManager, propSheet, defaultPropSheet).getTreeNode());
    		}
		} 
    }
    
    public static void addNode(ResourceManager resManager, DefaultMutableTreeNode parentTreeNode, Map<String, PropertySheet> propSheets) {
    	if (propSheets == null) {
    		return;
    	}
    	for(Entry<String, PropertySheet> entry : propSheets.entrySet()) {
    		if (entry.getValue().isVisible()) {
    			PropertySheet defaultPropSheet = PropertyManager.getInstance().getDefaultPropSheet(entry.getValue());
    			parentTreeNode.add(valueOf(resManager, entry.getValue(), defaultPropSheet).getTreeNode());
    		}
		} 
    }
}