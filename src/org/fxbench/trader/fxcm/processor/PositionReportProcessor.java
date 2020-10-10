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
 * Created: Nov 13, 2006 11:09:40 AM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.SideFactory;
import com.fxcm.fix.posttrade.ClosedPositionReport;
import com.fxcm.fix.posttrade.PositionReport;
import com.fxcm.messaging.ITransportable;
import com.fxcm.messaging.util.ThreadSafeNumberFormat;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TPosition;
import org.fxbench.entity.TPosition.BnS;
import org.fxbench.trader.TradingServerSession;
import org.fxbench.ui.panel.ClosedPositionPanel;
import org.fxbench.ui.panel.OpenPositionPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DecimalFormat;

public class PositionReportProcessor implements IProcessor {
    private final Log mLogger = LogFactory.getLog(PositionReportProcessor.class);
    private static final DecimalFormat FORMAT = new ThreadSafeNumberFormat().getInstance();

    public void process(ITransportable aTransportable) {
        if (aTransportable instanceof ClosedPositionReport) {
            processClose(aTransportable);
        } else {
            processOpen(aTransportable);
        }
    }

    public void processOpen(ITransportable aTransportable) {
    	TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
        TradingServerSession aTradingServerSession = tradeDesk.getTradingServerSession();
        PositionReport aPr = (PositionReport) aTransportable;
//        tradeDesk.syncServerTime(aPr.getTransactTime().toDate());
        mLogger.debug("client: inc position report = " + aPr);
        mLogger.debug("********************************************");
        mLogger.debug("Text                = " + aPr.getText());
        mLogger.debug("FXCMPosID           = " + aPr.getFXCMPosID());
        mLogger.debug("RefFXCMPosID        = " + aPr.getFXCMPosIDRef());
        mLogger.debug("PositionQty         = " + FORMAT.format(aPr.getPositionQty().getQty()));
        mLogger.debug("OrderID             = " + aPr.getOrderID());
        mLogger.debug("SecondaryClOrdID    = " + aPr.getSecondaryClOrdID());
        mLogger.debug("SettlePrice         = " + aPr.getSettlPrice());
        mLogger.debug("FXCMPosOpenTime     = " + aPr.getFXCMPosOpenTime().toString());
        mLogger.debug("TransactTime        = " + aPr.getTransactTime().toString());
        mLogger.debug("********************************************");
        if (aPr.getTotalNumPosReports() > 0 || aPr.getPosReqID() == null) {
            if (aPr.getAccount() == null) {
                mLogger.debug("updating interest = " + aPr.getFXCMPosInterest());
                TPosition position = tradeDesk.getPositions().getPosition(aPr.getFXCMPosID());
                mLogger.debug("old position.getInterest() = " + position.getInterest());
                position.setInterest(aPr.getFXCMPosInterest());
                mLogger.debug("new position.getInterest() = " + position.getInterest());
                return;
            }
            TPosition position = tradeDesk.getOpenPosition(aPr.getFXCMPosID());
            if (position == null) {
                position = new TPosition(
                	TPosition.Stage.Open, OpenPositionPanel.getFieldDefStub());
            }
            position.setAccountId(String.valueOf(aPr.getParties().getFXCMAcctID()));
            position.setAccountId(aPr.getAccount());
            position.setAmount((long) aPr.getPositionQty().getQty());
            try {
                position.setSymbol(aPr.getInstrument().getSymbol());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            position.setCurrencyTradable(true);
            position.setOpen(aPr.getSettlPrice());
            position.setTradeID(String.valueOf(aPr.getFXCMPosID()));
            position.setOpenTime(aPr.getFXCMPosOpenTime().toDate());
//            position.setoQTXT(aPr.getSecondaryClOrdID());
            position.setInterest(aPr.getFXCMPosInterest());
            position.setCom(aPr.getFXCMPosCommission());
            position.setUsedMargin(aPr.getFXCMUsedMargin());
//            position.setBatch(aPr.getTotalNumPosReports() > 0);
//            position.setLast(aPr.isLastRptRequested());
            if (aPr.getPositionQty().getSide() == SideFactory.BUY) {
                position.setBS(BnS.BUY);
            } else {
                position.setBS(BnS.SELL);
            }
            if (tradeDesk.getOpenPosition(String.valueOf(aPr.getFXCMPosID())) != null) {
                tradeDesk.updateOpenPosition(position);
            } else {
                tradeDesk.addOpenPosition(position);
            }
        }
        if (aPr.isLastRptRequested() && aTradingServerSession.containRequestId(aPr.getPosReqID())) {
        	aTradingServerSession.removeRequestId(aPr.getPosReqID());
            aTradingServerSession.doneProcessing();
        }
    }

    public void processClose(ITransportable aTransportable) {
    	TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
        TradingServerSession aTradingServerSession = tradeDesk.getTradingServerSession();
        ClosedPositionReport aCpr = (ClosedPositionReport) aTransportable;
        mLogger.debug("client: inc closed position report = " + aCpr);
        mLogger.debug("********************************************");
        mLogger.debug("Text                         = " + aCpr.getText());
        mLogger.debug("PositionQty                  = " + FORMAT.format(aCpr.getPositionQty().getQty()));
        mLogger.debug("FXCMPosID                    = " + aCpr.getFXCMPosID());
        mLogger.debug("FXCMPosIDRef                 = " + aCpr.getFXCMPosIDRef());
        mLogger.debug("OrderID                      = " + aCpr.getOrderID());
        mLogger.debug("FXCMCloseOrderID             = " + aCpr.getFXCMCloseOrderID());
        mLogger.debug("FXCMCloseClOrdID             = " + aCpr.getFXCMCloseClOrdID());
        mLogger.debug("SecondaryClOrdID             = " + aCpr.getSecondaryClOrdID());
        mLogger.debug("FXCMCloseSecondaryClOrdID    = " + aCpr.getFXCMCloseSecondaryClOrdID());
        mLogger.debug("SettlPrice                   = " + aCpr.getSettlPrice());
        mLogger.debug("FXCMCloseSettlPrice          = " + aCpr.getFXCMCloseSettlPrice());
        mLogger.debug("TransactTime                 = " + aCpr.getTransactTime().toString());
        mLogger.debug("FXCMPosOpenTime              = " + aCpr.getFXCMPosOpenTime().toString());
        mLogger.debug("FXCMPosCloseTime             = " + aCpr.getFXCMPosCloseTime().toString());
        mLogger.debug("********************************************");
        tradeDesk.removeOpenPosition(String.valueOf(aCpr.getFXCMPosID()));
        if (aCpr.getTotalNumPosReports() > 0 || aCpr.getPosReqID() == null) {
            TPosition position = new TPosition(
            	TPosition.Stage.Closed, ClosedPositionPanel.getFieldDefStub());
            position.setAccountId(String.valueOf(aCpr.getParties().getFXCMAcctID()));
            position.setAccountId(aCpr.getAccount());
            position.setAmount((long) aCpr.getPositionQty().getQty());
            try {
                position.setSymbol(aCpr.getInstrument().getSymbol());
            } catch (NotDefinedException e) {
                e.printStackTrace();
            }
//            position.setCurrencyTradable(true);
            position.setOpen(aCpr.getSettlPrice());
            position.setClose(aCpr.getFXCMCloseSettlPrice());            
            position.setGrossPL(aCpr.getFXCMPosClosePNL());
            position.setNetPL(aCpr.getFXCMPosClosePNL() + aCpr.getFXCMPosCommission() + aCpr.getFXCMPosInterest());
            position.setTradeID(String.valueOf(aCpr.getFXCMPosID()));
            position.setOpenTime(aCpr.getFXCMPosOpenTime().toDate());
            position.setCloseTime(aCpr.getFXCMPosCloseTime().toDate());
            position.setInterest(aCpr.getFXCMPosInterest());
            position.setUsedMargin(aCpr.getFXCMUsedMargin());
            position.setCom(aCpr.getFXCMPosCommission());
//            position.setoQTXT(aCpr.getSecondaryClOrdID());
//            position.setcQTXT(aCpr.getFXCMCloseSecondaryClOrdID());
            if (aCpr.getPositionQty().getSide() == SideFactory.BUY) {
                position.setBS(BnS.BUY);
            } else {
                position.setBS(BnS.SELL);
            }
            position.resetPl(tradeDesk.getTradingServerSession().getPointSize(position.getSymbol()));
            tradeDesk.addClosedPosition(position);
        }
        if (aCpr.isLastRptRequested() && aTradingServerSession.containRequestId(aCpr.getPosReqID())) {
        	aTradingServerSession.removeRequestId(aCpr.getPosReqID());
            aTradingServerSession.doneProcessing();
        }
    }
}
