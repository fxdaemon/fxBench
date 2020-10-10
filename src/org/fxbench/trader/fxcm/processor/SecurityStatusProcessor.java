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
 * Created: Nov 13, 2006 11:37:43 AM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.processor;

import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.SecurityStatus;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.messaging.ITransportable;

import org.fxbench.BenchApp;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class SecurityStatusProcessor implements IProcessor {
    private final Log mLogger = LogFactory.getLog(SecurityStatusProcessor.class);

    public void process(ITransportable aTransportable) {
        SecurityStatus aSecurityStatus = (SecurityStatus) aTransportable;
        mLogger.debug("received aSecurityStatus = " + aSecurityStatus);
        TradeDesk tradeDesk = BenchApp.getInst().getTradeDesk();
        FxcmServerSession session = (FxcmServerSession)tradeDesk.getTradingServerSession();
        TradingSessionStatus sessionStatus = session.getTradingSessionStatus();
        TradingSecurity incoming = aSecurityStatus.getTradingSecurity();
        try {
            if (incoming != null) {
                TradingSecurity existing = sessionStatus.getSecurity(incoming.getSymbol());
                if (existing != null) {
                    if (existing.fill(incoming)) {
                        
                        TOffer rate = tradeDesk.getOffer(existing.getSymbol());
                        if (rate != null) {
                            rate.setIntrB(existing.getFXCMSymInterestBuy());
                            rate.setIntrS(existing.getFXCMSymInterestSell());
//                            rate.setContractMultiplier(existing.getContractMultiplier());
//                            rate.setFXCMCondDistStop(existing.getFXCMCondDistStop());
//                            rate.setFXCMCondDistLimit(existing.getFXCMCondDistLimit());
//                            rate.setFXCMCondDistEntryLimit(existing.getFXCMCondDistEntryLimit());
//                            rate.setFXCMCondDistEntryStop(existing.getFXCMCondDistEntryStop());
//                            rate.setFXCMMaxQuantity(existing.getFXCMMaxQuantity());
//                            rate.setFXCMMinQuantity(existing.getFXCMMinQuantity());
//                            rate.setFXCMTradingStatus(existing.getFXCMTradingStatus());
                            if ((existing.getProduct() == 0 || existing.getProduct() == IFixDefs.PRODUCT_CURRENCY)
                                && existing.getFactor() != 0) {
//                                rate.setContractSize(existing.getFactor());
                            }
                            if (existing.getFXCMSubscriptionStatus() == null
                                || IFixDefs.FXCMSUBSCRIPTIONSTATUS_SUBSCRIBE.equals(existing.getFXCMSubscriptionStatus())) {
                                rate.setSubscribed(true);
                            } else {
                                rate.setSubscribed(false);
                            }
                            if (rate.isSubscribed()) {
                                MarketDataRequest mdr = new MarketDataRequest();
                                mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
                                mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_BIDASK);
                                TradingSecurity security = sessionStatus.getSecurity(rate.getSymbol());
                                mdr.addRelatedSymbol(security);
                                session.send(mdr);
                            }
                            tradeDesk.updateOffer(rate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            mLogger.error("problem filling update", e);
        }
    }
}
