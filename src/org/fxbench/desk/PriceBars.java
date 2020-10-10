package org.fxbench.desk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxbench.entity.TOffer;
import org.fxbench.entity.TPriceBar;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.trader.IServerTimeListener;
import org.fxbench.trader.ServerTime;
import org.fxbench.util.properties.ChartSchema;
import org.fxbench.util.properties.SettingManager;
import org.fxbench.util.signal.ISignalListener;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signaler;
import org.fxbench.util.signal.Signal.SignalType;

public class PriceBars extends Signaler implements ISignalListener, IServerTimeListener
{
	private TradeDesk tradeDesk;
	private Map<String, List<TPriceBar>> mapPriceBars;

	public PriceBars(TradeDesk tradeDesk) {
    	this.tradeDesk = tradeDesk;
    	mapPriceBars = new HashMap<String, List<TPriceBar>>();
    	tradeDesk.addServerTimeListener(this);
    }
	
	public boolean isEmpty() {
		synchronized (mapPriceBars) {
			return mapPriceBars.isEmpty();
		}
    }
    
    public int size() {
    	synchronized (mapPriceBars) {
    		return mapPriceBars.size();
    	}
    }
    
    public void clear() {
    	synchronized (mapPriceBars) {
	    	for (List<TPriceBar> val : mapPriceBars.values()) {
	    	    val.clear();
	    	}
	    	mapPriceBars.clear();
    	}
    	Runtime.getRuntime().gc();
    }
    
    public void clear(String symbol, String period) {
    	clear(getKey(symbol, period));
    }
    
    public void clear(String key) {
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(key);
    		if (priceBarList != null) {
    			priceBarList.clear();
    			mapPriceBars.remove(key);
    		}
    	}
    }
    
    public List<TPriceBar> get(String key) {
    	synchronized (mapPriceBars) {
    		return mapPriceBars.get(key);
    	}
    }
    
    private String getKey(String symbol, String period) {
    	return symbol + ChartSchema.DELIM + period;
    }
    
    public List<TPriceBar> get(String symbol, String period) {
    	return get(getKey(symbol, period));
    }
    
    public List<TPriceBar> get(String key, Date fromDate, Date toDate) {
    	List<TPriceBar> retPriceBarList = null;
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(key);
    		if (priceBarList != null) {
    			retPriceBarList = new ArrayList<TPriceBar>();
    			for (TPriceBar priceBar : priceBarList) {
    	    		if (priceBar.getStartDate().compareTo(fromDate) >= 0 &&
    	    			priceBar.getStartDate().compareTo(toDate) <= 0) {
    	    			retPriceBarList.add(priceBar);
    	    		}
    	    	}
    		}
    	}
    	return retPriceBarList;
    }
    
    public List<TPriceBar> get(String symbol, String period, Date fromDate, Date toDate) {
        return get(getKey(symbol, period), fromDate, toDate);
    }
    
    public List<TPriceBar> get(String symbol, Interval interval, Date toDate, int size, int offset) {
    	final ReferSpot referSpot = new ReferSpot();
    	referSpot.symbol = symbol;
    	referSpot.interval = interval;
    	referSpot.referDate = toDate;
    	referSpot.referSize = size;
    	referSpot.referOffset = offset;
    	
    	int count = 0;
		int beginPos = 0;
		int endPos = 0;
		int listSize = 0;
		List<TPriceBar> getPriceBarList = null;
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(getKey(symbol, interval.name()));
        	if (priceBarList != null) {
        		listSize = priceBarList.size();
        		if (listSize > 1) {
        			referSpot.headDate = priceBarList.get(0).getStartDate();
        			referSpot.tailDate = priceBarList.get(listSize - 1).getStartDate();
        			endPos = listSize;
    	    		for (int i = listSize - 1; i >= 0; i--) {
    	    			TPriceBar priceBar =  priceBarList.get(i);
    	    			int compare = priceBar.getStartDate().compareTo(toDate);
    	    			if (compare == 0) {
    	    				count++;
    	    				endPos = i + 1;
    	    			} else if (compare < 0) {
    	    				count++;
        					if (i < listSize - 1) {
        						if (priceBarList.get(i + 1).getStartDate().compareTo(toDate) > 0) {
        							endPos = i + 1;
        						}
        					}
    	    			}
    	    			if (count >= size) {
    	    				beginPos = i;
    	    				break;
    	    			}
    	    		}
    	    		
    	    		beginPos += offset;
    	    		if (beginPos < 0) {
    	    			endPos -= beginPos;
    	    			beginPos = 0;
    	    		}
    	    		
    	    		endPos += offset;
    	    		if (endPos > listSize) {
    	    			beginPos -= (endPos - listSize);
    	    			endPos = listSize;
    	    		}
    	    		
    	    		getPriceBarList = new ArrayList<TPriceBar>(priceBarList.subList(beginPos, endPos));
        		}
        	}
    	}
    	
    	referSpot.listSize = listSize;
    	referSpot.takeBeginPos = beginPos;
    	referSpot.takeEndPos = endPos;
    	
