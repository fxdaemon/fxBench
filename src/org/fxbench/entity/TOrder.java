package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.Utils;

public class TOrder extends BaseEntity
{
	private String orderID;			//The unique number of the order. The number is unique within the connection (Real or Demo).
	private String requestID;		//The unique identifier of the request to create an order. The identifier is unique within the connection (Real or Demo).
	private String accountId;		//The unique number of the account the order is placed from. The number is unique within the connection (Real or Demo). 
//	private String accountName;		//The unique name of the account the order is placed from. This is the name that is displayed to the Trading Station user.
	private String offerID;			//The unique number of the instrument the order is placed for.
	private String symbol;			//The symbol indicating the instrument the order is placed for. For example, EUR/USD, USD/JPY, GBP/USD.
	private String tradeID;			//The unique number of the position to be opened or closed by the order. The number is unique within the connection (Real or Demo).
	private boolean netQuantity;	// For SE/LE orders (see Orders.Type column) defines whether the order is Net stop\limit or simple stop\limit. 
									//For open market orders defines whether the order is "close all by instrument" or simple open market order.
	private BnS bs;				//The trade operation.
	private String stage;			//Defines whether the order is placed to open or to close the position.
									// "O" - the order is placed to open the position
									// "C" - the order is placed to close the position.
//	private int side;				//Defines whether the order is immediate (i.e. executed immediately upon the trader's request) or conditional 
									// "0"  - The order is immediate.
									// "-1" - The order is conditional, with the price of the order below the current market price.
									// "1"  - The order is conditional, with the price of the order above the current market price.
	private String type;			//The type of the order. 
								// "S"  - Stop.
								// "L"  - Limit.
								// "SE" - Entry Stop.
								// "LE" - Entry Limit.
								// "C"  - Close.
								// "O"  - Open.
								// "M"  - Margin Call.
//	private String fixStatus;		//The state of the order, based on the FIX protocol.
								// "W" - Waiting.
								// "P" - In process.
								// "I" - Dealer intervention.
								// "Q" - Requoted.
								// "U" - Pending calculated.
								// "E" - Executing.
								// "C" - Cancelled.
								// "R" - Rejected.
								// "T" - Expired.
								// "F" - Executed.
								// "G" - Not Available.
	private String status;		//The legacy status of the order which is supported for the backward compatibility. 
								// "N" - Waiting (corresponds to the FixStatus value "W").
								// "Y" - In process (corresponds to the FixStatus value "P", "I").
								// "R" - Requoted (corresponds to the FixStatus value "Q").
								// "I" - Cancelled (corresponds to the FixStatus values "C" and "R", "T").
								// "E" - Executing (corresponds to the FixStatus value "E", "F").
								// "P" - Executing (corresponds to the FixStatus value "U").
								// "M" - Margin Call.
								// "Q" - Equity Stop.
								// "T" - Executed (corresponds to the FixStatus value "F").
//	private int statusCode;		//The status of the order which is distinguished by the Trading Station and is used by the Order2Go.
								// 0 - Waiting (corresponds to the Status value "N").
								// 1 - In process (corresponds to the Status value "Y").
								// 2 - Canceled (corresponds to the Status value "I").
								// 3 - Requoted (corresponds to the Status value "R").
								// 4 - Margin call (corresponds to the Status value "M").
								// 5 - Executing (corresponds to the Status value "E", "P").
								// 6 - Equity stop (corresponds to the Status value "Q").
//	private String statusCaption;//The status of the order which is distinguished by the Trading Station and is displayed to the Trading Station user. 
								// "Waiting" - Corresponds to the StatusCode value "0".
								// "In process" - Corresponds to the StatusCode value "1".
								// "Canceled" - Corresponds to the StatusCode value "2".
								// "Requoted" - Corresponds to the StatusCode value "3".
								// "Margin call" - Corresponds to the StatusCode value "4".
								// "Executing" - Corresponds to the StatusCode value "5".
								// "Equity stop" -  Corresponds to the StatusCode value "6".
//	private int lot;			//The amount of the order expressed in the base currency of the instrument. For example, 
								//the value 20,000 for EUR/USD means that the amount of the order is 20,000 Euros, for USD/JPY - 20,000 US dollars, etc.
	private double amount;		//The amount of the order (in thousands) as specified by the Trading Station user. For example, 
								//the value 20 for EUR/USD means that the amount of the order is 20,000 Euros, for USD/JPY - 20,000 US dollars, etc.
	private double rate;		//The price the order is placed at.
	private double stop;		//The price of the associated stop order (the loss limit level).
	private double untTrlMove;	//The distance in pips between the current price and the price at which the price of the trailing stop order will be changed to the next level. 
								//The column is meaningful for stop orders only with the trailing stop mode. The trailing stop mode can be detected using the following condition: TrlRate <> 0.
	private double limit;		//The price of the associated limit order (the profit limit level).
	private Date tmTime;		//The date and time of the last update of the order. The date and time are in the UTC time.
//	private boolean isBuy;		//Defines whether the order is placed to buy or to sell the instrument. 
								//Possible values: "True" - buy, "False" - sell.
	private boolean isConditionalOrder;	//Defines whether the order is a conditional order. 
								// "True"  - the order is conditional
								// "False" - the order is not conditional.
	private boolean isEntryOrder;//Defines whether the order is an entry order. 
								//"True"  - the order is an entry order
								//"False" - the order is not an entry order.
//	private int lifetime;		//The time (in seconds) during which the trader must accept or reject the order requoted by the dealer. 
								//This is applicable only to immediate orders in the "requoted" state (see order statuses). For all other orders, the value is "0".
//	private String atMarket;	//The distance (in pips) from the order Rate within which the trader allows the order to be executed. 
								//If the market price moves beyond the allowed distance, the order must not be executed. The property is applicable only to open/close range orders 
								//(i.e. when the order must be executed at any price within the price range specified by the trader, or not executed at all).
	private int trlMinMove;		//The size in pips of the market movement after which the trailing stop must be moved following the market. 
								//The column is meaningful for stop orders only with the trailing stop mode. The property does not make sense for dynamic trailing stop. 
								//The trailing stop mode can be detected using the following condition: TrlRate <> 0.
	private double trlRate;		//The price at which the position can be closed when the associated stop order is set or last time adjust. 
								//The value of the property is updated each time the stop order automatically adjusts itself. This is applicable only to stop orders with the trailing stop feature activated.
	private int distance;		//The distance (in pips) from the market price to the price specified in the stop order when the stop order is set. 
								//This is the distance that must be kept the same when the stop order automatically adjusts itself as the market price changes in the trader's favor 
								//(i.e. when the trailing stop feature is activated).
	private String gtc;			//The property defines whether the order is good-till-cancelled (remains active until the trader cancels it or until the trade is executed). 
								// "Y" - the order is good-till-cancelled
								// ""  - the order is not good-till-cancelled, i.e. must be executed immediately or not executed at all.
	private String kind;		//The type of the account the order is placed from. 
								//"32" - Trading account.
								//"36" - Managed account.
								//"38" - Controlled account. 
	private String qTXT;		//The text comment that can be added to the order.
	private String stopOrderID;	//The unique number of the associated stop order. The number is unique within the connection (Real or Demo).
								//Makes sense for opening orders (Entry or Market). If there is no associated stop order, the property has the empty value.
	private String limitOrderID;//The unique number of the associated limit order. The number is unique within the connection (Real or Demo). 
								//Makes sense for opening orders (Entry or Market). If there is no associated limit order, the property has the empty value.
//	private int typeSL;			//Defines the way in which the stop or limit order is set. Makes sense for stop and limit orders. 
								// 1 - The stop/limit order is set as the absolute rate (the simple stop/limit order).
								// 2 - The stop/limit order is set as a distance in pips to the open price (the pegged stop/limit order).
								// 3 - The stop/limit order is set as a distance in pips to the close price (the pegged stop/limit order).
//	private int typeStop;		//Defines the way in which the associated stop order is set. Make sense for opening orders (entry and market). 
								// 0 - The associated stop order is not specified.
								// 1 - The associated stop order is set as the absolute rate (the simple stop order).
								// 2 - The associated stop order is set as a distance in pips to the open price (the pegged stop order).
								// 3 - The associated stop order is set as a distance in pips to the close price (the pegged stop order).
//	private int typeLimit;		//Defines the way in which the associated limit order is set. Make sense for opening orders (entry and market).
								// 0 - The associated limit order is not specified.
								// 1 - The associated limit order is set as the absolute rate (the simple limit order).
								// 2 - The associated limit order is set as a distance in pips to the open price (the pegged limit order).
								// 3 - The associated limit order is set as a distance in pips to the close price (the pegged limit order).
//	private int oCOBulkID;		//The unique number of the OCO order in which the order is contained. If the order is not linked in an OCO order, the value is "0".
	
