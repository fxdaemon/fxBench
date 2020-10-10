package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.SerialVersion;
import org.fxbench.util.Utils;

public class TOffer extends BaseEntity {

	private static final long serialVersionUID = SerialVersion.APPVERSION;
	
	private String offerID;			//The unique number of the instrument.
	private String symbol;			//The symbol indicating the instrument. For example, EUR/USD, USD/JPY, GBP/USD.
	private int symbolType;			//The type of the instrument. Possible values:
	private double bid;				//The current market price the instrument can be sold at.
	private double ask;				//The current market price the instrument can be bought at.
	private double high;			//The highest Ask price (buy price) of the instrument for the current trading day. The trading days are from 17:00 to 17:00 EST.
	private double low;				//The lowest Bid price (sell price) of the instrument for the current trading day. The trading days are from 17:00 to 17:00 EST.
	private double intrS;			//The interest that is added to/subtracted from the account for holding overnight a 1 lot sell position opened in this instrument. The interest is expressed in the account currency.
	private double intrB;			// The interest that is added to/subtracted from the account for holding overnight a 1 lot buy position opened in this instrument. The interest is expressed in the account currency.
	private String contractCurrency;//The base currency of the instrument (EUR - in EUR/USD, USD - in USD/JPY, GBP - in GBP/USD, etc).
//	private int contractSize;		//The legacy lot size in the base currency of the instrument which is supported for the backward compatibility. Always equals 1.
	private int digits;				//The number of digits after the decimal point to which the price of the instrument is rounded in calculations. For example, it is 5 for EUR/USD and GBP/USD, and 3 for USD/JPY.
//	private int defaultSortOrder;	//The sequence number of the instrument in the list of instruments displayed to the Trading Station user.
	private double pipCost;			//The cost of one pip per lot. The cost is expressed in the account currency. PipCost is used for calculation of the P/L value in account currency.
//	private double mmr;				//Minimum Margin Requirement. The amount of funds which must be allocated for trading 1 lot in this instrument and depends on leverage. 
									//The lot size is defined by BaseUnitSize (see the Accounts table). The minimum margin requirement is expressed in the account currency. 
									//For example, for $100,000 lot size and 1:100 leverage the minimum margin requirement is $1,000.
	private Date time;				//The date and time of the last update of the instrument. The date and time are in the UTC time.
//	private int bidChangeDirection;	//The direction the Bid price (sell price) of the instrument is changing in. 
//	private int askChangeDirection;	//The direction the Ask price (buy price) of the instrument is changing in. 
//	private int hiChangeDirection;	//The direction the highest Ask price (buy price) of the instrument is changing in.
//	private int lowChangeDirection;	//The direction the lowest Bid price (sell price) of the instrument is changing in.
//	private String quoteID;			//The unique number of the pair of prices (Bid and Ask) the instrument can be traded at.
//	private String bidID;			//The unique number of the Bid price.
//	private String askID;			//The unique number of the Ask price.
//	private Date bidExpireDate;		//The date and time up to which the Bid price is available for trading. The date and time are in the UTC time.
//	private Date askExpireDate;		//The date and time up to which the Ask price is available for trading. The date and time are in the UTC time.
	private boolean bidTradable;		//Defines whether the Bid price of the instrument is available for trading.
	private boolean askTradable;		//Defines whether the Ask price of the instrument is available for trading.
	private double pointSize;		//A size of a pip. The PointSize is a decimal number. For example, it is 0.0001 for EUR/USD, GBP/USD, and USD/CHF, and 0.01 for USD/JPY. 
									//Please note, to define the minimal possible change of the price, use the Digits column of the offers table.
	
	private double oldBid;
	private double oldAsk;
	private boolean subscribed;	//is rate subscribed on server
	private boolean tradable;	
	private FieldDefStub<FieldDef> fieldDefStub;
	
	public TOffer() {
	}

	public TOffer(FieldDefStub<FieldDef> fieldDefStub) {
		initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
	}
	
