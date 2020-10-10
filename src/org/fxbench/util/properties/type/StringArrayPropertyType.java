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
import org.fxbench.util.properties.editor.ComboBoxEditorPanel;


/**
 * Property type for boolean values.
 *
 * @Creation date (10/28/2003 1:36 PM)
 */
public class StringArrayPropertyType extends AbstractPropertyType
{
	private Object[] dataList;
	
    /**
     * Implementation of IValidator for boolean values.
     */
    class Validator implements IValidator {
        /**
         * Check validity of specified value.
         *
         * @param asValue specified value
         *
         * @return object of class Boolean or throws exception
         *
         * @throws ValidationException throws when value not valid.
         */
        public Object validate(String asValue) {
        	return asValue;
        }
    }

    /**
     * Constructor.
     */
    public StringArrayPropertyType(String typeName) {
    	super(typeName);
        setValidator(new Validator());
    }

    @Override
	public Object[] getDataList() {
		return dataList;
	}
    
    @Override
	public void setDataList(Object[] datas) {
		this.dataList = datas;
	}

	/**
     * Returns renderer for specified value.
     *
     * @param aValue target value
     */
    @Override
	public AbstractEditorPanel getRenderer(Object aValue) {
    	return new ComboBoxEditorPanel(aValue, dataList, this);
	}

	/**
     * Returns string representation of value for storing.
     *
     * @param aValue specified value
     */
    public String toString(Object aValue) {
        return (String)aValue;
    }
    
	@Override
	public String toShowText(Object aValue) {
		return toString(aValue);
	}

	@Override
	public Object toValue(String s) {
		try {
			return validator.validate(s);
		} catch (Exception e) {
			return null;
		}
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