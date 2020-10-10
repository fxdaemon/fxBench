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



import java.util.Enumeration;

import org.fxbench.desk.Orders;
import org.fxbench.desk.TradeDesk;
import org.fxbench.entity.TOrder;
import org.fxbench.util.signal.Signal;
import org.fxbench.util.signal.Signal.SignalType;

/**
 * Class represents combobox with orders ids.
 */
public class OrdersComboBox extends BusinessDataComboBox {
    /**
     * Instance of combo box model.
     */
    private AbstractComboBoxModel mModel;

    /**
     * Returns combo box model.
     */
    @Override
    public AbstractComboBoxModel getComboBoxModel() {
        if (mModel == null) {
            mModel = new Model();
        }
        return mModel;
    }

    /**
     * Returns order id of selected item.
     */
    public String getSelectedOrder() {
        Object selectedItem = getSelectedItem();
        return selectedItem == null ? null : selectedItem.toString();
    }

    /**
     * Sets selected item by order id.
     *
     * @param aOrderID id of the order
     */
    public void selectOrder(String aOrderID) {
        if (aOrderID != null) {
            try {
                Orders orders = getTradeDesk().getOrders();
                TOrder order = orders.getOrder(aOrderID);
                setSelectedIndex(orders.indexOf(order));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setSelectedIndex(-1);
        }
    }

    /**
     * Subscribes combo box for business data.
     */
    @Override
    public void subscribeBusinessData() throws Exception {
        getTradeDesk().getOrders().subscribe(this, SignalType.ADD);
        getTradeDesk().getOrders().subscribe(this, SignalType.CHANGE);
        getTradeDesk().getOrders().subscribe(this, SignalType.REMOVE);
    }

    /**
     * Invokes on receiving of signals.
     */
    public boolean updateOnSignal(Signal aSignal, Item aItem) throws Exception {
        boolean rc = false; //If there're no changes, don't redraw
        if (aItem != null) {
            if (getSelectedIndex() == aItem.getIndex()) {
                if (isStatusEnabled() ^ aItem.isEnabled()) {
                    setStatusEnabled(aItem.isEnabled());
                    rc = true;
                }
            }
        }
        return rc;
    }

    /**
     * AbstractComboBoxModel based class.
     */
    private class Model extends AbstractComboBoxModel {
        /**
         * Returns element at combo box by index.
         *
         * @param aIndex index of element
         */
        public Object getElementAt(int aIndex) {
            try {
                TOrder order = (TOrder) getTradeDesk().getOrders().get(aIndex);
                if (order.isEntryOrder()) {
                    return newItem(aIndex, order.getOrderID(), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Returns the length of the list.
         */
        public int getSize() {
        	return getTradeDesk().getOrders().getEntryOrderSize();
        }
    }
}
