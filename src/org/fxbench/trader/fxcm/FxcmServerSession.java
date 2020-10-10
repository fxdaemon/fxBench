/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/TradingServerSession.java#6 $
 *
 * Copyright (c) 2007 FXCM, LLC. All
 * 32 Old Slip, New York, NY 10005 USA
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
 * $History: $
 *  1/6/2004 - created by Ushik
 * ----work in progress---
 *  12/1/2004   Andre   originally this class was originally the UserSession,
 *  changes have been made to accomodate the new fxcm messaging system
 *  ripped out old tradingapi messaging code.
 * ---------------------------
 * 05/09/2006   Andre Mermegas: get initial offers
 * 07/05/2006   Andre Mermegas: fix for processing collateral reports for new accounts added while running
 * 12/07/2006   Andre Mermegas: added colinqack processor
 * 01/18/2006   Andre Mermegas: update to pin support
 * 08/04/2008   Andre Mermegas: add JTS party to orders
 * 02/25/2009   Andre Mermegas: add USE_ORIGIN_RATE to override dealer prices with trader prices
 */
package org.fxbench.trader.fxcm;

import com.fxcm.GenericException;
import com.fxcm.entity.ICode;
import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.Parties;
import com.fxcm.fix.Party;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.other.BusinessMessageReject;
import com.fxcm.fix.posttrade.CollateralInquiryAck;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.fix.posttrade.RequestForPositionsAck;
import com.fxcm.fix.pretrade.EMail;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataRequestReject;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.Quote;
import com.fxcm.fix.pretrade.SecurityStatus;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderCancelReject;
import com.fxcm.fix.trade.OrderCancelReplaceRequest;
import com.fxcm.fix.trade.OrderCancelRequest;
import com.fxcm.fix.trade.OrderSingle;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.fxcm.messaging.IUserSession;
import com.fxcm.messaging.TradingSessionDesc;
import com.fxcm.messaging.util.AuthenticationException;
import com.fxcm.messaging.util.IConnectionManager;

import org.fxbench.BenchApp;
import org.fxbench.desk.PriceBars;
import org.fxbench.desk.TradeDesk;
import org.fxbench.desk.PriceBars.ReferSpot;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.trader.dialog.ChangePasswordDialog;
import org.fxbench.trader.dialog.PINDialog;
import org.fxbench.trader.fxcm.dialog.TradingSessionDialog;
import org.fxbench.trader.fxcm.processor.BusinessMessageRejectProcessor;
import org.fxbench.trader.fxcm.processor.CollateralInquiryAckProcessor;
import org.fxbench.trader.fxcm.processor.CollateralReportProcessor;
import org.fxbench.trader.fxcm.processor.EMailProcessor;
import org.fxbench.trader.fxcm.processor.ExecutionReportProcessor;
import org.fxbench.trader.fxcm.processor.IProcessor;
import org.fxbench.trader.fxcm.processor.MarketDataSnapshotProcessor;
import org.fxbench.trader.fxcm.processor.OrderCancelRejectProcessor;
import org.fxbench.trader.fxcm.processor.PositionReportProcessor;
import org.fxbench.trader.fxcm.processor.QuoteProcessor;
import org.fxbench.trader.fxcm.processor.RequestForPositionsAckProcessor;
import org.fxbench.trader.fxcm.processor.SecurityStatusProcessor;
import org.fxbench.trader.fxcm.processor.TradingSessionStatusProcessor;
import org.fxbench.trader.fxcm.request.ChangePasswordRequest;
import org.fxbench.trader.fxcm.OraCodeFactory;
import org.fxbench.trader.ConnectionsManager;
import org.fxbench.trader.LoginException;
import org.fxbench.ui.BenchFrame;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.trader.TradingServerSession;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 *
 */
public class FxcmServerSession extends TradingServerSession implements IGenericMessageListener, IStatusMessageListener
{
	private final static int PRICEBAR_LOAD_SIZE = 250;
	
    private static final IGateway GATEWAY = GatewayFactory.createGateway();
    private Map<ICode, IProcessor> mProcessorMap;
    private TradingSessionStatus mTradingSessionStatus;

