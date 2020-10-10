/*
 * $Header: //depot/FXCM/New_CurrentSystem/Main/FXCM_SRC/TRADING_SDK/tradestation/src/main/fxts/stations/transport/tradingapi/LoginRequest.java#1 $
 * 
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
 *
 */
package org.fxbench.trader.local;

import java.util.Date;

import com.fxcm.GenericException;

import org.fxbench.trader.BaseRequest;
import org.fxbench.trader.IReqCollection;
import org.fxbench.trader.IRequest;
import org.fxbench.trader.IRequester;
import org.fxbench.trader.Liaison.LiaisonStatus;
import org.fxbench.trader.LiaisonException;
import org.fxbench.trader.TradingAPIException;
import org.fxbench.util.properties.PropertySheet;

/**
 * Class LoginRequest.<br>
 * <br>
 * It is responsible for:
 * <ul>
 * <li> storing user id and password; </li>
 * <li> preparing and sending to FX Trade Server appropriate for it login request object. </li>
 * </ul>
 * <br>
 * Creation date (9/5/2003 3:30 PM)
 */
public class LoginRequest extends BaseRequest implements IRequester
{
	private final static String ACCOUNT_NAME = "username";
	private final static String DATA_PATH = "datapath";
	private final static String START_DATE = "startdate";
	private final static String DB_HOST = "dbhost";
	private final static String DB_NAME = "dbname";
	private final static String DB_USER = "dbuser";
	private final static String DB_PASSWORD = "dbpassword";
	
    private PropertySheet loginPropsheet;

    public LoginRequest(PropertySheet propSheet) {
    	this.loginPropsheet = propSheet;
    }
    
    public LiaisonStatus doIt() throws LiaisonException {
        try {
        	getTradeDesk().getTradingServerSession().login(
        		getDbUser(), getDbPassword(), getDbName(), getDbHost());
        } catch (LiaisonException le) {
            throw le;
        } catch (GenericException ce) {
            throw new TradingAPIException(ce, "IDS_LOGIN_FAILURE");
        } catch (Exception e) {
            e.printStackTrace();
            throw new TradingAPIException(e, "IDS_CANNOT_GET_HOST_DESCRIPTOR");
        }
        return null;
    }

    public IRequest getRequest() {
        return this;
    }

    public IRequester getSibling() {
        return null;
    }

    public void toQueue(IReqCollection aQueue) {
        aQueue.add(this);
    }

	public String getAccountName() {
		return loginPropsheet.getStrVal(ACCOUNT_NAME);
	}
	
	public String getDataPath() {
		return loginPropsheet.getStrVal(DATA_PATH);
	}
	
	public Date getStartDate() {
		return loginPropsheet.getDateVal(START_DATE);
	}

	public String getDbHost() {
		return loginPropsheet.getStrVal(DB_HOST);
	}

	public String getDbName() {
		return loginPropsheet.getStrVal(DB_NAME);
	}

	public String getDbUser() {
		return loginPropsheet.getStrVal(DB_USER);
	}

	public String getDbPassword() {
		return loginPropsheet.getStrVal(DB_PASSWORD);
	}

}