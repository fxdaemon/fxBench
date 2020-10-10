package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.Utils;

public class TPosition extends BaseEntity
{
	private String tradeID;		//The unique number of the open position. The number is unique within the connection (Real or Demo).
	private String accountId;	//The unique number of the account the position is opened on. The number is unique within the connection (Real or Demo).
//	private String accountName;	//The unique name of the account the position is opened on. This is the name that is displayed to the Trading Station user. The name is unique within the connection (Real or Demo).
	private String offerID;		//The unique number of the instrument the position is opened in.
	private String symbol;		//The symbol indicating the instrument the position is opened in. For example, EUR/USD, USD/JPY, GBP/USD.
//	private int lot;			//The amount of the open position expressed in the base currency. For example, 
								//the value 20,000 for EUR/USD indicates that the total amount of the position is 20,000 Euros, for USD/JPY - 20,000 US dollars, etc.
	private double amount;		//The amount of the open position (in thousands) as specified by the Trading Station user. For example, 
								//the value 20 for EUR/USD indicates that the total amount of the position is 20,000 Euros, for USD/JPY - 20,000 US dollars, etc.
	private BnS bs;				//The trade operation the position is opened by. Possible values: "B" - buy, "S" - sell.
	private double open;		//The price the position is opened at.
	private double close;		//The price at which the position can be closed at the current moment.
	private double stop;		//The price of the associated stop order (loss limit level). If there is no associated stop order, the property has the value "0".
	private double untTrlMove;	//The distance (in pips) between the current market price and the price at which the price of the trailing stop order will be changed to the next level. 
	private double limit;		//The price of the associated limit order (profit limit level). If there is no associated limit order, the property has the value "0".
	private double high;
	private double low;
	private double pl;			//The current profit/loss on the position in pips.
	private double grossPL;		//The current profit/loss on the position in the account currency.
	private double netPL;
	private double com;			//The commission, i.e. the amount of funds that is subtracted from the account for various reasons which are defined individually 
	private double interest;	//The interest, i.e. the cumulative amount of funds that is added to/subtracted from the account for holding the position overnight. The interest is expressed in the account currency.
 	private Date openTime;		//The date and time when the position was opened. The date and time are in the UTC time.
	private Date closeTime;		//The date and time when the position was closed. The date and time are in the UTC time.
 	private String kind;		//The type of the account the position is opened on. 
 								// "32" - Trading account.
								// "36" - Managed account.
								// "38" - Controlled account.
	private String quoteID;		//The unique number of the pair of prices (Bid and Ask) the position is opened at.
	private String openOrderID;	//The unique number of the order the position is opened by. The number is unique within the connection (Real or Demo).
	private String openOrderReqID;	//The unique identifier of the order request the position is opened by. The key is unique within the connection (Real or Demo).
	private String closeOrderID;	//The unique number of the order the position was closed by.
	private String closeOrderReqID;	//The unique identifier of the order request the position was closed by.
//	private String oQTXT;			//The text comment added to the order the position is opened by.
//	private String cQTXT;		//The text comment added to the order the position was closed by.
	private String stopOrderID;	//The unique number of the associated stop order. The number is unique within the connection (Real or Demo). 
	private String limitOrderID;//The unique number of the associated limit order. The number is unique within the connection (Real or Demo). 
	
	private double trailingRate;
	private int trailStop;
	private double usedMargin;
	private Stage stage;
	private FieldDefStub<FieldDef> fieldDefStub;
	
	private String tradeMethodPeriod;
	private String tradeMethodName;
	private String trailStopPeriod;
	private String trailStopName;
	private String trailMoveTsName;
	
	public TPosition() {
	}

	public TPosition(Stage stage, FieldDefStub<FieldDef> fieldDefStub) {
    	this.stage = stage;
    	initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
    }
	
