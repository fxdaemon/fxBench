package org.fxbench.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.SerialVersion;
import org.fxbench.util.Utils;

public class TAccount extends BaseEntity {

	private static final long serialVersionUID = SerialVersion.APPVERSION;
	
	private String accountId;			//The unique number of the account.
	private String accountName;			//The unique name of the account.
	private double balance;				//The amount of funds in the account without taking into consideration profits and losses on all open positions.
	private double equity;				//The amount of funds in the account, including profits and losses on all open positions (the "floating" balance of the account).
	private double dayPL;				//The profit and loss (both "floating" and realized) during the current trading day. The trading days are from 17:00 to 17:00 EST.
	private double nontrdEqty;			//The amount of funds that is deposited, transferred to, and/or withdrawn from the account during the current trading day 
								//(i.e. the portion of the account's "floating" balance that reflects non-trading activity).
	private double m2MEquity;			//The "floating" balance of the account at the beginning of the trading day. The trading days are from 17:00 to 17:00 EST.
	private double usedMargin;			//The amount of funds currently committed to maintain all open positions in the account.
	private double usableMargin;		//The amount of funds available to open new positions or to absorb any losses on the existing positions. Once the UsableMargin reaches zero, 
								//the margin call order is triggered on the account and positions are automatically liquidated at the best available price. 
								//Together, UsableMargin and UsedMargin make up the Equity of the account.
	private double grossPL;				//The profit and loss on all open positions in the account. The GrossPL equals the difference between the Equity and the Balance of the account.
	private String kind;				//The type of the account. 
								//Possible values:
								// "32" - Trading account
								// "36" - Managed account
								// "38" - Controlled account
	private String marginCall;			//The limitation state of the account, each state determines the operations that can be performed on the account.
								//Possible values:
								// "Y" - Margin call (all positions are liquidated, new positions cannot be opened).
								// "W" - Warning about possible margin call (positions can be closed, new positions cannot be opened).
								// "Q" - Equity stop (all positions are liquidated, new positions cannot be opened up to the end of the trading day).
								// "A" - Equity alert (positions can be closed, new positions cannot be opened up to the end of the trading day.
								// "N" - No limitations (no limitations are imposed on the account operations). 
	private boolean isUnderMarginCall;	//Defines whether the account is under the margin call process. 
								//Possible values: 
								// "True" - the account is under the margin call (the UsableMargin reached "0" and open positions were closed because of the insufficient funds), 
								// "False" - the account is not under the margin call (there are funds available to open new position and cover losses on the existing positions).
	private String hedging;				//Defines a trade mode which determines how trades can be performed on the account. There are three kinds of the trade mode.
								//Possible values:
								// "Y" - hedging is allowed (both buy and sell positions can be open in the same instrument at the same time. A separate order is required to close each of the buy and sell positions). 
								// "N" - hedging is not allowed (either a buy or sell position can be open in the instrument at a time. Placing a trade in the opposite direction on the instrument that already has open position(s) always causes closing of the open position(s).
								// "0" - netting only (there is one position for each instrument showing the total amount of the instrument either bought or sold which have not yet been offset by opposite trades).	
	private int amountLimit;			//The maximum amount per trade that is allowed on the account. The amount is expressed in the base currency of the instrument 
								//(for example, if the value is 30,000, the maximum amount per trade for EUR/USD is 30,000 Euros, for USD/JPY - 30,000 US dollars, etc). 
								//If there are no restrictions on the maximum amount of the trade, this property has the value: "0". 
								//Deprecated: Please use TradingSettingsProviderAut to retrieve this data, instead of retrieving Data directly from this table.
	private double baseUnitSize;			//The size of one lot, i.e. the minimum amount per trade that is allowed on the account. The amount is expressed in the base currency of the instrument 
								//(for example, if the value is 1,000, one lot for EUR/USD is 1,000 Euros, for USD/JPY - 1,000 US dollars, etc). The total amount of the trade must consist of the whole number of lots.
	private double marginReq;
	private boolean locked;
	private boolean invisible;
	private FieldDefStub<FieldDef> fieldDefStub;
	
	public TAccount() {
	}
	
	public TAccount(FieldDefStub<FieldDef> fieldDefStub) {
		initFields(fieldDefStub.getFieldDefArray());
		this.fieldDefStub = fieldDefStub;
	}
	
