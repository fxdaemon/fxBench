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
 *
 * 05/18/2006   Andre Mermegas: extends ABaseDialog
 */
package org.fxbench.trader.dialog;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.trader.Connection;
import org.fxbench.trader.ConnectionsManager;
import org.fxbench.trader.IConnectionManagerListener;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.fxcm.LoginRequest;
import org.fxbench.ui.auxi.RiverLayout;
import org.fxbench.ui.auxi.UIManager;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.Utils;
import org.fxbench.util.properties.PropertyManager;
import org.fxbench.util.properties.PropertySheet;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Class LoginDialog.<br>
 * <br>
 * It is responsible for input name and password of client
 * <ul>
 * <li> creating login dialog; </li>
 * <li> getting login parameters. </li>
 * </ul>
 * <br>
 * Creation date (9/5/2003 11:01 AM)
 */
public class LoginDialog extends BaseDialog {
	private ResourceManager resourceManager;
    //Login dialog exit code. It's set from closeDialog.
    private int mExitCode;
    
    /**
     * Main panel.
     */
    private LoginDialogPane mLoginDialogPane;

    /**
     * Constructor.
     *
     * @param aOwner frame owner
     */
    public LoginDialog(Frame aOwner) {
        super(aOwner);
        resourceManager = getTradeDesk().getTradingServerSession().getResourceManager();
        mLoginDialogPane = new LoginDialogPane(this);
        getContentPane().add(mLoginDialogPane);
    }

    /**
     * Closes the dialog.
     *
     * @param aExitCode code of exiting
     */
    public void closeDialog(int aExitCode) {
        mExitCode = aExitCode;
        super.closeDialog(aExitCode);
    }

    /**
     * @return exit code
     */
    public int getExitCode() {
        return mExitCode;
    }

    /**
	 * @return the resourceManager
	 */
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
     * @return login parameters
     */
    public IRequester getLoginParameters() {
        return mLoginDialogPane.getLoginParameters();
    }

    /**
     * showModal. <br>
     * Assign behaviour of window, subscribe on changing the openning positions,
     * show window and process its closing.
     *
     * @return Code of terminations
     *
     * @throws NumberFormatException when input rate is incorrect (unlikely).
     */
    public int showModal() {
    	ConnectionsManager.register(mLoginDialogPane);
        mExitCode = JOptionPane.CLOSED_OPTION;
        if (resourceManager != null) {
            setTitle(resourceManager.getString("IDS_LOGIN_DIALOG_TITLE"));
        }
        setModal(true);
        pack();
        // if we have read and entered a previous username, put the focus into the password field
        String userName = mLoginDialogPane.getUserNameTextField().getText();
        if (userName != null && userName.trim().length() != 0) {
            mLoginDialogPane.getPasswordTextField().requestFocus();
        }

        //sets minimal sizes for components
        Dimension dim = mLoginDialogPane.getSize();
        mLoginDialogPane.setMinimumSize(dim);

        //setResizable(false);
        setLocationRelativeTo(getOwner());
        setVisible(true);
        ConnectionsManager.unregister(mLoginDialogPane);
        return mExitCode;
    }

    private class LoginDialogPane extends JComponent implements IConnectionManagerListener {
        private JButton mCancelButton;
        private JLabel mCapslockLabel;
        private JComboBox mConnectionComboBox;
        private JLabel mConnectionNameLabel;
        private LoginDialog mDialog;
        private JButton mLoginButton;
        private JLabel mPasswordLabel;
        private JPasswordField mPasswordTextField;
        private JButton mSettingsButton;
        private JLabel mUserNameLabel;
        private JTextField mUserNameTextField;

        /**
         * @param aLoginDialog dialog
         */
        public LoginDialogPane(LoginDialog aLoginDialog) {
            mDialog = aLoginDialog;
            mUserNameLabel = UIManager.getInst().createLabel(mDialog.getResourceManager().getString("IDS_USER_NAME_LABEL"));
            mPasswordLabel = UIManager.getInst().createLabel(mDialog.getResourceManager().getString("IDS_USER_PWD_LABEL"));
            mSettingsButton = UIManager.getInst().createButton(mDialog.getResourceManager().getString("IDS_SETTINGS"));
            mCapslockLabel = UIManager.getInst().createLabel(">>> caps lock is on <<<");
            mCapslockLabel.setForeground(Color.RED);
            mSettingsButton.setMnemonic('s');
            mSettingsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    ConnectionManagerDialog mcd = new ConnectionManagerDialog(BenchApp.getInst().getMainFrame());
                    mcd.showModal();
                }
            });
            mCancelButton = UIManager.getInst().createButton(mDialog.getResourceManager().getString("IDS_BUTTON_CANCEL_TITLE"));
            mCancelButton.setMnemonic('c');
            mLoginButton = UIManager.getInst().createButton(mDialog.getResourceManager().getString("IDS_LOGIN_OK_TITLE"));
            mLoginButton.setMnemonic('l');
            mLoginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    if (mDialog != null) {
                        if (getPasswordTextField().getPassword().length == 0) {
                            JOptionPane.showMessageDialog(BenchApp.getInst().getMainFrame(),
                                                          "Error: no password supplied",
                                                          "Error: no password supplied",
                                                          JOptionPane.ERROR_MESSAGE);
                        } else {
                            closeDialog(JOptionPane.OK_OPTION);
                        }
                    }
                }
            });

            mDialog.getRootPane().setDefaultButton(mLoginButton);
            mCancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent aEvent) {
                    closeDialog(JOptionPane.CANCEL_OPTION);
                }
            });
            //sets for exting by escape
            mCancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                                                             "Exit");
            mCancelButton.getActionMap().put("Exit", new AbstractAction() {
                public void actionPerformed(ActionEvent aEvent) {
                    closeDialog(JOptionPane.CANCEL_OPTION);
                }
            });
            Font font = new Font(mDialog.getResourceManager().getString("IDS_LOGIN_DIALOG_FONT"), Font.PLAIN, 12);
            mUserNameLabel.setFont(font);
            mPasswordLabel.setFont(font);
            mLoginButton.setFont(font);
            mSettingsButton.setFont(font);
            mCancelButton.setFont(font);
            mCapslockLabel.setFont(font);
            mUserNameTextField = UIManager.getInst().createTextField();
            mPasswordTextField = UIManager.getInst().createPasswordField();
            mUserNameTextField.setFont(font);
            mPasswordTextField.setFont(font);
            mUserNameTextField.setColumns(10);
            mPasswordTextField.setColumns(10);
