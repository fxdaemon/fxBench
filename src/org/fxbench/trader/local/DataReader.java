package org.fxbench.trader.local;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.entity.TPriceBar;
import org.fxbench.entity.TPriceBar.Interval;
import org.fxbench.util.Utils;

public class DataReader
{
	private final static Log logger = LogFactory.getLog(DBAccess.class);
	
	private final static String FILE_EXT = ".dat";
//	private final static char SEPARATOR = '/';
	
	public static List<TPriceBar> read(String fileDir, Date startDate, String symbol, Interval interval) {
		StringBuffer sb = new StringBuffer();
		sb.append(fileDir).append(File.separator);
		sb.append(symbol.replace('/', '-')).append(File.separator);
		sb.append(Utils.getYear(startDate)).append(File.separator);
		sb.append(getFileName(interval.name(), startDate)).append(FILE_EXT);		
//		return read(sb.toString(), symbol, interval, startDate, new Date(Long.MAX_VALUE));
		return read(sb.toString(), symbol, interval, new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
	}
	
	public static List<TPriceBar> read(String filePath, String symbol, Interval interval) {
		return read(filePath, symbol, interval, new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
	}
	
	public static List<TPriceBar> read(String filePath, String symbol, Interval interval, Date fromDate, Date toDate) {
		List<TPriceBar> priceBarList = new ArrayList<TPriceBar>();
		try {
			File file = new File(filePath);
			if (file.exists() && file.isFile() && file.canRead()){
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					TPriceBar priceBar = TPriceBar.valueOf(symbol, interval, line);
					if (priceBar != null) {
						int compareFrom = priceBar.getStartDate().compareTo(fromDate);
						int compareTo = priceBar.getStartDate().compareTo(toDate);
						if (compareFrom >= 0 && compareTo <= 0) {
							priceBarList.add(priceBar);
						} else if (compareTo > 0) {
							break;
						}
					}
				}
				br.close();
			} else {
				logger.warn("File is not exist ==> " + filePath);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return priceBarList;
	}
	
	public static Date addStartDate(Date startDate, String period, int step) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		if (isMonthly(period)) {
			cal.add(Calendar.MONTH, step);
		} else {
			cal.add(Calendar.YEAR, step);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	private static boolean isMonthly(String period) {
		if (period.equals(Interval.T.name()) ||
			period.equals(Interval.m1.name()) ||
			period.equals(Interval.m5.name()) ||
			period.equals(Interval.m15.name()) ||
			period.equals(Interval.m30.name()))
			return true;
		else
			return false;
	}
	
	private static String getFileName(String period, Date startDate) {
		if (isMonthly(period)) {
			period = period + "." + Utils.formatId(Utils.getMonth(startDate), 2);
		}		
		return period;
	}
}
