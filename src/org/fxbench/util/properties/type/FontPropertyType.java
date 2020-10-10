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
import org.fxbench.util.properties.editor.FontEditor;
import org.fxbench.util.properties.editor.FontEditorPanel;

public class FontPropertyType extends AbstractPropertyType
{
	public static final Font DefaultFont = new Font("Dialog", 1, 12);
		
    public FontPropertyType(String typeName) {
    	super(typeName);
        setEditor(new FontEditor());
    }
    
    @Override
	public Object[] getDataList() {
		return null;
	}
    
    @Override
    public void setDataList(Object[] datas) {
	}

    public AbstractEditorPanel getRenderer(Object oValue) {
//      getEditor().setValue(oValue);
        return new FontEditorPanel(oValue, this);
    }

    public String toString(Object aValue) {
    	Font font = (Font)aValue;
		return font.getName() + AbstractPropertyType.DELIM + 
			font.getStyle() + AbstractPropertyType.DELIM + 
			font.getSize();
    }

    @Override
    public String toShowText(Object aValue) {
    	Font font = (Font)aValue;
        String style;
        switch (font.getStyle()) {
            case Font.PLAIN:
                style = "Plain";
                break;
            case Font.BOLD:
                style = "Bold";
                break;
            case Font.ITALIC:
                style = "Italic";
                break;
            case Font.BOLD + Font.ITALIC:
                style = "Italic + Bold";
                break;
            default:
                style = "Plain";
        }
        String name = clipFontFamily(font.getFamily());
        String size = Integer.toString(font.getSize());
        return name + AbstractPropertyType.DELIM + 
        	style + AbstractPropertyType.DELIM + 
        	size;
    }

	@Override
	public Object toValue(String s) {
		Font font = DefaultFont;
		if (s != null) {
			String[] fonts = s.split(AbstractPropertyType.DELIM);
			if (fonts.length == 3) {
				font = new Font(fonts[0], new Integer(fonts[1]), new Integer(fonts[2]));
			}
		}
		return font;
	}

	@Override
	public boolean equals(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null) {
    		return false;
    	}
		Font i1 = (Font)obj1;
		Font i2 = (Font)obj2;
    	return i1.equals(i2);
	}
	
	public static String clipFontFamily(String aFamily) {
        String tempName = aFamily;
        int pos = tempName.indexOf(".");
        if (pos >= 0) {
            String rest = tempName.substring(pos + 1);
            if (rest.contains("bold")
                || rest.contains("italic")
                || rest.contains("BOLD")
                || rest.contains("ITALIC")) {
                tempName = tempName.substring(0, pos);
            }
        }
        return tempName;
    }

	@Override
	public Object cloneValue(Object obj) {
		try {
			Font val = (Font)obj;
			return new Font(val.getName(), val.getStyle(), val.getSize());
		} catch (Exception e) {
			return null;
		}
	}
}