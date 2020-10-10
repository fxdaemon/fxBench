package org.fxbench.ui;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager.LookAndFeelInfo;

import org.fxbench.BenchApp;
import org.fxbench.entity.TPriceBar;
import org.fxbench.trader.ITraderConstants;
import org.fxbench.trader.LiaisonListener;
import org.fxbench.trader.action.ChangePasswordAction;
import org.fxbench.trader.action.ClosePositionAction;
import org.fxbench.trader.action.CreateEntryOrderAction;
import org.fxbench.trader.action.CreateMarketOrderAction;
import org.fxbench.trader.action.CurrencySubscriptionAction;
import org.fxbench.trader.action.LoginAction;
import org.fxbench.trader.action.RemoveEntryOrderAction;
import org.fxbench.trader.action.ReportAction;
import org.fxbench.trader.action.RequestForQuoteAction;
import org.fxbench.trader.action.SetStopLimitAction;
import org.fxbench.trader.action.SetStopLimitOrderAction;
import org.fxbench.trader.action.ToggleStatusBarAction;
import org.fxbench.trader.action.ToggleToolbarAction;
import org.fxbench.trader.action.UpdateEntryOrderAction;
import org.fxbench.trader.dialog.AboutDialog;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.ui.docking.dock.FloatDock;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockableState;
import org.fxbench.ui.docking.dockable.action.DefaultDockableStateAction;
import org.fxbench.ui.docking.event.DockingEvent;
import org.fxbench.ui.docking.event.DockingListener;
import org.fxbench.ui.help.HelpManager;
import org.fxbench.ui.panel.BenchPanel;
import org.fxbench.ui.panel.ChartPanel;
import org.fxbench.util.PreferencesDialog;
import org.fxbench.util.properties.ChartSchema;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.SettingManager;

public class BenchMenu extends JMenuBar
{
	public enum MenuType {
		Connected, Disconnected;
	}
	
	private MenuType menyType; 
	private JMenuBar connectedMenu = null;
    private JMenuBar disconnectedMenu = null;
    private BenchFrame mainFrame = null;
	
	public BenchMenu(BenchFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.menyType = MenuType.Disconnected;
	}
	
	/**
	 * @return the menyType
	 */
	public MenuType getMenyType() {
		return menyType;
	}

	/**
	 * @param menyType the menyType to set
	 */
	public void setMenyType(MenuType menyType) {
		this.menyType = menyType;
	}

	public JMenuBar getMenuBar() {
		return getMenuBar(menyType);
	}
	
	public JMenuBar getMenuBar(MenuType menutype) {
		if (menutype == MenuType.Connected) {
			if (connectedMenu == null) {
				connectedMenu = createConnectedMenu();
			}
			return connectedMenu;
		} else {
			if (disconnectedMenu == null) {
				disconnectedMenu = createDisconnectedMenu();
			}
			return disconnectedMenu;
		}
	}
	
