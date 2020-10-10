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
 * Created: Nov 13, 2006 11:06:35 AM
 *
 * $History: $
 * 08/04/2008   Andre Mermegas: workaround for backoffice 1 baseunitsize bug, set account hedging on both
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.messaging.ITransportable;

import org.fxbench.BenchApp;
import org.fxbench.entity.TAccount;
import org.fxbench.desk.TradeDesk;
import org.fxbench.trader.TradingServerSession;
import org.fxbench.ui.panel.AccountPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollateralReportProcessor implements IProcessor {
    private final Log mLogger = LogFactory.getLog(CollateralReportProcessor.class);

    public void process(ITransportable aTransportable) {
        TradingServerSession session = BenchApp.getInst().getTradeDesk().getTradingServerSession();
        CollateralReport collateralReport = (CollateralReport) aTransportable;
        BenchApp.getInst().getTradeDesk().getTradingServerSession().setTradingSessionID(
        		collateralReport.getTradingSessionSubID());
        TAccount acct = BenchApp.getInst().getTradeDesk().getAccount(collateralReport.getAccount());
        mLogger.debug("client: inc collateral report = " + collateralReport);
        if (session.containRequestId(collateralReport.getCollInquiryID()) && acct == null || acct == null) {
            acct = new TAccount(AccountPanel.getFieldDefStub());
            int acctType = collateralReport.getParties().getFXCMAcctType();
            if (acctType == IFixDefs.FXCM_ACCT_TYPE_CONTROLLED) {
                acct.setInvisible(true);
            }
            if (acctType == IFixDefs.FXCM_ACCT_TYPE_CLEARING) {
                acct.setLocked(true);
            }
            //xxx workaround for back backoffice bug
            if (collateralReport.getQuantity() == 1) {
                acct.setBaseUnitSize(1000);
            } else {
                acct.setBaseUnitSize(collateralReport.getQuantity());
            }
            acct.setAccountId(String.valueOf(collateralReport.getParties().getFXCMAcctID()));
            acct.setAccountName(collateralReport.getAccount());
            acct.setBalance(collateralReport.getCashOutstanding());
            acct.setMarginReq(collateralReport.getMarginRatio());
            acct.setUsedMargin(collateralReport.getFXCMUsedMargin());
//            acct.setBatch(collateralReport.getTotNumReports() > 0);
//            acct.setLast(collateralReport.isLastRptRequested());
            if ("Y".equals(collateralReport.getFXCMMarginCall())) {
                acct.setUnderMarginCall(true);
            } else {
                acct.setUnderMarginCall(false);
            }
            acct.setHedging(collateralReport.getParties().getFXCMPositionMaintenance());
            BenchApp.getInst().getTradeDesk().addAccount(acct);
            if (collateralReport.isLastRptRequested()) {
            	session.removeRequestId(collateralReport.getCollInquiryID());
                session.doneProcessing();
            }
        } else {
            //if its not a part of a mass request and we dont have it in our desk we assume its an update
            acct.setBalance(collateralReport.getCashOutstanding());
            acct.setMarginReq(collateralReport.getMarginRatio());
            acct.setUsedMargin(collateralReport.getFXCMUsedMargin());
            acct.setBaseUnitSize(collateralReport.getQuantity());
            if ("Y".equals(collateralReport.getFXCMMarginCall())) {
                acct.setUnderMarginCall(true);
            } else {
                acct.setUnderMarginCall(false);
            }
            acct.setHedging(collateralReport.getParties().getFXCMPositionMaintenance());
            BenchApp.getInst().getTradeDesk().updateAccount(acct);
        }
    }
}