	public TPosition(Stage stage, FieldDefStub<FieldDef> fieldDefStub, TPosition src) {
		this(stage, fieldDefStub);
		setTradeID(src.tradeID);
		setAccountId(src.accountId);
//		setAccountName(src.accountName);
		setOfferID(src.offerID);
		setSymbol(src.symbol);
//		setLot(src.lot);
		setAmount(src.amount);
		setBS(src.bs);
		setOpen(src.open);
		setClose(src.close);
		setStop(src.stop);
		setUntTrlMove(src.untTrlMove);
		setLimit(src.limit);
		setHigh(src.high);
		setLow(src.low);
		setPl(src.pl);
		setGrossPL(src.grossPL);
		setNetPL(src.netPL);
		setCom(src.com);
		setInterest(src.interest);
		setOpenTime(src.openTime);
		setCloseTime(src.closeTime);
		setKind(src.kind);
		setQuoteID(src.quoteID);
		setOpenOrderID(src.openOrderID);
		setOpenOrderReqID(src.openOrderReqID);
		setCloseOrderID(src.closeOrderID);
		setCloseOrderReqID(src.closeOrderReqID);
//		setoQTXT(src.oQTXT);
//		setcQTXT(src.cQTXT);
		setStopOrderID(src.stopOrderID);
		setLimitOrderID(src.limitOrderID);		
		setTradeMethodPeriod(src.tradeMethodPeriod);
		setTradeMethodName(src.tradeMethodName);
		setTrailStopPeriod(src.trailStopPeriod);
		setTrailStopName(src.trailStopName);
		setTrailMoveTsName(src.trailMoveTsName);
	}
	
	public String getTradeID() {
		return tradeID;
	}
	public String getTicketID() {
		return tradeID;
	}
	public void setTradeID(String tradeID) {
		this.tradeID = tradeID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_ID), tradeID);
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_ID), accountId);
	}
	public void setAccountIdEx(String accountId) {
		this.accountId = accountId;
	}
//	public String getAccountName() {
//		return accountName;
//	}
	public String getAccount() {
		return accountId;
	}
//	public void setAccountName(String accountName) {
//		this.accountName = accountName;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_NAME), accountName);
//	}
	public String getOfferID() {
		return offerID;
	}
	public void setOfferID(String offerID) {
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
		
		String precisionStr = Utils.getFormatStr(
				BenchApp.getInst().getTradeDesk().getTradingServerSession().getSymbolPrecision(symbol), '.', '#');
		int fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_OPEN);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_STOP);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_LIMIT);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_HIGH);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.TRADE_LOW);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
	}
//	public int getLot() {
//		return lot;
//	}
//	public void setLot(int lot) {
//		this.lot = lot;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_LOT), lot);
//	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_AMOUNT), amount);
	}
	public BnS getBS() {
		return bs;
	}
	public void setBS(BnS bs) {
		this.bs = bs;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_BS), bs);
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_OPEN), open);
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE), close);
	}
	public double getStop() {
		return stop;
	}
	public void setStop(double stop) {
		this.stop = stop;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_STOP), stop);
	}
	public double getUntTrlMove() {
		return untTrlMove;
	}
	public void setUntTrlMove(double untTrlMove) {
		this.untTrlMove = untTrlMove;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_UNT_TRL_MOVE), untTrlMove);
	}
	public double getLimit() {
		return limit;
	}
	public void setLimit(double limit) {
		this.limit = limit;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_LIMIT), limit);
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_HIGH), high);
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_LOW), low);
	}
	public double getPl() {
		return pl;
	}
	public void setPl(double pl) {
		this.pl = pl;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_PL), pl);
	}
	public double getGrossPL() {
		return grossPL;
	}
	public void setGrossPL(double grossPL) {
		this.grossPL = grossPL;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_GROSS_PL), grossPL);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_NET_PL), getNetPnL());
	}
	public double getNetPL() {
		return netPL;
	}
	public double getNetPnL() {
		return grossPL - com + interest;
	}
	public void setNetPL(double netPL) {
		this.netPL = netPL;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_NET_PL), netPL);
	}
	public double getCom() {
		return com;
	}
	public void setCom(double com) {
		this.com = com;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_COM), com);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_NET_PL), getNetPnL());
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_INTEREST), interest);
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_NET_PL), getNetPnL());
	}
	public Date getOpenTime() {
		return openTime;
	}
	public String getOpenTimeText() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return openTime == null ? "1970-01-01" : simpleDateFormat.format(openTime);
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_OPEN_TIME), openTime);
	}
	public Date getCloseTime() {
		return closeTime;
	}
	public String getCloseTimeText() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return closeTime == null ? "1970-01-01" : simpleDateFormat.format(closeTime);
	}
	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE_TIME), closeTime);
	}
	public boolean isBuy() {
		return bs == BnS.BUY ? true : false;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_KIND), kind);
	}
	public String getQuoteID() {
		return quoteID;
	}
	public void setQuoteID(String quoteID) {
		this.quoteID = quoteID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_QUOTE_ID), quoteID);
	}
	public String getOpenOrderID() {
		return openOrderID;
	}
	public void setOpenOrderID(String openOrderID) {
		this.openOrderID = openOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_OPEN_ORDER_ID), openOrderID);
	}
	public String getOpenOrderReqID() {
		return openOrderReqID;
	}
	public void setOpenOrderReqID(String openOrderReqID) {
		this.openOrderReqID = openOrderReqID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_OPEN_ORDER_REQ_ID), openOrderReqID);
	}
	public String getCloseOrderID() {
		return closeOrderID;
	}
	public void setCloseOrderID(String closeOrderID) {
		this.closeOrderID = closeOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE_ORDER_ID), closeOrderID);
	}
	public String getCloseOrderReqID() {
		return closeOrderReqID;
	}
	public void setCloseOrderReqID(String closeOrderReqID) {
		this.closeOrderReqID = closeOrderReqID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_CLOSE_ORDER_REQ_ID), closeOrderReqID);
	}
