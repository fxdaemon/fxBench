package org.fxbench.trader.local;

import org.fxbench.desk.PriceBars;
import org.fxbench.desk.TradeDesk;
import org.fxbench.desk.PriceBars.ReferSpot;
import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TOrder;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.trader.ConnectionsManager;
import org.fxbench.trader.TradingServerSession;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LocalServerSession extends TradingServerSession
{
	DBAccess dbAccess;

    public LocalServerSession(TradeDesk tradeDesk) {
    	super(tradeDesk);
    }
    
    @Override
	public LocalLiaison getLiaison() {
		return (LocalLiaison)liaison;
	}

	public void setLiaison(LocalLiaison liaison) {
		this.liaison = liaison;
	}

	@Override
    public String getParameterValue(String aName) {
        return null;
    }
    
	@Override
    public TimeZone getTimeZone() {
    	return TimeZone.getDefault();
    }

	@Override
    public String getSessionID() {
        return null;
    }

	@Override
    public int getUserKind() {
        return 0;
    }

	@Override
    public boolean isUnlimitedCcy() {
        return false;
    }

	@Override
    public double getPointSize(String symbol) {
    	if (symbol == null) {
            return 0.0;
        }
    	TOffer offer = tradeDesk.getOffer(symbol);
    	return offer == null ? 0.0 : offer.getPointSize();
    }
    
	@Override
    public int getSymbolPrecision(String symbol) {
    	return Utils.getPrecision(getPointSize(symbol));
    }
    
	@Override
    public String getAccountCurrency() {
        return "JPY";
    }
    
	@Override
    public void getUserObjects() throws Exception {
    	TAccount account = new TAccount(getLiaison().getLoginRequest().getAccountName());
    	List<BaseEntity> accountList = dbAccess.select(account);
    	if (accountList.size() == 0) {
    		return;
    	}
    	tradeDesk.addAccounts(accountList);
    	
    	List<BaseEntity> offerList = dbAccess.select(new TOffer());
    	if (offerList.size() == 0) {
    		return;
    	}
    	tradeDesk.addOffers(offerList);
    	
    	TOrder order = new TOrder();
    	order.setAccountIdEx(getLiaison().getLoginRequest().getAccountName());
    	List<BaseEntity> orderList = dbAccess.select(order);
    	if (orderList.size() > 0) {
    		tradeDesk.addOrders(orderList);
    	}
    	
    	TPosition position = new TPosition();
    	position.setAccountIdEx(getLiaison().getLoginRequest().getAccountName());
    	List<BaseEntity> positionList = dbAccess.select(position);
    	if (positionList.size() > 0) {
    		tradeDesk.addClosedPositions(positionList);
    	}
    }

    /**
     * log into the trade server
     *
     * @param aUsername user
     * @param aPassword pass
     * @param aTerminal terminal
     * @param aUrl url
     *
     * @throws Exception aex
     */
	@Override
    public void login(String dbUserName, String dbPassword, String dbName, String dbHost) throws Exception {
        mLogout = false;
        mUsername = dbUserName;
        mPassword = dbPassword;
        mTerminal = dbName;
        mHostUrl = dbHost;

        dbAccess = DBAccess.newInstance();
        dbAccess.connect(dbHost, dbName, dbUserName, dbPassword);
        getUserObjects();

        ConnectionsManager.setLastConnection(mTerminal, mUsername);
        PropertyManager.getInstance().loadLoginUserProperties(mUsername);
        
        liaison.communicationEstablished();
    }
    
    /**
     * log out of the trade server
     */
	@Override
    public void logout() {
        mLogout = true;
        if (dbAccess != null) {
        	dbAccess.close();
        }
    }

	@Override
    public void relogin() {
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TradingServerSession");
        sb.append(", mCfgFile='").append(mCfgFile).append('\'');
        sb.append(", mHostUrl='").append(mHostUrl).append('\'');
        sb.append(", mResMan=").append(resourceManager);
        sb.append(", mStopLimitOrderMap=").append(mStopLimitOrderMap);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean isLockView() {
    	return true;
    }
    
    @Override
    public Date getOrginChartEndDate() {
    	return tradeDesk.getServerTime();
    }
    
	@Override
	public boolean isReCalcTable() {
		return false;
	}

	@Override
	public boolean isCreateServerTimeThread() {
		return false;
	}

	@Override
	public int getShiftMaxBars() {
		return Integer.MAX_VALUE;
	}
	
	private void loadPriceBarFromHost(String symbol, Interval interval, Date loadToDate, int loadSize) {
		Date loadFromDate = PriceBars.addPriceBarStartDate(interval.getSeconds() * -1, loadToDate, loadSize);
		loadToDate = DataReader.addStartDate(loadToDate, interval.name(), 1);
		loadPriceBarFromHost(symbol, interval.name(), loadFromDate, loadToDate);
	}

	@Override
	public void firePriceBarsReferSpot(ReferSpot referSpot) {
		int takeSize = referSpot.takeEndPos - referSpot.takeBeginPos;
		Date loadToDate = new Date(referSpot.referDate.getTime() - Calendar.getInstance().get(Calendar.ZONE_OFFSET));
		if (referSpot.listSize == 0 || takeSize == 0) {
			tradeDesk.getPriceBars().clear();
			loadPriceBarFromHost(referSpot.symbol, referSpot.interval, loadToDate, referSpot.referSize);
		} else {
//			if (referSpot.takeBeginPos < referSpot.referSize) {
//				tradeDesk.getPriceBars().clear();
//				loadPriceBarFromHost(referSpot.symbol, referSpot.interval, loadToDate, referSpot.referSize);
//			} else if (referSpot.listSize - referSpot.takeEndPos < referSpot.referSize ||
//					referSpot.referOffset > 0 && referSpot.takeEndPos == referSpot.listSize) {
//				tradeDesk.getPriceBars().clear();
//				loadToDate = PriceBars.addPriceBarStartDate(referSpot.interval.getSeconds(), loadToDate, referSpot.referSize);
//				loadPriceBarFromHost(referSpot.symbol, referSpot.interval, loadToDate, referSpot.referSize);
//			}
			if (referSpot.referOffset > 0 && referSpot.takeEndPos == referSpot.listSize) {
				tradeDesk.getPriceBars().clear();
				loadToDate = PriceBars.addPriceBarStartDate(referSpot.interval.getSeconds(), loadToDate, referSpot.referSize);
				loadPriceBarFromHost(referSpot.symbol, referSpot.interval, loadToDate, referSpot.referSize);
			} else {// if (referSpot.referOffset < 0 && referSpot.takeBeginPos == 0) {
				if (takeSize > 0 && takeSize < referSpot.referSize) {
					tradeDesk.getPriceBars().clear();
					loadPriceBarFromHost(referSpot.symbol, referSpot.interval, loadToDate, referSpot.referSize);					
				}
			}
		}
	}
}
