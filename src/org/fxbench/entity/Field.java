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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 */
public class Field implements Cloneable {
	public enum FieldType {
    	NOT_SORTABLE, STRING, INT, DOUBLE, DATE;
    }
	
    private int fieldNo;
    private String fieldName;
    private Object fieldVal;
    private FieldType fieldType;
    private String fieldFormat;
    private boolean isShow;
    
    public Field(int no) {
    	fieldNo = no;
    }
    
    /**
	 * @return the fieldNo
	 */
	public int getFieldNo() {
		return fieldNo;
	}

	/**
	 * @param fieldNo the fieldNo to set
	 */
	public void setFieldNo(int fieldNo) {
		this.fieldNo = fieldNo;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldVal
	 */
	public Object getFieldVal() {
		return fieldVal;
	}
	
	/**
	 * @param fieldVal the fieldVal to set
	 */
	public void setFieldVal(Object fieldVal) {
		this.fieldVal = fieldVal;
	}

	/**
	 * @return the fieldType
	 */
	public FieldType getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return the fieldFormat
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * @param fieldFormat the fieldFormat to set
	 */
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}
	
	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public Field clone() {
		try {
			return (Field)super.clone();
		} catch (CloneNotSupportedException e) {  
			return null;  
		}  
	}

	public String getFormatText() {
		if (fieldVal == null) {
			return null;
		}
		if (fieldFormat == null || fieldFormat.length() == 0) {
			return fieldVal.toString();
		} else {
			if (fieldType == FieldType.DOUBLE) {
				DecimalFormat decimalFormat = new DecimalFormat(fieldFormat);
				return decimalFormat.format(fieldVal);
			} else if (fieldType == FieldType.DATE) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fieldFormat);
				return simpleDateFormat.format(fieldVal);
			} else {
				return fieldVal.toString();
			}
		}
	}
	
	public String toString () {
		if (fieldVal == null) {
			return null;
		} else {
			return fieldVal.toString();
		}
	}
}