	public TOffer(FieldDefStub<FieldDef> fieldDefStub, TOffer src) {
		this(fieldDefStub);
		setOfferId(src.offerID);
		setPointSize(src.pointSize);
		setSymbol(src.symbol);
		setSymbolType(src.symbolType);
		setBid(src.bid);
		setAsk(src.ask);
		setHigh(src.high);
		setLow(src.low);
		setIntrS(src.intrS);
		setIntrB(src.intrB);
		setContractCurrency(src.contractCurrency);
//		setContractSize(src.contractSize);
		setDigits(src.digits);
//		setDefaultSortOrder(src.defaultSortOrder);
		setPipCost(src.pipCost);
		setTime(src.time);
//		setBidChangeDirection(src.bidChangeDirection);
//		setAskChangeDirection(src.askChangeDirection);
//		setHiChangeDirection(src.hiChangeDirection);
//		setLowChangeDirection(src.lowChangeDirection);
//		setQuoteID(src.quoteID);
//		setBidID(src.bidID);
//		setAskID(src.askID);
//		setBidExpireDate(src.bidExpireDate);
//		setAskExpireDate(src.askExpireDate);
		setBidTradable(src.bidTradable);
		setAskTradable(src.askTradable);
	}
	
	public String getOfferId() {
		return offerID;
	}
	public void setOfferId(String offerID) {
		this.offerID = offerID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ID), offerID);
	}
	public String getSymbol() {
		return symbol;
	}
	public String getCurrency() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.SYMBOL), symbol);
		
		int precision = BenchApp.getInst().getTradeDesk().getTradingServerSession().getSymbolPrecision(symbol);
		if (precision == 0) {
			precision = Utils.getPrecision(pointSize);
		}
		String precisionStr = Utils.getFormatStr(precision, '.', '#');
		int fieldNo = fieldDefStub.getFieldNo(FieldDef.OFFER_ASK);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.OFFER_BID);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.OFFER_HIGH);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.OFFER_LOW);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.OFFER_POINT_SIZE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		
	}
	public int getSymbolType() {
		return symbolType;
	}
	public void setSymbolType(int symbolType) {
		this.symbolType = symbolType;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.SYMBOL_TYPE), symbolType);
	}
	public double getBid() {
		return bid;
	}
	public double getSellPrice() {
		return bid;
	}
	public void setBid(double bid) {
		oldBid = this.bid;
		this.bid = bid;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_BID), bid);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_SPREAD), getSpread());
	}
	public double getAsk() {
		return ask;
	}
	public double getBuyPrice() {
		return ask;
	}
	public void setAsk(double ask) {
		oldAsk = this.ask;
		this.ask = ask;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK), ask);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_SPREAD), getSpread());
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_HIGH), high);
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_LOW), low);
	}
	public double getIntrS() {
		return intrS;
	}
	public void setIntrS(double intrS) {
		this.intrS = intrS;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_INTR_S), intrS);
	}
	public double getIntrB() {
		return intrB;
	}
	public void setIntrB(double intrB) {
		this.intrB = intrB;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_INTR_B), intrB);
	}
	public String getContractCurrency() {
		return contractCurrency;
	}
	public void setContractCurrency(String contractCurrency) {
		this.contractCurrency = contractCurrency;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_CONTRACT_CURRENCY), contractCurrency);
	}
//	public int getContractSize() {
//		return contractSize;
//	}
//	public void setContractSize(int contractSize) {
//		this.contractSize = contractSize;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_CONTRACT_SIZE), contractSize);
//	}
	public int getDigits() {
		return digits;
	}
	public void setDigits(int digits) {
		this.digits = digits;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_DIGITS), digits);
	}
//	public int getDefaultSortOrder() {
//		return defaultSortOrder;
//	}
//	public void setDefaultSortOrder(int defaultSortOrder) {
//		this.defaultSortOrder = defaultSortOrder;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_DEFAULT_SORT_ORDER), defaultSortOrder);
//	}
	public double getPipCost() {
		return pipCost;
	}
	public void setPipCost(double pipCost) {
		this.pipCost = pipCost;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_PIP_COST), pipCost);
	}