	private String orderType;
	private boolean isLimit;
	private boolean isStop;
	private boolean isTrailingStop;
	private FieldDefStub<FieldDef> fieldDefStub;
	
	public TOrder() {
	}

	public TOrder(FieldDefStub<FieldDef> fieldDefStub) {
		initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
	}
	
	public TOrder(FieldDefStub<FieldDef> fieldDefStub, TOrder src) {
		this(fieldDefStub);
		setOrderID(src.orderID);
		setRequestID(src.requestID);
		setAccountId(src.accountId);
//		setAccountName(src.accountName);
		setOfferID(src.offerID);
		setSymbol(src.symbol);
		setTradeId(src.tradeID);
		setNetQuantity(src.netQuantity);
		setBS(src.bs);
		setStage(src.stage);
//		setSide(src.side);
		setType(src.type);
//		setFixStatus(src.fixStatus);
		setStatus(src.status);
//		setStatusCode(src.statusCode);
//		setStatusCaption(src.statusCaption);
//		setLot(src.lot);
		setAmount(src.amount);
		setRate(src.rate);
		setStop(src.stop);
		setUntTrlMove(src.untTrlMove);
		setLimit(src.limit);
		setTime(src.tmTime);
//		setBuy(src.isBuy);
		setConditionalOrder(src.isConditionalOrder);
		setEntryOrder(src.isEntryOrder);
//		setLifetime(src.lifetime);
//		setAtMarket(src.atMarket);
		setTrlMinMove(src.trlMinMove);
		setTrlRate(src.trlRate);
		setDistance(src.distance);
		setGtc(src.gtc);
		setKind(src.kind);
		setQTXT(src.qTXT);
		setStopOrderID(src.stopOrderID);
		setLimitOrderID(src.limitOrderID);
//		setTypeSL(src.typeSL);
//		setTypeStop(src.typeStop);
//		setTypeLimit(src.typeLimit);
//		setoCOBulkID(src.oCOBulkID);
	}
	
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_ID), orderID);
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.REQUEST_ID), requestID);
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
		int fieldNo = fieldDefStub.getFieldNo(FieldDef.ORDER_BUY_PRICE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.ORDER_SELL_PRICE);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.ORDER_LIMIT);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
		fieldNo = fieldDefStub.getFieldNo(FieldDef.ORDER_STOP);
		setFieldFormat(fieldNo, getFieldFormat(fieldNo) + precisionStr);
	}
	public String getTradeId() {
		return tradeID;
	}
	public void setTradeId(String tradeID) {
		this.tradeID = tradeID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.TRADE_ID), tradeID);
	}
	public boolean isNetQuantity() {
		return netQuantity;
	}
	public void setNetQuantity(boolean netQuantity) {
		this.netQuantity = netQuantity;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_NET_QUANTITY), netQuantity);
	}
	public BnS getBS() {
		return bs;
	}
	public void setBS(BnS bs) {
		this.bs = bs;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_BS), bs);
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STAGE), stage);
	}
