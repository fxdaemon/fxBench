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
package org.fxbench.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.trader.IServerTimeListener;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.ServerTime;
import org.fxbench.ui.auxi.UIFrontEnd;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ILocaleListener;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Class of status bar component.
 * This class provides more easy interface for creating and
 * filling by elements of status bar then standart layout managers.
 */
public class BenchStatusBar extends JComponent
{
    private static final int INSERTS_VALUE = 2;
    private static final int CONNECTING_INDICATOR = 1;
    private static final int CONNECTING_STATUS = 2;
    private static final int USER_NAME = 3;
    private static final int SERVER_NAME = 4;
    private static final int SERVER_TIME = 5;
    
    private BenchFrame mainFrame;
    private TradeDesk tradeDesk;
    private ResourceManager resourceManager;
    private LiaisonStatusIndicator statusIndicator;
    private ServerTimePane timePane;
    
    //Vector of panes in the same order as they are appeared in status bar.
    private Vector<JComponent> mPanes = new Vector<JComponent>();

    /**
     * Constructor
     */
    public BenchStatusBar(ResourceManager resourceManager, BenchFrame mainFrame) {
    	this.resourceManager = resourceManager;
    	this.mainFrame = mainFrame;
    	this.tradeDesk = BenchApp.getInst().getTradeDesk();
        setLayout(UIFrontEnd.getInstance().getSideLayout());
        createStatusBar();
    }
    
    private void createStatusBar() {
        //first pane
        addLabel(resourceManager.getString("IDS_READY"), 1.0);

        //second pane
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.setPreferredSize(new Dimension(16, 16));
        statusIndicator = new LiaisonStatusIndicator();
        jp.add(BorderLayout.CENTER, statusIndicator);
        jp.setBorder(BorderFactory.createEtchedBorder());
        addPane(jp);
        //adds for prompting
        jp.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_CONNECTING_INDICATOR"));

        //third pane
        addLabel(resourceManager.getString("IDS_STATE_DISCONNECTED"), 140, BorderFactory.createEtchedBorder());
        //adds for prompting
        jp.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_CONNECTING_STATUS"));

        //fourth pane
        addLabel("", 80, BorderFactory.createEtchedBorder());
        //adds for prompting
        jp.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_USER_NAME"));

        //fifth pane
        addLabel("", 80, BorderFactory.createEtchedBorder());
        //adds for prompting
        jp.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_SERVER_NAME"));

        //sixth pane
        timePane = new ServerTimePane(resourceManager);
        resourceManager.addLocaleListener(timePane);
        timePane.setBorder(BorderFactory.createEtchedBorder());
        addPane(timePane, 180);
        //adds for prompting
        timePane.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_SERVER_TIME"));
    }

    /**
     * Adds JLabel with the specified text as the pane to the status bar.
     * Creates non-resizable pane which size is equal to the size defined by a layout manager.
     *
     * @param aLabel   component to adding
     * @param aWeight accords to weight parameter in SideLayout
     *
     * @return index of the pane (0-based)
     */
    public int addLabel(String aLabel, double aWeight) {
        JPanel jp = new JPanel(new BorderLayout());
        jp.add(UIManager.getInst().createLabel(aLabel), BorderLayout.WEST);

        add(jp, createConstraints(mPanes.size(), 0, GridBagConstraints.BOTH, INSERTS_VALUE, aWeight));
        mPanes.add(jp);
        return mPanes.size() - 1;
    }

    /**
     * Adds JLabel with the specified text and preferred width as the pane to the status bar.
     * Creates non-resizable pane which size is equal to the size defined by a layout manager.
     *
     * @param aLabel   component to adding
     * @param aWidth  preferred width of component
     * @param aBorder border object
     *
     * @return index of the pane (0-based)
     */
    public int addLabel(String aLabel, int aWidth, Border aBorder) {
        JPanel jp = new JPanel(new BorderLayout());
        jp.setPreferredSize(new Dimension(aWidth, 0));
        jp.setMinimumSize(new Dimension(aWidth, 0));
        jp.add(UIManager.getInst().createLabel(aLabel), BorderLayout.WEST);
        jp.setBorder(aBorder);

        add(jp, createConstraints(mPanes.size(), 0, GridBagConstraints.BOTH, INSERTS_VALUE));
        mPanes.add(jp);
        return mPanes.size() - 1;
    }

    /**
     * Adds arbitrary JComponent as the pane to the status bar.
     * Creates non-resizable pane which size is equal to the size defined by a layout manager.
     *
     * @param aPane component to adding
     *
     * @return index of the pane (0-based)
     */
    public int addPane(JComponent aPane) {
        add(aPane, createConstraints(mPanes.size(), 0, GridBagConstraints.BOTH, INSERTS_VALUE));
        mPanes.add(aPane);
        return mPanes.size() - 1;
    }

