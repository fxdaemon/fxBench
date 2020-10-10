/*
 * $Header:$
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
 * Created: Nov 13, 2006 10:58:21 AM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.other.BusinessMessageReject;
import com.fxcm.messaging.ITransportable;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.trader.TradingServerSession;
import org.fxbench.trader.fxcm.OraCodeFactory;
import org.fxbench.ui.BenchFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 */
public class BusinessMessageRejectProcessor implements IProcessor {
    private final Log mLogger = LogFactory.getLog(BusinessMessageRejectProcessor.class);

    public void process(ITransportable aTransportable) {
    	TradingServerSession aTradingServerSession = BenchApp.getInst().getTradeDesk().getTradingServerSession();
        final BusinessMessageReject aBmr = (BusinessMessageReject) aTransportable;
        mLogger.debug("client inc: business message request = " + aBmr);
        if (aTradingServerSession.containRequestId(aBmr.getBusinessRejectRefID())) {
        	aTradingServerSession.removeRequestId(aBmr.getBusinessRejectRefID());
            aTradingServerSession.doneProcessing();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	BenchFrame mainFrame = BenchApp.getInst().getMainFrame();
                    JOptionPane.showMessageDialog(mainFrame,
                                                  OraCodeFactory.toMessage(aBmr.getText()),
                                                  "Problem with your request..",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
