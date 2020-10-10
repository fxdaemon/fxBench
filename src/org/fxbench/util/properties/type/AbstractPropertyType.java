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

/**
 * APropertyType
 * This abstract class is base for all used types of data.
 *
 * @Creation date (10/24/2003 1:36 PM)
 */
public abstract class AbstractPropertyType implements Cloneable
{
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String STRING = "string";
    public static final String STRING_ARRAY = "string[]";
    public static final String PASSWORD = "password";
    public static final String BOOLEAN = "boolean";
    public static final String DATE = "date";
    public static final String COLOR = "color";
    public static final String FONT = "font";
    public static final String DELIM = ",";
    
    /**
     * Optional property of type. It not used for simple types.
     */
    protected IEditor editor;
    /**
     * Optional property of type. It not used for that types
     * which are got from special editors.
     */
    protected IValidator validator;
    /**
     * Obligatory property of type
     */
    protected String typeName;

    /* Constructor */

    /**
     * Creates new type of data.
     */
    protected AbstractPropertyType(String typeName) {
    	this.typeName = typeName;
    }

    /**
     * Returns editor.
     *
     * @return mEditor early set editor for this type of data.
     */
    public IEditor getEditor() {
        return editor;
    }

    /**
     * This method is called to create editor and prohibition validator
     * for this type of data.
     *
     * @param aEditor editor for this type
     */
    public final void setEditor(IEditor aEditor) {
        validator = null;
        editor = aEditor;
    }

    /**
     * Returns name
     *
     * @return typeName early set name for this type of data.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Returns renderer.
     *
     * @param aoValue Object, which type defines what renderer to return
     *
     * @return AValueEditorPanel special panel for displaying
     *         a given type of data in the list.
     */
    public abstract AbstractEditorPanel getRenderer(Object aoValue);

    /**
     * Returns validator.
     *
     * @return validator early set validator for this type of data.
     */
    public IValidator getValidator() {
        return validator;
    }

    /* -- Public methods -- */

    /**
     * This method is called to create validator and prohibition
     * editor for this type of data.
     *
     * @param aValidator : what setting.
     */
    public final void setValidator(IValidator aValidator) {
        editor = null;
        validator = aValidator;
    }

    /**
     * Assign name a type of data.
     *
     * @param asName name a type of data
     */
    public void setTypeName(String asName) {
        typeName = asName;
    }

    /**
     * Returs string representation of value for storing.
     *
     * @param aValue Object, which should be present.
     *
     * @return String presentation which will be used for storage
     *
     * @throws
     */
    public abstract String toString(Object aValue);

    /**
     * Returs string representation of value for showing.
     *
     * @param aValue Object, which should be present.
     *
     * @return String presentation which will be used to draw on screen.
     */
    public abstract String toShowText(Object aValue);
    
    public abstract Object toValue(String s);
    
    public abstract Object cloneValue(Object obj);
    
    public abstract Object[] getDataList();
    public abstract void setDataList(Object[] datas);
    
    public abstract boolean equals(Object obj1, Object obj2);

    /**
     * This method is used for transformation of value in string format
     * in its native value.
     *
     * @param asValue: string record of value.
     *
     * @return Object representing type of data.
     *
     * @throws ValidationException
     */
    public Object toValue(Object asValue) throws ValidationException {
        if (validator != null) {
            return validator.validate((String) asValue);
        }
        if (editor != null) {
            return asValue;
        }
        return null;
    }
    
    @Override
    public AbstractPropertyType clone() {
		try {  
			return (AbstractPropertyType)super.clone();
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}
    
    @Override
	public boolean equals(Object obj) {
    	return typeName.equals(((AbstractPropertyType)obj).getTypeName());
    }
    
    public static AbstractPropertyType valueOf(String typeName) {
    	if (typeName.equals(INT)) {
    		return new IntegerPropertyType(INT);
    	} else if (typeName.equals(LONG)) {
    		return new LongPropertyType(LONG);
    	} else if (typeName.equals(DOUBLE)) {
    		return new DoublePropertyType(DOUBLE);
    	} else if (typeName.equals(STRING)) {
    		return new StringPropertyType(STRING);
    	} else if (typeName.equals(PASSWORD)) {
    		return new PasswordPropertyType(PASSWORD);
    	} else if (typeName.equals(BOOLEAN)) {
    		return new BooleanPropertyType(BOOLEAN);
    	} else if (typeName.equals(DATE)) {
    		return new DatePropertyType(DATE);
    	} else if (typeName.equals(COLOR)) {
    		return new ColorPropertyType(COLOR);
    	} else if (typeName.equals(FONT)) {
    		return new FontPropertyType(FONT);
    	} else if (typeName.equals(STRING_ARRAY)) {
    		return new StringArrayPropertyType(STRING_ARRAY);
    	} else {
    		return null;
    	}
    }
}