/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/MainFrame.java#2 $
 *
 * Copyright (c) 2008 FXCM, LLC.
 * 32 Old Slip, New York NY, 10005 USA
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
 *
 * $History: $
 *
 * 09/05/2003   KAV     Initial creation
 * 1/6/2004     USHIK   Ask connection name from Tradedesk instead liaison
 * 4/27/2004    USHIK   Adds button "Charts" and menu "Charts"
 * 05/10/2006   Andre Mermeags: set/get lookandfeel using system property preference
 * 07/05/2006   Andre Mermegas: fixes for dynamic look and feel changes,
        autoarrange will always reset windows to default position
        bugfix for autoarrange logic, no rates window was causing all windows to disappear
 * 05/17/2007   Andre Mermegas: show restart message on language,look and feel change
 */
package org.fxbench.ui;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.fxbench.BenchApp;
import org.fxbench.trader.ILiaisonListener;
import org.fxbench.trader.ITraderConstants;
import org.fxbench.trader.Liaison;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.LiaisonListenerStub;
import org.fxbench.trader.action.ClosePositionAction;
import org.fxbench.trader.action.CreateEntryOrderAction;
import org.fxbench.trader.action.CreateMarketOrderAction;
import org.fxbench.trader.action.RemoveEntryOrderAction;
import org.fxbench.trader.action.ReportAction;
import org.fxbench.trader.action.RequestForQuoteAction;
import org.fxbench.trader.action.SetStopLimitAction;
import org.fxbench.trader.action.SetStopLimitOrderAction;
import org.fxbench.trader.action.TradeAction.ActionType;
import org.fxbench.trader.action.UpdateEntryOrderAction;
import org.fxbench.trader.dialog.BaseDialog;
import org.fxbench.ui.BenchMenu.MenuType;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.ui.panel.AccountPanel;
import org.fxbench.ui.panel.BenchPanel;
import org.fxbench.ui.panel.ClosedPositionPanel;
import org.fxbench.ui.panel.MessagePanel;
import org.fxbench.ui.panel.OpenPositionPanel;
import org.fxbench.ui.panel.OrderPanel;
import org.fxbench.ui.panel.SummaryPanel;
import org.fxbench.ui.panel.SymbolPanel;
import org.fxbench.util.ILocaleListener;
import org.fxbench.util.ResourceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Locale;
import java.util.Observer;

/**
 * It's main frame of the application.
 * This class if application frame work.
 */
public class BenchFrame extends JFrame implements ILocaleListener, ILiaisonListener
{
	private final Log mLogger = LogFactory.getLog(BenchFrame.class);

    private static final int INSERT_SIZE = 70;
    
    private BenchPanel benchPanel;
    private BenchMenu benchMenu;
    private BenchStatusBar statusBar;
    private JDialog mWaitDialog;
    
    private boolean mExitingRequired;
    private LiaisonStatus mCurStat = LiaisonStatus.DISCONNECTED;    
    private Observer mTitleObserver;

    public BenchFrame() {
        ResourceManager resmng = BenchApp.getInst().getResourceManager();

        //adds to array of locale listeners
        resmng.addLocaleListener(this);

        //creates the LiaisonListenerStub
        LiaisonListenerStub liaisonStub = new LiaisonListenerStub(this);

        //registers LiaisonListenerStub at Liaison
        BenchApp.getInst().getTradeDesk().getLiaison().addLiaisonListener(liaisonStub);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        //creating of the menu
        benchMenu = new BenchMenu(this);
        setJMenuBar(benchMenu.getMenuBar());
        
        //creating of the statusbar
        statusBar = new BenchStatusBar(resmng, this);
        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        getContentPane().add(BorderLayout.SOUTH, statusBar);
        
        //sets size of the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(INSERT_SIZE, INSERT_SIZE, screenSize.width - INSERT_SIZE * 2, screenSize.height - INSERT_SIZE * 2);

        //fires loading of localised resources
        onChangeLocale(resmng);
        
        //adds window listener
        addWindowListener(
            new WindowAdapter() {
                /**
                 * Invoked when a window has been closed as the
                 * result of calling dispose on the window.
                 */
                @Override
                public void windowClosed(WindowEvent aEvent) {
                    System.exit(1);
                }

                /**
                 * Invokes when a window is in the process of being closed.
                 */
                @Override
                public void windowClosing(WindowEvent aEvent) {
                    //stars procedure of exiting from application
                    exitFromApp();
                }
            });
        
        // Set the frame properties and show it.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		pack();
		setVisible(true);
    }
    
    public BenchPanel getBenchPanel() {
    	return benchPanel;
    }
    
    public BenchMenu getBenchMenu() {
    	return benchMenu;
    }
    
    public BenchStatusBar getStatusBar() {
    	return statusBar;
    }

    public LiaisonStatus getCurStatus() {
    	return mCurStat;
    }
    
