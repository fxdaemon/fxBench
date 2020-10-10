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
import org.fxbench.util.properties.editor.SimpleEditorPanel;

public class LongPropertyType extends AbstractPropertyType {
    private static class Validator implements IValidator {
        public Object validate(String asValue) throws ValidationException {
            try {
                return Long.valueOf(asValue);
            } catch (Exception e) {
                throw new ValidationException("IDS_NUMBER_FORMAT_INVALID");
            }
        }
    }

    public LongPropertyType(String typeName) {
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
        return new SimpleEditorPanel(aValue, this);
    }

    public String toString(Object aValue) {
        return ((Long)aValue).toString();
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
		Long i1 = (Long)obj1;
		Long i2 = (Long)obj2;
    	return i1.equals(i2);
	}

	@Override
	public Object cloneValue(Object obj) {
		try {
			Long val = (Long)obj;
			return new Long(val.longValue());
		} catch (Exception e) {
			return null;
		}
	}
    
}