    public FxcmServerSession(TradeDesk tradeDesk) {
    	super(tradeDesk);
        mProcessorMap = new HashMap<ICode, IProcessor>();
        mProcessorMap.put(BusinessMessageReject.OBJ_TYPE, new BusinessMessageRejectProcessor());
        mProcessorMap.put(TradingSessionStatus.OBJ_TYPE, new TradingSessionStatusProcessor());
        mProcessorMap.put(RequestForPositionsAck.OBJ_TYPE, new RequestForPositionsAckProcessor());
        mProcessorMap.put(CollateralReport.OBJ_TYPE, new CollateralReportProcessor());
        mProcessorMap.put(PositionReport.OBJ_TYPE, new PositionReportProcessor());
        mProcessorMap.put(ExecutionReport.OBJ_TYPE, new ExecutionReportProcessor());
        mProcessorMap.put(MarketDataSnapshot.OBJ_TYPE, new MarketDataSnapshotProcessor());
        mProcessorMap.put(EMail.OBJ_TYPE, new EMailProcessor());
        mProcessorMap.put(OrderCancelReject.OBJ_TYPE, new OrderCancelRejectProcessor());
        mProcessorMap.put(Quote.OBJ_TYPE, new QuoteProcessor());
        mProcessorMap.put(CollateralInquiryAck.OBJ_TYPE, new CollateralInquiryAckProcessor());
        mProcessorMap.put(SecurityStatus.OBJ_TYPE, new SecurityStatusProcessor());
        mProcessorMap.put(MarketDataRequestReject.OBJ_TYPE, new IProcessor() {
            public void process(ITransportable aTransportable) {
                mLogger.debug("MarketDataRequestReject = " + aTransportable);
                doneProcessing();
            }
        });
    }

    private void fillJTSParty(Parties aParties) {
        Party party = new Party("fxcm.com", "C", "13");
        party.setSubParty("4", "JavaTSApp");
        party.setSubParty("4444", "JavaTS");
        aParties.addParty(party);
    }
    
    /**
	 * @return the liaison
	 */
	public FxcmLiaison getLiaison() {
		return (FxcmLiaison)liaison;
	}

	/**
	 * @param liaison the liaison to set
	 */
	public void setLiaison(FxcmLiaison liaison) {
		this.liaison = liaison;
	}

    public IGateway getGateway() {
        return GATEWAY;
    }

    public String getParameterValue(String aName) {
        return mTradingSessionStatus.getParameterValue(aName);
    }
    
    public TimeZone getTimeZone() {
    	return TimeZone.getTimeZone(getParameterValue("BASE_TIME_ZONE"));
    }

    /**
     * Returns the current active session id
     *
     * @return sessionid
     */
    public String getSessionID() {
        return GATEWAY.getSessionID();
    }


    /**
     * Gets the UserKind
     *
     * @return
     *
     * @see com.fxcm.fix.IFixDefs.FXCM_ACCT_TYPE_CUSTOMER
     * @see com.fxcm.fix.IFixDefs.FXCM_ACCT_TYPE_DEALER
     * @see com.fxcm.fix.IFixDefs.FXCM_ACCT_TYPE_TRADER
     */
    public int getUserKind() {
        return GATEWAY.getUserKind();
    }

    /**
     * @return sessionstatus
     */
    public TradingSessionStatus getTradingSessionStatus() {
        return mTradingSessionStatus;
    }

    /**
     * @param aTradingSessionStatus sessionstatus
     */
    public void setTradingSessionStatus(TradingSessionStatus aTradingSessionStatus) {
        mTradingSessionStatus = aTradingSessionStatus;
    }

    public boolean isUnlimitedCcy() {
        String unlimited = mTradingSessionStatus.getParameterValue("UNLIMITED_CCY_SUBSCRIPTION");
        return !(unlimited == null || "N".equalsIgnoreCase(unlimited));
    }

    public double getPointSize(String symbol) {
    	return symbol == null ? 0.0 : Utils.getPointSize(getSymbolPrecision(symbol));
    }
    
