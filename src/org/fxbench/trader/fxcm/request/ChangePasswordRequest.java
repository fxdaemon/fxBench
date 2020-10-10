/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/requests/ChangePasswordRequest.java#1 $
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
 * Created: Oct 17, 2008 11:11:18 AM
 *
 * $History: $
 */
package org.fxbench.trader.fxcm.request;

import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.other.UserRequest;

import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.fxcm.FxcmServerSession;
import org.fxbench.trader.TradingAPIException;

/**
 */
public class ChangePasswordRequest extends BaseRequest implements IRequester
{
    private String mConfirmNewPassword;
    private String mNewPassword;
    private String mOldPassword;

    public LiaisonStatus doIt() throws LiaisonException {
        try {
            UserRequest ur = new UserRequest();
            ur.setUserRequestType(IFixDefs.USERREQUESTTYPE_CHANGEPASSWORD);
            ur.setPassword(mOldPassword);
            ur.setNewPassword(mNewPassword);
            getTradeDesk().getTradingServerSession().setPassword(mNewPassword);
            ((FxcmServerSession)getTradeDesk().getTradingServerSession()).send(ur);
            return LiaisonStatus.READY;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TradingAPIException(e, "IDS_INVALID_REQUEST_FIELD");
        }
    }

    public IRequest getRequest() {
        return this;
    }

    public IRequester getSibling() {
        return null;
    }

    public void setConfirmNewPassword(String aConfirmNewPassword) {
        mConfirmNewPassword = aConfirmNewPassword;
    }

    public void setNewPassword(String aNewPassword) {
        mNewPassword = aNewPassword;
    }

    public void setOldPassword(String aOldPassword) {
        mOldPassword = aOldPassword;
    }

    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }
}
