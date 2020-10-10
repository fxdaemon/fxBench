/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/processors/TradingSessionStatusProcessor.java#3 $
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
 * Created: Nov 13, 2006 11:02:16 AM
 *
 * $History: $
 * 05/11/2007   Andre Mermegas: BASE_UNIT_SIZE instead of FRACTION_SIZE
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.messaging.ITransportable;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.ui.panel.SymbolPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Enumeration;

/**
 *
 */
public class TradingSessionStatusProcessor implements IProcessor {
    private final Log mLogger = LogFactory.getLog(TradingSessionStatusProcessor.class);

    public void process(ITransportable aTransportable) {
    	TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
        TradingSessionStatus tss = (TradingSessionStatus) aTransportable;
        FxcmServerSession tradingServerSession = (FxcmServerSession)tradeDesk.getTradingServerSession();
        tradingServerSession.setTradingSessionStatus(tss);
        
        tradeDesk.syncServerTime(tss.getTransactTime().toDate(), false);
        if (tss.getRequestID() != null) {
            Enumeration enumeration = tss.getSecurities();
            while (enumeration.hasMoreElements()) {
                TradingSecurity ts = (TradingSecurity) enumeration.nextElement();
                try {
                    if (tradeDesk.getOffer(ts.getSymbol()) == null) {
                        TOffer rate = new TOffer(SymbolPanel.getFieldDefStub()); 
//                        rate.setOpenAsk(0);
//                        rate.setOpenBid(0);
                        rate.setIntrB(0);
                        rate.setIntrS(0);
//                        rate.setTradable(false);
                        rate.setAsk(0);
                        rate.setBid(0);
//                        rate.setHighBuyPrice(0);
//                        rate.setLowBuyPrice(0);
                        rate.setSymbol(ts.getSymbol());
//                        rate.setProduct(ts.getProduct());
//                        rate.setContractMultiplier(ts.getContractMultiplier());
//                        rate.setFXCMCondDistStop(ts.getFXCMCondDistStop());
//                        rate.setFXCMCondDistLimit(ts.getFXCMCondDistLimit());
//                        rate.setFXCMCondDistEntryLimit(ts.getFXCMCondDistEntryLimit());
//                        rate.setFXCMCondDistEntryStop(ts.getFXCMCondDistEntryStop());
//                        rate.setFXCMMaxQuantity(ts.getFXCMMaxQuantity());
//                        rate.setFXCMMinQuantity(ts.getFXCMMinQuantity());
//                        rate.setFXCMTradingStatus(ts.getFXCMTradingStatus());
                        rate.setContractCurrency(ts.getCurrency());
                        if (ts.getProduct() == 0 || ts.getProduct() == IFixDefs.PRODUCT_CURRENCY) {
                            if (tss.getParameterValue("BASE_UNIT_SIZE") != null) {
//                                rate.setContractSize(Integer.parseInt(tss.getParameterValue("BASE_UNIT_SIZE")));
                            }
                        } else {
//                            rate.setContractSize(ts.getFactor());
                        }
                        rate.setTime(new Date());
                        rate.setOfferId(String.valueOf(ts.getFXCMSymID()));
                        if (tradingServerSession.getUserKind() == IFixDefs.FXCM_SESSION_TYPE_TRADER) {
                            if (ts.getFXCMSubscriptionStatus() == null || IFixDefs.FXCMSUBSCRIPTIONSTATUS_SUBSCRIBE.equals(ts.getFXCMSubscriptionStatus())) {
                                rate.setSubscribed(true);
                            } else {
                                rate.setSubscribed(false);
                            }
                        } else {
                            rate.setSubscribed(true);
                        }
                        mLogger.debug("client: adding rate = " + rate.getSymbol());
                        tradeDesk.addOffer(rate);
                    } else {
                        mLogger.debug("client: skipping rate = " + ts.getSymbol());
                    }
                } catch (Exception aException) {
                    aException.printStackTrace();
                }
            }
        }
        tradingServerSession.doneProcessing();
    }
}