//	public int getSide() {
//		return side;
//	}
//	public void setSide(int side) {
//		this.side = side;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_SIDE), side);
//	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TYPE), type);
	}
//	public String getFixStatus() {
//		return fixStatus;
//	}
//	public void setFixStatus(String fixStatus) {
//		this.fixStatus = fixStatus;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_FIX_STATUS), fixStatus);
//	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STATUS), status);
	}
//	public int getStatusCode() {
//		return statusCode;
//	}
//	public void setStatusCode(int statusCode) {
//		this.statusCode = statusCode;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STATUS_CODE), statusCode);
//	}
//	public String getStatusCaption() {
//		return statusCaption;
//	}
//	public void setStatusCaption(String statusCaption) {
//		this.statusCaption = statusCaption;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STATUS_CAPTION), statusCaption);
//	}
//	public int getLot() {
//		return lot;
//	}
//	public void setLot(int lot) {
//		this.lot = lot;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_LOT), lot);
//	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_AMOUNT), amount);
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
		if (bs == BnS.BUY) {
			setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_BUY_PRICE), rate);
		} else if (bs == BnS.SELL) {
			setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_SELL_PRICE), rate);
		}
	}
	public double getStop() {
		return stop;
	}
	public void setStop(double stop) {
		this.stop = stop;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STOP), stop);
	}
	public double getUntTrlMove() {
		return untTrlMove;
	}
	public double getTrailStop() {
		return untTrlMove;
	}
	public void setUntTrlMove(double untTrlMove) {
		this.untTrlMove = untTrlMove;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_UNT_TRL_MOVE), untTrlMove);
	}
	public double getLimit() {
		return limit;
	}
	public void setLimit(double limit) {
		this.limit = limit;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_LIMIT), limit);
	}
	public Date getTime() {
		return tmTime;
	}
	public void setTime(Date tmTime) {
		this.tmTime = tmTime;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TIME), tmTime);
	}