//    	SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				tradeDesk.getTradingServerSession().firePriceBarsReferSpot(referSpot);
//			}
//		});
    	
    	return getPriceBarList;
    }
        
    public TPriceBar getHead(String symbol, String period) {
    	return getHead(getKey(symbol, period));
    }
    
    public TPriceBar getHead(String key) {
    	synchronized (mapPriceBars) {
	    	List<TPriceBar> priceBarList = mapPriceBars.get(key);
	    	if (priceBarList == null || priceBarList.size() == 0) {
	    		return null;
	    	}
	    	return priceBarList.get(0);
    	}
    }
    
    public TPriceBar getTail(String symbol, String period) {
    	return getTail(getKey(symbol, period));
    }
    
    public TPriceBar getTail(String key) {
    	synchronized (mapPriceBars) {
	    	List<TPriceBar> priceBarList = mapPriceBars.get(key);
	    	if (priceBarList == null || priceBarList.size() == 0) {
	    		return null;
	    	}
	    	return priceBarList.get(priceBarList.size() - 1);
    	}
    }
    
    public void add(List<TPriceBar> priceBarList) {
    	if (priceBarList == null || priceBarList.size() == 0) {
    		return;
    	}
    	
    	String key = priceBarList.get(0).getKey();
    	TPriceBar notifyTail = null;
    	    	
    	synchronized (mapPriceBars) {
    		List<TPriceBar> existPriceBarList = mapPriceBars.get(key);
	    	if (existPriceBarList == null || existPriceBarList.size() == 0) {
	    		mapPriceBars.put(key, priceBarList);
	    		existPriceBarList = priceBarList;
	    		notifyTail = priceBarList.get(priceBarList.size() - 1);
	    	} else {
	    		TPriceBar addHead = priceBarList.get(0);
	    		TPriceBar addTail = priceBarList.get(priceBarList.size() - 1);
	    		TPriceBar existHead = existPriceBarList.get(0);
	    		TPriceBar existTail = existPriceBarList.get(existPriceBarList.size() - 1);
	    		
	    		if (addHead.getStartDate().compareTo(existHead.getStartDate()) < 0) {
	    			int addEndPos = 1;
	    			for (; addEndPos < priceBarList.size(); addEndPos++) {
	    				if (priceBarList.get(addEndPos).getStartDate().compareTo(existHead.getStartDate()) >= 0) {
	    					break;
	    				}
	    			}
	    			existPriceBarList.addAll(0, priceBarList.subList(0, addEndPos));
	    			notifyTail = priceBarList.get(addEndPos - 1);
//	    			int beginPos = 0;
//	    			Integer latestPos = mapReferencdPos.get(getKey(addHead.getSymbol(), addHead.getInterval().name()));
//	    			if (latestPos != null) {
//	    				beginPos = latestPos.intValue();
//	    			} else {
//	    				beginPos = existPriceBarList.size() - 1;
//	    			}
//	    			tradeDesk.getTradingServerSession().reservePriceBars(addHead, beginPos);
	    			
	    		} else if (addHead.getStartDate().compareTo(existTail.getStartDate()) <= 0 &&
	    					addTail.getStartDate().compareTo(existTail.getStartDate()) > 0) {
	    			int addBeginPos = 1;
	    			for (; addBeginPos < priceBarList.size(); addBeginPos++) {
	    				if (priceBarList.get(addBeginPos).getStartDate().compareTo(existTail.getStartDate()) > 0) {
	    					break;
	    				}
	    			}
	    			existPriceBarList.addAll(priceBarList.subList(addBeginPos, priceBarList.size()));
	    			notifyTail = addTail;
	    		} else if (addHead.getStartDate().compareTo(existTail.getStartDate()) > 0) {
	    			existPriceBarList.addAll(priceBarList);
	    			notifyTail = addTail;
	    		}    		
	    	}
    	}
    	
    	if (notifyTail != null) {
    		notify(Signal.newAddSignal(0, notifyTail));
    	}
    }
    
    public void add(TPriceBar priceBar) {
    	String key = priceBar.getKey();
    	int addIndex = 0;
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(key);
	    	if (priceBarList == null) {
	    		priceBarList = new ArrayList<TPriceBar>();
	    		mapPriceBars.put(key, priceBarList);
	    	}
	    	priceBarList.add(priceBar);
	    	addIndex = priceBarList.size() - 1;
    	}
    	notify(Signal.newAddSignal(addIndex, priceBar));
    }
    
    public void append(TPriceBar priceBar) {
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(priceBar.getKey());
    		if (priceBarList != null && priceBarList.size() > 0) {
    			TPriceBar tail = priceBarList.get(priceBarList.size() - 1);
    	    	long intervalSeconds = Interval.valueOf(tail.getInterval().toString()).getMilliSecond();
    	    	Date nextFromDate = new Date(tail.getStartDate().getTime() + intervalSeconds);
    	    	Date nextToDate = new Date(tail.getStartDate().getTime() + 2 * intervalSeconds);
    	    	if (priceBar.getStartDate().compareTo(tail.getStartDate()) >= 0 &&
    	        	priceBar.getStartDate().compareTo(nextFromDate) <= 0) {
    	    		priceBar.setStartDate(tail.getStartDate());
    	    		priceBarList.set(priceBarList.size() - 1, priceBar);
    	    	} else if (priceBar.getStartDate().compareTo(nextFromDate) >= 0 &&
    	    		priceBar.getStartDate().compareTo(nextToDate) <= 0) {
    	    		priceBar.setStartDate(nextFromDate);
    	    		priceBarList.add(priceBar);
    	    	}
        	}
    	}
    }
    
    public TPriceBar set(TPriceBar priceBar) {
    	TPriceBar oldPriceBar = null;
    	int setIndex = 0;
    	synchronized (mapPriceBars) {
    		List<TPriceBar> priceBarList = mapPriceBars.get(priceBar.getKey());
    		if (priceBarList != null && priceBarList.size() > 0) {
    			TPriceBar tail = priceBarList.get(priceBarList.size() - 1);
    	    	if (priceBar.getStartDate().equals(tail.getStartDate())) {
    	    		oldPriceBar = priceBarList.set(priceBarList.size() - 1, priceBar);
    	    	}
    	    	setIndex = priceBarList.size() - 1;
    		}
    	}
    	notify(Signal.newChangeSignal(setIndex, priceBar, oldPriceBar));
    	return oldPriceBar;
    }
    
    public void removeLeaveTail(String symbol, String period, int leaveSize) {
    	removeLeaveTail(getKey(symbol, period), leaveSize);
    }
    
    public void removeLeaveTail(String key, int leaveSize) {
    	synchronized (mapPriceBars) {
	    	List<TPriceBar> priceBarList = mapPriceBars.get(key);
	    	if (priceBarList != null) {
	    		int totalSize = priceBarList.size();
		    	if (totalSize > leaveSize) {
		    		List<TPriceBar> subList = new ArrayList<TPriceBar>(
		    				priceBarList.subList(0, totalSize - leaveSize));
		    		priceBarList.clear();
		    		priceBarList.addAll(subList);
		    		subList.clear();
		    	}
	    	}
    	}
    }
    
    public void enableRecalc(boolean aEnable) {
        if (aEnable) {
            tradeDesk.getOffers().subscribe(this, SignalType.CHANGE);
        } else {
            tradeDesk.getOffers().unsubscribe(this, SignalType.CHANGE);
        }
    }
    
    public static Date addPriceBarStartDate(int interval, Date toDate, int size) {
    	int openWday = SettingManager.getInstance().getMarketOpenWday();
    	int openHour = SettingManager.getInstance().getMarketOpenHour();
    	int closeWday = SettingManager.getInstance().getMarketCloseWday();
    	int closeHour = SettingManager.getInstance().getMarketCloseHour();
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(toDate);
    	while (size > 0) {
    		calendar.add(Calendar.SECOND, interval);
    		int wday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    		int hour = calendar.get(Calendar.HOUR_OF_DAY);
    		if (wday == closeWday && hour > closeHour ||
    			wday == openWday && hour < openHour ||
    			wday == 0/*sunday*/) {
    		} else {
    			size--;
    		}
    	}
    	return calendar.getTime();
    }
    
	@Override
	// come from offer
    public void onSignal(Signaler src, Signal signal) {
    	if (signal != null) {
	        Object obj = signal.getElement();
	        if (obj instanceof TOffer) {
	        	TOffer offer = (TOffer)obj;
	        	List<TPriceBar> changePriceBarList = new ArrayList<TPriceBar>();
	        	synchronized (mapPriceBars) {
		        	for (Map.Entry<String, List<TPriceBar>> entry : mapPriceBars.entrySet()) {
		        		String key = entry.getKey();
		        		if (key.startsWith(offer.getSymbol())) {
		        			List<TPriceBar> priceBarList = entry.getValue();
		        			if (priceBarList.size() > 0) {
		        				TPriceBar priceBar = priceBarList.get(priceBarList.size() - 1);
		        				priceBar.updateByOffer(offer);
		        				changePriceBarList.add(priceBar);
		        			}
		        		}
		        	}
	        	}
	        	for (TPriceBar priceBar : changePriceBarList) {
	        		notify(Signal.newChangeSignal(-1, priceBar, priceBar));
	        	}
	        }
    	}
    }
	
	@Override
	//come from servertime thread
	public void timeUpdated(ServerTime aTime) {		
		List<TPriceBar> tailPriceBarList = new ArrayList<TPriceBar>();
		
		synchronized (mapPriceBars) {
			for (Map.Entry<String, List<TPriceBar>> entry : mapPriceBars.entrySet()) {
				List<TPriceBar> priceBarList = entry.getValue();
				if (priceBarList.size() > 0) {
					TPriceBar tail = priceBarList.get(priceBarList.size() - 1);
					if (aTime.getTime() - tail.getStartDate().getTime() >= tail.getInterval().getMilliSecond()) {
						tailPriceBarList.add(tail);
					}
					
				}				
			}
		}
		
		for (TPriceBar priceBar : tailPriceBarList) {
			Date endDate = new Date();
			endDate.setTime(priceBar.getStartDate().getTime() + priceBar.getInterval().getMilliSecond() * 3);
			tradeDesk.getTradingServerSession().loadPriceBarFromHost(
					priceBar.getSymbol(), priceBar.getInterval().name(), priceBar.getStartDate(), endDate);
		}
	}
	
	public class ReferSpot
	{
		public String symbol;
		public Interval interval;
		public Date headDate;
		public Date tailDate;
		public int listSize;
		public Date referDate;
		public int referSize;
		public int referOffset;
		public int takeBeginPos;
		public int takeEndPos;
	}
}