    public int getSymbolPrecision(String symbol) {
    	boolean jpyIsInThePair = Utils.isCurrencyInThePair("JPY", symbol);
    	try {
    		TradingSecurity security = mTradingSessionStatus.getSecurity(symbol);
    		int precision = security.getFXCMSymPrecision();
    		if (jpyIsInThePair && precision > 2 || !jpyIsInThePair && precision > 4) {
    			precision--;
            }
    		return precision;
    	} catch (NotDefinedException e) {
    		int ret = 4;
            if (jpyIsInThePair) {
                ret = 2;
            }
            return ret;
        }
    }
    
    /**
     * Returns account currency.
     */
    public String getAccountCurrency() {
        return getParameterValue("BASE_CRNCY");
    }
    
    /**
     * Get User Objects
     *
     * @throws Exception aex
     */
    public void getUserObjects() throws Exception {
        GATEWAY.requestTradingSessionStatus();
        beginProcessing();

        String value = mTradingSessionStatus.getParameterValue("FORCE_PASSWORD_CHANGE");
        if (value != null && "Y".equalsIgnoreCase(value)) {
            BenchApp tradeApp = BenchApp.getInst();
            String msg = "For your security, you are required to change your password.";
            String title = tradeApp.getResourceManager().getString("IDS_MAINFRAME_SHORT_TITLE");
            JOptionPane.showMessageDialog(tradeApp.getMainFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE);
            ChangePasswordDialog dialog = new ChangePasswordDialog(tradeApp.getMainFrame());
            if (dialog.showModal() == JOptionPane.OK_OPTION) {
                ChangePasswordRequest cpr = new ChangePasswordRequest();
                cpr.setOldPassword(dialog.getOldPassword());
                cpr.setNewPassword(dialog.getNewPassword());
                cpr.setConfirmNewPassword(dialog.getConfirmNewPassword());
                cpr.doIt();
            } else {
                throw new GenericException("");
            }
        }

        Enumeration<TradingSecurity> securities = (Enumeration<TradingSecurity>) mTradingSessionStatus.getSecurities();
        MarketDataRequest mdr = new MarketDataRequest();
        mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
        mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_BIDASK);
        while (securities.hasMoreElements()) {
            TradingSecurity ts = securities.nextElement();
            if (ts.getFXCMSubscriptionStatus() == null
                || IFixDefs.FXCMSUBSCRIPTIONSTATUS_SUBSCRIBE.equals(ts.getFXCMSubscriptionStatus())) {
                mdr.addRelatedSymbol(ts);
            }
        }
        String requestID = GATEWAY.sendMessage(mdr);
        addRequestId(requestID, "OFFERS");
        mLogger.debug("mMarketDataRequestID = " + requestID);
        setWaitDialogText(BenchApp.getInst().getResourceManager().getString("IDS_GETTING_OFFERS"));
        beginProcessing();

        requestID = GATEWAY.requestAccounts();
        addRequestId(requestID, "ACCOUNTS");
        mLogger.debug("mAccountMassID = " + requestID);
        setWaitDialogText(BenchApp.getInst().getResourceManager().getString("IDS_GETTING_ACCOUNTS"));
        beginProcessing();

        requestID = GATEWAY.requestOpenPositions();
        addRequestId(requestID, "OPEN_POSITIONS");
        mLogger.debug("mOpenPositionMassID = " + requestID);
        setWaitDialogText(BenchApp.getInst().getResourceManager().getString("IDS_GETTING_OPEN_POSITIONS"));
        beginProcessing();

        requestID = GATEWAY.requestOpenOrders();
        addRequestId(requestID, "OPEN_ORDERS");
        mLogger.debug("mOpenOrderMassID = " + requestID);
        setWaitDialogText(BenchApp.getInst().getResourceManager().getString("IDS_GETTING_OPEN_ORDERS"));
        beginProcessing();

