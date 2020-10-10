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

import org.fxbench.desk.Accounts;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TAccount;


/**
 * Impementation of combobox with accounts.
 * Creation date (9/27/2003 4:07 PM)
 */
public class SimpleAccountsComboBox extends BusinessDataComboBox {
    /**
     * Combobox model.
     */
    private AbstractComboBoxModel mModel;

    /**
     * Returns combobox model.
     */
    @Override
    public AbstractComboBoxModel getComboBoxModel() {
        if (mModel == null) {
            mModel = new Model();
        }
        return mModel;
    }

    /**
     * Returns id of selectid accont.
     */
    public String getSelectedAccount() {
        Object selectedItem = getSelectedItem();
        return selectedItem == null ? null : selectedItem.toString();
    }

    /**
     * Sets selected account.
     *
     * @param aAccount id of the account
     */
    public void selectAccount(String aAccount) {
        if (aAccount != null) {
            try {
                Accounts accounts = getTradeDesk().getAccounts();
                TAccount account = accounts.getAccount(aAccount);
                setSelectedIndex(accounts.indexOf(account));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setSelectedIndex(-1);
        }
    }

    /**
     * Subscribes to receiving of business data.
     */
    @Override
    public void subscribeBusinessData() throws Exception {
    }

    /**
     * Concrete implementation of AbstractComboBoxModel.
     */
    private class Model extends AbstractComboBoxModel {
        /**
         * Returns element at combo box by index.
         *
         * @param aIndex index of element
         */
        public Object getElementAt(int aIndex) {
            try {
                TAccount account = (TAccount) getTradeDesk().getAccounts().get(aIndex);
                boolean enabled = !account.isUnderMarginCall() && !account.isLocked();
                return newItem(aIndex, account.getAccountName(), enabled);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Returns size of combobox.
         */
        public int getSize() {
            int size = 0;
            try {
                size = getTradeDesk().getAccounts().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }
    }
}