//	public String getoQTXT() {
//		return oQTXT;
//	}
//	public void setoQTXT(String oQTXT) {
//		this.oQTXT = oQTXT;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_OQ_TXT), oQTXT);
//	}
//	public String getcQTXT() {
//		return cQTXT;
//	}
//	public void setcQTXT(String cQTXT) {
//		this.cQTXT = cQTXT;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_CQ_TXT), cQTXT);
//	}
	public String getStopOrderID() {
		return stopOrderID;
	}
	public void setStopOrderID(String stopOrderID) {
		this.stopOrderID = stopOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_STOP_ORDER_ID), stopOrderID);
	}
	public String getLimitOrderID() {
		return limitOrderID;
	}
	public void setLimitOrderID(String limitOrderID) {
		this.limitOrderID = limitOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_LIMIT_ORDER_ID), limitOrderID);
	}
	
	/**
	 * @return the trailingRate
	 */
	public double getTrailingRate() {
		return trailingRate;
	}
	/**
	 * @param trailingRate the trailingRate to set
	 */
	public void setTrailingRate(double trailingRate) {
		this.trailingRate = trailingRate;
	}
	/**
	 * @return the trailStop
	 */
	public int getTrailStop() {
		return trailStop;
	}
	/**
	 * @param trailStop the trailStop to set
	 */
	public void setTrailStop(int trailStop) {
		this.trailStop = trailStop;
	}
	
	/**
	 * @return the mUsedMargin
	 */
	public double getUsedMargin() {
		return usedMargin;
	}
	/**
	 * @param mUsedMargin the mUsedMargin to set
	 */
	public void setUsedMargin(double mUsedMargin) {
		this.usedMargin = mUsedMargin;
	}
		
	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public String getTradeMethodPeriod() {
		return tradeMethodPeriod;
	}

	public void setTradeMethodPeriod(String tradeMethodPeriod) {
		this.tradeMethodPeriod = tradeMethodPeriod;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_METHOD_PERIOD), tradeMethodPeriod);
	}

	public String getTradeMethodName() {
		return tradeMethodName;
	}

	public void setTradeMethodName(String tradeMethodName) {
		this.tradeMethodName = tradeMethodName;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_METHOD_NAME), tradeMethodName);
	}

	public String getTrailStopPeriod() {
		return trailStopPeriod;
	}

	public void setTrailStopPeriod(String trailStopPeriod) {
		this.trailStopPeriod = trailStopPeriod;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_TRAIL_STOP_PERIOD), trailStopPeriod);
	}

	public String getTrailStopName() {
		return trailStopName;
	}

	public void setTrailStopName(String trailStopName) {
		this.trailStopName = trailStopName;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_TRAIL_STOP_NAME), trailStopName);
	}

	public String getTrailMoveTsName() {
		return trailMoveTsName;
	}

	public void setTrailMoveTsName(String trailMoveTsName) {
		this.trailMoveTsName = trailMoveTsName;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_TRAIL_MOVE_TS_NAME), trailMoveTsName);
	}

	public Date getTime() {
		return stage == Stage.Open ? openTime: closeTime;
	}
	
	public boolean isBeingClosed() {
		return stage == Stage.Closed ? true : false;
	}
	
	public void resetPl(double pointSize) {
		double diff = isBuy() ? close - open : open - close;
		double pipPl = Utils.round(diff, pointSize) / pointSize;
        setPl(pipPl);
    }
	
	public double getMaxPl() {
		double pointSize = BenchApp.getInst().getTradeDesk().getTradingServerSession().getPointSize(symbol);
		if (high == 0 || low == 0 || pointSize == 0) {
			return 0;
		} else {
			return isBuy() ? (high - open) / pointSize : (open - low) / pointSize;
		}
	}
	
	public double getMinPl() {
		double pointSize = BenchApp.getInst().getTradeDesk().getTradingServerSession().getPointSize(symbol);
		if (high == 0 || low == 0 || pointSize == 0) {
			return 0;
		} else {
			return isBuy() ? (low - open) / pointSize : (open - high) / pointSize;
		}
	}

	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Position");
        sb.append("{mAccountID='").append(accountId).append('\'');