        requestID = GATEWAY.requestClosedPositions();
        addRequestId(requestID, "CLOSED_POSITIONS");
        mLogger.debug("mClosedPositionMassID = " + requestID);
        setWaitDialogText(BenchApp.getInst().getResourceManager().getString("IDS_GETTING_CLOSED_POSITIONS"));
        beginProcessing();
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
    public void login(String aUsername, String aPassword, String aTerminal, String aUrl) throws Exception {
        mLogout = false;
        mUsername = aUsername;
        mPassword = aPassword;
        mTerminal = aTerminal;
        mHostUrl = aUrl;
//        UserPreferences preferences = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
        GATEWAY.logout();
        GATEWAY.registerGenericMessageListener(this);
        GATEWAY.registerStatusMessageListener(this);
//        mCfgFile = liaison.getServerConfigFile(mUsername);
        mCfgFile = ConnectionsManager.getServerConfigFile();
        try {
            FXCMLoginProperties props = new FXCMLoginProperties(mUsername, mPassword, mTerminal, mHostUrl, "".equals(mCfgFile) ? null : mCfgFile);
            //props.addProperty(IConnectionManager.HTTP_IMPLEMENTATION, "apache");
            //props.addProperty(IConnectionManager.MSG_FLAGS, String.valueOf(IFixDefs.CHANNEL_MARKET_DATA));
            //props.addProperty(IConnectionManager.RELOGIN_TIMEOUT, "0");
//            String value = preferences.getString("Server.secure." + mTerminal);
            String value = ConnectionsManager.getSecureConnection(mTerminal);
            if (value != null) {
                props.addProperty(IConnectionManager.SECURE_PREF, value);
            }
            props.addProperty(IConnectionManager.APP_INFO, "fxBench");
//            if (preferences.getBoolean("Proxy.use")) {
//                props.addProperty(IConnectionManager.PROXY_SERVER, preferences.getString("Proxy.host"));
//                props.addProperty(IConnectionManager.PROXY_PORT, preferences.getString("Proxy.port"));
//                props.addProperty(IConnectionManager.PROXY_UID, preferences.getString("Proxy.user"));
//                props.addProperty(IConnectionManager.PROXY_PWD, preferences.getString("Proxy.password"));
//            }
            if (ConnectionsManager.isUseProxy()) {
                props.addProperty(IConnectionManager.PROXY_SERVER, ConnectionsManager.getProxyHost());
                props.addProperty(IConnectionManager.PROXY_PORT, ConnectionsManager.getProxyPort());
                props.addProperty(IConnectionManager.PROXY_UID, ConnectionsManager.getProxyUser());
                props.addProperty(IConnectionManager.PROXY_PWD, ConnectionsManager.getProxyPassword());
            }
            TradingSessionDesc[] tradingSessions = GATEWAY.getTradingSessions(props);
            BenchFrame mainFrame = BenchApp.getInst().getMainFrame();
            TradingSessionDesc selectedSession;
            if (tradingSessions.length > 1) {
                TradingSessionDialog tsd = new TradingSessionDialog(mainFrame, tradingSessions);
                int selection = tsd.showModal();
                selectedSession = tradingSessions[selection];
                mLogger.debug("selection = " + selectedSession);
            } else {
                selectedSession = tradingSessions[0];
            }
            String pinRequired = selectedSession.getProperty("PIN_REQUIRED");
            if (pinRequired != null && "Y".equalsIgnoreCase(pinRequired)) {
                PINDialog pinDialog = new PINDialog(mainFrame);
                pinDialog.setVisible(true);
                Properties properties = new Properties();
                properties.setProperty(IUserSession.PIN, pinDialog.getPIN());
                GATEWAY.openSession(selectedSession, properties);
            } else {
                GATEWAY.openSession(selectedSession);
            }
        } catch (Exception e) {
            //loop through all nested causes looking for authentication exception
            Throwable cause = e;
            while (cause.getCause() != null && !(cause instanceof AuthenticationException)) {
                cause = cause.getCause();
            }
            String error = OraCodeFactory.toMessage(cause.getMessage());
            if (error != null && error.length() > 0) {
                throw new LoginException(cause, error);
            } else if (e instanceof AuthenticationException || cause instanceof AuthenticationException) {
                String value = "Login failed: Incorrect user name or password.";
                throw new LoginException(cause, value);
            } else {
                String value = "Login failed. Server does not return session id.";
                throw new LoginException(cause, value);
            }
        }

        getUserObjects();

//        Connection cx = ConnectionsManager.getConnection(mTerminal);
//        cx.setUsername(mUsername);
//        ConnectionsManager.updateAddConnection(mTerminal, cx);
//        preferences.set("Server.last.connected.terminal", mTerminal);
        ConnectionsManager.setLastConnection(mTerminal, mUsername);
        PropertyManager.getInstance().loadLoginUserProperties(mUsername);
    }
    
