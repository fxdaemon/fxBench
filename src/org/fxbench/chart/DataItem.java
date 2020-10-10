/*
* Copyright 2020 FXDaemon
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.fxbench.chart;

import java.io.Serializable;
import java.util.Date;

import org.fxbench.entity.TPriceBar;
import org.fxbench.util.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class DataItem implements Serializable, Comparable<DataItem>
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;

    private long time;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;

    public DataItem(long time, double close) {
        this.time = time;
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.close = close;
        this.volume = 0;
    }

    public DataItem(long time, double open, double high, double low, double close, double volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

//    public DataItem getEmptyDataItem()
//    { return null; }

    public long getTime() {
    	return time;
    }

    public void setTime(long time) {
    	this.time = time;
    }

    public Date getDate() {
    	return new Date(time);
    }

    public double getOpen() {
    	return open;
    }

    public void setOpen(double open) {
    	this.open = open; 
    }

    public double getHigh() {
    	return high;
    }

    public void setHigh(double high) {
    	this.high = high;
    }

    public double getLow() {
    	return low;
    }

    public void setLow(double low) {
    	this.low = low;
    }

    public double getClose() {
    	return close;
    }

    public void setClose(double close) {
    	this.close = close;
    }

    public double getVolume() {
    	return volume;
    }

    public void setVolume(double volume) {
    	this.volume = volume; 
    }
    
    public double getPrice(PriceType priceType) {
    	switch (priceType) {
    	case Open:
    		return open;
    	case High:
    		return high;
    	case Low:
    		return low;
    	case Close:
    		return close;
    	case Volume:
    		return volume;
    	case Typical:
    		return (high + low + close) / 3;
    	case Median:
    		return (high + low) / 2;
    	case Weighted:
    		return (high + low + 2 * close) / 4;
    	default:
    		return 0;
    	}
    }

	@Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        
        if (!(obj instanceof DataItem)) {
            return false;
        }
        
        DataItem that = (DataItem) obj;
        if (getTime() != that.getTime() ||
    		getOpen() != that.getOpen() ||
    		getHigh() != that.getHigh() ||
    		getLow() != that.getLow() ||
    		getClose() != that.getClose() ||
    		getVolume() != that.getVolume()){
            return false;
        }
        
        return true;
    }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (int) (this.time ^ (this.time >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.open) ^ (Double.doubleToLongBits(this.open) >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.high) ^ (Double.doubleToLongBits(this.high) >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.low) ^ (Double.doubleToLongBits(this.low) >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.close) ^ (Double.doubleToLongBits(this.close) >>> 32));
		hash = 53 * hash + (int) (Double.doubleToLongBits(this.volume) ^ (Double.doubleToLongBits(this.volume) >>> 32));
		return hash;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(time)).append(",");
        //sb.append("DataItem [open=");
        sb.append(Double.toString(open)).append(",");
        //sb.append(", high=");
        sb.append(Double.toString(high)).append(",");
        //sb.append(", low=");
        sb.append(Double.toString(low)).append(",");
        //sb.append(", close=");
        sb.append(Double.toString(close)).append(",");
        //sb.append(", volume=");
        sb.append(Double.toString(volume));
        //sb.append(", time=");
        return sb.toString();
    }

	@Override
	public int compareTo(DataItem item) {
		return getDate().compareTo(item.getDate());
	}

	public boolean updateClose(DataItem item) {
		return Double.compare(close, item.getClose()) != 0;
	}
	
	public static DataItem valueOf(TPriceBar priceBar) {
		return new DataItem(
				priceBar.getStartDate().getTime(),
				priceBar.getAskOpen(),
				priceBar.getAskHigh(),
				priceBar.getAskLow(),
				priceBar.getAskClose(),
				0);
	}
	
	public static DataItem valueOfB(TPriceBar priceBar) {
		return new DataItem(
				priceBar.getStartDate().getTime(),
				priceBar.getBidOpen(),
				priceBar.getBidHigh(),
				priceBar.getBidLow(),
				priceBar.getBidClose(),
				0);
	}

	public enum PriceType {
		Open, High, Low, Close, Volume, Typical, Median, Weighted;
	}
}
