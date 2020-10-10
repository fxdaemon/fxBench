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

import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;

import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.properties.type.AbstractPropertyType;

public class PasswordEditorPanel extends SimpleEditorPanel {
    public PasswordEditorPanel(Object aValue, AbstractPropertyType aType) {
        super(aValue, aType);
    }

    /**
     * The AValueEditorPanel subclass that doesn't have editor should refine
     * method getUserInput()
     */
    public Object getUserInput() {
        return new String(((JPasswordField) getTextControl()).getPassword());
    }

    protected JTextComponent newTextControl() {
        JPasswordField passwordField = UIManager.getInst().createPasswordField(getType().toShowText(getValue()));
        passwordField.setHorizontalAlignment(JPasswordField.LEFT);
        return passwordField;
    }
}