    /**
     * Adds arbitrary JComponent as the pane to the status bar.
     * Additional parameter defines, how much of extra space is given
     * to the component during resize.
     *
     * @param aPane  component to adding
     * @param aWidth preferred width of component
     *
     * @return index of the pane (0-based)
     */
    public int addPane(JComponent aPane, int aWidth) {
        aPane.setPreferredSize(new Dimension(aWidth, 0));
        aPane.setMinimumSize(new Dimension(aWidth, 0));
        add(aPane, createConstraints(mPanes.size(), 0, GridBagConstraints.BOTH, INSERTS_VALUE));
        mPanes.add(aPane);
        return mPanes.size() - 1;
    }

    /**
     * Creates and sets the SideConstraints object
     *
     * @param aGridx   corresponds to gridx's value of SideConstraints
     * @param aGridy   corresponds to gridy's value of SideConstraints
     * @param aFill    corresponds to fill's value of SideConstraints
     * @param aInserts corresponds to insets's values of SideConstraints
     *
     * @return created SideConstraints object
     */
    private GridBagConstraints createConstraints(int aGridx, int aGridy, int aFill, int aInserts) {
        GridBagConstraints aContr = UIFrontEnd.getInstance().getSideConstraints();
        aContr.gridx = aGridx;
        aContr.gridy = aGridy;
        aContr.fill = aFill;
        aContr.insets.top = aInserts;
        aContr.insets.bottom = aInserts;
        aContr.insets.left = aInserts;
        aContr.insets.right = aInserts;
        return aContr;
    }

    /**
     * Creates and sets the SideConstraints object
     *
     * @param aGridx   corresponds to gridx's value of SideConstraints
     * @param aGridy   corresponds to gridy's value of SideConstraints
     * @param aFill    corresponds to fill's value of SideConstraints
     * @param aInserts corresponds to insets's values of SideConstraints
     * @param aWeightx corresponds to weightx's value of SideConstraints
     *
     * @return created SideConstraints object
     */
    private GridBagConstraints createConstraints(int aGridx, int aGridy, int aFill, int aInserts, double aWeightx) {
        GridBagConstraints aContr = UIFrontEnd.getInstance().getSideConstraints();
        aContr.gridx = aGridx;
        aContr.gridy = aGridy;
        aContr.fill = aFill;
        aContr.insets.top = aInserts;
        aContr.insets.bottom = aInserts;
        aContr.insets.left = aInserts;
        aContr.insets.right = aInserts;
        aContr.weightx = aWeightx;
        return aContr;
    }

    /**
     * Finds the specified component as the pane
     *
     * @param aPane component
     *
     * @return components's index or -1 if not found.
     */
    public int findPane(JComponent aPane) {
        for (int i = 0; i < 0; i++) {
            if (mPanes.get(i) == aPane) {
                return i;
            }
        }
        //if not found
        return -1;
    }

    /**
     * Returns pane's component at the specified index.
     *
     * @param aPaneIdx index at status bat
     *
     * @return pane's component
     */
    public JComponent getPane(int aPaneIdx) {
        return (JComponent) mPanes.get(aPaneIdx);
    }

    /**
     * Get count of panes.
     *
     * @return count of panes
     */
    public int getPaneCount() {
        return mPanes.size();
    }

    /**
     * Gets text of JLabel-based component
     *
     * @param aPaneIdx index of component
     *
     * @return text, if the specified pane is JLabel-based, and null - else.
     */
    public String getText(int aPaneIdx) {
        JLabel jl;
        if (mPanes.get(aPaneIdx) instanceof JPanel) {
            //gets pane with specified id
            JPanel jp = (JPanel) mPanes.get(aPaneIdx);
            //gets array of components
            Component[] comps = jp.getComponents();
            if (comps != null) {
                if (comps[0] != null) {
                    if (comps[0] instanceof JLabel) {
                        jl = (JLabel) comps[0];
                        return jl.getText();
                    }
                }
            }
        } else if (mPanes.get(aPaneIdx) instanceof JLabel) {
            jl = (JLabel) mPanes.get(aPaneIdx);
            return jl.getText();
        }
        return null;
    }

