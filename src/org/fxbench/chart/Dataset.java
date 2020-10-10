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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.fxbench.chart.DataItem.PriceType;
import org.fxbench.entity.TPriceBar;
import org.fxbench.util.SerialVersion;

/**
 *
 * @author viorel.gheba
 */
public class Dataset
{
    private static final long serialVersionUID = SerialVersion.APPVERSION;
    public static final String OPEN = "Open";
    public static final String HIGH = "High";
    public static final String LOW = "Low";
    public static final String CLOSE = "Close";
    public static final String VOLUME = "Volume";
    public static final String[] LIST = new String[] {
        OPEN, HIGH, LOW, CLOSE
    };

    private List<DataItem> data;

    public Dataset() {
        this.data = new ArrayList<DataItem>();
    }

    public Dataset(List<DataItem> list) {
        data = list;
    }

    public boolean isNull() {
        return (data == null);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    public int getSize() {
    	return isNull() ? 0 : data.size();
    }
    
    public void clear() {
    	data.clear();
    }

    public void sort() {
		Collections.sort(data);
    }

    public int getItemsCount() {
        return data.size();
    }

    public int getLastIndex() {
		int index = data.size() - 1;
        return index < 0 ? 0 : index;
    }

    public List<DataItem> getDataItems() {
        return data;
    }

    public DataItem getDataItem(int index) {
        return index >= 0 && index < data.size() ? data.get(index) : null;
    }

    public void setDataItem(DataItem item) {
    	if (data.size() > 0) {
    		data.set(data.size() - 1, item);
    	}
    }
    
    public void setDataItem(int index, DataItem item) {
        if (index >=0 && index < data.size()) {
        	data.set(index, item);
        }
    }

    public void addDataItem(DataItem item) {
        data.add(item);
    }

    public long[] getTimeValues() {
        long[] values = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
			values[i] = data.get(i).getTime();
        }
        return values;
    }

    public Date[] getDateValues() {
        Date[] values = new Date[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getDate();
        }
        return values;
    }

