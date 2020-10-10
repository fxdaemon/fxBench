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
package org.fxbench.trader;

import org.fxbench.desk.PriceBars.ReferSpot;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOrder;
import org.fxbench.BenchApp;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.ui.BenchFrame;
import org.fxbench.util.ResourceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 */
public abstract class TradingServerSession
{
	protected Log mLogger = LogFactory.getLog(TradingServerSession.class);
	protected String mCfgFile;
	protected String mHostUrl;
	protected String mPassword;
//	protected String mRequestID;
	protected Map<String, String> requestIdMap;
	protected String tradingSessionID;
	protected String mTerminal;
	protected String mUsername;
	protected boolean mLogout;
	protected final Object mMUTEX = new Object();
	protected Map<String, TOrder> mStopLimitOrderMap;
	protected ResourceManager resourceManager;
	protected TradeDesk tradeDesk;
	protected Liaison liaison;
	
    public TradingServerSession(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    	requestIdMap = new HashMap<String, String>();
        mStopLimitOrderMap = new HashMap<String, TOrder>();
        try {
            resourceManager = ResourceManager.getManager("org.fxbench.trader.resources.Resources");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(BenchApp.getInst().getMainFrame(),
                                          "Resource manager not created!",
                                          "fxBench",
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
    }

    public String getTradingSessionID() {
		return tradingSessionID;
	}

	public void setTradingSessionID(String tradingSessionID) {
		this.tradingSessionID = tradingSessionID;
	}

	/**
     * Updates session finished flag
     * <br>&nbsp;
     */
    public void adjustStatus() {
        Liaison liaison = tradeDesk.getLiaison(); 
        synchronized (liaison) {
        	LiaisonStatus status = liaison.getStatus();
            if (status == LiaisonStatus.READY) {
                liaison.setStatus(LiaisonStatus.RECEIVING);
            } else if (status == LiaisonStatus.RECEIVING) {
                liaison.setStatus(LiaisonStatus.READY);
            }
        }
    }

    protected void beginProcessing() {
        synchronized (mMUTEX) {
            try {
                mMUTEX.wait();
            } catch (InterruptedException e) {
                //
            }
        }
    }

    /**
     * Clear the orders map
     */
    public void clearOrderMap() {
        mStopLimitOrderMap.clear();
    }

    public void doneProcessing() {
        synchronized (mMUTEX) {
            mMUTEX.notifyAll();
        }
    }

    /**
     * @return The host url that we are connected to
     */
    public String getHostUrl() {
        return mHostUrl;
    }

    /**
	 * @return the mPassword
	 */
	public String getPassword() {
		return mPassword;
	}

	/**
	 * @param mPassword the mPassword to set
	 */
	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

//	/**
//	 * @return the mRequestID
//	 */
//	public String getRequestID() {
//		return mRequestID;
//	}
//
//	/**
//	 * @param mRequestID the mRequestID to set
//	 */
//	public void setRequestID(String mRequestID) {
//		this.mRequestID = mRequestID;
//	}

	public String addRequestId(String requestId, String value) {
		synchronized (requestIdMap) {
			return requestIdMap.put(requestId, value);
		}
	}
	
	public String removeRequestId(String requestId) {
		synchronized (requestIdMap) {
			return requestIdMap.remove(requestId);
		}
	}
	
	public boolean containRequestId(String requestId) {
		synchronized (requestIdMap) {
			return requestIdMap.containsKey(requestId);
		}
	}
	
	public String getRequestValue(String requestId) {
		synchronized (requestIdMap) {
			return requestIdMap.get(requestId);
		}
	}
	
	public Map<String, TOrder> getStopLimitOrderMap() {
        return mStopLimitOrderMap;
    }

    public String getTerminal() {
        return mTerminal;
    }

    public void setTerminal(String aTerminal) {
        mTerminal = aTerminal;
    }
    
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String aUsername) {
        mUsername = aUsername;
    }
    
    public ResourceManager getResourceManager() {
    	return resourceManager;
    }
    
    /**
	 * @return the liaison
	 */
	public Liaison getLiaison() {
		return liaison;
	}

	/**
	 * @param liaison the liaison to set
	 */
	public void setLiaison(Liaison liaison) {
		this.liaison = liaison;
	}
	
	protected void setWaitDialogText(String aText) {
        BenchFrame mainFrame = BenchApp.getInst().getMainFrame();
        JLabel aLabel = null;
        if (mainFrame.getWaitDialog() != null && mainFrame.getWaitDialog().isVisible()) {
            aLabel = (JLabel) mainFrame.getWaitDialog().getContentPane().getComponent(0);
        }
        if (aLabel != null) {
            aLabel.setText(aText);
        }
    }
	
	public void loadPriceBarFromHost(String symbol, String interval, Date loadFromDate, Date loadToDate) {
		IRequest request = liaison.getRequestFactory().getPriceHistory(symbol, interval, loadFromDate, loadToDate);
		try {
			liaison.sendRequest(request);
		} catch (Exception e) {
		}
	}
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TradingServerSession");
        sb.append(", mCfgFile='").append(mCfgFile).append('\'');
        sb.append(", mHostUrl='").append(mHostUrl).append('\'');
//        sb.append(", mRequestID='").append(mRequestID).append('\'');
        sb.append(", resourceManager=").append(resourceManager);
        sb.append(", mStopLimitOrderMap=").append(mStopLimitOrderMap);
        sb.append('}');
        return sb.toString();
    }
    
    public abstract String getParameterValue(String aName);
	public abstract TimeZone getTimeZone();
    public abstract String getSessionID();
    public abstract int getUserKind();
    public abstract boolean isUnlimitedCcy();
    public abstract double getPointSize(String symbol);
    public abstract int getSymbolPrecision(String symbol);
    public abstract String getAccountCurrency();
    public abstract void getUserObjects() throws Exception;
    public abstract boolean isLockView();
    public abstract Date getOrginChartEndDate();
    public abstract boolean isReCalcTable(); 
    public abstract boolean isCreateServerTimeThread();
    public abstract int getShiftMaxBars();
    public abstract void firePriceBarsReferSpot(ReferSpot referSpot);
    
    public abstract void login(String aUsername, String aPassword, String aTerminal, String aUrl) throws Exception;
    public abstract void logout();
    public abstract void relogin();
}