    /**
     * log out of the trade server
     */
    public void logout() {
        mLogout = true;
        mStopLimitOrderMap.clear();
        GATEWAY.logout();
        //remove the generic message listener, stop listening to updates
        GATEWAY.removeGenericMessageListener(this);
        //remove the status message listener, stop listening to status changes
        GATEWAY.removeStatusMessageListener(this);
    }

    public void messageArrived(ISessionStatus aStatus) {
        if (liaison != null) {   // that cannot be , but just in case
            if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_ERROR) {
            	liaison.communicationError();
            } else if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTED) {
            	liaison.communicationBroken();
            } else if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTING) {
            	liaison.communicationDisconnecting();
            } else if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_LOGGEDIN) {
            	liaison.communicationEstablished();
            } else if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_READY) {
            	liaison.communicationEstablished();
            } else if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_CONNECTING) {
            	liaison.communicationConnecting();
            }
        }
    }

    public void messageArrived(ITransportable aMessage) {
        IProcessor processor = mProcessorMap.get(aMessage.getType());
        if (processor == null) {
            mLogger.debug("Unhandled Message " + aMessage.getClass() + " == " + aMessage);
        } else {
            processor.process(aMessage);
        }
    }

    public void relogin() {
        if (mLogout) {
            return;
        }
        final JDialog jd = BenchApp.getInst().getMainFrame().createWaitDialog("Session Lost...Reconnecting");
        jd.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent aEvent) {
                Thread worker = new Thread(new Runnable() {
                    public void run() {
                        try {
                        	GATEWAY.relogin();
                        	tradeDesk.getLiaison().refresh();
                            jd.dispose();
                            jd.setVisible(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                worker.start();
            }
        });
        jd.setVisible(true);
    }

    /**
     * sends a msg
     *
     * @param aTransportable message
     */
    public String send(ITransportable aTransportable) {
        try {
            if (aTransportable instanceof OrderSingle) {
                fillJTSParty(((OrderSingle) aTransportable).getParties());
            } else if (aTransportable instanceof OrderCancelReplaceRequest) {
                fillJTSParty(((OrderCancelReplaceRequest) aTransportable).getParties());
            } else if (aTransportable instanceof OrderCancelRequest) {
                fillJTSParty(((OrderCancelRequest) aTransportable).getParties());
            }
            return GATEWAY.sendMessage(aTransportable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TradingServerSession");
        sb.append(", mCfgFile='").append(mCfgFile).append('\'');
        sb.append(", mHostUrl='").append(mHostUrl).append('\'');
        sb.append(", mProcessorMap=").append(mProcessorMap);
//        sb.append(", mRequestID='").append(mRequestID).append('\'');
        sb.append(", mResMan=").append(resourceManager);
        sb.append(", mStopLimitOrderMap=").append(mStopLimitOrderMap);
        sb.append(", mTradingSessionStatus=").append(mTradingSessionStatus);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean isLockView() {
    	return false;
    }
    
    @Override
    public Date getOrginChartEndDate() {
    	return null;
    }
    
	@Override
	public boolean isReCalcTable() {
		return true;
	}

	@Override
	public boolean isCreateServerTimeThread() {
		return true;
	}
	
	@Override
	public int getShiftMaxBars() {
		return Integer.MAX_VALUE;//PRICEBAR_LOAD_SIZE;
	}
	
	private void loadPriceBarFromHost(String symbol, Interval interval, Date loadToDate) {
		Date loadFromDate = PriceBars.addPriceBarStartDate(interval.getSeconds() * -1, loadToDate, PRICEBAR_LOAD_SIZE);
		loadPriceBarFromHost(symbol, interval.name(), loadFromDate, loadToDate);
	}

	@Override
	public void firePriceBarsReferSpot(ReferSpot referSpot) {
		if (referSpot.listSize == 0) {
			loadPriceBarFromHost(referSpot.symbol, referSpot.interval, referSpot.referDate);
		} else {
			int takeSize = referSpot.takeEndPos - referSpot.takeBeginPos;
			if (takeSize > 0 && takeSize < referSpot.referSize) {
				loadPriceBarFromHost(referSpot.symbol, referSpot.interval, referSpot.headDate);
			}
		}
	}
}
