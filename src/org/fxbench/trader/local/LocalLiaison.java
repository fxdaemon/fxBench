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
package org.fxbench.trader.local;

import org.fxbench.trader.IRequestFactory;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.local.RequestFactory;

import javax.swing.SwingUtilities;

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
public class LocalLiaison extends Liaison
{
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
    public LocalLiaison() {
    }
    
    /**
     * Cleans up all allocated resources
     */
    public void cleanup() {
        mRequestFactory = null;
        if (getStatus() != LiaisonStatus.DISCONNECTED) {
            if (getStatus() == LiaisonStatus.DISCONNECTING) {
            } else {
                setStatus(LiaisonStatus.DISCONNECTING);
                disconnect();
            }
        }
        mRequestFactory = null;
        super.cleanup();
    }

    public void communicationBroken() {
    }

    public void communicationConnecting() {
    }

    public void communicationDisconnecting() {
    }

    public void communicationError() {
    }

    public void communicationEstablished() {
    	mTradeDesk.syncServerTime(mLoginRequest.getStartDate(), false);
    	setStatus(LiaisonStatus.READY);
    }

    /**
     * Does disconnection
     */
    public void disconnect() {
        stop();
        dispatchLogoutCompleted();
        mTradeDesk.getTradingServerSession().logout();
        setStatus(LiaisonStatus.DISCONNECTED);
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
            mRequestFactory = new RequestFactory(mLoginRequest.getDataPath());
        }
        return mRequestFactory;
    }

    /**
     * Returns flag that the market is closed
     */
    public boolean isMarketClosed() {
        return false;
    }

    /**
     * Starts Reader, Ping and Sender threads
     */
    @Override
    public void login(IRequester aLoginRequest) {
        if (mLoginRequest == null) {
            setStatus(LiaisonStatus.CONNECTING);
            mLoginRequest = (LoginRequest)aLoginRequest;
            new Thread() {
                @Override
                public void run() {
                    try {
                        mLoginRequest.doIt();
                        mTradeDesk.setLoginUserName(mLoginRequest.getDbUser());
                        LocalLiaison.this.dispatchLoginCompleted();
                        LocalLiaison.this.start();
                        LocalLiaison.this.setStatus(LiaisonStatus.READY);
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
        if (getStatus() == LiaisonStatus.DISCONNECTED) {
            return;
        }
        setStatus(LiaisonStatus.DISCONNECTING);
        stop();
        dispatchLogoutCompleted();
        mTradeDesk.getTradingServerSession().logout();
        setStatus(LiaisonStatus.DISCONNECTED);
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
}

