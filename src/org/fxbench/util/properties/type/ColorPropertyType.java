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

import java.awt.*;

import org.fxbench.util.properties.editor.AbstractEditorPanel;
import org.fxbench.util.properties.editor.ColorEditor;
import org.fxbench.util.properties.editor.ColorEditorPanel;

/**
 * Property type for color values.
 *
 * @Creation date (10/28/2003 1:36 PM)
 */
public class ColorPropertyType extends AbstractPropertyType {
	public static final Color DEFAULT_COLOR = new Color(255, 255, 255);
	
    /**
     * Constructor.
     */
    public ColorPropertyType(String typeName) {
    	super(typeName);
        setEditor(new ColorEditor());
    }

    @Override
	public Object[] getDataList() {
		return null;
	}
    
    @Override
    public void setDataList(Object[] datas) {
	}
    
    /**
     * Returns renderer for specified value.
     *
     * @param aValue target value
     */
    public AbstractEditorPanel getRenderer(Object oValue) {
        return new ColorEditorPanel(oValue, this);
    }

    /**
     * Returns string representation of value for storing.
     *
     * @param aValue specified value
     */
    public String toString(Object aValue) {
    	Color color = (Color)aValue;
    	return color.getRed() + AbstractPropertyType.DELIM + 
    		color.getGreen() + AbstractPropertyType.DELIM + 
    		color.getBlue();
    }

	@Override
	public String toShowText(Object aValue) {
		Color c = (Color) aValue;
        return " " + c.getRed() + AbstractPropertyType.DELIM + 
        	" " + c.getGreen() + AbstractPropertyType.DELIM +  
        	" " + c.getBlue();
	}

	@Override
	public Object toValue(String s) {
		Color value = DEFAULT_COLOR;
		if (s != null) {
			String[] colors = s.split(AbstractPropertyType.DELIM);
			if (colors.length == 3) { 
				value = new Color(new Integer(colors[0]), new Integer(colors[1]), new Integer(colors[2]));
			}
		}
		return value;
	}

	@Override
	public boolean equals(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null) {
    		return false;
    	}
		Color i1 = (Color)obj1;
		Color i2 = (Color)obj2;
    	return i1.equals(i2);
	}
	
	public static int getFontStyleNo(String fontStyleText) {
		if (fontStyleText.equals("Plain")) {
			return Font.PLAIN;
		} else if (fontStyleText.equals("Bold")) {
			return Font.BOLD;
		} else if (fontStyleText.equals("Italic")) {
			return Font.ITALIC;
		} else if (fontStyleText.equals("Bold Italic")) {
			return Font.BOLD + Font.ITALIC;
		} else {
			return Font.PLAIN;
		}
	}

	@Override
	public Object cloneValue(Object obj) {
		try {
			Color val = (Color)obj;
			return new Color(val.getRed(), val.getGreen(), val.getBlue(), val.getAlpha());
		} catch (Exception e) {
			return null;
		}
	}
}