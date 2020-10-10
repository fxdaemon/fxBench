/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/trader/ui/MessageBoxRunnable.java#1 $
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
 * Created: Aug 29, 2007 10:59:09 AM
 *
 * $History: $
 */
package org.fxbench.ui.auxi;

import org.fxbench.BenchApp;
import org.fxbench.trader.LiaisonException;

import javax.swing.JOptionPane;

/**
 */
public class MessageBoxRunnable implements Runnable {
    private LiaisonException mException;

    public MessageBoxRunnable(LiaisonException aException) {
        mException = aException;
    }

    public void run() {
        String message = mException.getLocalizedMessage();
        String title = BenchApp.getInst().getResourceManager().getString("IDS_MAINFRAME_SHORT_TITLE");
        JOptionPane.showMessageDialog(BenchApp.getInst().getMainFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }
}