//        sb.append(", mAccount='").append(accountName).append('\'');
        sb.append(", mAmount=").append(amount);
//        sb.append(", mBatch=").append(mBatch);
        sb.append(", mClosePrice=").append(close);
        sb.append(", mCloseTime=").append(closeTime);
        sb.append(", mCommission=").append(com);
        sb.append(", mCurrency='").append(symbol).append('\'');
//        sb.append(", mCurrencyTradable=").append(mCurrencyTradable);
//        sb.append(", mCustomText='").append(oQTXT).append('\'');
//        sb.append(", mCustomText2='").append(cQTXT).append('\'');
        sb.append(", mGrossPnL=").append(grossPL);
        sb.append(", mInterest=").append(interest);
//        sb.append(", mIsBeingClosed=").append(mIsBeingClosed);
//        sb.append(", mLastRptRequested=").append(mLastRptRequested);
        sb.append(", mLimit=").append(limit);
        sb.append(", mLimitOrderID='").append(limitOrderID).append('\'');
        sb.append(", mNetPnL=").append(netPL);
        sb.append(", mOpenPrice=").append(open);
        sb.append(", mOpenTime=").append(openTime);
        sb.append(", mPipPL=").append(pl);
        sb.append(", mSide=").append(bs);
        sb.append(", mStop=").append(stop);
        sb.append(", mStopMove=").append(untTrlMove);
        sb.append(", mStopOrderID='").append(stopOrderID).append('\'');
        sb.append(", mTicketID='").append(tradeID).append('\'');
        sb.append(", mTrailStop=").append(trailStop);
        sb.append(", mTrailingRate=").append(trailingRate);
        sb.append(", mUsedMargin=").append(usedMargin);
        sb.append('}');
        return sb.toString();
    }
	
	@Override
	public String getKey() {
		return tradeID;
	}
	
	@Override
	public String getSelSql() {
		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT * FROM `Trade`");
//		sb.append(" WHERE");
//		sb.append(" `AccountId`=").append("'").append(accountId).append("'");
		sb.append("SELECT t.*");
		sb.append(",ts.TradeMethodPeriod,ts.TradeMethodName,ts.TrailStopPeriod,ts.TrailStopName,ts.TrailMoveTsName");
		sb.append(" FROM `Trade` t, `TsContext` ts");
		sb.append(" WHERE");
		sb.append(" t.AccountId=ts.AccountId");
		sb.append(" AND t.TradeID=ts.TradeID");
		sb.append(" AND t.AccountId=").append("'").append(accountId).append("'");
		if (tradeID != null && tradeID.length() > 0) {
			sb.append(" AND t.TradeID=").append("'").append(tradeID).append("'");
		}
		sb.append(" AND ts.TradeMethodName<>''");
		return sb.toString();
	}
	
	@Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
		TPosition position = new TPosition();
		position.tradeID = resultSet.getString("TradeID");	
		position.accountId = resultSet.getString("AccountId");	
//		position.accountName = resultSet.getString("AccountName");	
		position.offerID = resultSet.getString("OfferID");	
		position.symbol = resultSet.getString("Symbol");	
