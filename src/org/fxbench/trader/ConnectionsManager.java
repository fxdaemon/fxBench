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
 */
package org.fxbench.trader;

import org.fxbench.util.properties.Property;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre Mermegas
 *         Date: Jan 16, 2006
 *         Time: 2:41:56 PM
 */
public class ConnectionsManager {
//    private static Map<String, Connection> cConnectionMap = new TreeMap<String, Connection>();
    private static List<IConnectionManagerListener> cListenerList = new ArrayList<IConnectionManagerListener>();
//    private static UserPreferences cPreferences = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
//    private static final String EMPTY_CODE = "-";

//    private static String convertToString() {
//        StringBuilder sb = new StringBuilder();
//        String[] keys = cConnectionMap.keySet().toArray(new String[cConnectionMap.size()]);
//        for (int i = 0; i < keys.length; i++) {
//            String key = keys[i];
//            Connection conn = cConnectionMap.get(key);
//            String username = conn.getUsername();
//            sb.append(username == null || "".equals(username.trim()) ? EMPTY_CODE : username)
//                    .append(";")
//                    .append(conn.getTerminal())
//                    .append(";")
//                    .append(conn.getUrl());
//            if (i + 1 != keys.length) {
//                sb.append("|");
//            }
//        }
//        return sb.toString();
//    }

    public static Connection getConnection(String terminal) {
//        return cConnectionMap.get(aTerminal);
    	PropertySheet propertySheet = getConnectionPropSheet(terminal);
    	if (propertySheet == null) {
    		return new Connection(null, null, null);
    	} else {
    		return new Connection(
    				propertySheet.getStrVal("username"),
    				terminal,
    				propertySheet.getStrVal("url"));
    	}
    	
    }
    
    public static PropertySheet getConnectionPropSheet(String terminal) {
		PropertySheet propertySheet = null;
		List<PropertySheet> connectionPropSheets = PropertyManager.getInstance().getConnectionPropSheets();
		if (connectionPropSheets != null) {
			for (PropertySheet propSheet : connectionPropSheets) {
				if (propSheet.getStrVal("terminal").equals(terminal)) {
					propertySheet = propSheet;
					break;
				}
			}
		}
		return propertySheet;
	}
    
    public static String getUserName(String terminal) {
//    	return cConnectionMap.get(aTerminal);
    	String userName = "";
    	PropertySheet propertySheet = getConnectionPropSheet(terminal);
    	if (propertySheet != null) {
    		userName = propertySheet.getStrVal("username");
    	}
        return userName;
    }
    
    public static String getSecureConnection(String terminal) {
//    	return cConnectionMap.get(aTerminal);
    	String secureConnection = "";
    	PropertySheet propertySheet = getConnectionPropSheet(terminal);
    	if (propertySheet != null) {
    		secureConnection = propertySheet.getProperty("secure_connection").toString();
    	}
        return secureConnection;
    }
    
    public static void setSecureConnection(String terminal, boolean isChecked) {
    	PropertySheet propertySheet = getConnectionPropSheet(terminal);
    	if (propertySheet != null) {
    		propertySheet.setProperty("secure_connection", Boolean.valueOf(isChecked));
    	}
    }
    
    public static String getUrl(String terminal) {
//    	return cConnectionMap.get(aTerminal);
    	String url = "";
    	PropertySheet propertySheet = getConnectionPropSheet(terminal);
    	if (propertySheet != null) {
    		url = propertySheet.getStrVal("url");
    	}
        return url;
    }

    public static String[] getTerminals() {
//        return cConnectionMap.keySet().toArray(new String[cConnectionMap.size()]);
    	String[] terminals = null;
    	List<PropertySheet> connectionPropSheets = PropertyManager.getInstance().getConnectionPropSheets();
		if (connectionPropSheets != null) {
			terminals = new String[connectionPropSheets.size()];
			for (int i = 0; i < connectionPropSheets.size(); i++) {
				terminals[i] = connectionPropSheets.get(i).getStrVal("terminal");
			}
		} else {
			terminals = new String[0];
		}
		return terminals;
    }
    
    public static String getConnectedTerminal() {
    	String terminal = null;
    	List<PropertySheet> connectionPropSheets = PropertyManager.getInstance().getConnectionPropSheets();
		if (connectionPropSheets != null) {
			for (PropertySheet propSheet : connectionPropSheets) {
				if (propSheet.getBoolVal("selected") == true) {
					terminal = propSheet.getStrVal("terminal");
					break;
				}
			}
		}
		return terminal;
    }
    
    public static void setLastConnection(String terminal, String userName) {
    	PropertySheet propSheet =  getConnectionPropSheet(terminal);
    	if (propSheet != null) {
    		propSheet.setProperty("selected", true);
    		propSheet.setProperty("username", userName);
    	}
    }
    
