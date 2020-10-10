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
 *
 * $History: $
 * 9/5/2003 created by USHIK
 * 4/27/2004 USHIK changed getReportURL method considering flexibility url template from User Preference
  * 12/1/2004   Andre fixed a bug where the tradedesk was holding onto its old entries after logging out,
  * now on logout we ask the tradedesk to clear its tables, also replaced the string concat with a stringbuffer
  * when generating the reportURL
 */
package org.fxbench.trader.fxcm;

import org.fxbench.BenchApp;
import org.fxbench.trader.IRequestFactory;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.request.RequestFactory;
import org.fxbench.trader.TradingAPIException;
import org.fxbench.util.properties.SettingManager;

import com.fxcm.fix.ITradSesStatus;
import com.fxcm.fix.TradSesStatusFactory;

import javax.swing.SwingUtilities;
import java.net.InetAddress;
import java.util.StringTokenizer;

/**
 * Class Liaison.<br>
 * <br>
 * The class Liaison extends ALiaisonImpl class and refines its methods.
 * The only singleton of Liaison class might be created.
 * The singleton of Liaison class can be accessed by static method getInstance().
 * It is responsible for:
 * <ul>
 * <li>creating login dialog;</li>
 * <li>doing the connection and storing the its parameters;</li>
 * <li>sending requests and receiving responses;</li>
 * <li>doing disconnection.</li>
 * </ul>
 * <br>
 * Creation date (9/5/2003 10:15 AM)
 */
public class FxcmLiaison extends Liaison
{
    /**
     * Flag that disconnect is requeried
     */
    private boolean mDisconnectRequeried;
    /**
     * Reference to disconnect thread
     */
    private DisconnectedThread mDisconnectedThread;
    /**
     * Reference to login parameters for reconnection
     */
    private LoginRequest mLoginRequest;
    /**
     * Reference to singleton of RequestFactory
     */
    protected IRequestFactory mRequestFactory;
    
    /**
     * Private constructor. Calls the constructor of superclass
     */
    public FxcmLiaison() {
    }
    