//		position.lot = resultSet.getInt("Lot");	
		position.amount = resultSet.getDouble("Amount");	
		position.bs = resultSet.getString("BS").equals("B") ? BnS.BUY : BnS.SELL;
		position.open = resultSet.getDouble("Open");	
		position.close = resultSet.getDouble("Close");	
		position.stop = resultSet.getDouble("Stop");	
		position.untTrlMove = resultSet.getDouble("UntTrlMove");	
		position.limit = resultSet.getDouble("Limit");	
		position.high = resultSet.getDouble("High");	
		position.low = resultSet.getDouble("Low");	
		position.pl = resultSet.getDouble("PL");	
		position.grossPL = resultSet.getDouble("GrossPL");	
//		position.netPL = resultSet.getDouble("");	
		position.com = resultSet.getDouble("Com");	
		position.interest = resultSet.getDouble("Int");	
		position.openTime = resultSet.getTimestamp("OpenTime");	
		position.closeTime = resultSet.getTimestamp("CloseTime");	
		position.kind = resultSet.getString("Kind");	
		position.quoteID = resultSet.getString("QuoteID");	
		position.openOrderID = resultSet.getString("OpenOrderID");	
		position.openOrderReqID = resultSet.getString("OpenOrderReqID");	
		position.closeOrderID = resultSet.getString("CloseOrderID");	
		position.closeOrderReqID = resultSet.getString("CloseOrderReqID");	
//		position.oQTXT = resultSet.getString("OQTXT");
//		position.cQTXT = resultSet.getString("CQTXT");
		position.stopOrderID = resultSet.getString("StopOrderID");
		position.limitOrderID = resultSet.getString("LimitOrderID");
		position.tradeMethodPeriod = resultSet.getString("TradeMethodPeriod");
		position.tradeMethodName = resultSet.getString("TradeMethodName");
		position.trailStopPeriod = resultSet.getString("TrailStopPeriod");
		position.trailStopName = resultSet.getString("TrailStopName");
		position.trailMoveTsName = resultSet.getString("TrailMoveTsName");
		return position;
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
		TRADE_ID(FieldType.INT, "", SwingConstants.LEFT),
		ACCOUNT_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		ACCOUNT_NAME(FieldType.STRING, "", SwingConstants.LEFT),
		OFFER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		SYMBOL(FieldType.STRING, "", SwingConstants.LEFT),
//		TRADE_LOT(FieldType.INT, "", SwingConstants.RIGHT),
		TRADE_AMOUNT(FieldType.INT, "", SwingConstants.RIGHT),
		TRADE_BS(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_OPEN(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_CLOSE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_STOP(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_UNT_TRL_MOVE(FieldType.DOUBLE, "#.#", SwingConstants.RIGHT),
		TRADE_LIMIT(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_HIGH(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_LOW(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		TRADE_PL(FieldType.DOUBLE, "#.#", SwingConstants.RIGHT),
		TRADE_GROSS_PL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		TRADE_NET_PL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		TRADE_COM(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		TRADE_INTEREST(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		TRADE_OPEN_TIME(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.LEFT),
		TRADE_CLOSE_TIME(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.LEFT),
		TRADE_KIND(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_QUOTE_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_OPEN_ORDER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_OPEN_ORDER_REQ_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_CLOSE_ORDER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_CLOSE_ORDER_REQ_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		TRADE_OQ_TXT(FieldType.STRING, "", SwingConstants.LEFT),
//		TRADE_CQ_TXT(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_STOP_ORDER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_LIMIT_ORDER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_METHOD_PERIOD(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_METHOD_NAME(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_TRAIL_STOP_PERIOD(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_TRAIL_STOP_NAME(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_TRAIL_MOVE_TS_NAME(FieldType.STRING, "", SwingConstants.LEFT);
		
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
			if (fieldDefArray[i] == FieldDef.TRADE_GROSS_PL ||
				fieldDefArray[i] == FieldDef.TRADE_NET_PL || 
				fieldDefArray[i] == FieldDef.TRADE_COM ||
				fieldDefArray[i] == FieldDef.TRADE_INTEREST) {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat() + digitsStr);
			} else {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			}
			fieldList.add(field);
		}
	}
	
	public enum BnS {
		BUY {
    		@Override
    		public String toString() {
    			return "BUY";
    		}
    	},
    	SELL {
    		@Override
    		public String toString() {
    			return "SELL";
    		}
    	};
    }
	
	public enum Stage {
    	Open {
    		@Override
    		public String toString() {
    			return "Open";
    		}
    	},
    	Closed {
    		@Override
    		public String toString() {
    			return "Closed";
    		}
    	};
    }

}