//	public boolean isBuy() {
//		return isBuy;
//	}
//	public void setBuy(boolean isBuy) {
//		this.isBuy = isBuy;
//	}
	public boolean isConditionalOrder() {
		return isConditionalOrder;
	}
	public void setConditionalOrder(boolean isConditionalOrder) {
		this.isConditionalOrder = isConditionalOrder;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_IS_CONDITION), isConditionalOrder);
	}
	public boolean isEntryOrder() {
		return isEntryOrder;
	}
	public void setEntryOrder(boolean isEntryOrder) {
		this.isEntryOrder = isEntryOrder;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_IS_ENTRY_ORDER), isEntryOrder);
	}
//	public int getLifetime() {
//		return lifetime;
//	}
//	public void setLifetime(int lifetime) {
//		this.lifetime = lifetime;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_LIFE_TIME), lifetime);
//	}
//	public String getAtMarket() {
//		return atMarket;
//	}
//	public void setAtMarket(String atMarket) {
//		this.atMarket = atMarket;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_AT_MARKET), atMarket);
//	}
	public int getTrlMinMove() {
		return trlMinMove;
	}
	public void setTrlMinMove(int trlMinMove) {
		this.trlMinMove = trlMinMove;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TRL_MIN_MOVE), trlMinMove);
	}
	public double getTrlRate() {
		return trlRate;
	}
	public void setTrlRate(double trlRate) {
		this.trlRate = trlRate;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TRL_RATE), trlRate);
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_DISTANCE), distance);
	}
	public String getGtc() {
		return gtc;
	}
	public void setGtc(String gtc) {
		this.gtc = gtc;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_GTC), gtc);
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_KIND), kind);
	}
	public String getQTXT() {
		return qTXT;
	}
	public String getCustomText() {
		return qTXT;
	}
	public void setQTXT(String qTXT) {
		this.qTXT = qTXT;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_Q_TXT), qTXT);
	}
	public String getStopOrderID() {
		return stopOrderID;
	}
	public void setStopOrderID(String stopOrderID) {
		this.stopOrderID = stopOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_STOP_ORDER_ID), stopOrderID);
	}
	public String getLimitOrderID() {
		return limitOrderID;
	}
	public void setLimitOrderID(String limitOrderID) {
		this.limitOrderID = limitOrderID;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_LIMIT_ORDER_ID), limitOrderID);
	}