    /**
     * Cleans up all allocated resources
     */
    public void cleanup() {
        mRequestFactory = null;
        if (getStatus() != LiaisonStatus.DISCONNECTED) {
            if (getStatus() == LiaisonStatus.DISCONNECTING && mDisconnectedThread != null) {
                try {
                    mDisconnectedThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                setStatus(LiaisonStatus.DISCONNECTING);
                disconnect();
            }
        }
        mRequestFactory = null;
        super.cleanup();
    }

    public void communicationBroken() {
    	((FxcmServerSession)mTradeDesk.getTradingServerSession()).relogin();
    }

    public void communicationConnecting() {
        if (getSessionID() != null) {
            setStatus(LiaisonStatus.RECONNECTING);
        }
    }

    public void communicationDisconnecting() {
    }

    public void communicationError() {
    }

    public void communicationEstablished() {
        if (getSessionID() != null) {
            setStatus(LiaisonStatus.READY);
        }
    }

    /**
     * Does disconnection
     */
    public void disconnect() {
        mDisconnectRequeried = true;
        stop();
        dispatchLogoutCompleted();
        if (getSessionID() != null) {
            setSessionID(null);
        }
        mTradeDesk.getTradingServerSession().logout();
        mDisconnectRequeried = false;
        setStatus(LiaisonStatus.DISCONNECTED);
        mDisconnectedThread = null;
    }

    protected InetAddress getLocalHost() throws LiaisonException {
        try {
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TradingAPIException(e, "IDS_UNKNOWN_HOST_LOCALHOST");
        }
    }

    /**
     * Returns mLoginParameters value
     */
    public LoginRequest getLoginRequest() {
        return mLoginRequest;
    }
    
    /**
     * Initializes mRequestFactory by newly created instance of RequestFactory
     * at once, returns mRequestFactory always.
     */
    public IRequestFactory getRequestFactory() {
        if (mRequestFactory == null) {
            mRequestFactory = new RequestFactory();
        }
        return mRequestFactory;
    }

    /**
     * Returns flag that the market is closed
     */
    public synchronized boolean isMarketClosed() {
        ITradSesStatus tradSesStatus = ((FxcmServerSession)mTradeDesk.getTradingServerSession()).getTradingSessionStatus().getTradSesStatus();
        return tradSesStatus == TradSesStatusFactory.CLOSED;
    }

    /**
     * @return client IP address
     */
    public String getClientIP() {
        try {
            return getLocalHost().getHostAddress();
        } catch (LiaisonException ex) {
            onCriticalError(ex);
        }
        return null;
    }

    private String getMacroValue(String aMacroName, String aAccountID, String aFrom, String aTo) {
        String sRC = aMacroName;
        if (aMacroName.startsWith("IDS_")) {
            try {
                sRC = BenchApp.getInst().getResourceManager().getString(aMacroName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("serverURL".equalsIgnoreCase(aMacroName)) {
            sRC = mTradeDesk.getTradingServerSession().getParameterValue("REPORTS_URL");
        } else if ("accID".equalsIgnoreCase(aMacroName)) {
            sRC = aAccountID;
        } else if ("fromDate".equalsIgnoreCase(aMacroName)) {
            sRC = aFrom;
        } else if ("toDate".equalsIgnoreCase(aMacroName)) {
            sRC = aTo;
        } else if ("SESSIONID".equalsIgnoreCase(aMacroName)) {
            sRC = getSessionID();
        } else if ("CONNECTIONNAME".equalsIgnoreCase(aMacroName)) {
            sRC = mTradeDesk.getTradingServerSession().getTradingSessionID();
        } else if ("language".equalsIgnoreCase(aMacroName)) {
            try {
                sRC = BenchApp.getInst().getResourceManager().getLocale().getLanguage();
            } catch (Exception e) {
                e.printStackTrace();
                sRC = "en";
            }
        }/* else {
            UserPreferences up = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
            String value = up.getString(aMacroName);
            if (value != null) {
                sRC = value;
            }
        }*/
        return sRC;
    }

    public String getReportURL(String aAccountID, String aFrom, String aTo) {
//        UserPreferences up = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//        String sReportFormat = up.getString("report.format");
    	String sReportFormat = SettingManager.getInstance().getReportFormat();
        StringTokenizer st = new StringTokenizer(sReportFormat, "%", false);
        int i = sReportFormat.startsWith("%") ? 1 : 0;
        boolean bEndWithMacro = sReportFormat.endsWith("%");
        StringBuffer reportBuffer = new StringBuffer();
        for (; st.hasMoreTokens(); i++) {
            String token = st.nextToken();
            if (i % 2 == 0) {
                reportBuffer.append(token);
            } else {
                if (bEndWithMacro || st.hasMoreTokens()) {
                    reportBuffer.append(getMacroValue(token, aAccountID, aFrom, aTo));
                } else {
                    reportBuffer.append("%").append(token);
                }
            }
        }
        mLogger.debug("report url = " + reportBuffer);
        return reportBuffer.toString();
    }

    /**
     * Starts Reader, Ping and Sender threads
     */
    @Override
    public void login(IRequester aLoginRequest) {
        if (mLoginRequest == null) {
            mDisconnectRequeried = false;
            setStatus(LiaisonStatus.CONNECTING);
            mLoginRequest = (LoginRequest)aLoginRequest;
            new Thread() {
                @Override
                public void run() {
                    try {
                        mLoginRequest.doIt();
                        mTradeDesk.setLoginUserName(mLoginRequest.getUID());
                        FxcmLiaison.this.setSessionID(mLoginRequest.getSessionID());
                        FxcmLiaison.this.dispatchLoginCompleted();
                        FxcmLiaison.this.start();
                        FxcmLiaison.this.setStatus(LiaisonStatus.READY);
                    } catch (LiaisonException ex) {
                        setStatus(LiaisonStatus.DISCONNECTED);
                        mLoginRequest = null;
                        dispatchLoginFailed(ex);
                        onCriticalError(ex);
                    }
                }
            }.start();
        }
    }

    /**
     * Starts Disconnect thread
     */
    @Override
    public void logout() {
        //clean up tradesk tables on logout.
        getTradeDesk().getOffers().clear();
        getTradeDesk().clear();
        mDisconnectRequeried = true;
        if (getSessionID() != null) {
            setSessionID(null);
        }
        if (getStatus() == LiaisonStatus.DISCONNECTED || mDisconnectedThread != null) {
            return;
        }
        mDisconnectedThread = new DisconnectedThread();
        setStatus(LiaisonStatus.DISCONNECTING);
        mDisconnectedThread.start();
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getTradeDesk().clear();
                mTradeDesk.getTradingServerSession().clearOrderMap();
                try {
                	mTradeDesk.getTradingServerSession().getUserObjects();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Disconnection thread.<br>
     * <br>
     * .<br>
     * <br>
     */
    private class DisconnectedThread extends Thread {
        public void run() {
            disconnect();
            mSessionID = null;
            mLoginRequest = null;
        }
    }

}

