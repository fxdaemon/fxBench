package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.ChartSchema;

public class TPriceBar extends BaseEntity implements Comparable<TPriceBar>
{
	private static final Log logger = LogFactory.getLog(TPriceBar.class);
	
	public static final Interval[] INTERVAL_LIST = {
		Interval.T, Interval.m1, 
		Interval.m5, Interval.m15, Interval.m30, 
		Interval.H1, Interval.H2, Interval.H3, 
		Interval.H4, Interval.H6, Interval.H8, 
		Interval.D1, Interval.W1, Interval.M1};
	public static final String[] SHOW_INTERVAL_LIST = {
		Interval.m1.name(), Interval.m5.name(), Interval.m15.name(),
		Interval.m30.name(), Interval.H1.name(), Interval.H2.name(),
		Interval.H3.name(), Interval.H4.name(), Interval.H6.name(),
		Interval.H8.name(), Interval.D1.name()};
	public static final Interval INTERVAL_MIN = Interval.T;
	public static final Interval INTERVAL_MAX = Interval.M1;
	
	public static final int PRICE_ASK = 0;
	public static final int PRICE_BID = 1;
	public static final int PRICE_ASK_CLOSE = 0;	//The close price of the ask bar
	public static final int PRICE_ASK_HIGH  = 1;	//The highest price of the ask bar
	public static final int PRICE_ASK_LOW   = 2;	//The lowest price of the ask bar
	public static final int PRICE_ASK_OPEN  = 3;	//The open price of the ask bar or ask tic
	public static final int PRICE_BID_CLOSE = 4;	//The highest price of the bid bar
	public static final int PRICE_BID_HIGH  = 5;	//The highest price of the bid bar
	public static final int PRICE_BID_LOW   = 6;	//The lowest price of the bid bar
	public static final int PRICE_BID_OPEN  = 7;	//The open price of the bid bar or bid tic
	public static final int PRICE_ASK_MEDIAN = 8;	//Median price of the ask bar, (high+low)/2. 
	public static final int PRICE_ASK_TYPICAL = 9;	//Typical price of the ask bar, (high+low+close)/3. 
	public static final int PRICE_ASK_WEIGHTED = 10;//Weighted close price of the ask bar, (high+low+close+close)/4. 
	public static final int PRICE_BID_MEDIAN = 11;	//Median price of the bid bar, (high+low)/2. 
	public static final int PRICE_BID_TYPICAL = 12;	//Typical price of the bid bar, (high+low+close)/3. 
	public static final int PRICE_BID_WEIGHTED = 13;//Weighted close pric of the bid bare, (high+low+close+close)/4.
	private static final String DELIM = ",";
	
	private String symbol;	//The symbol indicating the instrument. For example, EUR/USD, USD/JPY, GBP/USD.
	private Interval interval;//The period of the price grouping.
							// "t" - Ticks. The number is always 1 for ticks.
							// "m" - Minutes. m1 (1 minute), m5 (five minutes), m15 (fifteen minutes), m30 (thirty minutes).
							// "H" - Hours. H1 (1 hour) only periods is supported.
							// "D" - Days. D1 (1 day) only periods is supported.
							// "W" - Weeks. W1 (1 week) only periods is supported.
							// "M" - Months. M1 (1 month) only periods is supported
	private Date startDate;	//The date and time of the begin of the period.
							//m1 bars will start at the whole minute (hh:mm:00), 
							//H1 bars will start from at whole hour (hh:00:00), 
							//m30 bars will start either at whole hour or at half-of-hour (hh:00:00 or hh:30:00). 
							//The day, weeks and months starts with the trading day instead of calendar day.
	private double askClose;//The close price of the ask bar.
	private double askHigh;	//The highest price of the ask bar.
	private double askLow;	//The lowest price of the ask bar.
	private double askOpen;	//The open price of the ask bar or ask tick.
	private double bidClose;//The close price of the bid bar.
	private double bidHigh;	//The highest prices of the bid bar.
	private double bidLow;	//The lowest price of the bid bar.
	private double bidOpen;	//The open price of the bid bar or bid tick.
	private FieldDefStub<FieldDef> fieldDefStub;
	
	public TPriceBar() {
	}

