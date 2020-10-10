/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/datatypes/IKey.java#1 $
 *
 * Copyright (c) 2008 FXCM, LLC.
 * 32 Old Slip, New York NY, 10005 USA
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
 * Author: Andre Mermegas
 * Created: Nov 29, 2006 3:17:32 PM
 *
 */
package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class BaseEntity implements Cloneable
{
	protected List<Field> fieldList = new ArrayList<Field>();
	
    public Field getField(int fieldNo) {
		return fieldNo >= 0 && fieldNo < fieldList.size() ? fieldList.get(fieldNo) : null;
	}
    
    public String getFieldFormat(int fieldNo) {
    	Field field = getField(fieldNo);
    	if (field == null) {
    		return "";
    	} else {
    		return field.getFieldFormat();
    	}
    }
    
    public void setFieldVal(int fieldNo, Object val) {
    	if (fieldNo >= 0 && fieldNo < fieldList.size()) {
    		Field field = fieldList.get(fieldNo);
    		field.setFieldVal(val);
    	}
    }
    
    public void setFieldFormat(int fieldNo, String format) {
    	if (fieldNo >= 0 && fieldNo < fieldList.size()) {
    		Field field = fieldList.get(fieldNo);
    		field.setFieldFormat(format);
    	}
    }
    
    public BaseEntity clone() {
		try { 
			BaseEntity entity = (BaseEntity)super.clone();
			entity.fieldList = new ArrayList<Field>(fieldList.size());
			for (int i = 0; i < fieldList.size(); i++) {
				entity.fieldList.set(i, fieldList.get(i).clone());
			}
			return entity;
		} catch (CloneNotSupportedException e) {  
			return null;
		}  
	}
    
    public abstract String getKey();
	public abstract String getSelSql();
	public abstract BaseEntity newEntity(ResultSet resultSet) throws SQLException;
}