	 /**
     * Find the menu with specified name.
     *
     * @param aName name of the menu
     * return founded menu and null if the menu not founded
     */
    public JMenu findMenu(String aName) {
        //gets current menu bar
        JMenuBar menuBar = getMenuBar();
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                //gets menu item name
                String sCurrentMenuName = (String) menu.getClientProperty(ITraderConstants.MENUITEM_NAME);
                if (sCurrentMenuName != null) {
                    if (aName.equals(sCurrentMenuName)) {
                        return menu;
                    }
                }
            }
        }

        //if the item with specified name not found
        return null;
    }

    /**
     * Find the item with specified name.
     *
     * @param aName name of the menu item
     * @param aMenu place of the search
     * return founded menu item and null if the menu item not founded
     */
    public JMenuItem findMenuItem(String aName, JMenu aMenu) {
        for (int i = 0; i < aMenu.getItemCount(); i++) {
            JMenuItem menuItem = aMenu.getItem(i);
            if (menuItem != null) {
                //gets menu item name
                String sCurrentItemName = (String) menuItem.getClientProperty(ITraderConstants.MENUITEM_NAME);
                if (sCurrentItemName != null) {
                    if (aName.equals(sCurrentItemName)) {
                        return menuItem;
                    }
                }
            }
        }

        //if the item with specified name not found
        return null;
    }

	/**
     * Initializes the connected menu
     *
     * @return connected menu
     */
	private JMenuBar createConnectedMenu() {
        UIManager uiManager = UIManager.getInst();
        //creates the menu bar
        JMenuBar menuBar = new JMenuBar();

        //gets user preferences
//        UserPreferences uiPrefs = UserPreferences.getUserPreferences();
        SetStopLimitOrderAction.newAction(null); //xxx added this so table listener gets set up correctly.

        //[File]
        JMenu menu = uiManager.createMenu("IDS_FILE", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "File");
        menu.setMnemonic('F');
        menuBar.add(menu);

        //[File][Logout]
        JMenuItem menuItem = uiManager.createMenuItem("IDS_LOGOUT", "ID_LOGOUT_ICON", null, "IDS_LOGOUT_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Logout")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Logout")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Logout");
        menuItem.setMnemonic('L');
        menu.add(menuItem);
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    //initiaites logout procedure.
                	BenchApp.getInst().getTradeDesk().getLiaison().logout();
                }
            });

        //[File][Change Password]
        ChangePasswordAction passwordAction = new ChangePasswordAction();
        uiManager.addAction(passwordAction, "IDS_CHANGE_PASS", null, null, null, null);
        menuItem = uiManager.createMenuItem(passwordAction);
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Change Password");
        menuItem.setMnemonic('C');
        menu.add(menuItem);

        //[File][Refresh]
        menuItem = uiManager.createMenuItem("IDS_REFRESH", null, null, "IDS_REFRESH_DESC");
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Refresh");
        menuItem.setMnemonic('R');
        menu.add(menuItem);
        menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                    	BenchApp.getInst().getTradeDesk().getLiaison().refresh();
                    }
                });

        //[File][Options]
        menuItem = uiManager.createMenuItem("IDS_OPTIONS", null, null, "IDS_OPTIONS_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Options")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Options")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Options");
        menuItem.setMnemonic('O');
        menu.add(menuItem);
        menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
