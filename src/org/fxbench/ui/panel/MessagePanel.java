/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/frames/MessagesFrame.java#1 $
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
 * Author: Andre Mermegas
 * Created: Jul 6, 2007 11:52:43 AM
 *
 * $History: $
 */
package org.fxbench.ui.panel;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.fxbench.BenchApp;
import org.fxbench.entity.TMessage;
import org.fxbench.entity.Field.FieldType;
import org.fxbench.entity.TMessage.FieldDef;
import org.fxbench.trader.dialog.MessageDialog;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.auxi.PanelTableModel;
import org.fxbench.util.FieldDefStub;
import org.fxbench.util.ResourceManager;
import org.fxbench.util.properties.PropertySheet;
import org.fxbench.util.signal.SignalVector;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

/**
 */
public class MessagePanel<E> extends TablePanel<E>
{
    public static final String NAME = "Messages";
    public static final String TITLE = "Message";
	public static final String TOOLTIP = "Message";
	
	private static final FieldDef[] FIELDS_DEF = {
		FieldDef.MESSAGE_TIME, FieldDef.MESSAGE_FROM, FieldDef.MESSAGE_SUBJECT};
	private static final FieldDefStub<FieldDef> fieldDefStub = new FieldDefStub<FieldDef>(FIELDS_DEF, FieldDef.class);

    public MessagePanel(BenchFrame mainFrame, ResourceManager aMan) {
    	super(mainFrame, aMan);
        setCurSortColumn(fieldDefStub.getFieldNo(FieldDef.MESSAGE_TIME));

    	//sets icon to internal frame
        URL iconUrl = getResourceManager().getResource("ID_MESSAGES_FRAME_ICON");
        if (iconUrl != null) {
        	barIcon = new ImageIcon(iconUrl);
        }

        setPreferences();
        mCellRenderer = getDefaultCellRenderer();
        mHeaderRenderer = getDefaultHeaderRenderer();
        setRenderers(mCellRenderer, mHeaderRenderer);

        getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent aEvent) {
                doSelect(aEvent);
                if (aEvent.getClickCount() == 2) {
                    JTable table = (JTable) aEvent.getComponent();
                    int row = table.rowAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                    String key = (String) table.getModel().getValueAt(row, fieldDefStub.getFieldNo(FieldDef.MESSAGE_TIME));
                    TMessage m = (TMessage) getSignalVector().get(key);
                    if (m != null) {
                        MessageDialog md = new MessageDialog(BenchApp.getInst().getMainFrame());
                        md.setMessage(m);
                        md.showModal();
                    }
                }
            }

            private void doSelect(MouseEvent aEvent) {
                JTable table = (JTable) aEvent.getComponent();
                int currentRow = table.rowAtPoint(new Point(aEvent.getX(), aEvent.getY()));
                if (aEvent.getButton() == MouseEvent.BUTTON3 || aEvent.isPopupTrigger()) {
                    if (currentRow != table.getSelectedRow()) {
                        table.getSelectionModel().setSelectionInterval(currentRow, currentRow);
                    }
                }
            }
        });

        fireSorting();
    }
    
    @Override
	protected String id() {
		return NAME;
	}

	@Override
	protected String title() {
		return TITLE;
	}

	@Override
	protected String tooltip() {
		return TOOLTIP;
	}

    public static FieldDefStub<FieldDef> getFieldDefStub() {
		return fieldDefStub;
	}
    
    @Override
	protected FieldType getColumnType(int column) {
		return column >=0 && column < FIELDS_DEF.length ?
				FIELDS_DEF[column].getFieldType() : FieldType.STRING;
	}
    
    @Override
	protected int getColumnAlignment(int column) {
		return column >=0 && column < FIELDS_DEF.length ?
				FIELDS_DEF[column].getFiledAlignment() : SwingConstants.LEFT;
	}
    
    @Override
	public void propertyUpdated(List<PropertySheet> aChangings) {
		super.propertyUpdated(aChangings);
		setPreferences();
	}

    @Override
    protected String getLocalizedTitle(ResourceManager aResourceMan) {
        if (aResourceMan != null) {
//            UserPreferences pref = UserPreferences.getUserPreferences(getTradeDesk().getUserName());
            int messageSize = getSignalVector().size();
            StringBuffer titleBuffer = new StringBuffer();
            titleBuffer.append(aResourceMan.getString("IDS_MESSAGES_TITLE"));
            if (messageSize > 0) {
                titleBuffer.append(" (").append(messageSize).append(")");
            }
            return titleBuffer.toString();
        }
        mLogger.debug("Error: MessagesFrame.getLocalizedTitle: aResourceMan is null");
        return null;
    }

    @Override
    protected SignalVector getSignalVector() {
        return getTradeDesk().getMessages();
    }

    @Override
    public PanelTableModel getTableModel(ResourceManager aResourceMan) {
        if (mTableModel == null) {
            mTableModel = new MessagesModel(aResourceMan);
        }
        return mTableModel;
    }

    @Override
    protected String getTableName() {
        return "message";
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    @Override
    public void onClose() {
    	super.onClose();
    }

    private class MessagesModel extends PanelTableModel {
        private MessagesModel(ResourceManager aMan) {
        	super(aMan, fieldDefStub.getFiledNameArray(), getSignalVector());
        }
    }
}
