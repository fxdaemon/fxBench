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

import java.awt.*;

import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.type.IEditor;

public class FontEditor implements IEditor {
    private FontChooser mFontChooser;

    @Override
	public Component getComponent() {
		return mFontChooser;
	}
    
    @Override
    public Component getComponent(ResourceManager resourceManager) {
    	if (mFontChooser == null) {
    		 mFontChooser = new FontChooser(resourceManager);
    	}
    	return mFontChooser;
    }

    @Override
    public Object getValue() {
        return mFontChooser.getCurrentFont();
    }

    @Override
    public void setValue(Object aValue) {
//        System.out.println("FontEditor.setValue("+aValue+")");
        mFontChooser.setCurrentFont((Font) aValue);
    }
}