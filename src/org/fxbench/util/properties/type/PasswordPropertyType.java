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
package org.fxbench.util.properties.type;

import org.fxbench.util.properties.editor.AbstractEditorPanel;
import org.fxbench.util.properties.editor.PasswordEditorPanel;

public class PasswordPropertyType extends AbstractPropertyType {
    class Validator implements IValidator {
        public Object validate(String asValue) {
            return asValue;
        }
    }

    public PasswordPropertyType(String typeName) {
    	super(typeName);
        setValidator(new Validator());
    }

    @Override
	public Object[] getDataList() {
		return null;
	}
    
    @Override
    public void setDataList(Object[] datas) {
	}
    
    public AbstractEditorPanel getRenderer(Object aValue) {
        return new PasswordEditorPanel(aValue, this);
    }

    public String toString(Object aValue) {
        return (String)aValue;
    }

	@Override
	public String toShowText(Object aValue) {
		return aValue.toString().length() == 0 ? aValue.toString() : "*";
	}

	@Override
	public Object toValue(String s) {
		return s;
	}

	@Override
	public boolean equals(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null) {
    		return false;
    	}
		String i1 = (String)obj1;
		String i2 = (String)obj2;
    	return i1.equals(i2);
	}

	@Override
	public Object cloneValue(Object obj) {
		try {
			return String.valueOf(obj);
		} catch (Exception e) {
			return null;
		}
	}
}