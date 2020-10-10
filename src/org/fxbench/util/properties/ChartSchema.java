package org.fxbench.util.properties;

import java.util.Date;

public class ChartSchema
{
	public static final String DELIM = ":";
	
	private long no;
	private String symbol;
	private String period;
	
	public ChartSchema(String symbol) {
		no = makeNo();
		this.symbol = symbol;
		this.period = ""; 
	}
	
	public ChartSchema(String symbol, String period) {
		no = makeNo();
		this.symbol = symbol;
		this.period = period; 
	}
	
	public ChartSchema(long no, String symbol, String period) {
		this.no = no;
		this.symbol = symbol;
		this.period = period; 
	}
	
	private long makeNo() {
		Date dt = new Date();
		return dt.getTime();
	}
	
	public long getNo() {
		return no;
	}

	public void setNo(long no) {
		this.no = no;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	public String toString() {
		return String.valueOf(no) + DELIM + symbol + DELIM + period;
	}
	
	public static ChartSchema valueOf(String schema) {
		String[] s = schema.split(DELIM);
		if (s.length == 2) {
			return new ChartSchema(s[0], s[1]);
		} else if (s.length == 3) {
			return new ChartSchema(Long.valueOf(s[0]), s[1], s[2]);
		} else {
			return new ChartSchema(schema);
		}
	}
}
