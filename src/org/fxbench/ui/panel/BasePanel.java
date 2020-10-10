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
package org.fxbench.ui.panel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.ui.BenchFrame;
import org.fxbench.ui.docking.dockable.DefaultDockable;
import org.fxbench.ui.docking.dockable.Dockable;
import org.fxbench.ui.docking.dockable.DockableState;
import org.fxbench.ui.docking.dockable.StateActionDockable;
import org.fxbench.ui.docking.dockable.action.DefaultDockableStateActionFactory;
import org.fxbench.ui.docking.event.DockingEvent;
import org.fxbench.ui.docking.event.DockingListener;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Base class for all child frames in trader application.
 */
public abstract class BasePanel extends JPanel implements DockingListener
{
	protected final Log mLogger = LogFactory.getLog(BasePanel.class);

    protected DefaultDockable defaultDockable;
    protected Icon barIcon = null;
    protected boolean restoreDockable;
    
    /**
     * reference to MainFrame
     */
    protected BenchFrame mMainFrame;

    /**
     * Constructor.
     *
     * @param aName      name of frame
     * @param aMainFrame main frame
     */
    public BasePanel(BenchFrame mainFrame) {
    	super(new BorderLayout());
        mMainFrame = mainFrame;
        restoreDockable = false;
    }
    
    protected TradeDesk getTradeDesk() {
    	return BenchApp.getInst().getTradeDesk();
    }

//    /**
//	 * @return the id
//	 */
//	public String getId() {
//		return id;
//	}
//
//	/**
//	 * @param id the id to set
//	 */
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	/**
//	 * @return the title
//	 */
//	public String getTitle() {
//		return title;
//	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
//		this.title = title;
		if (defaultDockable == null) {
		} else {
			defaultDockable.setTitle(title);
		}
	}

//	/**
//	 * @return the tooltip
//	 */
//	public String getTooltip() {
//		return tooltip;
//	}

	/**
	 * @param tooltip the tooltip to set
	 */
	public void setTooltip(String tooltip) {
//		this.tooltip = tooltip;
		if (defaultDockable == null) {
			super.setToolTipText(tooltip);
		} else {
			defaultDockable.setDescription(tooltip);
		}
	}

	/**
	 * @return the batIcon
	 */
	public Icon getBarIcon() {
		return barIcon;
	}

	/**
	 * @param batIcon the batIcon to set
	 */
	public void setBarIcon(Icon batIcon) {
		this.barIcon = batIcon;
	}

	/**
     * Returns reference to mainframe
     */
    public BenchFrame getMainFrame() {
        return mMainFrame;
    }

    /**
     * Sets reference to MainFrame
     */
    public void setMainFrame(BenchFrame aMainFrame) {
        mMainFrame = aMainFrame;
    }

    /**
     * Loads settings from the persistent preferences.
     */
    public void loadSettings() {
//        UserPreferences preferences;
//        try {
//            preferences = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return;
//        }

        //loads settings from preferences
//        int x = preferences.getInt("childframe." + getName() + ".x");
//        int y = preferences.getInt("childframe." + getName() + ".y");
//        int width = preferences.getInt("childframe." + getName() + ".width");
//        int height = preferences.getInt("childframe." + getName() + ".height");
//        boolean visible = preferences.getBoolean("childframe." + getName() + ".visible");
//
//        //sets position of the frame
//        setBounds(new Rectangle(x, y, width, height));
//
//        //sets visibility of the frame
//        setVisible(visible);
//
//        //sets state of the corresponding menu item
//        setMenuItemState(visible);
    }

    /* -- Public methods -- */

    /**
     * Saves settings to the persistent preferences.
     */
    public void saveSettings() {
//        UserPreferences preferences;
//
//        //Get PersistenceStorage
//        try {
//            preferences = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return;
//        }
//
//        //sets the settings to preferences
//        preferences.set("childframe." + getName() + ".x", getX());
//        preferences.set("childframe." + getName() + ".y", getY());
//        preferences.set("childframe." + getName() + ".width", getWidth());
//        preferences.set("childframe." + getName() + ".height", getHeight());
//        preferences.set("childframe." + getName() + ".visible", isVisible());
    }

    /**
     * Set state of the corresponding menu item.
     *
     * @param aVisible state of the visibility
     */
//    public void setMenuItemState(boolean aVisible) {
//        //finds menu by name
//        JMenu menu = mMainFrame.getBenchMenu().findMenu("Window");
//
//        //if third menu not initialised
//        if (menu == null) {
//            return;
//        }
//
//        //finds menu item by name
//        JMenuItem item = mMainFrame.getBenchMenu().findMenuItem(getName(), menu);
//
//        //if item not initialised
//        if (item == null) {
//            return;
//        }
//
//        //set item state
//        item.setSelected(aVisible);
//    }
    
    public Dockable createDockable() {
		// Create the dockable.
    	defaultDockable = new DefaultDockable(id(), this, title(), barIcon);
		// Add a description to the dockable. It will be displayed in the tool tip.
    	defaultDockable.setDescription(tooltip());    	
		return addActions(defaultDockable);
	}
	
	 private Dockable addActions(Dockable defaultDockable) {
		Dockable warpper = new StateActionDockable(defaultDockable, new DefaultDockableStateActionFactory(), new int[0]);
		int[] states = {DockableState.NORMAL, DockableState.MAXIMIZED, DockableState.CLOSED};
		warpper = new StateActionDockable(warpper, new DefaultDockableStateActionFactory(), states);
		warpper.addDockingListener(this);
		return warpper;
	}
	 
	 public void dockingWillChange(DockingEvent dockingEvent) {
	 }
	 
	 public void dockingChanged(DockingEvent dockingEvent) {
//		 DockableEvent dockableEvent = (DockableEvent)dockingEvent;
//		 if (dockableEvent.getDockable().getState() == DockableState.CLOSED) {
//			 onClose();
//		 }
		 if (dockingEvent.getDestinationDock() == null) {
			 if (!restoreDockable) {
				 onClose();
			 }
		 }
	 }
	 
	 public abstract void onClose();
	 protected abstract String id();
	 protected abstract String title();
	 protected abstract String tooltip();
}