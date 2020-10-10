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
package org.fxbench.trader.dialog.component;

import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TAccount;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;

/**
 * Combobox with accounts.
 * <br>
 * Creation date (9/27/2003 4:07 PM)
 */
public class AccountsComboBox extends SimpleAccountsComboBox {
    /**
     * Selects accont by ID.
     *
     * @param aAccount id of the account
     */
    @Override
    public void selectAccount(String aAccount) {
        super.selectAccount(aAccount);
        boolean enabled = aAccount != null;
        if (enabled) {
            int index = getSelectedIndex();
            enabled = index >= 0;
            if (enabled) {
                try {
                    enabled = !((TAccount) getTradeDesk().getAccounts().get(index)).isUnderMarginCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (mDefaultActor != null) {
            mDefaultActor.setEnabled(enabled);
        }
    }

    /**
     * Subscribes to receiving of business data.
     */
    @Override
    public void subscribeBusinessData() throws Exception {
        getTradeDesk().getAccounts().subscribe(this, SignalType.ADD);
        getTradeDesk().getAccounts().subscribe(this, SignalType.CHANGE);
        getTradeDesk().getAccounts().subscribe(this, SignalType.REMOVE);
    }

    /**
     * Invokes on arriving of the signal.
     */
    public boolean updateOnSignal(Signal aSignal, Item aItem) throws Exception {
        //If there're no changes, don't redraw
        boolean rc = false;
        if (getSelectedIndex() == aItem.getIndex()) {
            if (isStatusEnabled() ^ aItem.isEnabled()) {
                setStatusEnabled(aItem.isEnabled());
                rc = true;
            }
        }
        return rc;
    }
}
