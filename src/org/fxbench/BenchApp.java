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
package org.fxbench;

/*
import javax.swing.SwingUtilities;

import org.fxbench.BenchPanel;

public class FxBenchApp {
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BenchPanel.createAndShowGUI();
			}
		});
    }
}
*/

import org.fxbench.desk.TradeDesk;
import org.fxbench.trader.dialog.LoginDialog;
import org.fxbench.trader.fxcm.LoginRequest;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.PersistentStorage;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Locale;

/**
 * This class represents trader application.
 * It contains entry point and responsible for create
 * and initialize main UI objects.
 */
public class BenchApp
{
	public static final String APP_NAME = "fxbencn";
	public final static String HOST_FXCM = "fxcm";
	public final static String HOST_LOCAL = "local";	
	
	private static final BenchApp INST = new BenchApp();
    private static String cUserName;
    private static String cPassword;
    private static String cTerminal;
    private static String cUrl;
    private static String host;
    private BenchFrame mainFrame;
    private TradeDesk tradeDesk;
    private ResourceManager resourceManager;

    /**
     * Private constructor.
     */
    private BenchApp() {
    }

    /**
     * This method is called in shutdown hook.
     */
    protected void exitInstance() {
        //saves current primary language to the persistent storage
        saveLocale();
        tradeDesk.getLiaison().cleanup();
        PersistentStorage storage;
        try {
            storage = PersistentStorage.getStorage();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        try {
            Dimension size = mainFrame.getSize();
            storage.set("preferences.frame.width", size.width);
            storage.set("preferences.frame.height", size.height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //flushes persistent storage
        try {
            storage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns one and only instance of the trader application.
     */
    public static BenchApp getInst() {
        return INST;
    }

    public static String getHost() {
		return host;
	}

	/**
     * Returns main application frame.
     */
    public BenchFrame getMainFrame() {
        return mainFrame;
    }

    public TradeDesk getTradeDesk() {
    	return tradeDesk;
    }
    
    /**
     * Returns main resource manager.
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
    
    /**
     * Creates application object.
     */
    protected void initInstance() {
        //adds shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                exitInstance();
            }
        });

        try {
            resourceManager = ResourceManager.getManager("org.fxbench.resources.Resources");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                                          "Resource manager not created!",
                                          "fxBench",
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        PersistentStorage storage;
        try {
            storage = PersistentStorage.getStorage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame,
                                          resourceManager.getString("IDS_ER_PERS_STORAGE_NOT_OPENED"),
                                          resourceManager.getString("IDS_MAINFRAME_SHORT_TITLE"),
                                          JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        //gets locale parameters from the storage
        String language = storage.getString("preferences.locale.language", null);
        String country = storage.getString("preferences.locale.country", null);

        //Create Locale or Get default Locale of the ResourceManager
        Locale locale;
        if (language != null && country != null) {
            locale = new Locale(language, country);
        } else {
            locale = resourceManager.getDefaultLocale();
        }
        ResourceManager.setPrimaryLocale(locale);
        UIManager.getInst().setResourceManager(resourceManager);
        
        //preload for ui experience
//        FontChooser.init();
//        PreferencesManager.getPreferencesManager(TradeDesk.getInst().getUserName()).init();

	    tradeDesk = new TradeDesk(host);
        mainFrame = new BenchFrame();
        //gets windows parameters from the storage
        int width = storage.getInt("preferences.frame.width", 800);
        int height = storage.getInt("preferences.frame.height", 600);
        mainFrame.setSize(width, height);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setLocation((screen.width - width) / 2, (screen.height - 20 - height) / 2);
        mainFrame.setVisible(true);
        
        if (host.equals(HOST_LOCAL)) {
        	List<PropertySheet> propSheetList = PropertyManager.getInstance().getConnectionPropSheets();
        	if (propSheetList == null || propSheetList.size() == 0) {
        		JOptionPane.showMessageDialog(mainFrame,
                        "Property file error.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
        		System.exit(1);
        	} else {
        		org.fxbench.trader.local.LoginDialog loginDialog = new org.fxbench.trader.local.LoginDialog(propSheetList.get(0));
        		if (loginDialog.showModal() == JOptionPane.OK_OPTION) {
                	tradeDesk.getLiaison().login(loginDialog.getLoginParameters());
                }
        	}
    	} else {
    		if (cUserName != null && cPassword != null && cTerminal != null && cUrl != null) {
            	tradeDesk.getLiaison().login(new LoginRequest(cUserName, cPassword, cTerminal, cUrl));
            } else {
                LoginDialog loginDialog = new LoginDialog(mainFrame);
                if (loginDialog.showModal() == JOptionPane.OK_OPTION) {
                	tradeDesk.getLiaison().login(loginDialog.getLoginParameters());
                }
            }
    	}
    }

    /**
     * Saves information about current locale to persistence storage.
     */
    private void saveLocale() {
        if (resourceManager != null) {
            Locale locale = resourceManager.getLocale();
            PersistentStorage storage;
            try {
                storage = PersistentStorage.getStorage();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
            String language = locale.getLanguage();
            String country = locale.getCountry();

            //sets locale parameters to the storage
            storage.set("preferences.locale.language", language);
            storage.set("preferences.locale.country", country);
        }
    }

    /**
     * Trader application entry point.
     */
    public static void main(String[] args) {
    	if (args.length == 1) {
        	host = args[0];
        } else if (args.length == 5) {
        	host = args[0];
            cUserName = args[1];
            cPassword = args[2];
            cTerminal = args[3];
            cUrl = args[4];
        } else {
        	System.out.println("Parameter error.");
        	System.exit(1);
        }
    	
//        UserPreferences preferences = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
//        String cx = preferences.getString("Server.Connections");
//        if (cx == null) {
//            String def = System.getProperty("Server.Default");
//            if (def == null) {
//                ConnectionsManager.setConnections(preferences.getString("Server.Default"));
//            } else {
//                ConnectionsManager.setConnections(def);
//            }
//        } else {
//            ConnectionsManager.setConnections(cx);
//        }
//        String laf = preferences.getString("Server.lookandfeel");
//        if (laf != null) {
//            try {
//                javax.swing.UIManager.setLookAndFeel(laf);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
            // try and load the native laf by default
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                //do nothing
            }
//        }
        INST.initInstance();
    }

}
