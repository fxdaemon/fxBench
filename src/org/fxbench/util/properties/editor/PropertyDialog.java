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
 * 05/18/2006   Andre Mermegas: extends ABaseDialog
 */
package org.fxbench.util.properties.editor;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fxbench.trader.dialog.BaseDialog;
import org.fxbench.ui.auxi.RiverLayout;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ILocaleListener;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.IPropertyListener;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.properties.TemplateManager;
import org.fxbench.util.properties.type.AbstractPropertyType;
import org.fxbench.util.properties.type.ColorPropertyType;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * PreferencesDialog allows to client to adjust program<br>
 * <br> in accordance with own preferences.
 * <ul>
 * <li> Herewith it can choose any one of its positions.</li>
 * <li> When closing is checked admissibility to this operations.</li>
 * </ul>
 * <br>
 * Creation date (24/09/2003 13:36 )
 */
public abstract class PropertyDialog extends BaseDialog
{
	protected JButton mApplyButton;
	protected JButton mOkButton;
	protected JButton mCancelButton;    
	protected JButton mResetToDefaultButton;
	protected PropertyMainPanel propertyMainPanel;
	protected ResourceManager mResMan;
	private int exitCode;
	
    /**
     * Constructor.
     *
     * @param aParent parent
     * @param aPreferencesPanel pref panel
     * @param aUserName username
     */
    public PropertyDialog(Frame aParent, String title, ResourceManager resourceManager) {
        super(aParent);
        mResMan = resourceManager;
        setTitle(mResMan.getString(title, title));
    }
        
    public void loadProperties(DefaultMutableTreeNode[] treeList) {
        propertyMainPanel = new PropertyMainPanel(this, treeList);
    	initUIComponent();
    }
    
    public void loadProperties(PropertySheetNode sheet) {
    	propertyMainPanel = new PropertyMainPanel(this, sheet);
    	initUIComponent();
    }
    
    private void initUIComponent() {
    	//creates buttons 
        mApplyButton = UIManager.getInst().createButton(
        		new AbstractAction(mResMan.getString("IDS_APPLY_BUTTON")) {
                    public void actionPerformed(ActionEvent aEvent) {
                    	if (propertyMainPanel.allowStopEditing()) {
                            propertyMainPanel.saveProperties();
                            applyAction();
                            mApplyButton.setEnabled(false);
                            mCancelButton.setEnabled(false);
                            mResetToDefaultButton.setEnabled(true);
                        }
                    }
                }); 
        mOkButton = UIManager.getInst().createButton(
        		new AbstractAction(mResMan.getString("IDS_OK_BUTTON")) {
                    public void actionPerformed(ActionEvent aEvent) {
                    	if (propertyMainPanel.allowStopEditing()) {
                            propertyMainPanel.saveProperties();
                            okAction();
                            closeDialog(JOptionPane.OK_OPTION);
                        }
                    }
                });
        mCancelButton = UIManager.getInst().createButton(
        		new AbstractAction(mResMan.getString("IDS_CANCEL_BUTTON")) {
                    public void actionPerformed(ActionEvent aEvent) {
//                    	propertyMainPanel.cancelEditing();
                        closeDialog(JOptionPane.CANCEL_OPTION);
                    }
                }); 
        mResetToDefaultButton = UIManager.getInst().createButton(
        		new AbstractAction(mResMan.getString("IDS_RESET_TO_DEFAULT_BUTTON")) {
                    public void actionPerformed(ActionEvent aEvent) {
                    	propertyMainPanel.cancelEditing();
                        propertyMainPanel.resetProperties();
                        mApplyButton.setEnabled(false);
                        mResetToDefaultButton.setEnabled(false);
                    }
                }); 
        mResetToDefaultButton.setEnabled(!PropertyManager.getInstance().isDefault());
        getRootPane().setDefaultButton(mOkButton);

        //sets layout
        getContentPane().setLayout(new RiverLayout());
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new RiverLayout());

        // Define buttons in main window of dialog
        mApplyButton.setEnabled(false);
        buttonsPanel.add(mApplyButton);
        buttonsPanel.add(mOkButton);
        buttonsPanel.add(mCancelButton);
        buttonsPanel.add(mResetToDefaultButton);

        //sets main panel
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new RiverLayout());
        framePanel.add("vfill hfill", propertyMainPanel);
        framePanel.add("br center", buttonsPanel);
        getContentPane().add("vfill hfill", framePanel);

        //sets for exiting
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        mCancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "Exit");
        mCancelButton.getActionMap().put("Exit", mOkButton.getAction());
        propertyMainPanel.setEscape();

        //adds window listener
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent aEvent) {
//                propertyMainPanel.cancelEditing();
//                mApplyButton.setEnabled(false);
            }
        });
        setUIComponent();
        Utils.setAllToBiggest(new JComponent[]{mApplyButton, mCancelButton, mOkButton, mResetToDefaultButton});
    }

    public ResourceManager getResourceManager() {
    	return mResMan;
    }
    
    public void closeDialog(int aExitCode) {
    	exitCode = aExitCode;
        super.closeDialog(aExitCode);
    }

    /**
     * setApplyButtonEnable.
     * Method providing of external management of condition Apply button.
     *
     * @param aEnabled true - Apply button set enabled, else false.
     */
    public void setApplyButtonEnable(boolean aEnabled) {
        mApplyButton.setEnabled(aEnabled);
    }

    /**
     * setCancelButtonEnable.
     * Method providing of external management of condition Cancel button.
     *
     * @param aEnabled true - Cancel button set enabled, else false.
     */
    public void setCancelButtonEnable(boolean aEnabled) {
        mCancelButton.setEnabled(aEnabled);
    }

    /**
     * setApplyButtonEnable.
     * Method providing of external management of condition Apply button.
     *
     * @param aEnabled true - Apply button set enabled, else false.
     */
    public void setResetButtonEnable(boolean aEnabled) {
        mResetToDefaultButton.setEnabled(aEnabled);
    }
    
    public Action getApplyButtonAction() {
    	return mApplyButton.getAction();
    }
    
    public Action getOkButtonAction() {
    	return mOkButton.getAction();
    }
    
    public Action getCancelButtonAction() {
    	return mCancelButton.getAction();
    }
    
    public Action getResetButtonAction() {
    	return mResetToDefaultButton.getAction();
    }

    /**
     * showModal
     * Make visible dialog how modal window.
     * Determing exterior of dialog
     *
     * @return miExitCode Code of return
     */
    public int showModal() {
        propertyMainPanel.expandTree();
        propertyMainPanel.setTreeListener();
        exitCode = JOptionPane.CLOSED_OPTION;
        setModal(true);
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        propertyMainPanel.removeTreeListener();
        return exitCode;
    }
	
    protected abstract void setUIComponent();
    protected abstract void applyAction();
    protected abstract void okAction();
    protected abstract void setButtonEnableBySetValue(boolean enable);
}
