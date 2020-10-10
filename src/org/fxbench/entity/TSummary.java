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
package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.Utils;

/**
 * Represent summary for a single currency.
 */
public class TSummary extends BaseEntity
{
    /**
     * Total contract size for all open buy positions
     */
    private long mAmountBuy;
    /**
     * Total contract size for all open sell positions
     */
    private long mAmountSell;
    /**
     * Average buy positions open price
     */
    private double mAvgBuyRate;
    /**
     * Average sell positions open price
     */
    private double mAvgSellRate;
    /**
     * Total buy profit-loss
     */
    private double mBuyPnL;
    /**
     * Symbol
     */
    private String mSymbol = "";
    /**
     * Total Gross profit-loss
     */
    private double mGrossTotalPnL;
    /**
     * Total Net profit-loss
     */
    private double mNetTotalPnL;
    /**
     * Total open positions count for the currency.
     */
    private int mPositionsCount;
    /**
     * Total sell profit-loss
     */
    private double mSellPnL;
    /**
     * Total contract size for the currency
     */
    private long mTotalAmount;

    private FieldDefStub<FieldDef> fieldDefStub;
    
    public TSummary(FieldDefStub<FieldDef> fieldDefStub) {
    	initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
	}
    
    /**
     * Gets total contract size for all open buy positions.
     */
    public long getAmountBuy() {
        return mAmountBuy;
    }