    public double[] getOpenValues() {
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getOpen();
        }
        return values;
    }

    public double[] getHighValues() {
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getHigh();
        }
        return values;
    }

    public double[] getLowValues() {
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getLow();
        }
        return values;
    }

    public double[] getCloseValues() {
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getClose();
        }
        return values;
    }

    public double[] getVolumeValues() {
        double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getVolume();
        }
        return values;
    }
    
    public double[] getPrices(PriceType priceType) {
    	double[] values = new double[data.size()];
        for (int i = 0; i < data.size(); i++) {
        	values[i] = data.get(i).getPrice(priceType);
        }
        return values;
    }

    public long getTimeAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getTime() : 0;
    }

    public void setTimeAt(int index, long value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setTime(value);
    	}
    }

    public Date getDateAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getDate() : null;        
    }

    public double getOpenAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getOpen() : 0;     
    }

    public void setOpenAt(int index, double value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setOpen(value);
    	}
    }

    public double getHighAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getHigh() : 0;
    }

    public void setHighAt(int index, double value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setHigh(value);
    	}
    }

    public double getLowAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getLow() : 0;
    }

    public void setLowAt(int index, double value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setLow(value);
    	}
    }

    public double getCloseAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getClose() : 0;
    }

    public void setCloseAt(int index, double value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setClose(value);
    	}
    }

    public double getVolumeAt(int index) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getVolume() : 0;
    }

    public void setVolumeAt(int index, double value) {
    	DataItem item = getDataItem(index);
    	if (item != null) {
    		item.setVolume(value);
    	}
    }

	public DataItem getLastDataItem() {
		return getDataItem(getLastIndex());
	}

    public long getLastTime() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getTime() : 0;
    }

    public Date getLastDate() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getDate() : null;
    }

    public double getLastOpen() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getOpen() : 0;
    }

    public double getLastHigh() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getHigh() : 0;
    }

    public double getLastLow() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getLow() : 0;
    }

    public double getLastClose() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getClose() : 0;
    }

    public double getLastVolume() {
    	DataItem item = getDataItem(getLastIndex());
        return item != null ? item.getVolume() : 0;
    }

    public double getPriceAt(int index, String priceType) {
        return getPriceAt(index, PriceType.valueOf(priceType));
    }

    public double getPriceAt(int index, PriceType priceType) {
    	DataItem item = getDataItem(index);
        return item != null ? item.getPrice(priceType) : 0;
    }

    public double getLastPrice(String priceType) {
    	return getLastPrice(PriceType.valueOf(priceType));
    }

    public double getLastPrice(PriceType priceType) {
    	DataItem item = getDataItem(getLastIndex());
    	return item != null ? item.getPrice(priceType) : 0;
    }
    
    public Dataset subDataset(int beginPos, int endPos) {
    	if (beginPos < 0) {
    		beginPos = 0;
    	}
    	if (endPos > data.size()) {
    		endPos = data.size();
    	}
    	if (beginPos > data.size() ||
    		endPos < 0 ||
    		beginPos > endPos) {
    		return null;
    	}
    	Dataset dataset = new Dataset();
    	dataset.data = new ArrayList<DataItem>(data.subList(beginPos, endPos));
    	return dataset;
    }
    
    public static Dataset valueOf(List<TPriceBar> priceBarList) {
    	Dataset dataset = new Dataset();
    	for (TPriceBar priceBar : priceBarList) {
    		dataset.data.add(DataItem.valueOf(priceBar));
    	}
    	return dataset;
    }
    
    public static Dataset valueOfB(List<TPriceBar> priceBarList) {
    	Dataset dataset = new Dataset();
    	for (TPriceBar priceBar : priceBarList) {
    		dataset.data.add(DataItem.valueOfB(priceBar));
    	}
    	return dataset;
    }

    public static Dataset EMPTY(int count) {
        List<DataItem> list = new ArrayList<DataItem>();
        for (int i = 0; i < count; i++) {
            list.add(i, null);
        }
        return new Dataset(list);
    }

    public static Dataset CONST(Dataset d, double ct) {
        if (d == null) {
            return null;
        }

        int count = d.getItemsCount();
        Dataset result = EMPTY(count);
        for (int i = 0; i < count; i++) {
            result.setDataItem(i, new DataItem(d.getTimeAt(i), ct));
        }

        return result;
    }

    public static Dataset LOG(Dataset d) {
        if (d == null) {
            return null;
        }

        int count = d.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++) {
            if (d.getDataItem(i) != null) {
                double open = Math.log10(d.getOpenAt(i));
                double high = Math.log10(d.getHighAt(i));
                double low = Math.log10(d.getLowAt(i));
                double close = Math.log10(d.getCloseAt(i));
                double volume = Math.log10(d.getVolumeAt(i));
                result.setDataItem(i, new DataItem(d.getTimeAt(i), open, high, low, close, volume));
            }
        }

        return result;
    }

    public static Dataset SUM(Dataset d1, Dataset d2)
    {
        if (d1 == null || d2 == null)
        {
            return null;
        }

        int count = d1.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
            result.setDataItem(i, new DataItem(
                    d1.getTimeAt(i),
                    d1.getOpenAt(i) + d2.getOpenAt(i),
                    d1.getHighAt(i) + d2.getHighAt(i),
                    d1.getLowAt(i) + d2.getLowAt(i),
                    d1.getCloseAt(i) + d2.getCloseAt(i),
                    d1.getVolumeAt(i) + d2.getVolumeAt(i)));
        }

        return result;
    }

    public static Dataset DIFF(Dataset d1, Dataset d2)
    {
        if (d1 == null || d2 == null)
        {
            return null;
        }

        int count = d1.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
            result.setDataItem(i, new DataItem(
                    d1.getTimeAt(i),
                    d1.getOpenAt(i) - d2.getOpenAt(i),
                    d1.getHighAt(i) - d2.getHighAt(i),
                    d1.getLowAt(i) - d2.getLowAt(i),
                    d1.getCloseAt(i) - d2.getCloseAt(i),
                    d1.getVolumeAt(i) - d2.getVolumeAt(i)));
        }

        return result;
    }

    public static Dataset MULTIPLY(Dataset d, double value)
    {
        if (d == null)
        {
            return null;
        }

        int count = d.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
            result.setDataItem(i, new DataItem(
                    d.getTimeAt(i),
                    d.getOpenAt(i) * value,
                    d.getHighAt(i) * value,
                    d.getLowAt(i) * value,
                    d.getCloseAt(i) * value,
                    d.getVolumeAt(i) * value));
        }

        return result;
    }

    public static Dataset DIV(Dataset d, double value)
    {
        if (d == null)
        {
            return null;
        }

        int count = d.getItemsCount();
        Dataset result = EMPTY(count);

        for (int i = 0; i < count; i++)
        {
            result.setDataItem(i, new DataItem(
                    d.getTimeAt(i),
                    d.getOpenAt(i) / value,
                    d.getHighAt(i) / value,
                    d.getLowAt(i) / value,
                    d.getCloseAt(i) / value,
                    d.getVolumeAt(i) / value));
        }

        return result;
    }

    public static Dataset HMA(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        Dataset wma_n2 = Dataset.WMA(dataset, period / 2);
        Dataset wma_n = Dataset.WMA(dataset, period);
        Dataset two_wma_n2 = Dataset.MULTIPLY(wma_n2, 2);
        Dataset diff = Dataset.DIFF(two_wma_n2, wma_n);

        Dataset result = Dataset.WMA(diff, (int) Math.sqrt(period));

        return result;
    }

    public static Dataset SMA(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        int count = dataset.getItemsCount();
        Dataset result = Dataset.EMPTY(count);

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
        {
            result.setDataItem(j, null);
        }

        for (int i = j + period - 1; i < count; i++)
        {
            long time = dataset.getTimeAt(i);
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            for (int k = 0; k < period; k++)
            {
                open += dataset.getOpenAt(i - k);
                high += dataset.getHighAt(i - k);
                low += dataset.getLowAt(i - k);
                close += dataset.getCloseAt(i - k);
                volume += dataset.getVolumeAt(i - k);
            }

            open /= (double) period;
            high /= (double) period;
            low /= (double) period;
            close /= (double) period;
            volume /= (double) period;

            result.setDataItem(i, new DataItem(time, open, high, low, close, volume));
        }
        return result;
    }

    public static Dataset EMA(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        int count = dataset.getItemsCount();
        Dataset result = Dataset.EMPTY(count);

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
        {
            result.setDataItem(j, null);
        }

        double open = 0;
        double high = 0;
        double low = 0;
        double close = 0;
        double volume = 0;

        for (int i = j; i < period + j && i < count; i++)
        {
            open += dataset.getOpenAt(i);
            high += dataset.getHighAt(i);
            low += dataset.getLowAt(i);
            close += dataset.getCloseAt(i);
            volume += dataset.getVolumeAt(i);

            if (i == period + j - 1)
            {
                open /= (double) period;
                high /= (double) period;
                low /= (double) period;
                close /= (double) period;
                volume /= (double) period;

                result.setDataItem(i, new DataItem(dataset.getTimeAt(i), open, high, low, close, volume));
            } else
            {
                result.setDataItem(i, null);
            }
        }

        double k = 2 / ((double) (period + 1));
        for (int i = period + j; i < dataset.getItemsCount(); i++)
        {
            open = (dataset.getOpenAt(i) - open) * k + open;
            high = (dataset.getHighAt(i) - high) * k + high;
            low = (dataset.getLowAt(i) - low) * k + low;
            close = (dataset.getCloseAt(i) - close) * k + close;
            volume = (dataset.getVolumeAt(i) - volume) * k + volume;

            result.setDataItem(i, new DataItem(dataset.getTimeAt(i), open, high, low, close, volume));
        }

        return result;
    }

    /*
     * Wilder does not use the standard exponential moving average formula.
     *
     * Indicators affected are:
    
     * Average True Range
     * Directional Movement System
     * Relative Strength Index
     * Twiggs Money Flow developed by Colin Twiggs using Wilder's moving average formula.
     *
     * http://www.incrediblecharts.com/indicators/wilder_moving_average.php
     * http://user42.tuxfamily.org/chart/manual/Exponential-Moving-Average.html
     */
    public static Dataset EMAWilder(Dataset dataset, int period)
    {
        int classic_ema_period = 2*period - 1;
        return Dataset.EMA(dataset, classic_ema_period);
    }

    public static Dataset WMA(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        int count = dataset.getItemsCount();
        Dataset result = Dataset.EMPTY(count);
        double denominator = ((double) period * ((double) period + 1)) / 2;

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null); j++)
        {
            result.setDataItem(j, null);
        }

        for (int i = j; i < period + j; i++)
        {
            result.setDataItem(i, null);
        }

        for (int i = period + j; i < count; i++)
        {
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            for (int k = i - period; k < i; k++)
            {
                open += (period - i + k + 1) * dataset.getOpenAt(k);
                high += (period - i + k + 1) * dataset.getHighAt(k);
                low += (period - i + k + 1) * dataset.getLowAt(k);
                close += (period - i + k + 1) * dataset.getCloseAt(k);
                volume += (period - i + k + 1) * dataset.getVolumeAt(k);
            }

            open /= denominator;
            high /= denominator;
            low /= denominator;
            close /= denominator;
            volume /= denominator;

            result.setDataItem(i, new DataItem(dataset.getTimeAt(i), open, high, low, close, volume));
        }

        return result;
    }

    public static Dataset TEMA(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        int count = dataset.getItemsCount();
        Dataset result = Dataset.EMPTY(dataset.getItemsCount());
        Dataset ema1 = EMA(dataset, period);
        Dataset ema2 = EMA(ema1, period);
        Dataset ema3 = EMA(ema2, period);

        int j = 0;
        for (j = 0; j < count && (dataset.getDataItem(j) == null || ema1.getDataItem(j) == null || ema2.getDataItem(j) == null || ema3.getDataItem(j) == null); j++)
        {
            result.setDataItem(j, null);
        }

        for (int i = j; i < count; i++)
        {
            double open = 0;
            double high = 0;
            double low = 0;
            double close = 0;
            double volume = 0;

            if (ema1.getCloseAt(i) != 0 && ema2.getCloseAt(i) != 0 && ema3.getCloseAt(i) != 0)
            {
                open = 3 * ema1.getOpenAt(i) - 3 * ema2.getOpenAt(i) + ema3.getOpenAt(i);
                high = 3 * ema1.getHighAt(i) - 3 * ema2.getHighAt(i) + ema3.getHighAt(i);
                low = 3 * ema1.getLowAt(i) - 3 * ema2.getLowAt(i) + ema3.getLowAt(i);
                close = 3 * ema1.getCloseAt(i) - 3 * ema2.getCloseAt(i) + ema3.getCloseAt(i);
                volume = 3 * ema1.getVolumeAt(i) - 3 * ema2.getVolumeAt(i) + ema3.getVolumeAt(i);
            }

            result.setDataItem(i, new DataItem(dataset.getTimeAt(i), open, high, low, close, volume));
        }

        return result;
    }

    public static Dataset[] ADX(Dataset dataset, int period)
    {
        if (dataset == null)
        {
            return null;
        }

        int count = dataset.getItemsCount();
        Dataset pdi = Dataset.EMPTY(count);
        Dataset mdi = Dataset.EMPTY(count);
        Dataset dx = Dataset.EMPTY(count);

        Dataset tr = Dataset.EMPTY(count);
        Dataset hmhp = Dataset.EMPTY(count);
        Dataset lmlp = Dataset.EMPTY(count);

        for (int i = 1; i < count; i++)
        {
            long time = dataset.getTimeAt(i);

            double tr0 = dataset.getHighAt(i) - dataset.getCloseAt(i - 1);
            double tr1 = Math.abs(dataset.getHighAt(i) - dataset.getCloseAt(i - 1));
            tr0 = Math.max(tr0, tr1);
            tr1 = Math.abs(dataset.getLowAt(i) - dataset.getCloseAt(i - 1));
            tr.setDataItem(i, new DataItem(time, Math.max(tr0, tr1)));

            if (dataset.getHighAt(i) <= dataset.getHighAt(i - 1) && dataset.getLowAt(i) < dataset.getLowAt(i - 1))
            {
                lmlp.setDataItem(i, new DataItem(time, dataset.getLowAt(i - 1) - dataset.getLowAt(i)));
            } else if (dataset.getHighAt(i) > dataset.getHighAt(i - 1) && dataset.getLowAt(i) >= dataset.getLowAt(i - 1))
            {
                hmhp.setDataItem(i, new DataItem(time, dataset.getHighAt(i) - dataset.getHighAt(i - 1)));
            } else
            {
                double tempH = Math.abs(dataset.getHighAt(i) - dataset.getHighAt(i - 1));
                double tempL = Math.abs(dataset.getLowAt(i) - dataset.getLowAt(i - 1));

                if (tempH > tempL)
                {
                    hmhp.setDataItem(i, new DataItem(time, tempH));
                } else
                {
                    lmlp.setDataItem(i, new DataItem(time, tempL));
                }
            }
        }

        Dataset strDS = Dataset.EMA(tr, period);
        Dataset shmhpDS = Dataset.EMA(hmhp, period);
        Dataset slmlpDS = Dataset.EMA(lmlp, period);

        for (int i = period; i < count; i++)
        {
            long time = dataset.getTimeAt(i);
            double curPDI = 0;
            double curMDI = 0;

            if (strDS.getDataItem(i) != null)
            {
                if (strDS.getCloseAt(i) != 0)
                {
                    curPDI = (shmhpDS.getCloseAt(i) / strDS.getCloseAt(i)) * 100;
                    curMDI = (slmlpDS.getCloseAt(i) / strDS.getCloseAt(i)) * 100;
                }
            }

            pdi.setDataItem(i, new DataItem(time, curPDI));
            mdi.setDataItem(i, new DataItem(time, curMDI));

            if (curPDI + curMDI != 0)
            {
                dx.setDataItem(i, new DataItem(time, (Math.abs(curPDI - curMDI) / (curPDI + curMDI) * 100)));
            }
        }

        Dataset adx = Dataset.EMA(dx, period);

        Dataset[] result = new Dataset[3];
        result[0] = pdi;
        result[1] = mdi;
        result[2] = adx;
        return result;
    }

    public static int getPrice(String price)
    {
        for (int i = 0; i < LIST.length; i++)
            if (price.equals(LIST[i]))
                return i;
        return 3; // Default Close
    }
    
    
    
	
}