//	public int getTypeSL() {
//		return typeSL;
//	}
//	public void setTypeSL(int typeSL) {
//		this.typeSL = typeSL;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TYPE_SL), typeSL);
//	}
//	public int getTypeStop() {
//		return typeStop;
//	}
//	public void setTypeStop(int typeStop) {
//		this.typeStop = typeStop;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TYPE_STOP), typeStop);
//	}
//	public int getTypeLimit() {
//		return typeLimit;
//	}
//	public void setTypeLimit(int typeLimit) {
//		this.typeLimit = typeLimit;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_TYPE_LIMIT), typeLimit);
//	}
//	public int getoCOBulkID() {
//		return oCOBulkID;
//	}
//	public void setoCOBulkID(int oCOBulkID) {
//		this.oCOBulkID = oCOBulkID;
//		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ORDER_OCO_BULK_ID), oCOBulkID);
//	}
	
	/**
	 * @return the orderType
	 */
	public String getOrderType() {
		return orderType;
	}
	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	/**
	 * @return the isLimit
	 */
	public boolean isLimit() {
		return isLimit;
	}
	/**
	 * @param isLimit the isLimit to set
	 */
	public void setLimit(boolean isLimit) {
		this.isLimit = isLimit;
	}
	/**
	 * @return the isStop
	 */
	public boolean isStop() {
		return isStop;
	}
	/**
	 * @param isStop the isStop to set
	 */
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	/**
	 * @return the isTrailingStop
	 */
	public boolean isTrailingStop() {
		return isTrailingStop;
	}
	/**
	 * @param isTrailingStop the isTrailingStop to set
	 */
	public void setTrailingStop(boolean isTrailingStop) {
		this.isTrailingStop = isTrailingStop;
	}
	
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Order");
//        sb.append("{mAccount='").append(accountName).append('\'');
//        sb.append(", mCurrencyTradable=").append(mCurrencyTradable);
        sb.append("{mCustomText='").append(qTXT).append('\'');
        sb.append(", mLimit=").append(limit);
        sb.append(", mLimitOrderID='").append(limitOrderID).append('\'');
        sb.append(", mSide=").append(bs);
        sb.append(", mStage=").append(stage);
        sb.append(", mStop=").append(stop);
        sb.append(", mStopOrderID='").append(stopOrderID).append('\'');
        sb.append(", mTradeId='").append(tradeID).append('\'');
        sb.append(", mTrailStop=").append(untTrlMove);
        sb.append(", mTrailingStop=").append(isTrailingStop);
        sb.append(", mcOrderType='").append(orderType).append('\'');
        sb.append(", mdLimit=").append(limit);
//        sb.append(", mdOfferRate=").append(mToStringFormatter.format(mdOfferRate));
        sb.append(", mdOrderRate=").append(rate);
        sb.append(", mdStop=").append(stop);
        sb.append(", mdtTime=").append(tmTime);
        sb.append(", mlAmount=").append(amount);
        sb.append(", msAccountID='").append(accountId).append('\'');
        sb.append(", msCurrency='").append(symbol).append('\'');
        sb.append(", msOrderID='").append(orderID).append('\'');
        sb.append(", msStatus='").append(status).append('\'');
        sb.append(", msType='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
	
	@Override
	public String getKey() {
		return orderID;
	}
	
	@Override
	public String getSelSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM `Order`");
		sb.append(" WHERE");
		sb.append(" `AccountId`=").append("'").append(accountId).append("'");
		if (orderID != null && orderID.length() > 0) {
			sb.append(" `OrderID`=").append("'").append(orderID).append("'");
		}
		return sb.toString();
	}
	
	@Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
		TOrder order = new TOrder();
		order.orderID = resultSet.getString("OrderID");
		order.requestID = resultSet.getString("RequestID");
		order.accountId = resultSet.getString("AccountId");
//		order.accountName = resultSet.getString("AccountName");
		order.offerID = resultSet.getString("OfferID");
		order.symbol = resultSet.getString("Symbol");
		order.tradeID = resultSet.getString("TradeID");
		order.netQuantity = resultSet.getBoolean("NetQuantity");
		order.bs = resultSet.getString("BS").equals("B") ? BnS.BUY : BnS.SELL;
		order.stage = resultSet.getString("Stage");
//		order.side = resultSet.getInt("Side");
		order.type = resultSet.getString("Type");
//		order.fixStatus = resultSet.getString("FixStatus");
		order.status = resultSet.getString("Status");
//		order.statusCode = resultSet.getInt("StatusCode");
//		order.statusCaption = resultSet.getString("StatusCaption");
//		order.lot = resultSet.getInt("Lot");
		order.amount = resultSet.getDouble("Amount");
		order.rate = resultSet.getDouble("Rate");
		order.stop = resultSet.getDouble("Stop");
		order.untTrlMove = resultSet.getDouble("UntTrlMove");
		order.limit = resultSet.getDouble("Limit");
		order.tmTime = resultSet.getTimestamp("Time");