	public TAccount(FieldDefStub<FieldDef> fieldDefStub, TAccount src) {
		this(fieldDefStub);
		setAccountId(src.accountId);
		setAccountName(src.accountName);
		setBalance(src.balance);
		setEquity(src.equity);
		setDayPL(src.dayPL);
		setNontrdEqty(src.nontrdEqty);
		setM2MEquity(src.m2MEquity);
		setUsedMargin(src.usedMargin);
		setUsableMargin(src.usableMargin);
		setGrossPL(src.grossPL);
		setKind(src.kind);
		setMarginCall(src.marginCall);
		setUnderMarginCall(src.isUnderMarginCall);
		setHedging(src.hedging);
		setAmountLimit(src.amountLimit);
		setBaseUnitSize(src.baseUnitSize);
		setMarginReq(src.marginReq);
	}
	
	public TAccount(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_ID), accountId);
	}

	public String getAccountName() {
		return accountName;
	}
	public String getAccount() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_NAME), accountName);
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_BALANCE), balance);
	}

	public double getEquity() {
		return equity;
	}

	public void setEquity(double equity) {
		this.equity = equity;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_EQUITY), equity);
	}

	public double getDayPL() {
		return dayPL;
	}

	public void setDayPL(double dayPL) {
		this.dayPL = dayPL;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_DAY_PL), dayPL);
	}

	public double getNontrdEqty() {
		return nontrdEqty;
	}

	public void setNontrdEqty(double nontrdEqty) {
		this.nontrdEqty = nontrdEqty;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_NONTRD_EQTY), nontrdEqty);
	}

	public double getM2MEquity() {
		return m2MEquity;
	}

	public void setM2MEquity(double m2mEquity) {
		m2MEquity = m2mEquity;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_M2M_EQUITY), m2mEquity);
	}

	public double getUsedMargin() {
		return usedMargin;
	}

	public void setUsedMargin(double usedMargin) {
		this.usedMargin = usedMargin;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_USED_MARGIN), usedMargin);
	}

	public double getUsableMargin() {
		return usableMargin;
	}

	public void setUsableMargin(double usableMargin) {
		this.usableMargin = usableMargin;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_USABLE_MARGIN), usableMargin);
	}

	public double getGrossPL() {
		return grossPL;
	}

	public void setGrossPL(double grossPL) {
		this.grossPL = grossPL;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_GROSSPL), grossPL);
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_KIND), kind);
	}

	public String getMarginCall() {
		return marginCall;
	}

	public void setMarginCall(String marginCall) {
		this.marginCall = marginCall;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_MARGIN_CALL), marginCall);
	}

	public boolean isUnderMarginCall() {
		return isUnderMarginCall;
	}

	public void setUnderMarginCall(boolean isUnderMarginCall) {
		this.isUnderMarginCall = isUnderMarginCall;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_ISUNDER_MARGIN_CALL), isUnderMarginCall);
	}

	public String getHedging() {
		return hedging;
	}

	public void setHedging(String hedging) {
		this.hedging = hedging;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_HEDGING), hedging);
	}

	public int getAmountLimit() {
		return amountLimit;
	}

	public void setAmountLimit(int amountLimit) {
		this.amountLimit = amountLimit;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_AMOUNT_LIMIT), amountLimit);
	}

	public double getBaseUnitSize() {
		return baseUnitSize;
	}

	public void setBaseUnitSize(double baseUnitSize) {
		this.baseUnitSize = baseUnitSize;
		setFieldVal(fieldDefStub.getFieldNo(FieldDef.ACCOUNT_BASE_UNIT_SIZE), baseUnitSize);
	}

	/**
	 * @return the marginReq
	 */
	public double getMarginReq() {
		return marginReq;
	}

	/**
	 * @param marginReq the marginReq to set
	 */
	public void setMarginReq(double marginReq) {
		this.marginReq = marginReq;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the invisible
	 */
	public boolean isInvisible() {
		return invisible;
	}

	/**
	 * @param invisible the invisible to set
	 */
	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public String getKindText(){
		if (kind == "32") {
			return "Trading account";
		} else if (kind == "32") {
			return "Managed account";
		} else if (kind == "32") {
			return "Controlled account";
		} else {
			return "";
		}
	}
	
	public String toString() {
//		String s = new String("\n>>>> [Account] <<<<");
//		s = s + "\nAccountID: " + accountId;
//		s = s + "\nAccountName: " + accountName;
//		s = s + "\nKind: " + kind + "(" + getKindText() + ")";
//		s = s + "\nBalance: " + balance;
//		s = s + "\nGrossPL: " + grossPL;
//		return s;
		StringBuffer sb = new StringBuffer();
        sb.append("Account");
        sb.append("{mAccountName='").append(accountName).append('\'');
        sb.append(", mAccountID='").append(accountId).append('\'');
        sb.append(", mBalance=").append(balance);
        sb.append(", mBaseUnitSize=").append(baseUnitSize);
//        sb.append(", mBatch=").append(mBatch);
        sb.append(", mEquity=").append(equity);
        sb.append(", mGrossPnL=").append(grossPL);
        sb.append(", mHedging='").append(hedging).append('\'');
        sb.append(", mIsUnderMarginCall=").append(isUnderMarginCall);
//        sb.append(", mLastRptRequested=").append(mLastRptRequested);
        sb.append(", mLocked=").append(locked);
        sb.append(", mMarginReq=").append(marginReq);
        sb.append(", mUsableMargin=").append(usableMargin);
        sb.append(", mUsedMargin=").append(usedMargin);
        sb.append(", mVisible=").append(invisible);
        sb.append('}');
        return sb.toString();
	}

	@Override
	public String getKey() {
		return accountName;
	}
	
	@Override
	public String getSelSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM `Account`");
		sb.append(" WHERE");
		sb.append(" `AccountID`=").append("'").append(accountId).append("'");
		return sb.toString();
	}
	
	@Override
	public BaseEntity newEntity(ResultSet resultSet) throws SQLException {
		TAccount account = new TAccount();
		account.accountId = resultSet.getString("AccountID");
		account.accountName = resultSet.getString("AccountName");
		account.balance = resultSet.getDouble("Balance");
		account.equity = resultSet.getDouble("Equity");
		account.dayPL = resultSet.getDouble("DayPL");
//		account.nontrdEqty = resultSet.getDouble("NontrdEqty");
//		account.m2MEquity = resultSet.getDouble("M2MEquity");
		account.usedMargin = resultSet.getDouble("UsedMargin");
		account.usableMargin = resultSet.getDouble("UsableMargin");
		account.grossPL = resultSet.getDouble("GrossPL");
		account.kind = resultSet.getString("AccountType");
//		account.marginCall = resultSet.getString("MarginCall");
//		account.isUnderMarginCall = resultSet.getBoolean("IsUnderMarginCall");
		account.hedging = resultSet.getString("Hedging");
//		account.amountLimit = resultSet.getInt("AmountLimit");
//		account.baseUnitSize = resultSet.getDouble("BaseUnitSize");
//		account.marginReq = resultSet.getDouble("");
		return account;
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
		ACCOUNT_ID(FieldType.STRING, "", SwingConstants.LEFT),
		ACCOUNT_NAME(FieldType.STRING, "", SwingConstants.LEFT),
		ACCOUNT_BALANCE(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_EQUITY(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_DAY_PL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_NONTRD_EQTY(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_M2M_EQUITY(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_USED_MARGIN(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_USABLE_MARGIN(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_GROSSPL(FieldType.DOUBLE, "#,##0", SwingConstants.RIGHT),
		ACCOUNT_KIND(FieldType.STRING, "", SwingConstants.RIGHT),
		ACCOUNT_MARGIN_CALL(FieldType.STRING, "", SwingConstants.RIGHT),
		ACCOUNT_ISUNDER_MARGIN_CALL(FieldType.STRING, "", SwingConstants.RIGHT),
		ACCOUNT_HEDGING(FieldType.STRING, "", SwingConstants.RIGHT),
		ACCOUNT_AMOUNT_LIMIT(FieldType.INT, "", SwingConstants.RIGHT),
		ACCOUNT_BASE_UNIT_SIZE(FieldType.DOUBLE, "#", SwingConstants.RIGHT);
		
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
			if (fieldDefArray[i] == FieldDef.ACCOUNT_BALANCE ||
				fieldDefArray[i] == FieldDef.ACCOUNT_EQUITY || 
				fieldDefArray[i] == FieldDef.ACCOUNT_DAY_PL ||
				fieldDefArray[i] == FieldDef.ACCOUNT_M2M_EQUITY ||
				fieldDefArray[i] == FieldDef.ACCOUNT_USED_MARGIN ||
				fieldDefArray[i] == FieldDef.ACCOUNT_USABLE_MARGIN ||
				fieldDefArray[i] == FieldDef.ACCOUNT_GROSSPL) {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat() + digitsStr);
			} else {
				field.setFieldFormat(fieldDefArray[i].getFieldFormat());
			}
			fieldList.add(field);
		}
	}

}