//            String sUserName = getTradeDesk().getUserName();
//            if (sUserName != null) {
//                mUserNameTextField.setText(sUserName);
//            }
            setDialogLayout();
        }

        public void closeDialog(int aExitCode) {
            mDialog.closeDialog(aExitCode);
        }

        /**
         * @return login parameters
         */
        public IRequester getLoginParameters() {
            String name = mUserNameTextField.getText();
            String connectionName = mConnectionComboBox.getSelectedItem().toString();
//            Connection cx = ConnectionsManager.getConnection(connectionName);
            return new LoginRequest(name, String.valueOf(mPasswordTextField.getPassword()), 
            		connectionName, ConnectionsManager.getUrl(connectionName));
        }

        /**
         * @return password text field
         */
        public JPasswordField getPasswordTextField() {
            return mPasswordTextField;
        }

        /**
         * @return username text field
         */
        public JTextField getUserNameTextField() {
            return mUserNameTextField;
        }

        private void initConnectionControls() {
            String connectionNameLabelText;
            String fontName = mDialog.getResourceManager().getString("IDS_LOGIN_DIALOG_FONT", "Default");
            Font font = new Font(fontName, Font.PLAIN, 12);
            try {
                ResourceManager manager = getTradeDesk().getTradingServerSession().getResourceManager();
                connectionNameLabelText = manager.getString("IDS_CONNECTION_NAME_LABEL", "Connection name:");
            } catch (Exception e) {
                throw new RuntimeException("Localization is not available");
            }
            mConnectionNameLabel = UIManager.getInst().createLabel(connectionNameLabelText);
            mConnectionNameLabel.setFont(font);
            mConnectionComboBox = new JComboBox();
            mConnectionComboBox.setFont(font);
            mUserNameTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent aEvent) {
                    mUserNameTextField.selectAll();
                }
            });
            mPasswordTextField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent aEvent) {
                    mPasswordTextField.selectAll();
                }
            });
            mPasswordTextField.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent aEvent) {
                    if (Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                        if (!mCapslockLabel.isShowing()) {
                            add("br center", mCapslockLabel);
                        }
                    } else {
                        if (mCapslockLabel.isShowing()) {
                            remove(mCapslockLabel);
                        }
                    }
                    mDialog.pack();
                }
            });
            mConnectionComboBox.setModel(new DefaultComboBoxModel(ConnectionsManager.getTerminals()));
            mConnectionComboBox.setFocusable(true);
            mConnectionComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent aEvent) {
                    String cx = (String) mConnectionComboBox.getSelectedItem();
//                    Connection fxcmcx = ConnectionsManager.getConnection(cx);
//                    if (fxcmcx != null) {
//                        mUserNameTextField.setText(fxcmcx.getUsername());
//                    }
                    mUserNameTextField.setText(ConnectionsManager.getUserName(cx));
                }
            });
            mConnectionComboBox.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent aEvent) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent aEvent) {
                    mPasswordTextField.setText("");
                    mPasswordTextField.requestFocus();
                }

                public void popupMenuCanceled(PopupMenuEvent aEvent) {
                }
            });
//            UserPreferences userPreferences = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//            String terminal = userPreferences.getString("Server.last.connected.terminal");
            String terminal = ConnectionsManager.getConnectedTerminal();
            if (terminal != null) {
                mConnectionComboBox.setSelectedItem(terminal);
            } else if (mConnectionComboBox.getItemCount() > 0) {
                mConnectionComboBox.setSelectedIndex(0);
            }
        }

        protected void setDialogLayout() {
            initConnectionControls();
            setLayout(new RiverLayout());

            add("left", mUserNameLabel);
            add("tab hfill", mUserNameTextField);

            add("br left", mPasswordLabel);
            add("tab hfill", mPasswordTextField);

            add("br left", mConnectionNameLabel);
            add("tab hfill", mConnectionComboBox);

            add("br center", mLoginButton);
            add("center", mSettingsButton);
            add("center", mCancelButton);
            Utils.setAllToBiggest(new JComponent[]{mLoginButton, mSettingsButton, mCancelButton});
        }
        
        public void updated() {
            try {
                mConnectionComboBox.setModel(new DefaultComboBoxModel(ConnectionsManager.getTerminals()));
//                UserPreferences userPreferences = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
//                String terminal = userPreferences.getString("Server.last.connected.terminal");
                String terminal = ConnectionsManager.getConnectedTerminal();
                if (terminal == null /*|| ConnectionsManager.getConnection(terminal) == null*/) {
                    if (mConnectionComboBox.getItemCount() > 0) {
                        mConnectionComboBox.setSelectedIndex(0);
                    }
                } else {
                    mConnectionComboBox.setSelectedItem(terminal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}