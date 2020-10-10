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

import javax.swing.*;

import org.fxbench.util.properties.Property;
import org.fxbench.util.properties.type.AbstractPropertyType;
import org.fxbench.util.properties.type.ValidationException;
import org.fxbench.util.ResourceManager;

import java.awt.*;
import java.util.Hashtable;

/**
 * AValueEditorPanel
 * This abstract class is base panel for presentation of all used types
 * of data in the cell of table.
 *
 * @Creation date (10/24/2003 1:36 PM)
 */
public abstract class AbstractEditorPanel extends JPanel {
    /**
     * Properties set by user
     */
    private Hashtable mParameters;
    /**
     * Pointer to parent dialog
     */
    private PropertyDialog mParentDialog;
    /**
     * Edited preferences property
     */
    private Property mPrefProperty;
    /**
     * Edited value type
     */
    private AbstractPropertyType mType;

    /*-- Data member --*/
    /**
     * Current edited value
     */
    private Object mValue;
    /**
     * Flag that means was value changed or no.
     */
    private boolean mbChanged;
    /**
     * Flag that value in panel is invalid
     */
    private boolean mbInvalid;

    /*-- Constructors --*/

    /**
     * Protected constructor.
     *
     * @param Object        aValue value of data.
     * @param APropertyType aType type of data.
     */
    protected AbstractEditorPanel(Object aValue, AbstractPropertyType aType) {
        setValue(aValue);
        setType(aType);
    }
    
    public ResourceManager getResourceManager() {
    	return mParentDialog.getResourceManager();
    }

    /** Public methods. */
    /**
     * Adds parameter to hash table.
     *
     * @param aParamKey   key of parameter
     * @param aParamValue value of parameter
     */
    public void addParameter(Object aParamKey, Object aParamValue) {
        if (mParameters == null) {
            mParameters = new Hashtable();
        }
        mParameters.put(aParamKey, aParamValue);
    }

    /**
     * Invoked on begin of editing of value.
     */
    public void beginEditing() {
    }

    /**
     * Invoked on end of editing of value.
     *
     * @param abCancelFlag signals was exiting by cancel or no
     */
    public void endEditing(boolean abCancelFlag) {
    }

    /**
     * Method returning that part editor panel when it is Editable
     * By default returns itself
     *
     * @return NotEditable component of panel.
     */
    public Component getEditedComponent() {
        setValueChanged(false);
        return this;
    }

    /**
     * Method returning that part editor panel when it is NotEditable
     *
     * @return NotEditable Component of panel.
     */
    public abstract Component getNotEditedComponent();

    /**
     * Returns value of parameter from hash table.
     *
     * @param aParamKey key of parameter
     *
     * @return value of parameter
     */
    public Object getParameterValue(Object aParamKey) {
        if (mParameters == null) {
            return null;
        } else {
            return mParameters.get(aParamKey);
        }
    }

    /**
     * Returns hashtable of parameters.
     */
    public Hashtable getParameters() {
        return mParameters;
    }

    /**
     * Sets hashtable of parameters.
     *
     * @param aParameters hashtable of parameters
     */
    public void setParameters(Hashtable aParameters) {
        mParameters = aParameters;
    }

    /**
     * Returns current up level dialog.
     */
    public PropertyDialog getParentDialog() {
        return mParentDialog;
    }

    /**
     * Panels of complex types should remember about up level dialog.
     *
     * @param aParentDialog parent dialog
     */
    public void setParentDialog(PropertyDialog aParentDialog) {
        mParentDialog = aParentDialog;
    }

    /**
     * Returns current edited preferences property.
     */
    public final Property getProperty() {
        return mPrefProperty;
    }

    /**
     * Sets preferences property for panel.
     *
     * @param aPrefProperty preferences property
     */
    public final void setProperty(Property aPrefProperty) {
        mPrefProperty = aPrefProperty;
    }

    /**
     * Returns title.
     */
    public String getTitle() {
        return "";
    }

    /**
     * Returns edited value type.
     */
    public final AbstractPropertyType getType() {
        return mType;
    }

    /**
     * Sets type of edited data in panel.
     *
     * @param aType type of value
     */
    public final void setType(AbstractPropertyType aType) {
        mType = aType;
    }

    /**
     * The AValueEditorPanel subclasses that doesn't have editor should
     * refine method getUserInput()
     *
     * @throws RuntimeException if try used not defined type of data
     */
    public Object getUserInput() {
        if (mType.getEditor() == null) {
            throw new RuntimeException("The AValueEditorPanel subclass "
                                       + getClass().getName()
                                       + " that doesn't have editor should refine method getUserInput()");
        }
        return getValue();
    }

    /**
     * Returns current edited value
     */
    public final Object getValue() {
        return mValue;
    }

    /**
     * Sets value edited data at panel (It`s not overrible method).
     *
     * @param aValue new value
     */
    public final void setValue(Object aValue) {
        mValue = aValue;
    }

    /**
     * Check valid or invalid value set for this panel.
     */
    public boolean isInvalid() {
        return mbInvalid;
    }

    /**
     * Returns was value changed or no.
     */
    public boolean isValueChanged() {
        return mbChanged;
    }

    /**
     * Refreshes all controls.
     */
    public abstract void refreshControls();

    /**
     * Provides renovation data in panels.
     *
     * @throws ValidationException
     */
    public final void refreshValue() throws ValidationException {
        Object oValue = mType.toValue(getUserInput());
        mValue = oValue;
        refreshControls();
    }

    /**
     * Requeshes focus.
     */
    public abstract void requestFocus();

    /**
     * Set flag of value in edit panel according with argument value.
     */
    public void setInvalid(boolean abInvalid) {
        mbInvalid = abInvalid;
    }

    /*-- Protected methods --*/

    /**
     * Sets was value changed or no.
     *
     * @param abChanged state of ability
     */
    protected void setValueChanged(boolean abChanged) {
        if (abChanged) {
//            mParentDialog.setResetButtonEnable(abChanged);
//            mParentDialog.setApplyButtonEnable(abChanged);
//            mParentDialog.setCancelButtonEnable(abChanged);
            mParentDialog.setButtonEnableBySetValue(abChanged);
        }
        mbChanged = abChanged;
    }

    /**
     * Sets value edited data at panel.
     *
     * @param aValue new value
     */
    public void setValueToEditor(Object aValue) {
    }

    /**
     * Convert current value of edited data in panel to string
     */
    public String toString() {
        return getValue().toString();
    }
}