//                        PreferencesManager preferencesManager =
//                                PreferencesManager.getPreferencesManager(BenchApp.getInst().getTradeDesk().getUserName());
//                        preferencesManager.showPreferencesDialog();
                        PreferencesDialog.createAndShowDialog();
                    }
                });

        menu.addSeparator();
        
        //[File][Toggle Toolbar]
        ToggleToolbarAction showhide = new ToggleToolbarAction();
        uiManager.addAction(showhide, "IDS_TOGGLE_TOOLBAR", null, null, null, null);
        menuItem = uiManager.createMenuItem(showhide);
        menuItem.setMnemonic('T');
        menu.add(menuItem);

        //[File][Toggle Statusbar]
        ToggleStatusBarAction statusBarAction = new ToggleStatusBarAction();
        uiManager.addAction(statusBarAction, "IDS_TOGGLE_STATUSBAR", null, null, null, null);
        menuItem = uiManager.createMenuItem(statusBarAction);
        menuItem.setMnemonic('S');
        menu.add(menuItem);
        menu.addSeparator();

        //[File][Language]
        //submenu = uiManager.createMenu("Language", null);
        //submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Language");
        //menu.add(submenu);
        //addSupportedLanguages(submenu);
        //menu.addSeparator();

        //[File][Look and Feel]
        JMenu submenu;
        submenu = uiManager.createMenu("IDS_LOOK_AND_FEEL", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "PLAF");
        menu.add(submenu);
        addLookandFeel(submenu);
        menu.addSeparator();

        //[File][Exit]
        menuItem = uiManager.createMenuItem("IDS_EXIT", "ID_EXIT_ICON", null, "IDS_EXIT_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Exit")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Exit")));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                	mainFrame.exitFromApp();
                }
            });
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Exit");
        menuItem.setMnemonic('X');
        menu.add(menuItem);

        //[Action]
        menu = uiManager.createMenu("IDS_ACTION", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Action");
        menu.setMnemonic('A');
        menuBar.add(menu);

        //[Action][Accounts]
        submenu = uiManager.createMenu("IDS_ACTION_ACCOUNTS", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Account");
        menu.add(submenu);
        //[Action][Accounts][Report]
        Action reportAction = ReportAction.newAction(null);
        uiManager.addAction(reportAction, "IDS_REPORT", "ID_REPORT_ICON", null, "IDS_REPORT_DESC", "IDS_REPORT_DESC");
        menuItem = uiManager.createMenuItem(reportAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Report")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Report")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Report");
        submenu.add(menuItem);

        //[Action][Dealing Rates]
        submenu = uiManager.createMenu("IDS_DEALING_RATES", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Dealing Rates");
        menu.add(submenu);
        //[Action][Dealing Rates][Create Market Order]
        Action marketOrderAction = CreateMarketOrderAction.newAction(null);
        uiManager.addAction(
                marketOrderAction,
                "IDS_CREATE_MARKET_ORDER",
                "ID_MARKET_ORDER_ICON",
                null,
                "IDS_CREATE_MARKET_ORDER_DESC",
                "IDS_CREATE_MARKET_ORDER_DESC");
        menuItem = uiManager.createMenuItem(marketOrderAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.MarketOrder")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("MarketOrder")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Create Market Order");
        submenu.add(menuItem);
        //[Action][Dealing Rates][Create Request For Quote]
        Action rfqAction = RequestForQuoteAction.newAction(null);
        uiManager.addAction(
                rfqAction,
                "IDS_REQUEST_FOR_QUOTE",
                "ID_MARKET_ORDER_ICON",
                null,
                "IDS_REQUEST_FOR_QUOTE_DESC",
                "IDS_REQUEST_FOR_QUOTE_DESC");
        menuItem = uiManager.createMenuItem(rfqAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.RFQ")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("RFQ")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Request For Quote");
        submenu.add(menuItem);
        //[Action][Dealing Rates][Create Entry Order]
        Action entryOrderAction = CreateEntryOrderAction.newAction(null);
        uiManager.addAction(
                entryOrderAction,
                "IDS_CREATE_ENTRY_ORDER",
                "ID_ENTRY_ICON",
                null,
                "IDS_CREATE_ENTRY_ORDER_DESC",
                "IDS_CREATE_ENTRY_ORDER_DESC");
        menuItem = uiManager.createMenuItem(entryOrderAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.EntryOrder")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("EntryOrder")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Create Entry Order");
        submenu.add(menuItem);
        //[Action][Dealing Rates][Currency Subscription List]
        Action subscriptionAction = new CurrencySubscriptionAction();
        uiManager.addAction(
                subscriptionAction,
                "IDS_CCY_SUBSCRIPTION_LIST",
                "ID_RATES_FRAME_ICON",
                null,
                "IDS_CCY_SUBSCRIPTION_LIST",
                "IDS_CCY_SUBSCRIPTION_LIST");
        menuItem = uiManager.createMenuItem(subscriptionAction);
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Currency Subscription List");
        submenu.add(menuItem);

        //[Action][Orders]
        submenu = uiManager.createMenu("IDS_ORDERS", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, OrderPanel.NAME);
        menu.add(submenu);
        //[Action][Orders][Create Entry Order]
        Action entryOrderAction2 = CreateEntryOrderAction.newAction(null);
        uiManager.addAction(
                entryOrderAction2,
                "IDS_CREATE_ENTRY_ORDER_2",
                "ID_ENTRY_ICON",
                null,
                "IDS_CREATE_ENTRY_ORDER_DESC_2",
                "IDS_CREATE_ENTRY_ORDER_DESC_2");
        menuItem = uiManager.createMenuItem(entryOrderAction2);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.EntryOrder")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("EntryOrder")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Create Entry Order");
        submenu.add(menuItem);
        //[Action][Orders][Change Entry Order]
        Action updateEntryOrderAction = UpdateEntryOrderAction.newAction(null);
        uiManager.addAction(
                updateEntryOrderAction,
                "IDS_UPDATE_ENTRY_ORDER",
                "ID_ENTRY_ICON",
                null,
                "IDS_UPDATE_ENTRY_ORDER_DESC",
                "IDS_UPDATE_ENTRY_ORDER_DESC");
        menuItem = uiManager.createMenuItem(updateEntryOrderAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.UpdateEntryOrder")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("UpdateEntryOrder")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Update Entry Order");
        submenu.add(menuItem);
        //[Action][Orders][Stop/Limit]
        Action stopLimitOrderAction = SetStopLimitOrderAction.newAction("STOP");
        uiManager.addAction(
                stopLimitOrderAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        menuItem = uiManager.createMenuItem(stopLimitOrderAction);
        submenu.add(menuItem);
        //[Action][Orders][Remove Order]
        Action removeEntryOrderAction = RemoveEntryOrderAction.newAction(null);
        uiManager.addAction(
                removeEntryOrderAction,
                "IDS_REMOVE_ORDER",
                "ID_CLOSE_ICON",
                null,
                "IDS_REMOVE_ORDER_DESC",
                "IDS_REMOVE_ORDER_DESC");
        menuItem = uiManager.createMenuItem(removeEntryOrderAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.RemoveEntryOrder")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("RemoveEntryOrder")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Remove Order");
        submenu.add(menuItem);

        //[Action][Positions]
        submenu = uiManager.createMenu("IDS_POSITIONS", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Positions");
        menu.add(submenu);
        //[Action][Positions][Stop/Limit]
        Action stopLimitPositionAction = SetStopLimitAction.newAction(null);
        uiManager.addAction(
                stopLimitPositionAction,
                "IDS_STOP_LIMIT",
                "ID_S_L_ICON",
                null,
                "IDS_STOP_LIMIT_DESC",
                "IDS_STOP_LIMIT_DESC");
        menuItem = uiManager.createMenuItem(stopLimitPositionAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.StopLimit")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("StopLimit")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Stop/Limit");
        submenu.add(menuItem);
        //[Action][Positions][Close]
        Action closePositionAction = ClosePositionAction.newAction(null);
        uiManager.addAction(closePositionAction,
                            "IDS_CLOSE",
                            "ID_CLOSE_ICON",
                            null,
                            "IDS_CLOSE_DESC",
                            "IDS_CLOSE_DESC");
        menuItem = uiManager.createMenuItem(closePositionAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.ClosePosition")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("ClosePosition")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Close");
        submenu.add(menuItem);
        
        //[Charts]
        menu = uiManager.createMenu("IDS_CHARTS", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Charts");
        menu.setMnemonic('C');
        menuBar.add(menu);
        //[Charts]{Open Chart}
        submenu = uiManager.createMenu("IDS_CHARTS_OPEN", null);
//        submenu.putClientProperty(ITraderConstants.MENUITEM_NAME, "OpenChart");
        menu.add(submenu);
        //[Charts]{Open Chart}[m1,m5,...]
        String[] intervalList = TPriceBar.SHOW_INTERVAL_LIST;
        for (int i = 0; i < intervalList.length; i++) {
	        menuItem = uiManager.createMenuItem(intervalList[i], null, null, intervalList[i]);
	        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, intervalList[i]);
	        menuItem.addActionListener(
	            new ActionListener() {
	                public void actionPerformed(ActionEvent aEvent) {
	                	JMenuItem item = (JMenuItem) aEvent.getSource();
	                	String symbol = mainFrame.getSymbolPanel().getSelectedCurrency();
	                	String period = (String)item.getClientProperty(ITraderConstants.MENUITEM_NAME);
	                	ChartSchema chartSchema = PropertyManager.getInstance().newChartProperty(symbol, period);
	                	ChartPanel chartPanel = new ChartPanel(
	                		mainFrame, chartSchema, PropertyManager.getInstance().getChartProperty(chartSchema.toString()));
	                	mainFrame.getBenchPanel().addChartPanel(chartPanel);
	                }
	            });
	        submenu.add(menuItem);
        }
        menu.addSeparator();
        //[Charts][Overlap]
        submenu = uiManager.createMenu("IDS_CHARTS_OVERLAP", null);
        menu.add(submenu);
        String chartOverlapMode = BenchPanel.getChartOverlayProperty();
        ButtonGroup group = new ButtonGroup();
        ActionListener rmbiActionListener = new ActionListener() {
		    public void actionPerformed(ActionEvent aEvent) {
		        JRadioButtonMenuItem item = (JRadioButtonMenuItem) aEvent.getSource();
		        String chartOverlapMode = (String)item.getClientProperty(ITraderConstants.MENUITEM_NAME);
		        mainFrame.getBenchPanel().setChartOverlapMode(chartOverlapMode);
		        BenchPanel.setChartOverlayProperty(chartOverlapMode);
		    }
		};
        //[Charts][Overlap][Tabbed]
        JRadioButtonMenuItem rbmi = UIManager.getInst().createRadioButtonMenuItem(
        		"IDS_CHARTS_OVERLAP_TABBED", null, null, "IDS_CHARTS_OVERLAP_TABBED");
        rbmi.putClientProperty(ITraderConstants.MENUITEM_NAME, BenchPanel.CHART_OVERLAP_TABBED);
	    if (chartOverlapMode.equals(BenchPanel.CHART_OVERLAP_TABBED)) {
	        rbmi.setSelected(true);
	    }
	    rbmi.addActionListener(rmbiActionListener);
	    submenu.add(rbmi);
	    group.add(rbmi);
        //[Charts][Overlap][Split]
	    rbmi = UIManager.getInst().createRadioButtonMenuItem(
        		"IDS_CHARTS_OVERLAP_SPLIT", null, null, "IDS_CHARTS_OVERLAP_SPLIT");
        rbmi.putClientProperty(ITraderConstants.MENUITEM_NAME, BenchPanel.CHART_OVERLAP_SPLIT);
	    if (chartOverlapMode.equals(BenchPanel.CHART_OVERLAP_SPLIT)) {
	        rbmi.setSelected(true);
	    }
	    rbmi.addActionListener(rmbiActionListener);
	    submenu.add(rbmi);
	    group.add(rbmi);

        //[Window]
        menu = uiManager.createMenu("IDS_WINDOW", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Window");
        menu.setMnemonic('W');
        menuBar.add(menu);
        //[Window]...
//        JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getChartDockable());
//        menu.add(cbMenuItem);
        JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getSymbolDockable());
		menu.add(cbMenuItem);
        cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getAccountDockable());
		menu.add(cbMenuItem);
		cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getSummaryDockable());
		menu.add(cbMenuItem);
		cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getOrderDockable());
		menu.add(cbMenuItem);
		cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getOpenPositionDockable());
		menu.add(cbMenuItem);
		cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getClosedPositionDockable());
		menu.add(cbMenuItem);
		cbMenuItem = new DockableMenuItem(mainFrame.getBenchPanel().getMessageDockable());
		menu.add(cbMenuItem);
		
        //[Help]
        menu = uiManager.createMenu("IDS_HELP_MENU", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Help");
        menu.setMnemonic('H');
        menuBar.add(menu);
        //[Help][Help]
        menuItem = uiManager.createMenuItem("IDS_HELP", "ID_HELP_ICON", null, "IDS_HELP_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Help")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Help")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Help");
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    HelpManager mgr = HelpManager.getInst();
                    //shows help window
                    mgr.showHelp();
                }
            });
        menu.add(menuItem);
        menuItem = uiManager.createMenuItem("IDS_ABOUT", null, null, "IDS_ABOUT_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.About")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("About")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "About");
        menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                        AboutDialog dialog = new AboutDialog(mainFrame);
                        dialog.showModal();
                    }
                });
        menu.add(menuItem);
        return menuBar;
    }
	
	/**
     * Initializes the disconnected menu
     *
     * @return disconnected menu
     */
    private JMenuBar createDisconnectedMenu() {
        UIManager uiManager = UIManager.getInst();
        //creates the menu bar
        JMenuBar menuBar = new JMenuBar();
        //gets user preferences
//        UserPreferences uiPrefs = UserPreferences.getUserPreferences();

        //[File]
        JMenu menu = uiManager.createMenu("IDS_FILE", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "File");
        menu.setMnemonic('F');
        menuBar.add(menu);

        //[File][Login]
        Action loginAction = new LoginAction();
        uiManager.addAction(loginAction, "IDS_LOGIN", "ID_LOGIN_ICON", null, null, "IDS_LOGIN_DESC");
        JMenuItem menuItem = uiManager.createMenuItem(loginAction);
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Login")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Login")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Login");
        menuItem.setMnemonic('L');
        menu.add(menuItem);

        //[File][Options]
        menuItem = uiManager.createMenuItem("IDS_OPTIONS", null, null, "IDS_OPTIONS_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Options")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Options")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Options");
        menuItem.setMnemonic('O');
        menu.add(menuItem);
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
//                    PreferencesManager preferencesManager = PreferencesManager.
//                            getPreferencesManager(BenchApp.getInst().getTradeDesk().getUserName());
//                    preferencesManager.showPreferencesDialog();
                	PreferencesDialog.createAndShowDialog();
                }
            });

        //[File][Language]
        //submenu = uiManager.createMenu("Language", null);
        //submenu.putClientProperty(IFXTSConstants.MENUITEM_NAME, "Language");
        //menu.add(submenu);
        //addSupportedLanguages(submenu);
        //menu.addSeparator();

        //[File][Exit]
        menuItem = uiManager.createMenuItem("IDS_EXIT", "ID_EXIT_ICON", null, "IDS_EXIT_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Exit")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Exit")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Exit");
        menuItem.setMnemonic('X');
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    mainFrame.exitFromApp();
                }
            });
        menu.add(menuItem);

        //[Help]
        menu = uiManager.createMenu("IDS_HELP_MENU", null);
