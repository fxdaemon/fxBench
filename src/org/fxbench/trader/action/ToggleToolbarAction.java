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
package org.fxbench.trader.action;

import org.fxbench.BenchApp;
import org.fxbench.util.properties.PropertyManager;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * @author Andre Mermegas
 *         Date: Jan 13, 2006
 *         Time: 11:49:07 AM
 */
public class ToggleToolbarAction extends AbstractAction
{
//    public static final String HIDE_TOGGLE_TOOLBAR = "toggle.toolbar";
    public static final String HIDE_TOGGLE_TOOLBAR = "preferences.frame.toolbar";

    public void actionPerformed(ActionEvent e) {
//        UserPreferences uiPrefs = UserPreferences.getUserPreferences(TradeDesk.getInst().getUserName());
        if (BenchApp.getInst().getMainFrame().getBenchPanel().isVisible()) {
//            uiPrefs.set(HIDE_TOGGLE_TOOLBAR, true);
        	PropertyManager.getInstance().setProperty(HIDE_TOGGLE_TOOLBAR, true);
            BenchApp.getInst().getMainFrame().getBenchPanel().setToolBarVisible(false);
        } else {
//            uiPrefs.set(HIDE_TOGGLE_TOOLBAR, false);
        	PropertyManager.getInstance().setProperty(HIDE_TOGGLE_TOOLBAR, false);
            BenchApp.getInst().getMainFrame().getBenchPanel().setToolBarVisible(true);
        }
    }
}