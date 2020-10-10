package org.fxbench.util.properties;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fxbench.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Viorel
 */
public class SettingManager
{    
	public static final String SETTING_NODE = "settings";
	public static final String SHORTCUTS_NODE = "shortcuts";
	public static final String REPORT_NODE = "report";
	public static final String MARKET_NODE = "market";
	private static final String XML_PATH = "org/fxbench/util/properties/template/";
    private static final String XML_NAME_SETTING = "settings.xml";
    private static SettingManager INSTANCE;

	private Map<String, PropertySheet> settingPropSheets;	

    public SettingManager() {
    	settingPropSheets = new HashMap<String, PropertySheet>();
    	loadFromXml();
    }
    
    private void loadFromXml() {
    	ClassLoader classLoader = SettingManager.class.getClassLoader();
        InputStream inputStream = null;
        
        try {
        	//setting
        	inputStream = classLoader.getResourceAsStream(XML_PATH + XML_NAME_SETTING);
        	if (inputStream != null) {
        		Document doc = XMLUtil.loadXMLDocument(inputStream);
        		Element rootNode = XMLUtil.getRoot(doc);
        		settingPropSheets.put(SHORTCUTS_NODE, PropertySheet.loadPropertySheet(rootNode, SHORTCUTS_NODE));
        		settingPropSheets.put(REPORT_NODE, PropertySheet.loadPropertySheet(rootNode, REPORT_NODE));
        		settingPropSheets.put(MARKET_NODE, PropertySheet.loadPropertySheet(rootNode, MARKET_NODE));
        	}
	        
        } catch (Exception ex) {
        }
    }
    
	public static SettingManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SettingManager();
		}
		return INSTANCE;
	}

	public PropertySheet getShortcutsPropSheet() {
		return settingPropSheets.get(SHORTCUTS_NODE);
	}

	public PropertySheet getReportPropSheet() {
		return settingPropSheets.get(REPORT_NODE);
	}
	
	public PropertySheet getMarketPropSheet() {
		return settingPropSheets.get(MARKET_NODE);
	}
	
	public String getShortcut(String shortcutName) {
		return getShortcutsPropSheet().getStrVal(shortcutName);
	}
	
	public String getReportFormat() {
		return getReportPropSheet().getStrVal("format");
	}
	
	public int getMarketOpenWday() {
		return getMarketPropSheet().getIntVal("open_wday");
	}
	
	public int getMarketOpenHour() {
		return getMarketPropSheet().getIntVal("open_hour");
	}
	
	public int getMarketCloseWday() {
		return getMarketPropSheet().getIntVal("close_wday");
	}
	
	public int getMarketCloseHour() {
		return getMarketPropSheet().getIntVal("close_hour");
	}
	
	//reserve
	public boolean marketIsOpen() {
		int openWday = getMarketOpenWday();
    	int openHour = getMarketOpenHour();
    	int closeWday = getMarketCloseWday();
    	int closeHour = getMarketCloseHour();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(new Date());
    	int wday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (wday == closeWday && hour >= closeHour ||
    		wday == openWday && hour <= openHour ||
    		wday == 0/*sunday*/) {
			return true;
		} else {
			return false;
		}
	}
	
}