//        menu.putClientProperty(ITraderConstants.MENUITEM_NAME, "Help");
        menu.setMnemonic('H');
        menuBar.add(menu);

        //[Help][Help]
        menuItem = uiManager.createMenuItem("IDS_HELP", "ID_HELP_ICON", null, "IDS_HELP_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.Help")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("Help")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "Help");
        menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                        HelpManager mgr = HelpManager.getInst();
                        //shows help window
                        mgr.showHelp();
                    }
                });
        //menuItem.setEnabled(false);
        menu.add(menuItem);
        
        //[Help][About]
        menuItem = uiManager.createMenuItem("IDS_ABOUT", null, null, "IDS_ABOUT_DESC");
//        if (uiPrefs != null) {
//            menuItem.setAccelerator(KeyStroke.getKeyStroke(uiPrefs.getString("mainframe.shortcuts.About")));
//        }
        menuItem.setAccelerator(KeyStroke.getKeyStroke(SettingManager.getInstance().getShortcut("About")));
//        menuItem.putClientProperty(ITraderConstants.MENUITEM_NAME, "About");
        menuItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent aEvent) {
                        AboutDialog dialog = new AboutDialog(mainFrame);
                        dialog.showModal();
                    }
                });
        menu.add(menuItem);
        return menuBar;
    }

	/**
	 * A check box menu item to add or remove the dockable.
	 */
	private class DockableMenuItem extends JCheckBoxMenuItem
	{
		public DockableMenuItem(Dockable dockable) {
			super(dockable.getTitle(), dockable.getIcon());
			setSelected(dockable.getDock() != null);
			DockableMediator dockableMediator = new DockableMediator(dockable, this);
			dockable.addDockingListener(dockableMediator);
			addItemListener(dockableMediator);
		}
		
	}
	
	/**
	 * A listener that listens when menu items with dockables are selected and deselected.
	 * It also listens when dockables are closed or docked.
	 */
	private class DockableMediator implements ItemListener, DockingListener
	{
		private Dockable dockable;
		private Action closeAction;
		private Action restoreAction;
		private JMenuItem dockableMenuItem;
		
		public DockableMediator(Dockable dockable, JMenuItem dockableMenuItem) {
			this.dockable = dockable;
			this.dockableMenuItem = dockableMenuItem;
			closeAction = new DefaultDockableStateAction(dockable, DockableState.CLOSED);
			restoreAction = new DefaultDockableStateAction(dockable, DockableState.NORMAL);
		}

		public void itemStateChanged(ItemEvent itemEvent) {
			dockable.removeDockingListener(this);
			if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
				// Close the dockable.
				closeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Close"));
			} else {
				// Restore the dockable.
				restoreAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Restore"));
			}
			dockable.addDockingListener(this);
		}

		public void dockingChanged(DockingEvent dockingEvent) {
			if (dockingEvent.getDestinationDock() != null) {
				dockableMenuItem.removeItemListener(this);
				dockableMenuItem.setSelected(true);
				dockableMenuItem.addItemListener(this);	
			} else {
				dockableMenuItem.removeItemListener(this);
				dockableMenuItem.setSelected(false);
				dockableMenuItem.addItemListener(this);
			}
		}

		public void dockingWillChange(DockingEvent dockingEvent) {}
	}
	
	private void addLookandFeel(JMenu aMenu)
	{
        final ButtonGroup group = new ButtonGroup();
        LookAndFeelInfo[] lafList = javax.swing.UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo laf : lafList) {
            JRadioButtonMenuItem rbmi = UIManager.getInst().
            	createRadioButtonMenuItem(laf.getName(), null, null, "IDS_LANGUAGE_DESC");
            rbmi.putClientProperty("Server.lookandfeel", laf.getClassName());
            if (laf.getName().equals(javax.swing.UIManager.getLookAndFeel().getName())) {
                rbmi.setSelected(true);
            }
            
            BenchApp.getInst().getTradeDesk().getLiaison().addLiaisonListener(
                new LiaisonListener() {
                    @Override
                    public void onLoginCompleted() {
                    //    UserPreferences userPreferences = UserPreferences.getUserPreferences();
                        Enumeration enumeration = group.getElements();
                        while (enumeration.hasMoreElements()) {
                            JRadioButtonMenuItem o = (JRadioButtonMenuItem) enumeration.nextElement();
                            String lookandfeel = PropertyManager.getInstance().getStrVal("preferences.menu.lookandfeel");
//                            if (o.getText().equalsIgnoreCase(userPreferences.getString("Server.lookandfeel"))) {
                            if (o.getText().equalsIgnoreCase(lookandfeel)) {
                                o.setSelected(true);
                            }
                        }
                    }
                });

            //adds listener to button
            rbmi.addActionListener(
                new ActionListener() {
                //    private UserPreferences mUserPreferences = UserPreferences.getUserPreferences();
                    public void actionPerformed(ActionEvent aEvent) {
                        JRadioButtonMenuItem item = (JRadioButtonMenuItem) aEvent.getSource();
                        String type = (String) item.getClientProperty("Server.lookandfeel");
                        try {
//                            mUserPreferences.setUserName(BenchApp.getInst().getTradeDesk().getUserName());
//                            mUserPreferences.set("Server.lookandfeel", type);
                        	PropertyManager.getInstance().setProperty("preferences.menu.lookandfeel", type);
                            javax.swing.UIManager.setLookAndFeel(type);
                            
                            // Iterate over the owner windows of the dock model.
                	        for (int index = 0; index < mainFrame.getBenchPanel().getDockModel().getOwnerCount(); index++) {
                	        	// Set the LaF on the owner.
                	        	Window owner = mainFrame.getBenchPanel().getDockModel().getOwner(index);
                	        	SwingUtilities.updateComponentTreeUI(owner);
                	        	
                	        	// Set the Laf on the floating windows.
                	        	FloatDock floatDock = mainFrame.getBenchPanel().getDockModel().getFloatDock(owner);
                	        	for (int childIndex = 0; childIndex < floatDock.getChildDockCount(); childIndex++) {
                	        		Component floatingComponent = (Component)floatDock.getChildDock(childIndex);
                	        		SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(floatingComponent));
                	        	}
                	        	
                	        	// Set the LaF on all the dockable components.
//                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getBenchPanel().getChartDockable().getContent());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getSymbolPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getAccountPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getSummaryPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getOrderPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getOpenPositionPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getClosedPositionPanel());
                	        	SwingUtilities.updateComponentTreeUI(mainFrame.getMessagePanel());
                	        }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(mainFrame, "A restart is required to completely apply the changes.");
                    }
                });
            aMenu.add(rbmi);
            group.add(rbmi);
        }
    }
	
}