    public SymbolPanel getSymbolPanel() {
		return benchPanel == null ? null : benchPanel.getSymbolPanel();
	}
	
	public AccountPanel getAccountPanel() {
		return benchPanel == null ? null : benchPanel.getAccountPanel();
	}
	
	public SummaryPanel getSummaryPanel() {
		return benchPanel == null ? null : benchPanel.getSummaryPanel();
	}
	
	public OrderPanel getOrderPanel() {
		return benchPanel == null ? null : benchPanel.getOrderPanel();
	}
	
	public OpenPositionPanel getOpenPositionPanel() {
		return benchPanel == null ? null : benchPanel.getOpenPositionPanel();
	}
	
	public ClosedPositionPanel getClosedPositionPanel() {
		return benchPanel == null ? null : benchPanel.getClosedPositionPanel();
	}
	
	public MessagePanel getMessagePanel() {
		return benchPanel == null ? null : benchPanel.getMessagePanel();
	}
    
    /**
     * Creates modal message dialog.
     *
     * @param asMessage the message to display
     * @param asTitle the title string for the dialog
     * @param aiMessageType message type
     *
     * @return message dialog
     */
    public JDialog createMessageDlg(String asMessage, String asTitle, int aiMessageType) {
        JOptionPane pane = new JOptionPane(asMessage, aiMessageType, JOptionPane.DEFAULT_OPTION, null, null, null);
        pane.setInitialValue(null);
        JDialog dialog = pane.createDialog(this, asTitle);
        pane.selectInitialValue();
        return dialog;
    }

