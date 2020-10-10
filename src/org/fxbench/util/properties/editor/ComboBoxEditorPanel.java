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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.properties.type.AbstractPropertyType;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Panel for presentation of Boolean type
 *
 * @Creation date (10/24/2003 1:36 PM)
 */
public class ComboBoxEditorPanel extends AbstractEditorPanel
{ 
    private JComboBox comboBox;
    private JLabel mPresentationLabel;
    
    /**
     * Constructor.
     * Image of editor panel for boolean data type.
     *
     * @param aValue value of data
     * @param aType  type of data
     */
    public ComboBoxEditorPanel(Object aValue, Object[] dataList, AbstractPropertyType aType) {
        super(aValue, aType);
        mPresentationLabel = UIManager.getInst().createLabel();
        setLayout(new BorderLayout());
        comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(dataList));
        comboBox.setSelectedItem(aValue);
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
            	setValueChanged(true);
            }
        });
        add(comboBox, BorderLayout.CENTER);        
    }

    /**
     * Sets image of not edited component (label) and return it.
     *
     * @return not edited image of panel.
     */
    public Component getNotEditedComponent() {       
        mPresentationLabel.setText(comboBox.getSelectedItem().toString());
        return mPresentationLabel;
    }

    /**
     * Returns string presentation data that is selected by user.
     *
     * @return string presentation of boolean type
     */
    public Object getUserInput() {
    	return comboBox.getSelectedItem();
    }

    /**
     * Provides renovation data in the panel.
     */
    public void refreshControls() {
    	mPresentationLabel.setText(comboBox.getSelectedItem().toString());
        super.repaint();
    }

    /**
     * Sets focus on the check box.
     */
    public void requestFocus() {
    	comboBox.requestFocus();
    }
}