/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/datatypes/Message.java#1 $
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
 * Created: Jul 6, 2007 12:09:05 PM
 *
 * $History: $
 */
package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.SwingConstants;

import org.fxbench.entity.Field.FieldType;
import org.fxbench.util.FieldDefStub;

public class TMessage extends BaseEntity {
    private Date mDate;
    private String mFrom;
    private String mFullText;
    private String mText;
    private FieldDefStub<FieldDef> fieldDefStub;

    public TMessage(Date aDate, String aFrom, String aText, String aFullText, FieldDefStub<FieldDef> fieldDefStub) {
        mDate = (Date) aDate.clone();
        mFrom = aFrom;
        if (aText == null || "".equals(aText.trim())) {
            mText = aFullText;
        } else {
            mText = aText;
        }
        mFullText = aFullText;
        
        initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TIME), this.mDate);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_FROM), this.mFrom);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_SUBJECT), this.mText);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TEXT), this.mFullText);
    }

    public Date getDate() {
        return (Date) mDate.clone();
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String aFrom) {
        mFrom = aFrom;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_FROM), this.mFrom);
    }

    public String getFullText() {
        return mFullText;
    }

    public void setFullText(String aFullText) {
        mFullText = aFullText;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TEXT), this.mFullText);
    }

    public String getText() {
        return mText;
    }

    public void setText(String aText) {
        mText = aText;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_SUBJECT), this.mText);
    }

    public void setDate(Date aDate) {
        mDate = (Date) aDate.clone();
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TIME), this.mDate);
    }
    
    @Override
    public String getKey() {
        return getField(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TIME)).getFormatText();
    }
    
    @Override
	public String getSelSql() {
		return null;
	}
    
    @Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
    	return null;
    }
	
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Message");
        sb.append("{mDate=").append(mDate);
        sb.append(", mFrom='").append(mFrom).append('\'');
        sb.append(", mFullText='").append(mFullText).append('\'');
        sb.append(", mText='").append(mText).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
    public Field getField(FieldDef fieldDef) {
		return getField(fieldDefStub.getFieldNo(fieldDef));
	}
    
    public String getFieldFromatText(FieldDef fieldDef) {
		Field field = getField(fieldDefStub.getFieldNo(fieldDef));
		if (field == null) {
			return "";
		} else {
			return field.getFormatText();
		}
	}
    
    public enum FieldDef {
		MESSAGE_TIME(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.LEFT),
		MESSAGE_FROM(FieldType.STRING, "", SwingConstants.LEFT),
		MESSAGE_SUBJECT(FieldType.STRING, "", SwingConstants.LEFT),
		MESSAGE_TEXT(FieldType.STRING, "", SwingConstants.LEFT);
		
		private FieldType fieldType;
		private String fieldFormat;
		private int fieldAlignment;
		private FieldDef(FieldType fieldType, String fieldFormat, int alignment) {
			this.fieldType = fieldType;
			this.fieldFormat = fieldFormat;
			this.fieldAlignment = alignment;
		}
		public FieldType getFieldType() {
			return fieldType;
		}
		public String getFieldFormat() {
			return fieldFormat;
		}
		public int getFiledAlignment() {
			return fieldAlignment;
		}
	}
    
	private void initFields(FieldDef[] fieldDefArray) {
		for (int i = 0; i < fieldDefArray.length; i++) {
			Field field = new Field(i);
			field.setFieldName(fieldDefArray[i].name());
			field.setFieldType(fieldDefArray[i].getFieldType());
			field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			fieldList.add(field);
		}
	}

}