    /**
     * Create waiting dialog.
     *
     * @param aText text of waiting message
     *
     * @return created waiting dialog
     */
    public JDialog createWaitDialog(String aText) {
        //get resouce manager
        ResourceManager resmng = BenchApp.getInst().getResourceManager();

        //creates frame
        JDialog dialog = new JDialog(this);
        dialog.setModal(true);
        dialog.setTitle(resmng.getString("IDS_MAINFRAME_SHORT_TITLE"));
        dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //adds label
        JLabel jl = UIManager.getInst().createLabel(aText);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        jl.setBorder(new EmptyBorder(15, 40, 15, 40));
        Container cp = dialog.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(jl, BorderLayout.CENTER);

        //sets size of the frame
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getOwner());
        return dialog;
    }

    /**
     * Exit fom application.
     */
    public void exitFromApp() {
        //get resouce manager
        ResourceManager resmng = BenchApp.getInst().getResourceManager();

        //shows confirmation dialog
        if (showConfirmationDlg(
                resmng.getString("IDS_EXIT_DLG_MESSAGE"),
                resmng.getString("IDS_MAINFRAME_SHORT_TITLE"),
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            //gets liaison
            Liaison liaison = BenchApp.getInst().getTradeDesk().getLiaison();
            //gets current status
            LiaisonStatus status = liaison.getStatus();
            if (status.equals(LiaisonStatus.DISCONNECTED)) {
                //disposes mainframe window
                dispose();
            } else {
                //initiaites logout procedure.
                liaison.logout();
                //sets demand for exit
                mExitingRequired = true;
            }
        }
    }

    /**
     * Returns Action of specified type.
     *
     * @param aTypes Action type
     * @param asCommand Action command
     *
     * @return aproppriate action
     */
    public Action getAction(ActionType aTypes, String asCommand) {
        if (aTypes == ActionType.CLOSE_POSITION) {
            return ClosePositionAction.newAction(asCommand);
        }
        if (aTypes == ActionType.CREATE_ENTRY_ORDER) {
            return CreateEntryOrderAction.newAction(asCommand);
        }
        if (aTypes == ActionType.CREATE_MARKET_ORDER) {
            return CreateMarketOrderAction.newAction(asCommand);
        }
        if (aTypes == ActionType.REMOVE_ENTRY_ORDER) {
            return RemoveEntryOrderAction.newAction(asCommand);
        }
        if (aTypes == ActionType.SET_STOP_LIMIT) {
            return SetStopLimitAction.newAction(asCommand);
        }
        if (aTypes == ActionType.SET_STOP_LIMIT_ORDER) {
            return SetStopLimitOrderAction.newAction(asCommand);
        }
        if (aTypes == ActionType.UPDATE_ENTRY_ORDER) {
            return UpdateEntryOrderAction.newAction(asCommand);
        }
        if (aTypes == ActionType.REQUEST_FOR_QUOTE) {
            return RequestForQuoteAction.newAction(asCommand);
        }
        if (aTypes == ActionType.REPORT) {
            return ReportAction.newAction(asCommand);
        }
        return null;
    }

    /**
     * Returns localized title.
     *
     * @param aResourceMan current resource manager
     */
    private String getLocalizedTitle(ResourceManager aResourceMan) {
        if (aResourceMan != null) {
            //localizes title of main frame
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_MAINFRAME_TITLE"));
            titleBuffer.append(" ");
            titleBuffer.append(ITraderConstants.CURRENT_VERSION);
            return titleBuffer.toString();
        }
        mLogger.debug("MainFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    /**
     * Returns waiting dialog.
     *
     * @return waiting dialog.
     */
    public JDialog getWaitDialog() {
        return mWaitDialog;
    }

    /**
     * Sets waiting dialog.
     *
     * @param aWaitDialog waiting dialog.
     */
    public void setWaitDialog(JDialog aWaitDialog) {
        mWaitDialog = aWaitDialog;
    }

    /**
     * This method is called when current locale of the aMan is changed.
     * It`s a ILiaisonListener method.
     *
     * @param aMan resource manager.
     */
    public void onChangeLocale(ResourceManager aMan) {
        setTitle(getLocalizedTitle(aMan));
        //localized icon of application
        URL iconUrl = aMan.getResource("ID_APPLICATION_ICON");
        if (iconUrl != null) {
            ImageIcon imageIcon = new ImageIcon(iconUrl);
            setIconImage(imageIcon.getImage());
        }

        //localizes status bar
        statusBar.onChangeLocale();
        //if not at disconnected state
        if (mCurStat != LiaisonStatus.DISCONNECTED) {
            statusBar.arrange();
        }
    }

    /**
     * This method is called when critical error occurred. Connection is closed.
     *
     * @param aEx liaison exception
     */
    public void onCriticalError(LiaisonException aEx) {
        ResourceManager resmng = BenchApp.getInst().getResourceManager();
        JOptionPane.showMessageDialog(
                this,
                aEx.getLocalizedMessage(),
                resmng.getString("IDS_MAINFRAME_SHORT_TITLE"),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called when status of liaison has changed.
     *
     * @param aStatus new setted liaison status
     */
    public void onLiaisonStatus(LiaisonStatus aStatus) {
        ResourceManager resmng = BenchApp.getInst().getResourceManager();
        
        //sets text in status bar
        statusBar.onLiaisonStatus(aStatus);
        
        if (aStatus == LiaisonStatus.CONNECTING) {
        	//sets current status
            mCurStat = LiaisonStatus.CONNECTING;
            //shows modal window with label Connecting: Please wait.
            mWaitDialog = createWaitDialog(resmng.getString("IDS_WAIT_CONNECTING"));
            mWaitDialog.setVisible(true);
            
        } else if (aStatus == LiaisonStatus.READY) {
            //Depending on previous state
            if (mCurStat == LiaisonStatus.CONNECTING) {//Previous state = Connecting                

                //Dispose Connecting modal dialog
                if (mWaitDialog != null) {
                    mWaitDialog.setVisible(false);
                    mWaitDialog.dispose();
                    mWaitDialog = null;
//                    if (BenchApp.getInst().getTradeDesk().getTradingServerSession().getUserKind() == IFixDefs.FXCM_SESSION_TYPE_CUSTOMER) {
//                        JOptionPane.showMessageDialog(this,
//                                                      "This account is in read only mode.\nYou will be unable to place trades on this account.");
//                    }
                }
                
                //creating of the panels
                benchPanel = BenchPanel.createAndShowGUI(this);

                //Change menu bar from disconnected to connected
            	benchMenu.setMenyType(MenuType.Connected);
            	setJMenuBar(benchMenu.getMenuBar());
                setCurrentLanguage(benchMenu);
                
            } else if (mCurStat == LiaisonStatus.RECONNECTING) {
            	//nothing
            } else if (mCurStat == LiaisonStatus.SENDING || mCurStat == LiaisonStatus.RECEIVING) {
                //nothing
            }
            
            //sets current status
            mCurStat = LiaisonStatus.READY;

        } else if (aStatus == LiaisonStatus.RECONNECTING) {
        	//sets current status
            mCurStat = LiaisonStatus.RECONNECTING;

        } else if (aStatus == LiaisonStatus.DISCONNECTING) {
        	//sets current status
            mCurStat = LiaisonStatus.DISCONNECTING;
            //shows modal window with label Disconnecting: Please wait
            mWaitDialog = createWaitDialog(resmng.getString("IDS_WAIT_DISCONNECTING"));
            mWaitDialog.setVisible(true);
        
        } else if (aStatus == LiaisonStatus.SENDING) {
        	//sets current status
            mCurStat = LiaisonStatus.SENDING;

        } else if (aStatus == LiaisonStatus.RECEIVING) {
        	//sets current status
            mCurStat = LiaisonStatus.RECEIVING;

        } else if (aStatus == LiaisonStatus.DISCONNECTED) {
//        	benchPanel.setVisible(false);
//        	saveSettings();

            //dispose Disconnecting modal dialog
            if (mWaitDialog != null) {
                mWaitDialog.dispose();
                mWaitDialog = null;
            }

            //change menu bar to disconnected
            benchMenu.setMenyType(MenuType.Disconnected);
        	setJMenuBar(benchMenu.getMenuBar());
            setCurrentLanguage(benchMenu);
            
            //sets current status
            mCurStat = LiaisonStatus.DISCONNECTED;
            //disposes mainframe window
            if (mExitingRequired) {
                dispose();
            }
        }
    }

    /**
     * This method is called when initiated login command has completed successfully.
     */
    public void onLoginCompleted() {
        updateTitle();
//        UserPreferences pref = UserPreferences.getUserPreferences();
//        if (mTitleObserver == null) {
//            mTitleObserver = new Observer() {
//                public void update(Observable aObservable, Object arg) {
//               //     if (arg.toString().contains(IClickModel.TRADING_MODE)) {
//                        updateTitle();
//               //     }
//                }
//            };
//        }
//        pref.addObserver(mTitleObserver);
    }

    /**
     * This method is called when initiated login command has failed. aEx
     * contains information about error.
     * param aEx
     */
    public void onLoginFailed(LiaisonException aEx) {
        aEx.printStackTrace();
    }

    /**
     * This method is called when initiated logout command has completed.
     */
    public void onLogoutCompleted() {
        setTitle(getLocalizedTitle(BenchApp.getInst().getResourceManager()));
//        UserPreferences pref = UserPreferences.getUserPreferences();
//        pref.deleteObserver(mTitleObserver);
    }

    /**
     * Sets radiobutton to selected state in apropriate with current language.
     *
     * @param aMenuBar menu bar of main frame
     */
    private void setCurrentLanguage(BenchMenu aMenuBar) {
        ResourceManager resmng = BenchApp.getInst().getResourceManager();
        Locale curLocale = resmng.getLocale();

        //if menu bar not created
        if (aMenuBar == null) {
            return;
        }
        JMenu menu = aMenuBar.findMenu("File");

        //if first menu not initialised
        if (menu == null) {
            return;
        }
        JMenu submenu = (JMenu) aMenuBar.findMenuItem("Language", menu);

        //if submenu not initialised
        if (submenu == null) {
            return;
        }
        for (int i = 0; i < submenu.getItemCount(); i++) {
            JMenuItem item = submenu.getItem(i);
            if (item == null) {
                continue;
            }
            Locale locale = (Locale) item.getClientProperty(ITraderConstants.LOCALE_KEY);
            if (locale.getISO3Language().equals(curLocale.getISO3Language())
                && locale.getISO3Country().equals(curLocale.getISO3Country())) {
                //set item to checked state
                item.setSelected(true);
            }
        }
    }

    /**
     * Shows confirmation dialog.
     *
     * @param asMessage the message to display
     * @param asTitle the title string for the dialog
     * @param aiMessageType message type
     *
     * @return confirmation dialog
     */
    public int showConfirmationDlg(String asMessage, String asTitle, int aiMessageType) {
        //get resouce manager
        ResourceManager resmng = BenchApp.getInst().getResourceManager();
        //load localized labels
        String[] arsLabels = new String[2];
        arsLabels[0] = resmng.getString("IDS_YES_BUTTON_LABEL");
        arsLabels[1] = resmng.getString("IDS_NO_BUTTON_LABEL");
        JOptionPane pane = new JOptionPane(asMessage, aiMessageType, JOptionPane.YES_NO_OPTION, null, arsLabels, null);
        pane.setInitialValue(null);
        JDialog dialog = pane.createDialog(this, asTitle);
        pane.selectInitialValue();
        dialog.setVisible(true);
        Object selectedValue = pane.getValue();
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        for (int counter = 0, maxCounter = arsLabels.length; counter < maxCounter; counter++) {
            if (arsLabels[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Shows a modal dialog and adds it to collections of shown dialogs.
     *
     * @param aDialog dialog for showing
     *
     * @return show the dialogs status
     */
    public int showDialog(BaseDialog aDialog) {
        return aDialog.showModal();
    }


    public void updateTitle() {
        StringBuffer titleBuffer = new StringBuffer(getLocalizedTitle(BenchApp.getInst().getResourceManager()));
//        UserPreferences prefs = UserPreferences.getUserPreferences();
//        String mode = prefs.getString(IClickModel.TRADING_MODE);
//        if (IClickModel.SINGLE_CLICK.equals(mode)) {
//            titleBuffer.append(" ~~~~~~ ONE CLICK TRADING ~~~~~~");
//        } else if (IClickModel.DOUBLE_CLICK.equals(mode)) {
//            titleBuffer.append(" ~~~~~~ DOUBLE CLICK TRADING ~~~~~~");
//        }
        setTitle(titleBuffer.toString());
    }
}