//	public double getMmr() {
//		return mmr;
//	}
//	public void setMmr(double mmr) {
//		this.mmr = mmr;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_MMR), mmr);
//	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_TIME), time);
	}
//	public int getBidChangeDirection() {
//		return bidChangeDirection;
//	}
//	public void setBidChangeDirection(int bidChangeDirection) {
//		this.bidChangeDirection = bidChangeDirection;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_BID_CHANGE_DIRECTION), bidChangeDirection);
//	}
//	public int getAskChangeDirection() {
//		return askChangeDirection;
//	}
//	public void setAskChangeDirection(int askChangeDirection) {
//		this.askChangeDirection = askChangeDirection;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK_CHANGE_DIRECTION), askChangeDirection);
//	}
//	public int getHiChangeDirection() {
//		return hiChangeDirection;
//	}
//	public void setHiChangeDirection(int hiChangeDirection) {
//		this.hiChangeDirection = hiChangeDirection;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_HI_CHANGE_DIRECTION), hiChangeDirection);
//	}
//	public int getLowChangeDirection() {
//		return lowChangeDirection;
//	}
//	public void setLowChangeDirection(int lowChangeDirection) {
//		this.lowChangeDirection = lowChangeDirection;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_LOW_CHANGE_DIRECTION), lowChangeDirection);
//	}
//	public String getQuoteID() {
//		return quoteID;
//	}
//	public void setQuoteID(String quoteID) {
//		this.quoteID = quoteID;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_QUOTE_ID), quoteID);
//	}
//	public String getBidID() {
//		return bidID;
//	}
//	public void setBidID(String bidID) {
//		this.bidID = bidID;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_BID_ID), bidID);
//	}
//	public String getAskID() {
//		return askID;
//	}
//	public void setAskID(String askID) {
//		this.askID = askID;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK_ID), askID);
//	}
//	public Date getBidExpireDate() {
//		return bidExpireDate;
//	}
//	public void setBidExpireDate(Date bidExpireDate) {
//		this.bidExpireDate = bidExpireDate;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_BID_EXPIRE_DATE), bidExpireDate);
//	}
//	public Date getAskExpireDate() {
//		return askExpireDate;
//	}
//	public void setAskExpireDate(Date askExpireDate) {
//		this.askExpireDate = askExpireDate;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK_EXPIRE_DATE), askExpireDate);
//	}
	public boolean getBidTradable() {
		return bidTradable;
	}
	public boolean isSellTradable() {
		return bidTradable;
	}
	public void setBidTradable(boolean bidTradable) {
		this.bidTradable = bidTradable;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_BID_TRADABLE), bidTradable);
	}
	public boolean getAskTradable() {
		return askTradable;
	}
	public boolean isBuyTradable() {
		return askTradable;
	}
	public void setAskTradable(boolean askTradable) {
		this.askTradable = askTradable;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK_TRADABLE), askTradable);
	}
	public double getPointSize() {
		return pointSize;
	}
	public void setPointSize(double pointSize) {
		this.pointSize = pointSize;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.OFFER_POINT_SIZE), pointSize);
	}
	
	public double getOldBid() {
		return oldBid;
	}

	public void setOldBid(double oldBid) {
		this.oldBid = oldBid;
	}

	public double getOldAsk() {
		return oldAsk;
	}

	public void setOldAsk(double oldAsk) {
		this.oldAsk = oldAsk;
	}

	/**
	 * @return the subscribed
	 */
	public boolean isSubscribed() {
		return subscribed;
	}
	/**
	 * @param subscribed the subscribed to set
	 */
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
	
	public boolean isTradable() {
		return tradable;
	}

	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	public double getAverage() {
		return (ask + bid) / 2;
	}
	
	public double getOpenPrice(BnS bs) {
		return bs == BnS.BUY ? ask : bid;
	}
	
	public double getClosePrice(BnS bs) {
		return bs == BnS.BUY ? bid : ask;
	}
	
	public double getSpread() {
		try {
			return pointSize == 0 ? 0 : (ask - bid) / pointSize;
		} catch (Exception e) {
            return 0;
        }
	}
	
	public String getRateFormatPattern() {
		return getFieldFormat(fieldDefStub.getFieldNo(FieldDef.OFFER_ASK));
	}
	
	public String getRateFormatText(double rate) {
		DecimalFormat decimalFormat = new DecimalFormat(getRateFormatPattern());
		return decimalFormat.format(rate);
	}
	
	public static String getRateFormatPattern(String currency) {
		return "#" + Utils.getFormatStr(
				BenchApp.getInst().getTradeDesk().getTradingServerSession().getSymbolPrecision(currency), '.', '#');
	}
	
	public static String getRateFormatText(String currency, double rate) {
		DecimalFormat decimalFormat = new DecimalFormat(getRateFormatPattern(currency));
		return decimalFormat.format(rate);
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Rate");
//        sb.append("{mBuyBlocked=").append(mBuyBlocked);
        sb.append(", mBuyInterest=").append(intrB);
//        sb.append(", mBuyLID=").append(mBuyLID);
//        sb.append(", mBuyPID=").append(mBuyPID);
        sb.append(", mBuyPrice=").append(ask);
        sb.append(", mBuyTradable=").append(askTradable);
        sb.append(", mContractCurrency='").append(contractCurrency).append('\'');
//        sb.append(", mContractMultiplier=").append(mContractMultiplier);
//        sb.append(", mContractSize=").append(contractSize);
        sb.append(", mCurrency='").append(symbol).append('\'');
//        sb.append(", mFXCMCondDistEntryLimit=").append(mFXCMCondDistEntryLimit);
//        sb.append(", mFXCMCondDistEntryStop=").append(mFXCMCondDistEntryStop);
//        sb.append(", mFXCMCondDistLimit=").append(mFXCMCondDistLimit);
//        sb.append(", mFXCMCondDistStop=").append(mFXCMCondDistStop);
//        sb.append(", mFXCMMaxQuantity=").append(mFXCMMaxQuantity);
//        sb.append(", mFXCMMinQuantity=").append(mFXCMMinQuantity);
//        sb.append(", mFXCMTradingStatus='").append(mFXCMTradingStatus).append('\'');
        sb.append(", mHighPrice=").append(high);
        sb.append(", mID=").append(offerID);
        sb.append(", mLastDate=").append(time);
        sb.append(", mLowPrice=").append(low);
//        sb.append(", mOldBuyPrice=").append(mOldBuyPrice);
//        sb.append(", mOldSellPrice=").append(mOldSellPrice);
//        sb.append(", mOpenAsk=").append(mOpenAsk);
//        sb.append(", mOpenBid=").append(mOpenBid);
        sb.append(", mPipCost=").append(pipCost);
//        sb.append(", mProduct=").append(mProduct);
//        sb.append(", mQuoteID='").append(quoteID).append('\'');
//        sb.append(", mSellBocked=").append(mSellBocked);
        sb.append(", mSellInterest=").append(intrS);
//        sb.append(", mSellLID=").append(mSellLID);
//        sb.append(", mSellPID=").append(mSellPID);
        sb.append(", mSellPrice=").append(bid);
        sb.append(", mSellTradable=").append(bidTradable);
        sb.append(", mSubscribed=").append(subscribed);
//          sb.append(", mTradable=").append(mTradable);
        sb.append('}');
        return sb.toString();
    }
	
	@Override
	public String getKey() {
		return symbol;
	}
	
	@Override
	public String getSelSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM `Offer`");
		return sb.toString();
	}
	
	@Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
		TOffer offer = new TOffer();
		offer.offerID = resultSet.getString("OfferID");
		offer.symbol = resultSet.getString("Symbol");
		offer.symbolType = resultSet.getInt("SymbolType");
		offer.bid = resultSet.getDouble("Bid");
		offer.ask = resultSet.getDouble("Ask");
		offer.high = resultSet.getDouble("Hi");
		offer.low = resultSet.getDouble("Low");
		offer.intrS = resultSet.getDouble("IntrS");
		offer.intrB = resultSet.getDouble("IntrB");
		offer.contractCurrency = resultSet.getString("ContractCurrency");
