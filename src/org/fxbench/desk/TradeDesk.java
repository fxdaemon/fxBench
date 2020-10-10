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
 * History:
 * 09/10/2003   ID   Created
 * 01/06/2004   USHIK Adds Connection name. msConnectionName property.
 * 12/08/2004   Andre Mermegas: added method to get order by tradeId
 * 12/10/2004   Andre Mermegas: updated the method that calculates PnL
 * 02/20/2006   Andre Mermegas: fix for ignoring java console frame if shown in webstart
 * 07/18/2006   Andre Mermegas: performance update
 * 12/08/2006   Andre Mermegas: update
 * 03/30/2007   Andre Mermegas: fixed bug in pnl, fixed dynamic updates to summary frame.
 */
package org.fxbench.desk;

import org.fxbench.BenchApp;
import org.fxbench.entity.BaseEntity;
import org.fxbench.entity.TAccount;
import org.fxbench.entity.TMessage;
import org.fxbench.entity.TOrder;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPosition.Stage;
import org.fxbench.trader.IServerTimeListener;
import org.fxbench.trader.ITraderConstants;
import org.fxbench.trader.Liaison;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonListener;
import org.fxbench.trader.ServerTime;
import org.fxbench.trader.TradingServerSession;
import org.fxbench.trader.fxcm.FxcmLiaison;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.local.LocalLiaison;
import org.fxbench.trader.local.LocalServerSession;
import org.fxbench.ui.panel.AccountPanel;
import org.fxbench.ui.panel.ClosedPositionPanel;
import org.fxbench.ui.panel.OrderPanel;
import org.fxbench.ui.panel.SymbolPanel;
import org.fxbench.util.Utils;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

/**
 * Main core class.
 * It stores all business data and performs necessary their transformations.
 */
public class TradeDesk
{
//	private final Log mLogger = LogFactory.getLog(TradeDesk.class);

    private Accounts mAccounts;
    private Offers mOffers;
    private Orders mOrders;
    private Positions mOpenPositions;
    private Positions mClosedPositions;
    private Summaries mSummaries;
    private Messages mMessages;
    private PriceBars priceBars;
    
    private String loginUserName;
    
    /**
     * Current server time. It's valid only when in Connected state.
     */
    private final ServerTime mServerTime = (ServerTime)ServerTime.UNKNOWN.clone();
    /**
     * Vector of Server Time  listeners
     */
    private final Vector<IServerTimeListener> mServerTimeListeners = new Vector<IServerTimeListener>();
    /**
     * Thread that updates server time mServerTime.
     * It's started when liaison becomes in Connected state
     * and stops when liaison is disconnected.
     */
    private Thread mServerTimeThread;
    /**
     * Flag if the mServerTimeThread should stop
     */
    private boolean mStop;
    /**
     * This is instance of the inner class that is used as intermediate
     * between TradeDesk and liaison (callback).
     */
    private TradeDeskWorker mWorker;
    
    private String accountCurrency;
    private int currencyFractionDigits;
    private TradingServerSession tradingServerSession;

    public TradeDesk(String host) {
    	mWorker = new TradeDeskWorker();
    	if (host.equals(BenchApp.HOST_FXCM)) {
    		Liaison liaison = new FxcmLiaison();
            liaison.setTradeDesk(this);
            liaison.addLiaisonListener(mWorker);
            tradingServerSession = new FxcmServerSession(this);
            tradingServerSession.setLiaison(liaison);
    	} else if (host.equals(BenchApp.HOST_LOCAL)) {
    		Liaison liaison = new LocalLiaison();
            liaison.setTradeDesk(this);
            liaison.addLiaisonListener(mWorker);
            tradingServerSession = new LocalServerSession(this);
            tradingServerSession.setLiaison(liaison);
    	}
    	
    	mAccounts = new Accounts(this);
        mOffers = new Offers(this);
        mOrders = new Orders(this);
        mOpenPositions = new Positions(this);
        mClosedPositions = new Positions(this);
        mSummaries = new Summaries(this);
        mMessages = new Messages(this);
        priceBars = new PriceBars(this);
    }

    /**
     * Adds Server Time listener to the trade desk.
     * It will get notified when event is arrived from the server.
     *
     * @param aListener Listener
     */
    public void addServerTimeListener(IServerTimeListener aListener) {
        if (aListener != null) {
            mServerTimeListeners.add(aListener);
        }
    }

    /**
     * Performs necessary cleanup actions.
     * Called before application exit.
     */
    public void cleanup() {
    	tradingServerSession.getLiaison().setTradeDesk(null);
    	tradingServerSession.getLiaison().removeLiaisonListener(mWorker);
    }