    /**
     * Sets total contract size for all open buy positions.
     */
    public void setAmountBuy(long alAmountBuy) {
        mAmountBuy = alAmountBuy;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AMOUNT_BUY), alAmountBuy);
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AMOUNT), alAmountBuy - this.mAmountSell);        
    }

    /**
     * Gets total contract size for all open sell positions.
     */
    public long getAmountSell() {
        return mAmountSell;
    }

    /**
     * Sets total contract size for all open sell positions.
     */
    public void setAmountSell(long alAmountSell) {
        mAmountSell = alAmountSell;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AMOUNT_SELL), alAmountSell);
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AMOUNT), this.mAmountBuy - alAmountSell);     
    }

    /**
     * Gets average buy positions open price.
     */
    public double getAvgBuyRate() {
        return mAvgBuyRate;
    }

    /**
     * Sets average buy positions open price.
     */
    public void setAvgBuyRate(double adAvgBuyRate) {
        mAvgBuyRate = adAvgBuyRate;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AVG_BUY), adAvgBuyRate);
    }

    /**
     * Gets average sell positions open price.
     */
    public double getAvgSellRate() {
        return mAvgSellRate;
    }

    /**
     * Sets average sell positions open price.
     */
    public void setAvgSellRate(double adAvgSellRate) {
        mAvgSellRate = adAvgSellRate;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_AVG_SELL), adAvgSellRate);
    }

    /**
     * Gets total buy profit-loss.
     */
    public double getBuyPnL() {
        return mBuyPnL;
    }

    /**
     * Sets total buy profit-loss.
     */
    public void setBuyPnL(double adBuyPnL) {
        mBuyPnL = adBuyPnL;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_PNL_BUY), adBuyPnL);
    }

    /**
     * Gets currency pair in format CCY1/CCY2.
     */
    public String getSymbol() {
        return mSymbol;
    }

    /**
     * Gets total profit-loss.
     */
    public double getGrossTotalPnL() {
        return mGrossTotalPnL;
    }

    /**
     * Sets total profit-loss.
     */
    public void setGrossTotalPnL(double adTotalPnL) {
        mGrossTotalPnL = adTotalPnL;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_GROSS_PL), adTotalPnL);
    }

    public double getNetTotalPnL() {
        return mNetTotalPnL;
    }

    public void setNetTotalPnL(double aNetTotalPnL) {
        mNetTotalPnL = aNetTotalPnL;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_NET_PL), aNetTotalPnL);
    }

    /**
     * Gets total open position count for the currency.
     */
    public int getPositionsCount() {
        return mPositionsCount;
    }

    /**
     * Sets total open position count for the currency.
     */
    public void setPositionsCount(int aiPositionsCount) {
        mPositionsCount = aiPositionsCount;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_POSITIONS_COUNT), aiPositionsCount);
    }

    /**
     * Gets total sell profit-loss.
     */
    public double getSellPnL() {
        return mSellPnL;
    }

    /**
     * Sets total sell profit-loss.
     */
    public void setSellPnL(double adSellPnL) {
        mSellPnL = adSellPnL;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SUMMARY_PNL_SELL), adSellPnL);
    }

    /**
     * Gets total contract size for the currency.
     */
    public long getTotalAmount() {
        return mTotalAmount;
    }

    /**
     * Sets total contract size for the currency.
     */
    public void setTotalAmount(long alTotalAmount) {
        mTotalAmount = alTotalAmount;
    }

    /**
     * Sets currency pair in format CCY1/CCY2.
     */
    public void setSymbol(String symbol) {
        mSymbol = symbol;
        setFieldVal(fieldDefStub.getFieldNo(FieldDef.SYMBOL), symbol);
        
        String precisionStr = Utils.getFormatStr(
        		BenchApp.getInst().getTradeDesk().getTradingServerSession().getSymbolPrecision(symbol), '.', '#');
        int fieldNo = fieldDefStub.getFieldNo(FieldDef.SUMMARY_AVG_SELL);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.SUMMARY_AVG_BUY);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.SUMMARY_SELL_RATE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.SUMMARY_BUY_RATE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Summary");
        sb.append("{mdAvgBuyRate=").append(mAvgBuyRate);
        sb.append(", mdAvgSellRate=").append(mAvgSellRate);
        sb.append(", mdBuyPnL=").append(mBuyPnL);
        sb.append(", mdSellPnL=").append(mSellPnL);
        sb.append(", mdTotalPnL=").append(mGrossTotalPnL);
        sb.append(", miPositionsCount=").append(mPositionsCount);
        sb.append(", mlAmountBuy=").append(mAmountBuy);
        sb.append(", mlAmountSell=").append(mAmountSell);
        sb.append(", mlTotalAmount=").append(mTotalAmount);
        sb.append(", msSymbol='").append(mSymbol).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
    @Override
	public String getKey() {
		return mSymbol;
	}
    
    @Override
	public String getSelSql() {
		return null;
	}
    
    @Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
		return null;
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
		SYMBOL(FieldType.STRING, "", SwingConstants.LEFT),
		SUMMARY_PNL_SELL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		SUMMARY_AMOUNT_SELL(FieldType.INT, "", SwingConstants.RIGHT),
		SUMMARY_AVG_SELL(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		SUMMARY_PNL_BUY(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		SUMMARY_AMOUNT_BUY(FieldType.INT, "", SwingConstants.RIGHT),
		SUMMARY_AVG_BUY(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		SUMMARY_SELL_RATE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		SUMMARY_BUY_RATE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		SUMMARY_POSITIONS_COUNT(FieldType.INT, "", SwingConstants.RIGHT),
		SUMMARY_AMOUNT(FieldType.INT, "", SwingConstants.RIGHT),
		SUMMARY_GROSS_PL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		SUMMARY_NET_PL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT);
		
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
		String digitsStr = Utils.getFormatStr(BenchApp.getInst().getTradeDesk().getCurrencyFractionDigits(), '.', '0');
		for (int i = 0; i < fieldDefArray.length; i++) {
			Field field = new Field(i);
			field.setFieldName(fieldDefArray[i].name());
			field.setFieldType(fieldDefArray[i].getFieldType());
			if (fieldDefArray[i] == FieldDef.SUMMARY_PNL_SELL ||
				fieldDefArray[i] == FieldDef.SUMMARY_PNL_BUY || 
				fieldDefArray[i] == FieldDef.SUMMARY_GROSS_PL ||
				fieldDefArray[i] == FieldDef.SUMMARY_NET_PL) {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat() + digitsStr);
			} else {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			}
			fieldList.add(field);
		}
	}
}
