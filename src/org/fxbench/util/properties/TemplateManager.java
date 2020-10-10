package org.fxbench.util.properties;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fxbench.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Viorel
 */
public class TemplateManager
{    
    public static final String TEMPLATE_NODE = "template";
    public static final String LOCAL_NODE = "local";
    public static final String FRAME_NODE = "frame";
    public static final String TRADING_MODE_NODE = "tradingmode";
    public static final String WINDOWS_NODE = "windows";
    public static final String MENU_NODE = "menu";
    public static final String PANELS_NODE = "panels";
	public static final String PANEL_TABLE_NODE = "table";
	public static final String PANEL_SYMBOL_NODE = "symbol";
	public static final String PANEL_ACCOUNT_NODE = "account";
	public static final String PANEL_OPENPOSITION_NODE = "openposition";
	public static final String PANEL_CLOSEDPOSITION_NODE = "closedposition";
	public static final String PANEL_ORDER_NODE = "order";
	public static final String PANEL_SUMMARY_NODE = "summary";
	public static final String PANEL_MESSAGE_NODE = "message";
	public static final String PANEL_CHART_NODE = "chart";
	public static final String DIALOGS_NODE = "dialogs";
	public static final String DIALOG_HELP_NODE = "help";
    public static final String PRICEBAR_NODE = "pricebar";
    public static final String OVERLAYS_NODE = "overlays";
	public static final String OVERLAY_NODE = "overlay";
	public static final String INDICATORS_NODE = "indicators";
	public static final String INDICATOR_NODE = "indicator";
	public static final String CHARTS_NODE = "charts";
	public static final String CHART_NODE = "chart";
	public static final String PREFERENCES_NODE = "preferences";
	private static final String XML_PATH = "org/fxbench/util/properties/template/";
    private static final String XML_NAME_PREFERENCE = "preferences.xml";
    private static final String XML_NAME_CHART = "charts.xml";    
    private static TemplateManager INSTANCE;
    
	private Map<String, PropertySheet> appPropSheets;
	private Map<String, PropertySheet> panelPropSheets;
	private Map<String, PropertySheet> dialogPropSheets;
	private PropertySheet priceBarPropSheet;
    private List<PropertySheet> overlayPropSheets;
    private List<PropertySheet> indicatorPropSheets;

    public TemplateManager() {
    	appPropSheets = new HashMap<String, PropertySheet>();
    	panelPropSheets = new HashMap<String, PropertySheet>();
    	dialogPropSheets = new HashMap<String, PropertySheet>();
    	overlayPropSheets = new ArrayList<PropertySheet>();
    	indicatorPropSheets = new ArrayList<PropertySheet>();
    	loadFromXml();
    }
    
    private void loadFromXml() {
    	ClassLoader classLoader = TemplateManager.class.getClassLoader();
        InputStream inputStream = null;
        
        try {
        	//preference
        	inputStream = classLoader.getResourceAsStream(XML_PATH + XML_NAME_PREFERENCE);
        	if (inputStream != null) {
        		Document doc = XMLUtil.loadXMLDocument(inputStream);
        		Element prefsNode = XMLUtil.getNode(doc, PREFERENCES_NODE);
        		Element[] prefChildList = XMLUtil.getChildNodes(prefsNode);
        		for (int i = 0; i < prefChildList.length; i++) {
        			String tagName = prefChildList[i].getTagName();
        			if (tagName.equals(PANELS_NODE)) {
        				List<PropertySheet> panelPropSheetList = PropertySheet.loadPropertySheets(prefChildList[i]);
        				for (PropertySheet propSheet : panelPropSheetList) {
        					panelPropSheets.put(propSheet.getName(), propSheet);
        				}			    		
        			} else if (tagName.equals(DIALOGS_NODE)) {
        				List<PropertySheet> panelPropSheetList = PropertySheet.loadPropertySheets(prefChildList[i]);
        				for (PropertySheet propSheet : panelPropSheetList) {
        					dialogPropSheets.put(propSheet.getName(), propSheet);
        				}
        			} else {
        				appPropSheets.put(tagName, PropertySheet.valueOf(prefChildList[i]));
        			}
        		}
        	}
        	
        	//charts
	        inputStream = classLoader.getResourceAsStream(XML_PATH + XML_NAME_CHART);
	        if (inputStream != null) {
	        	Document doc = XMLUtil.loadXMLDocument(inputStream);
	        	Element rootNode = XMLUtil.getNode(doc, CHART_NODE);
        		Element[] childList = XMLUtil.getChildNodes(rootNode);
        		for (int i = 0; i < childList.length; i++) {
        			String tagName = childList[i].getTagName();
        			if (tagName.equals(PRICEBAR_NODE)) {
        				priceBarPropSheet = PropertySheet.valueOf(childList[i]);
        			} else if (tagName.equals(OVERLAY_NODE)) {
        				overlayPropSheets.add(PropertySheet.valueOf(childList[i]));
        			} else if (tagName.equals(INDICATOR_NODE)) {
        				indicatorPropSheets.add(PropertySheet.valueOf(childList[i]));
        			}
        		}
	        }
	        
        } catch (Exception ex) {
        }
    }
    
