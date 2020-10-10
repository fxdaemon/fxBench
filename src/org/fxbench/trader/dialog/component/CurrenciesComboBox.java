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

import org.fxbench.desk.Offers;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOffer;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;


/**
 * Combobox with currencies.
 * <br>
 * Creation date (9/27/2003 6:58 PM)
 */
public class CurrenciesComboBox extends BusinessDataComboBox {
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
     * Returns selected currency.
     */
    public String getSelectedCurrency() {
        Object selectedItem = getSelectedItem();
        return selectedItem == null ? null : selectedItem.toString();
    }

    /**
     * Selects currency by ID.
     *
     * @param aCurrencyID id of the currency
     */
    public void selectCurrency(String aCurrencyID) {
        if (aCurrencyID != null) {
            try {
                Offers rates = getTradeDesk().getOffers();
                TOffer rate = rates.getOffer(aCurrencyID);
                int nItem = rates.indexOf(rate);
                setSelectedIndex(nItem);
                setStatusEnabled(rate.isTradable());
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
        getTradeDesk().getOffers().subscribe(this, SignalType.ADD);
        getTradeDesk().getOffers().subscribe(this, SignalType.CHANGE);
        getTradeDesk().getOffers().subscribe(this, SignalType.REMOVE);
    }

    /**
     * Calls when a Change Signal has come in
     */
        public boolean updateOnSignal(Signal aSignal, Item aItem) throws Exception {
        boolean rc = false;
        if (getSelectedIndex() == aItem.getIndex()) {
            if (isStatusEnabled() ^ aItem.isEnabled()) {
                setStatusEnabled(aItem.isEnabled());
                rc = true;
            }
        }
        return rc;
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
                TOffer rate = (TOffer) getTradeDesk().getOffers().get(aIndex);
                return newItem(aIndex, rate.getCurrency(), rate.isTradable());
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
                size = getTradeDesk().getOffers().size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }
    }
}