    public static String getServerConfigFile() {
    	PropertySheet serverPropSheet =  PropertyManager.getInstance().getServerPropSheet();
    	return serverPropSheet == null ? "" : serverPropSheet.getStrVal("config.file");
    }

    public static int getServerTcpTimeout(String aUserID) {
    	PropertySheet serverPropSheet =  PropertyManager.getInstance().getServerPropSheet();
    	return serverPropSheet == null ? 0 : serverPropSheet.getIntVal("timeout");
    }
    
    public static boolean isUseProxy() {
    	PropertySheet proxyPropSheet =  PropertyManager.getInstance().getProxyPropSheet();
    	return proxyPropSheet == null ? false : proxyPropSheet.getBoolVal("use");
    }
    
    public static String getProxyHost() {
    	PropertySheet proxyPropSheet =  PropertyManager.getInstance().getProxyPropSheet();
    	return proxyPropSheet == null ? "" : proxyPropSheet.getStrVal("host");
    }
    
    public static int getProxyPort() {
    	PropertySheet proxyPropSheet =  PropertyManager.getInstance().getProxyPropSheet();
    	return proxyPropSheet == null ? 0 : proxyPropSheet.getIntVal("port");
    }
    
    public static String getProxyUser() {
    	PropertySheet proxyPropSheet =  PropertyManager.getInstance().getProxyPropSheet();
    	return proxyPropSheet == null ? "" : proxyPropSheet.getStrVal("user");
    }
    
    public static String getProxyPassword() {
    	PropertySheet proxyPropSheet =  PropertyManager.getInstance().getProxyPropSheet();
    	return proxyPropSheet == null ? "" : proxyPropSheet.getStrVal("password");
    }

    private static void notifyListeners() {
        for (IConnectionManagerListener listener : cListenerList) {
            listener.updated();
        }
    }

    public static void register(IConnectionManagerListener aConnectionManagerPanel) {
        cListenerList.add(aConnectionManagerPanel);
    }

    public static void unregister(IConnectionManagerListener aConnectionManagerPanel) {
        cListenerList.remove(aConnectionManagerPanel);
    }

    public static void remove(String aTerminal) {
//        if (aTerminal != null) {
//            cConnectionMap.remove(aTerminal);
//        }
//        cPreferences.set("Server.Connections", convertToString());
    	PropertySheet propertySheet = getConnectionPropSheet(aTerminal);
    	if (propertySheet != null) {
    		PropertyManager.getInstance().getConnectionPropSheets().remove(propertySheet);
			notifyListeners();
		}
    }

//    public static void setConnections(String aConnections) {
//        // format example
//        // "andre;CW;http://devmw2.dev.fxcm.com:9999/Hosts.jsp|andmer;Demo;http://fxcorporate.com/Phosts.jsp"
//        if (aConnections != null) {
//            String[] pipes = Util.splitToArray(aConnections, "|");
//            for (String pipe : pipes) {
//                String[] pair = Util.splitToArray(pipe, ";");
//                String username = pair[0];
//                String terminal = pair[1];
//                String url = pair[2];
//                Connection conn = new Connection(EMPTY_CODE.equals(username) ? "" : username, terminal, url);
//                cConnectionMap.put(terminal, conn);
//            }
//        }
//        cPreferences.set("Server.Connections", aConnections);
//        notifyListeners();
//    }

    public static void updateAddConnection(String aTerminal, Connection aFXCMConnection) {
//        if (aTerminal != null) {
//            cConnectionMap.remove(aTerminal);
//        }
//        cConnectionMap.put(aFXCMConnection.getTerminal(), aFXCMConnection);
//        cPreferences.set("Server.Connections", convertToString());
    	PropertySheet propSheet = getConnectionPropSheet(aTerminal);
    	if (aTerminal == null) {
    		propSheet = new PropertySheet(aTerminal, "");
    		Property prop = Property.valueOf("selected", "", "boolean", "false");
    		propSheet.addProperty(prop);
    		prop = Property.valueOf("username", "", "string", aFXCMConnection.getUsername());
    		prop.setVisible(true);
    		propSheet.addProperty(prop);
    		prop = Property.valueOf("password", "", "string", "");
    		propSheet.addProperty(prop);
    		prop = Property.valueOf("terminal", "", "string", aTerminal);
    		prop.setVisible(true);
    		propSheet.addProperty(prop);
    		prop = Property.valueOf("url", "", "string", aFXCMConnection.getUrl());
    		prop.setVisible(true);
    		propSheet.addProperty(prop);
    		prop = Property.valueOf("secure_connection", "", "boolean", "false");
    		prop.setVisible(true);
    		propSheet.addProperty(prop);
    		PropertyManager.getInstance().getConnectionPropSheets().add(propSheet);
    	} else {
    		propSheet.setProperty("username", aFXCMConnection.getUsername());
    		propSheet.setProperty("url", aFXCMConnection.getUrl());
    	}
        notifyListeners();
    }
}