	public static TemplateManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TemplateManager();
		}
		return INSTANCE;
	}

	public PropertySheet getLocalPropSheet() {
		return appPropSheets.get(LOCAL_NODE);
	}
	
	public PropertySheet getFramePropSheet() {
		return appPropSheets.get(FRAME_NODE);
	}
	
	public PropertySheet getTradingModePropSheet() {
		return appPropSheets.get(TRADING_MODE_NODE);
	}
	
	public PropertySheet getWindowsPropSheet() {
		return appPropSheets.get(WINDOWS_NODE);
	}
	
	public Map<String, PropertySheet> getAppPropSheets() {
		return appPropSheets;
	}
	
	public Map<String, PropertySheet> getAppPropSheetsA() {
		return appPropSheets == null ? null : TemplateManager.cloneHashMap(appPropSheets);
	}
	
	public PropertySheet getTablePanelPropSheet() {
		return panelPropSheets.get(PANEL_TABLE_NODE);
	}

	public PropertySheet getSymbolPanelPropSheet() {
		return panelPropSheets.get(PANEL_SYMBOL_NODE);
	}

	public PropertySheet getAccountPanelPropSheet() {
		return panelPropSheets.get(PANEL_ACCOUNT_NODE);
	}

	public PropertySheet getOpenPositionPanelPropSheet() {
		return panelPropSheets.get(PANEL_OPENPOSITION_NODE);
	}

	public PropertySheet getClosedPositionPanelPropSheet() {
		return panelPropSheets.get(PANEL_CLOSEDPOSITION_NODE);
	}

	public PropertySheet getOrderPanelPropSheet() {
		return panelPropSheets.get(PANEL_ORDER_NODE);
	}

	public PropertySheet getSummaryPanelPropSheet() {
		return panelPropSheets.get(PANEL_SUMMARY_NODE);
	}

	public PropertySheet getMessagePanelPropSheet() {
		return panelPropSheets.get(PANEL_MESSAGE_NODE);
	}
	
	public Map<String, PropertySheet> getPanelPropSheets() {
		return panelPropSheets;
	}
	
	public Map<String, PropertySheet> getPanelPropSheetsA() {
		return panelPropSheets == null ? null : TemplateManager.cloneHashMap(panelPropSheets);
	}

	public PropertySheet getHelpDialogPropSheet() {
		return dialogPropSheets.get(DIALOG_HELP_NODE);
	}
	
	public Map<String, PropertySheet> getDialogPropSheets() {
		return dialogPropSheets;
	}
	
	public Map<String, PropertySheet> getDialogPropSheetsA() {
		return dialogPropSheets == null ? null : TemplateManager.cloneHashMap(dialogPropSheets);
	}
	
	public PropertySheet getPriceBarPropSheet() {
		return priceBarPropSheet;
	}
	
	public PropertySheet getPriceBarPropSheetA() {
		return priceBarPropSheet.clone();
	}

	public List<PropertySheet> getOverlayPropSheets() {
		return overlayPropSheets;
	}
	
	public List<PropertySheet> getOverlayPropSheetsA() {
		return overlayPropSheets == null ? null : TemplateManager.cloneArrayList(overlayPropSheets);
	}

	public List<PropertySheet> getIndicatorPropSheets() {
		return indicatorPropSheets;
	}

	public List<PropertySheet> getIndicatorPropSheetsA() {
		return indicatorPropSheets == null ? null : TemplateManager.cloneArrayList(indicatorPropSheets);
	}
	
	public static ArrayList<PropertySheet> cloneArrayList(List<PropertySheet> srcList) {
		ArrayList<PropertySheet> newList = new ArrayList<PropertySheet>();
		for (PropertySheet propSheet : srcList) {
			newList.add(propSheet.clone());
		}
		return newList;
	}
	
	public static HashMap<String, PropertySheet> cloneHashMap(Map<String, PropertySheet> srcMap) {
		HashMap<String, PropertySheet> newMap = new HashMap<String, PropertySheet>();
		for(Entry<String, PropertySheet> entry : srcMap.entrySet()){
			newMap.put(entry.getKey(), entry.getValue().clone());
		}
		return newMap;
	}
}