    /**
     * Returns Accounts collection.
     *
     * @return Accounts
     */
    public Accounts getAccounts() {
        return mAccounts;
    }

    /**
     * Returns collection of closed Positions
     *
     * @return collection of closed Positions
     */
    public Positions getClosedPositions() {
        return mClosedPositions;
    }

    /**
     * Determines entry distance
     *
     * @return entry distance
     */
    public double getConditionalDistance() {
        double condDist = 1; //default value of 1
        //check to see if we have a pip distance in tss
        String parameter = tradingServerSession.getParameterValue("COND_DIST");
        if (parameter != null) {
            condDist = Double.parseDouble(parameter);
        }
        if (condDist == 0) {
            condDist = 1;
        }
        return condDist;
    }

    /**
     * Determines entry distance
     *
     * @return entry distance
     */
    public double getConditionalEntryDistance() {
        double entryPipDistance = ITraderConstants.RATE_DISPERSION; //default value of 5
        //check to see if we have a pip distance in tss
        String parameter = tradingServerSession.getParameterValue("COND_DIST_ENTRY");
        if (parameter != null) {
            entryPipDistance = Double.parseDouble(parameter);
        }
        if (entryPipDistance == 0) {
            entryPipDistance = 1;
        }
        return entryPipDistance;
    }

    public Messages getMessages() {
        return mMessages;
    }

    /**
     * Returns open positions collection.
     *
     * @return OpenPositions
     */
    public Positions getOpenPositions() {
        return mOpenPositions;
    }

    /**
     * Returns Orders collection.
     *
     * @return Orders
     */
    public Orders getOrders() {
        return mOrders;
    }

    /**
     * Returns Offers collection.
     *
     * @return Offers
     */
    public Offers getOffers() {
        return mOffers;
    }

    /**
     * Returns server time.
     *
     * @return Date
     */
    public Date getServerTime() {
        synchronized (mServerTime) {
            return (Date) mServerTime.clone();
        }
    }

    /**
     * Returns Summary collection.
     *
     * @return Summaries
     */
    public Summaries getSummaries() {
        return mSummaries;
    }
    
    public PriceBars getPriceBars() {
    	return priceBars;
    }

    /**
     * Returns current user name
     *
     * @return username
     */
    public String getLoginUserName() {
        return loginUserName;
    }

    /**
     * Removes servertime listener from the trade desk.
     *
     * @param aListener Listener
     */
    public void removeServerTimeListener(IServerTimeListener aListener) {
        mServerTimeListeners.remove(aListener);
    }

    /**
     * Sets current user name
     *
     * @param aUserName UserName
     */
    public void setLoginUserName(String aUserName) {
        loginUserName = aUserName;
    }

    /**
	 * @return the tradingServerSession
	 */
	public TradingServerSession getTradingServerSession() {
		return tradingServerSession;
	}

	/**
	 * @param tradingServerSession the tradingServerSession to set
	 */
	public void setTradingServerSession(TradingServerSession tradingServerSession) {
		this.tradingServerSession = tradingServerSession;
	}

	/**
	 * @return the accountCurrency
	 */
	public String getAccountCurrency() {
		return accountCurrency;
	}