    /**
     * Sets text of JLabel-based component
     *
     * @param aPaneIdx index of component
     * @param asText   new text
     *
     * @return true, if the specified pane is JLabel-based, and false - else.
     */
    public boolean setText(int aPaneIdx, String asText) {
        Component[] comps;
        if (mPanes.get(aPaneIdx) instanceof JPanel) {
            //gets pane with specified id
            JPanel jp = (JPanel) mPanes.get(aPaneIdx);
            //gets array of components
            comps = jp.getComponents();
            if (comps != null) {
                if (comps[0] != null) {
                    if (comps[0] instanceof JLabel) {
                        JLabel jl = (JLabel) comps[0];
                        jl.setText(asText);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void arrange() {
    	 try {
             for (int i = 2; i < 5; i++) {
                 JComponent comp = getPane(i);
                 Dimension oldSize = comp.getSize();
                 String sText = getText(i);
                 if (sText != null) {
                     FontMetrics fm = comp.getGraphics().getFontMetrics(comp.getFont());
                     int iWidth = fm.stringWidth(sText);
                     if (i == 2) {
                         iWidth *= 1.5;
                     }
                     comp.setSize(new Dimension(iWidth + 15, (int) oldSize.getHeight()));
                     comp.setPreferredSize(new Dimension(iWidth + 15, (int) oldSize.getHeight()));
                 }
             }
         } catch (Exception e) {
             //
         }
    }
    
    public void onChangeLocale() {
        if (getPaneCount() > 3) {
            setText(0, resourceManager.getString("IDS_READY"));
            setText(2, tradeDesk.getLiaison().getStatusText(resourceManager));
        }
        try {
            //localizes statusbar panel`s prompting
            JComponent jc = getPane(CONNECTING_INDICATOR);
            jc.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_CONNECTING_STATUS"));
            jc = getPane(CONNECTING_STATUS);
            jc.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_CONNECTING_STATUS"));
            jc = getPane(USER_NAME);
            jc.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_USER_NAME"));
            jc = getPane(SERVER_NAME);
            jc.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_SERVER_NAME"));
            jc = getPane(SERVER_TIME);
            jc.getAccessibleContext().setAccessibleDescription(resourceManager.getString("IDS_STATUSBAR_SERVER_TIME"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onLiaisonStatus(LiaisonStatus aStatus) {
    	if (aStatus == LiaisonStatus.CONNECTING) {
            //sets text in status bar
            setText(2, resourceManager.getString("IDS_STATE_CONNECTING"));
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.CONNECTING);
            
        } else if (aStatus == LiaisonStatus.READY) {
        	LiaisonStatus curStatus = mainFrame.getCurStatus();
            //Depending on previous state
            if (curStatus == LiaisonStatus.CONNECTING) {
                //Previous state = Connecting
                setText(2, resourceManager.getString("IDS_STATE_CONNECTED"));
                //sets connection name
                String sConnectionName = tradeDesk.getTradingServerSession().getTerminal();
                String database = tradeDesk.getTradingServerSession().getTradingSessionID();
                if (sConnectionName != null && database != null) {
                    setText(4, sConnectionName + ":" + database);
                } else {
                    setText(4, "");
                }
                String sUserName = tradeDesk.getLoginUserName();
                if (sUserName != null) {
                    setText(3, sUserName);
                } else {
                    setText(3, "");
                }
                //starts timer
                timePane.startTimer();
                arrange();
            } else if (curStatus == LiaisonStatus.RECONNECTING) {
                //Previous state = Reconnecting
                //sets text in status bar to Connected
                setText(2, resourceManager.getString("IDS_STATE_CONNECTED"));
            } else if (curStatus == LiaisonStatus.SENDING || curStatus == LiaisonStatus.RECEIVING) {
                //nothing
            }
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.READY);
            
        } else if (aStatus == LiaisonStatus.RECONNECTING) {
            //sets text in status bar
            setText(2, resourceManager.getString("IDS_STATE_RECONNECTING"));
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.RECONNECTING);
            
        } else if (aStatus == LiaisonStatus.DISCONNECTING) {
            setText(2, resourceManager.getString("IDS_STATE_DISCONNECTING"));
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.DISCONNECTING);

        } else if (aStatus == LiaisonStatus.SENDING) {
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.SENDING);

        } else if (aStatus == LiaisonStatus.RECEIVING) {
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.RECEIVING);

        } else if (aStatus == LiaisonStatus.DISCONNECTED) {
            //sets text in status bar
            setText(2, resourceManager.getString("IDS_STATE_DISCONNECTED"));
            setText(3, "");
            setText(4, "");
            //stops timer
            timePane.stopTimer();
            //sets status indicator
            statusIndicator.onLiaisonStatus(LiaisonStatus.DISCONNECTED);
        }
    }
    
    private class LiaisonStatusIndicator extends JPanel
    {
        /**
         * Color of state LiaisonStatus.DISCONNECTED.
         */
        private final Color DISCONNECTED_COLOR = Color.RED;
        /**
         * Color of state LiaisonStatus.READY.
         */
        private final Color READY_COLOR = Color.GREEN;
        /**
         * Color of state LiaisonStatus.SENDING.
         */
        private final Color SENDING_COLOR = Color.YELLOW;
        /**
         * Color of state LiaisonStatus.RECEIVING.
         */
        private final Color RECEIVING_COLOR = Color.BLUE;

        /* -- Constructors -- */

        /**
         * Constructor.
         */
        public LiaisonStatusIndicator() {
            //set disconected indicator by default
            setBackground(DISCONNECTED_COLOR);
        }

        /* -- Public methods -- */

        /**
         * Overrides JPanel's method.
         */
        public Dimension getPreferredSize() {
            return new Dimension(13, 13);
        }

        /**
         * This method is called when status of the liaison is changed.
         */
        public void onLiaisonStatus(LiaisonStatus aStatus) {
            if (aStatus == LiaisonStatus.DISCONNECTED || aStatus == LiaisonStatus.CONNECTING ||
                aStatus == LiaisonStatus.RECONNECTING || aStatus == LiaisonStatus.DISCONNECTING) {
                setBackground(DISCONNECTED_COLOR);
            } else if (aStatus == LiaisonStatus.READY) {
                setBackground(READY_COLOR);
            } else if (aStatus == LiaisonStatus.SENDING) {
                setBackground(SENDING_COLOR);
            } else if (aStatus == LiaisonStatus.RECEIVING) {
                setBackground(RECEIVING_COLOR);
            }
        }
    }
    
    private class ServerTimePane extends JLabel implements ILocaleListener
    {
        /**
         * Is the first tick?
         */
        private boolean mFirstTick = true;

        /**
         * Time line format
         */
        private SimpleDateFormat mFormatter;

        /**
         * Update label thread.
         */
        private IServerTimeListener mServerTimeListener;
        /**
         * Localized text.
         */
        private String mText;
        /* Runnable interface instance for updating time field at status bar */
        private TextUpdater mTextUpdater;
        /**
         * Formated time string.
         */
        private String mTime;
        
        private ResourceManager resourceManager;

        /**
         * Constructor.
         */
        public ServerTimePane(ResourceManager resourceManager) {
            setText("");
            mTextUpdater = new TextUpdater();
            this.resourceManager = resourceManager;
        }

        @Override
        protected void paintComponent(Graphics aGraphics) {
            if (UIManager.getInst().isAAEnabled()) {
                Graphics2D g2d = (Graphics2D) aGraphics;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            super.paintComponent(aGraphics);
        }

        /**
         * This method is called when the locale of the aMan is changed.
         */
        public void onChangeLocale(ResourceManager aMan) {
            if (!tradeDesk.getServerTime().equals(ServerTime.UNKNOWN)) {
                mText = aMan.getString("IDS_SERVER_TIME");
                String sTime = Utils.format(
                		tradeDesk.getServerTime(), aMan.getString("IDS_SERVER_TIME_FORMAT"));
                //if time is changed
                setText(mText + " " + sTime);
                mTime = sTime;
                updateWidth();
            }
        }

        /**
         * Starts timer thread.
         */
        public void startTimer() {
            //sets localized text
            mText = resourceManager.getString("IDS_SERVER_TIME");
            mFirstTick = true;
            mFormatter = new SimpleDateFormat(resourceManager.getString("IDS_SERVER_TIME_FORMAT"));
            TimeZone tz = tradeDesk.getTradingServerSession().getTimeZone();
            if (tz != null) {
                mFormatter.setTimeZone(tz);
            }
            mServerTimeListener = new IServerTimeListener() {
                public void timeUpdated(ServerTime aTime) {
                    String sTime = mFormatter.format(aTime);
                    //if time is changed
                    if (!sTime.equals(mTime)) {
                        mTextUpdater.setTimeString(sTime);
                        EventQueue.invokeLater(mTextUpdater);
                    }
                }
            };
            try {
                tradeDesk.addServerTimeListener(mServerTimeListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Stops timer thread. Returns when the thread is finished.
         */
        public void stopTimer() {
            setText("");
            IServerTimeListener tmp = mServerTimeListener;
            mServerTimeListener = null;
            if (tmp != null) {
                try {
                    tradeDesk.removeServerTimeListener(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Updates width of components.
         */
        protected void updateWidth() {
            try {
                Dimension oldSize = getSize();
                String text = getText();
                if (text != null) {
                    FontMetrics fm = getGraphics().getFontMetrics(getFont());
                    int width = fm.stringWidth(text);
                    setSize(new Dimension(width + 15, (int) oldSize.getHeight()));
                    setPreferredSize(new Dimension(width + 15, (int) oldSize.getHeight()));
                }
            } catch (Exception e) {
                //
            }
        }

        private class TextUpdater implements Runnable {
            private String mTimeString;

            public void run() {
                setText(mText + " " + mTimeString);
                mTime = mTimeString;
                if (mFirstTick) {
                    updateWidth();
                }
                mFirstTick = false;
            }

            void setTimeString(String aTime) {
                mTimeString = aTime;
            }
        }
    }
}