package org.fxbench.util;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JComponent;

public class Utils
{
	private Utils(){
	}
	
	static public boolean isMac() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Mac")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * Sets given components preferred size and minimum width to the same as
     * the one with the max preferred size.
     *
     * @param aComponents JComponent[] the components who's size is checked and set.
     */
    public static void setAllToBiggest(JComponent[] aComponents) {
        Dimension widest = null;
        Dimension tallest = null;
        for (JComponent comp : aComponents) {
            Dimension dim = comp.getPreferredSize();
            if (widest == null || dim.width > widest.width) {
                widest = dim;
            }
            if (tallest == null || dim.height > tallest.height) {
                tallest = dim;
            }
        }
        for (JComponent comp : aComponents) {
            if (widest != null && tallest != null) {
                comp.setPreferredSize(new Dimension(widest.width, tallest.height));
                comp.setMinimumSize(new Dimension(widest.width, tallest.height));
            }
        }
    }

    public static String mergeArrayToString(Object[] objList, String aDelim) {
    	String s = null;
    	if (objList != null && objList.length > 0) {
    		s = objList[0].toString();
    		for(int i = 1; i < objList.length; i++) {
    			s = s + aDelim + objList[i].toString();
    		}
    	}
    	return s;
    }
    
    /**
     * Checks if specified currency pair contains specified currency
     *
     * @param aCurrency currency
     * @param aPair pair
     *
     * @return true if the pair contains the currency, otherwise false
     *         (if asCurrency or asPair or both equals null false returned)
     */
    public static boolean isCurrencyInThePair(String aCurrency, String aPair) {
        try {
            if (aCurrency == null || aPair == null) {
                return false;
            }
            String[] currencies = splitCurrencyPair(aPair);
            String base = currencies[0]; //position
            String counter = currencies[1]; //tradable
            return aCurrency.equalsIgnoreCase(base) || aCurrency.equalsIgnoreCase(counter);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Splits specified currency pair into 2 currency names
     *
     * @param aPair currency pair
     *
     * @return array
     */
    public static String[] splitCurrencyPair(String aPair) {
        try {
            if (aPair == null) {
                return null;
            }
            String[] currencies = new String[2];
            int indexOfSlash = aPair.indexOf("/");
            currencies[0] = aPair.substring(0, indexOfSlash);
            currencies[1] = aPair.substring(indexOfSlash + 1, aPair.length());
            return currencies;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Merges 2 currencies into a pair and return the pair.
     *
     * @param aCCY1 1st currency to be in the pair
     * @param aCCY2 2nd currency to be in the pair
     *
     * @return currency pair or null CCY1 or CCY2 or both == null
     */
    public static String toPair(String aCCY1, String aCCY2) {
        if (aCCY1 == null || aCCY2 == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(aCCY1).append("/").append(aCCY2);
        return sb.toString();
    }
    
    public static String format(TimeZone tz, double aDouble) {
        DecimalFormat decimalFormat = null;
        if (TimeZone.getTimeZone("Japan").getDisplayName().equals(tz.getDisplayName())) {
        	decimalFormat = new DecimalFormat("#,##0");            
        } else {
        	decimalFormat = new DecimalFormat("#,##0.00");
        }
        return decimalFormat.format(aDouble);
    }
    
    public static String format(Date date, String format) {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
    }
    
    public static String format(Date date) {
		return format(date, "yyyy/MM/dd HH:mm:ss");
    }
    
    public static String formatId(int id, int size) {
    	String s = String.valueOf(id);
    	char[] zero = size - s.length() > 0 ? new char[size - s.length()] : new char[0];
    	for (int i = 0; i < zero.length; i++) {
    		zero[i] = '0';
    	}
    	return String.valueOf(zero) + s;
    }
    
    public static double round(double d, double decimal) {
    	return decimal == 0 ? d : ((double)(int)(d / decimal + 0.5)) * decimal;
    }
    
    public static int ceil(double d) {
    	if (d >= 0) {
    		return Double.valueOf(Math.ceil(d)).intValue();
    	} else {
    		return Double.valueOf(Math.ceil(d * -1)).intValue() * -1;
    	}
    }
    
    public static int round(double d) {
    	if (d >= 0) {
    		return Double.valueOf(Math.round(d)).intValue();
    	} else {
    		return Double.valueOf(Math.round(d * -1)).intValue() * -1;
    	}
    }
    
    public static int getFractionDigits(String symbol) {
    	Currency cur = Currency.getInstance(symbol);
    	return cur.getDefaultFractionDigits();
    }
    
    public static int getPrecision(double pointSize) {
    	int precision = 0;
    	while (pointSize > 0 && pointSize < 1) {
    		precision++;
    		pointSize *= 10;
    	}
    	return precision;
    }
    
    public static double getPointSize(int precision) {
    	double pointSize = 1;
        for (int i = 0; i < precision; i++) {
        	pointSize /= 10;
        }
        return pointSize;
    }
    
    // digits=2, prex='.', c='0'  ==>  return: ".00" 
    public static String getFormatStr(int digits, char prex, char c) {
    	String s = "";
		if (digits > 0 ) {
			s += prex;
			for (int i = 0; i < digits; i++) {
				s += c;
			}
		}
		return s;
    }
    
    public static Date praseDateStr(String dateStr, TimeZone timeZone) throws Exception {
    	if (dateStr.charAt(0) >= 48 && dateStr.charAt(0) <= 57) {
    		DateFormat dateFormat = DateFormat.getInstance();
    		dateFormat.setTimeZone(timeZone);
    		return dateFormat.parse(dateStr);
    	} else {
    		throw new Exception();
    	}
    }
    
    public static Date str2date(String s) throws Exception {
    	if (s == null) {
    		return null;
    	}
    	String[] ss = s.split("[-/: ]");
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(new Date(0));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	switch (ss.length) {
	    	case 1:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		break;
	    	case 2:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		cal.set(Calendar.MONTH, Integer.valueOf(ss[1]).intValue() - 1);
	    		break;
	    	case 3:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		cal.set(Calendar.MONTH, Integer.valueOf(ss[1]).intValue() - 1);
	    		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(ss[2]).intValue());
	    		break;
	    	case 4:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		cal.set(Calendar.MONTH, Integer.valueOf(ss[1]).intValue() - 1);
	    		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(ss[2]).intValue());
	    		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(ss[3]).intValue());
	    		break;
	    	case 5:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		cal.set(Calendar.MONTH, Integer.valueOf(ss[1]).intValue() - 1);
	    		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(ss[2]).intValue());
	    		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(ss[3]).intValue());
	    		cal.set(Calendar.MINUTE, Integer.valueOf(ss[4]).intValue());
	    		break;
	    	case 6:
	    		cal.set(Calendar.YEAR, Integer.valueOf(ss[0]).intValue());
	    		cal.set(Calendar.MONTH, Integer.valueOf(ss[1]).intValue() - 1);
	    		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(ss[2]).intValue());
	    		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(ss[3]).intValue());
	    		cal.set(Calendar.MINUTE, Integer.valueOf(ss[4]).intValue());
	    		cal.set(Calendar.SECOND, Integer.valueOf(ss[5]).intValue());
	    		break;
	    	default:
    	}
    	return cal.getTime();
    }
    
    public static int getYear(Date date) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
    }
    
    public static int getMonth(Date date) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
    }
}