	public TPriceBar(FieldDefStub<FieldDef> fieldDefStub) {
		initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.SYMBOL), symbol);
		
		String precisionStr = Utils.getFormatStr(
				BenchApp.getInst().getTradeDesk().getTradingServerSession().getSymbolPrecision(symbol), '.', '#');
		int fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_OPEN);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_HIGH);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_LOW);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_CLOSE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_OPEN);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_HIGH);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_LOW);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_CLOSE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_MEDIAN);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_TYPICAL);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_WEIGHTED);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_MEDIAN);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_TYPICAL);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_WEIGHTED);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_DATE), startDate);
	}
	public Interval getInterval() {
		return interval;
	}
	public void setInterval(Interval interval) {
		this.interval = interval;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_INTERVAL), interval);
	}
	public double getAskClose() {
		return askClose;
	}
	public void setAskClose(double askClose) {
		this.askClose = askClose;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_CLOSE), askClose);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_TYPICAL), getAskTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_WEIGHTED), getAskWeighted());
	}
	public double getAskHigh() {
		return askHigh;
	}
	public void setAskHigh(double askHigh) {
		this.askHigh = askHigh;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_HIGH), askHigh);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_MEDIAN), getAskMedian());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_TYPICAL), getAskTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_WEIGHTED), getAskWeighted());
	}
	public double getAskLow() {
		return askLow;
	}
	public void setAskLow(double askLow) {
		this.askLow = askLow;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_LOW), askLow);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_MEDIAN), getAskMedian());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_TYPICAL), getAskTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_WEIGHTED), getAskWeighted());
	}
	public double getAskOpen() {
		return askOpen;
	}
	public void setAskOpen(double askOpen) {
		this.askOpen = askOpen;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_ASK_OPEN), askOpen);
	}
	public double getBidClose() {
		return bidClose;
	}
	public void setBidClose(double bidClose) {
		this.bidClose = bidClose;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_CLOSE), bidClose);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_TYPICAL), getBidTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_WEIGHTED), getBidWeighted());
		
	}
	public double getBidHigh() {
		return bidHigh;
	}
	public void setBidHigh(double bidHigh) {
		this.bidHigh = bidHigh;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_HIGH), bidHigh);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_MEDIAN), getBidMedian());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_TYPICAL), getBidTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_WEIGHTED), getBidWeighted());
	}
	public double getBidLow() {
		return bidLow;
	}
	public void setBidLow(double bidLow) {
		this.bidLow = bidLow;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_LOW), bidLow);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_MEDIAN), getBidMedian());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_TYPICAL), getBidTypical());
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_WEIGHTED), getBidWeighted());
	}
	public double getBidOpen() {
		return bidOpen;
	}
	public void setBidOpen(double bidOpen) {
		this.bidOpen = bidOpen;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.PRICEBAR_BID_OPEN), bidOpen);
	}
	
	public double getAskMedian() {
		return (askHigh + askLow) / 2;
	}

	public double getAskTypical() {
		return (askHigh + askLow + askClose) / 3;
	}

	public double getAskWeighted() {
		return (askHigh + askLow + 2 * askClose) / 4;
	}

	public double getBidMedian() {
		return (bidHigh + bidLow) / 2;
	}

	public double getBidTypical() {
		return (bidHigh + bidLow + bidClose) / 3;
	}

	public double getBidWeighted() {
		return (bidHigh + bidLow + 2 * bidClose) / 4;
	}
	
	public double getPrice(int priceKbn) {
		if (priceKbn == PRICE_ASK_CLOSE) {
			return askClose;
		} else if (priceKbn == PRICE_ASK_HIGH) {
			return askHigh;
		} else if (priceKbn == PRICE_ASK_LOW) {
			return askLow;
		} else if (priceKbn == PRICE_ASK_OPEN) {
			return askOpen;
		} else if (priceKbn == PRICE_BID_CLOSE) {
			return bidClose;
		} else if (priceKbn == PRICE_BID_HIGH) {
			return bidHigh;
		} else if (priceKbn == PRICE_BID_LOW) {
			return bidLow;
		} else if (priceKbn == PRICE_BID_OPEN) {
			return bidOpen;
		} else if (priceKbn == PRICE_ASK_MEDIAN) {
			return getAskMedian();
		} else if (priceKbn == PRICE_ASK_TYPICAL) {
			return getAskTypical();
		} else if (priceKbn == PRICE_ASK_WEIGHTED) {
			return getAskWeighted();
		} else if (priceKbn == PRICE_BID_MEDIAN) {
			return getBidMedian();
		} else if (priceKbn == PRICE_BID_TYPICAL) {
			return getBidTypical();
		} else if (priceKbn == PRICE_BID_WEIGHTED) {
			return getBidWeighted();
		} else {
			return 0;
		}
	}
	
	public void updateByOffer(TOffer offer) {
		setAskClose(offer.getAsk());
		if (askHigh < offer.getAsk()) {
			setAskHigh(offer.getAsk());
		}
		if (askLow > offer.getAsk()) {
			setAskLow(offer.getAsk());
		}
		
		setBidClose(offer.getBid());
		if (bidHigh < offer.getBid()) {
			setBidHigh(offer.getBid());
		}
		if (bidLow > offer.getBid()) {
			setBidLow(offer.getBid());
		}
	}
	
	@Override
	public String getKey() {
		return symbol + ChartSchema.DELIM + interval;
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
	public int compareTo(TPriceBar priceBar) {
		return this.startDate.compareTo(priceBar.getStartDate());
	}
	
	public static TPriceBar valueOf(String symbol, Interval interval, String lineData) {
		String[] splitStr = lineData.split(DELIM);
		if (splitStr.length == 9) {
			try {
				TPriceBar priceBar = new TPriceBar();
				priceBar.symbol = symbol;
				priceBar.interval = interval;
				priceBar.startDate = Utils.praseDateStr(splitStr[0], TimeZone.getTimeZone("UTC"));
				priceBar.askOpen = Double.valueOf(splitStr[1]);
				priceBar.askHigh = Double.valueOf(splitStr[2]);
				priceBar.askLow = Double.valueOf(splitStr[3]);
				priceBar.askClose = Double.valueOf(splitStr[4]);
				priceBar.bidOpen = Double.valueOf(splitStr[5]);
				priceBar.bidHigh = Double.valueOf(splitStr[6]);
				priceBar.bidLow = Double.valueOf(splitStr[7]);
				priceBar.bidClose = Double.valueOf(splitStr[8]);
				return priceBar;
			} catch (ParseException pe) {
				logger.error(pe.getMessage());
				return null;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
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
		PRICEBAR_INTERVAL(FieldType.STRING, "", SwingConstants.LEFT),
		PRICEBAR_DATE(FieldType.DATE, "yyyy/MM/dd HH:mm:ss", SwingConstants.LEFT),
		PRICEBAR_ASK_OPEN(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_HIGH(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_LOW(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_CLOSE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_OPEN(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_HIGH(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_LOW(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_CLOSE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_MEDIAN(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_TYPICAL(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_ASK_WEIGHTED(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_MEDIAN(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_TYPICAL(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		PRICEBAR_BID_WEIGHTED(FieldType.DOUBLE, "#", SwingConstants.RIGHT);
		
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
	
//	public static String[] getIntervalList() {
//		String[] intervals = new String[13];
//		intervals[0] = Interval.m1.name();
//		intervals[1] = Interval.m5.name();
//		intervals[2] = Interval.m15.name();
//		intervals[3] = Interval.m30.name();
//		intervals[4] = Interval.H1.name();
//		intervals[5] = Interval.H2.name();
//		intervals[6] = Interval.H3.name();
//		intervals[7] = Interval.H4.name();
//		intervals[8] = Interval.H6.name();
//		intervals[9] = Interval.H8.name();
//		intervals[10] = Interval.D1.name();
//		intervals[11] = Interval.W1.name();
//		intervals[12] = Interval.M1.name();
//		return intervals;
//	}
	
	public enum Interval {
		un("unknown", 0),
		T("Tick", 1),
		m1("One Minute", 1 * 60),
		m5("Five Minutes", 5 * 60),
		m15("15 Minutes", 15 * 60),
		m30("30 Minutes", 30 * 60),
		H1("1 Hour", 3600),
		H2("2 Hour", 2 * 3600),
		H3("3 Hour", 3 * 3600),
		H4("4 Hour", 4 * 3600),
		H6("6 Hour", 6 * 3600),
		H8("8 Hour", 8 * 3600),
		D1("1 Day", 24 * 3600),
		W1("1 Week", 7 * 24 * 3600),
		M1("1 Month", 30 * 24 * 3600);
		
		private String text;
		private int seconds;
		
		private Interval(String t, int s) {
			text = t;
			seconds = s;
		}
		public String getText() {
			return text;
		}
		public int getSeconds() {
			return seconds;
		}
		public int getMilliSecond() {
			return seconds * 1000;
		}
		public double multiple(Interval interval) {
			return seconds / interval.getSeconds();
		}
	}

	static public Interval IntervalValueOfName(String name) {
		Interval interval = Interval.un;
		if (name != null) {
			for (int i = 0; i < INTERVAL_LIST.length; i++) {
				if (INTERVAL_LIST[i].name().equals(name)) {
					interval = INTERVAL_LIST[i];
					break;
				}
			}
		}
		return interval;
	}
}
