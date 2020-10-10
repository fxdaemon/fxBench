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
 * Created: Nov 13, 2006 11:13:58 AM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.pretrade.EMail;
import com.fxcm.messaging.ITransportable;

import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TMessage;
import org.fxbench.trader.dialog.MessageDialog;
import org.fxbench.ui.panel.MessagePanel;
import org.fxbench.BenchApp;

import javax.swing.SwingUtilities;

public class EMailProcessor implements IProcessor {
    public void process(final ITransportable aTransportable) {
        System.out.println("client: inc EMail = " + aTransportable);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EMail mail = (EMail) aTransportable;                
                TMessage m = new TMessage(
                				mail.getOrigTime().toDate(),
                                mail.getFrom(),
                                mail.getSubject(),
                                mail.getText(),
                                MessagePanel.getFieldDefStub());
                BenchApp.getInst().getTradeDesk().addMessage(m);
                MessageDialog dialog = new MessageDialog(BenchApp.getInst().getMainFrame());
                dialog.setMessage(m);
                dialog.showModal();
            }
        });
    }
}