//		offer.contractSize = resultSet.getInt("ContractSize");
		offer.digits = resultSet.getInt("Digits");
//		offer.defaultSortOrder = resultSet.getInt("DefaultSortOrder");
		offer.pipCost = resultSet.getDouble("PipCost");
//		offer.mmr = resultSet.getDouble("MMR");
		offer.time = resultSet.getTimestamp("Time");
//		offer.bidChangeDirection = resultSet.getInt("BidChangeDirection");
//		offer.askChangeDirection = resultSet.getInt("AskChangeDirection");
//		offer.hiChangeDirection = resultSet.getInt("HiChangeDirection");
//		offer.lowChangeDirection = resultSet.getInt("LowChangeDirection");
//		offer.quoteID = resultSet.getString("QuoteID");
//		offer.bidID = resultSet.getString("BidID");
//		offer.askID = resultSet.getString("AskID");
//		offer.bidExpireDate = resultSet.getTimestamp("BidExpireDate");
//		offer.askExpireDate = resultSet.getTimestamp("AskExpireDate");
		offer.bidTradable = resultSet.getBoolean("BidTradable");
		offer.askTradable = resultSet.getBoolean("AskTradable");
		offer.pointSize = resultSet.getDouble("PointSize");
		return offer;
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
		OFFER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		SYMBOL(FieldType.STRING, "", SwingConstants.LEFT),
		SYMBOL_TYPE(FieldType.INT, "", SwingConstants.RIGHT),
		OFFER_ASK(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		OFFER_BID(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		OFFER_SPREAD(FieldType.DOUBLE, "#.#", SwingConstants.RIGHT),
		OFFER_HIGH(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		OFFER_LOW(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		OFFER_INTR_B(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		OFFER_INTR_S(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		OFFER_CONTRACT_CURRENCY(FieldType.STRING, "", SwingConstants.LEFT),
//		OFFER_CONTRACT_SIZE(FieldType.INT, "", SwingConstants.RIGHT),
		OFFER_DIGITS(FieldType.INT, "", SwingConstants.RIGHT),
//		OFFER_DEFAULT_SORT_ORDER(FieldType.STRING, "", SwingConstants.LEFT),
		OFFER_PIP_COST(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
//		OFFER_MMR(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		OFFER_TIME(FieldType.DATE, "HH:mm:ss", SwingConstants.RIGHT),
//		OFFER_BID_CHANGE_DIRECTION(FieldType.INT, "", SwingConstants.RIGHT),
//		OFFER_ASK_CHANGE_DIRECTION(FieldType.INT, "", SwingConstants.RIGHT),
//		OFFER_HI_CHANGE_DIRECTION(FieldType.INT, "", SwingConstants.RIGHT),
//		OFFER_LOW_CHANGE_DIRECTION(FieldType.INT, "", SwingConstants.RIGHT),
//		OFFER_QUOTE_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		OFFER_BID_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		OFFER_ASK_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		OFFER_BID_EXPIRE_DATE(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.RIGHT),
//		OFFER_ASK_EXPIRE_DATE(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.RIGHT),
		OFFER_BID_TRADABLE(FieldType.STRING, "", SwingConstants.RIGHT),
		OFFER_ASK_TRADABLE(FieldType.STRING, "", SwingConstants.RIGHT),
		OFFER_POINT_SIZE(FieldType.DOUBLE, "#", SwingConstants.RIGHT);
		
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
			if (fieldDefArray[i] == FieldDef.OFFER_INTR_B ||
				fieldDefArray[i] == FieldDef.OFFER_INTR_S || 
				fieldDefArray[i] == FieldDef.OFFER_PIP_COST /*||
				fieldDefArray[i] == FieldDef.OFFER_MMR*/) {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat() + digitsStr);
			} else {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			}
			fieldList.add(field);
		}
	}
}