	/**
	 * @param accountCurrency the accountCurrency to set
	 */
	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}

	/**
	 * @return the currencyFractionDigits
	 */
	public int getCurrencyFractionDigits() {
		return currencyFractionDigits;
	}

	/**
	 * @param currencyFractionDigits the currencyFractionDigits to set
	 */
	public void setCurrencyFractionDigits(int currencyFractionDigits) {
		this.currencyFractionDigits = currencyFractionDigits;
	}

	/**
	 * @return the liaison
	 */
	public Liaison getLiaison() {
		return tradingServerSession.getLiaison();
	}
    
	public DecimalFormat getRateFormat(String symbol) {
		TOffer offer = (TOffer)getOffers().get(symbol);
        if (offer != null) {
        	return new DecimalFormat(offer.getRateFormatPattern());
        } else {
        	return new DecimalFormat("#");
        }
	}
	
	public double calcPipCost(TOffer offer) {
        TAccount account = (TAccount)mAccounts.get(0);
        if (account == null) {
        	return 0.0;
        }
        
        double baseUnitSize = account.getBaseUnitSize();
        double pipSize = tradingServerSession.getPointSize(offer.getSymbol());
        String book = tradingServerSession.getAccountCurrency();
        String[] pair = Utils.splitCurrencyPair(offer.getSymbol());
        if (pair == null || pair.length != 2) {
        	return 0.0;
        }
        
        // try and find the crosspairs
        TOffer simpleCross1 = mOffers.getOffer(Utils.toPair(pair[1], book));
        TOffer simpleCross2 = mOffers.getOffer(Utils.toPair(book, pair[1]));
        TOffer unusualCross1 = mOffers.getOffer(Utils.toPair(pair[0], book));
        TOffer unusualCross2 = mOffers.getOffer(Utils.toPair(book, pair[0]));
//        TOffer simpleCFD1 = mOffers.getOffer(Utils.toPair(offer.getContractCurrency(), book));
//        TOffer simpleCFD2 = mOffers.getOffer(Utils.toPair(book, offer.getContractCurrency()));
        if (offer.getSymbol().startsWith(book)) { //1
        	return baseUnitSize * pipSize / offer.getAverage();
        } else if (offer.getSymbol().endsWith(book)) { //2
        	return baseUnitSize * pipSize;
        } else if (simpleCross1 != null) { //3
            return baseUnitSize * pipSize * simpleCross1.getAverage();           
        } else if (simpleCross2 != null) { //3
        	return baseUnitSize * pipSize * simpleCross2.getAverage();
        } else if (unusualCross1 != null) { //4
            return baseUnitSize * pipSize * unusualCross1.getAverage() / offer.getAverage();            
        } else if (unusualCross2 != null) { //4
        	return baseUnitSize * pipSize * unusualCross2.getAverage() / offer.getAverage();
        } else { //5,6
            // exotic cross, try and find a major for conversions
//            double toMajor = findConversionRateToMajor(aRate).getPrice();
//            double tobook = findConversionRateToBook().getPrice();
//            if (tobook != 0 && toMajor != 0) {
//                double pipcost = contractSize * pipSize * toMajor * tobook;
//                aRate.setPipCost(pipcost);
//            }
        	return 0.0;
        }
    }
	
	public void addAccount(TAccount account) {
		if (account != null) {
            mAccounts.add(account);
        }		
	}
	
	public void addAccounts(List<BaseEntity> accountList) {
		for (BaseEntity entity : accountList) {
			mAccounts.add(new TAccount(AccountPanel.getFieldDefStub(), (TAccount)entity));
		}
	}

	public void addClosedPosition(TPosition positin) {
		if (positin != null) {
            mClosedPositions.add(positin);
        }
	}
	
	public void addClosedPositions(List<BaseEntity> positionList) {
		for (BaseEntity entity : positionList) {
			mClosedPositions.add(new TPosition(Stage.Closed, ClosedPositionPanel.getFieldDefStub(), (TPosition)entity));
		}
	}

	public void addMessage(TMessage message) {
		if (message != null) {
            mMessages.add(message);
        }
	}

	public void addOpenPosition(TPosition position) {
		if (position != null) {
            mOpenPositions.add(position);
        }
	}

	public void addOrder(TOrder order) {
		if (order != null) {
            mOrders.add(order);
        }
	}
	
	public void addOrders(List<BaseEntity> orderList) {
		for (BaseEntity entity : orderList) {
			mOrders.add(new TOrder(OrderPanel.getFieldDefStub(), (TOrder)entity));
		}
	}

	public void addOffer(TOffer offer) {
		if (offer != null) {
            mOffers.add(offer);
        }
	}
	
	public void addOffers(List<BaseEntity> offerList) {
		for (BaseEntity entity : offerList) {
			mOffers.add(new TOffer(SymbolPanel.getFieldDefStub(), (TOffer)entity));
		}
	}

	public void clear() {
		mOrders.clear();
        mOpenPositions.clear();
        mAccounts.clear();
        mSummaries.clear();
        mClosedPositions.clear();
        mMessages.clear();
	}

	public TAccount getAccount(String accountID) {
		return mAccounts.getAccount(accountID);
	}

	public TPosition getOpenPosition(String ticketID) {
		return mOpenPositions.getPosition(ticketID);
	}

	public TOrder getOrder(String orderID) {
		return mOrders.getOrder(orderID);
	}

	public TOrder getOrderByTradeId(String tradeID) {
		return mOrders.getOrderByTradeId(tradeID);
	}

	public Positions getPositions() {
		return mOpenPositions;
	}

	public TOffer getOffer(String currency) {
		return mOffers.getOffer(currency);
	}

	public void removeOpenPosition(String ticketID) {
		mOpenPositions.remove(ticketID);
	}

	public void removeOrder(String orderID) {
		mOrders.remove(orderID);
	}

	public void updateAccount(TAccount account) {
		if (account == null) {
            return;
        }
        int index = mAccounts.indexOf(account);
        if (index >= 0) {
            mAccounts.set(index, account);
        }
	}

	public void updateOpenPosition(TPosition position) {
		if (position == null) {
            return;
        }
        int index = mOpenPositions.indexOf(position);
        if (index >= 0) {
            mOpenPositions.set(index, position);
        }
	}

	public void updateOrder(TOrder order) {
		if (order == null) {
            return;
        }
        int index = mOrders.indexOf(order);
        if (index >= 0) {
        	mOrders.set(index, order);
        }
	}

	public void updateOffer(TOffer offer) {
		if (offer == null) {
            return;
        }
        int index = mOffers.indexOf(offer);
        if (index >= 0) {
        	mOffers.set(index, offer);
        }
	}
	
	public void syncServerTime(Date dtTime, boolean isDispatch) {
		mWorker.syncServerTime(dtTime, isDispatch);
	}
	
    private class TradeDeskWorker extends LiaisonListener {
        /**
         * current liaison status
         */
        private LiaisonStatus mCurStat = LiaisonStatus.DISCONNECTED;

        /**
         * Creates time thread.
         */
        void createServerTimeThread() {
            if (mServerTimeThread != null || !tradingServerSession.isCreateServerTimeThread()) {
                return;
            }
            
            mStop = false;
            mServerTimeThread = new Thread() {
                private final Object mObj = new Object();

                @Override
                public void run() {
                    while (!mStop) {
                        synchronized (mObj) {
                            try {
                                mObj.wait(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                        syncServerTime(mServerTime.getTime() + 1000);
                    }
                }
            };
            mServerTimeThread.start();
        }

        /**
         * Dispatches of setting of server time.
         */
        private void dispatchSetServerTime() {
            synchronized (mServerTimeListeners) {
                for (IServerTimeListener listener : mServerTimeListeners) {
                    listener.timeUpdated(mServerTime);
                }
            }
        }
        
        /**
         * Synchronizes server time stored on the client side with actual server time.
         * Trade desk keeps current server time and updates it.
         *
         * @param aTime current time
         */
        public void syncServerTime(Date aTime, boolean isDispatch) {
            if (aTime != null) {
                synchronized (mServerTime) {
                    GregorianCalendar server = new GregorianCalendar();
                    server.setTime(mServerTime);
                    GregorianCalendar incoming = new GregorianCalendar();
                    incoming.setTime(aTime);
                    if (incoming.after(server)) {
                        mServerTime.setTime(aTime.getTime());
                    }
                }
                if (isDispatch) {
                	dispatchSetServerTime();
                	createServerTimeThread();
                }
            }
        }

        /**
         * Synchronizes server time.
         *
         * @param aTime time
         */
        public void syncServerTime(long aTime) {
            synchronized (mServerTime) {
                mServerTime.setTime(aTime);
            }
            dispatchSetServerTime();
        }
        
        @Override
        public void onLoginCompleted() {
        	accountCurrency = tradingServerSession.getAccountCurrency();
            currencyFractionDigits = Utils.getFractionDigits(accountCurrency);
        }

        /**
         * This method is called when status of liaison has changed.
         *
         * @param aStatus Status
         */
        @Override
        public void onLiaisonStatus(LiaisonStatus aStatus) {
            if (aStatus == null) {
                return;
            }
            if (mCurStat == LiaisonStatus.CONNECTING && aStatus == LiaisonStatus.READY) {
                if (!mServerTime.equals(ServerTime.UNKNOWN) && mServerTimeThread == null) {
                    createServerTimeThread();
                }
            } else if (aStatus == LiaisonStatus.DISCONNECTED) {
                // stopping mServerTimeThread thread
                Thread tmp = mServerTimeThread;
                mServerTimeThread = null;
                if (tmp != null) {
                    mStop = true;
                    tmp.interrupt();
                    try {
                        tmp.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (tradingServerSession.isReCalcTable()) {
	            if (mCurStat == LiaisonStatus.DISCONNECTED && aStatus == LiaisonStatus.CONNECTING
	                || mCurStat == LiaisonStatus.RECONNECTING && aStatus != LiaisonStatus.DISCONNECTED) {
	                mAccounts.enableRecalc(true);
	                mOpenPositions.enableRecalc(true);
	                mOrders.enableRecalc(true);
	                mSummaries.enableRecalc(true);
	                priceBars.enableRecalc(true);
	            }
	            if (aStatus == LiaisonStatus.DISCONNECTED
	                || aStatus == LiaisonStatus.DISCONNECTING
	                || aStatus == LiaisonStatus.RECONNECTING) {
	                mAccounts.enableRecalc(false);
	                mOpenPositions.enableRecalc(false);
	                mOrders.enableRecalc(false);
	                mSummaries.enableRecalc(false);
	                priceBars.enableRecalc(false);
	            }
            }
            mCurStat = aStatus;
        }
    }
}