//		order.isBuy = resultSet.getBoolean("IsBuy");
		order.isConditionalOrder = resultSet.getBoolean("IsConditionalOrder");
		order.isEntryOrder = resultSet.getBoolean("IsEntryOrder");
//		order.lifetime = resultSet.getInt("Lifetime");
//		order.atMarket = resultSet.getString("AtMarket");
		order.trlMinMove = resultSet.getInt("TrlMinMove");
		order.trlRate = resultSet.getDouble("TrlRate");
		order.distance = resultSet.getInt("Distance");
		order.gtc = resultSet.getString("GTC");
		order.kind = resultSet.getString("Kind");
		order.qTXT = resultSet.getString("QTXT");
		order.stopOrderID = resultSet.getString("StopOrderID");
		order.limitOrderID = resultSet.getString("LimitOrderID");
//		order.typeSL = resultSet.getInt("TypeSL");
//		order.typeStop = resultSet.getInt("TypeStop");
//		order.typeLimit = resultSet.getInt("TypeLimit");
//		order.oCOBulkID = resultSet.getInt("OCOBulkID");
		return order;
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
		ORDER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		REQUEST_ID(FieldType.STRING, "", SwingConstants.LEFT),
		ACCOUNT_ID(FieldType.STRING, "", SwingConstants.LEFT),
//		ACCOUNT_NAME(FieldType.STRING, "", SwingConstants.LEFT),
		OFFER_ID(FieldType.STRING, "", SwingConstants.LEFT),
		SYMBOL(FieldType.STRING, "", SwingConstants.LEFT),
		TRADE_ID(FieldType.STRING, "", SwingConstants.LEFT),
		ORDER_NET_QUANTITY(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_BS(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_STAGE(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_SIDE(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_TYPE(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_FIX_STATUS(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_STATUS(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_STATUS_CODE(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_STATUS_CAPTION(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_LOT(FieldType.INT, "", SwingConstants.RIGHT),
		ORDER_AMOUNT(FieldType.INT, "", SwingConstants.RIGHT),
		ORDER_BUY_PRICE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		ORDER_SELL_PRICE(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		ORDER_STOP(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		ORDER_UNT_TRL_MOVE(FieldType.DOUBLE, "", SwingConstants.RIGHT),
		ORDER_LIMIT(FieldType.DOUBLE, "#", SwingConstants.RIGHT),
		ORDER_TIME(FieldType.DATE, "MM/dd/yyyy HH:mm", SwingConstants.RIGHT),
		ORDER_IS_CONDITION(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_IS_ENTRY_ORDER(FieldType.STRING, "", SwingConstants.RIGHT),
//		ORDER_LIFE_TIME(FieldType.INT, "", SwingConstants.RIGHT),
//		ORDER_AT_MARKET(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_TRL_MIN_MOVE(FieldType.INT, "", SwingConstants.RIGHT),
		ORDER_TRL_RATE(FieldType.DOUBLE, "", SwingConstants.RIGHT),
		ORDER_DISTANCE(FieldType.INT, "", SwingConstants.RIGHT),
		ORDER_GTC(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_KIND(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_Q_TXT(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_STOP_ORDER_ID(FieldType.STRING, "", SwingConstants.RIGHT),
		ORDER_LIMIT_ORDER_ID(FieldType.STRING, "", SwingConstants.RIGHT);
//		ORDER_TYPE_SL(FieldType.INT, "", SwingConstants.RIGHT),
//		ORDER_TYPE_STOP(FieldType.INT, "", SwingConstants.RIGHT),
//		ORDER_TYPE_LIMIT(FieldType.INT, "", SwingConstants.RIGHT),
//		ORDER_OCO_BULK_ID(FieldType.STRING, "", SwingConstants.RIGHT);
		
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
    
	protected void initFields(FieldDef[] fieldDefArray) {
		for (int i = 0; i < fieldDefArray.length; i++) {
			Field field = new Field(i);
			field.setFieldName(fieldDefArray[i].name());
			field.setFieldType(fieldDefArray[i].getFieldType());
			field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			fieldList.add(field);
		}
	}
	
}
