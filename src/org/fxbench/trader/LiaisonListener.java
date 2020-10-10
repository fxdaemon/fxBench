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
package org.fxbench.trader;

import org.fxbench.trader.Liaison.LiaisonStatus;

/**
 * ALiaisonListener class
 * This is default implementation of ILiaisonListener interface.
 */
public abstract class LiaisonListener implements ILiaisonListener {
    /**
     * This method is called when critical error occurred. Connection is closed.
     *
     * @param liaisonException
     */
    public void onCriticalError(LiaisonException liaisonException) {
    }

    /**
     * This method is called when status of liaison has changed.
     *
     * @param status
     */
    public void onLiaisonStatus(LiaisonStatus status) {
    }

    /**
     * This method is called when initiated login command has completed successfully.
     */
    public void onLoginCompleted() {
    }

    /**
     * This method is called when initiated login command has failed. liaisonException
     * contains information about error.
     *
     * @param liaisonException
     */
    public void onLoginFailed(LiaisonException liaisonException) {
    }

    /**
     * This method is called when initiated logout command has completed.
     */
    public void onLogoutCompleted() {
    }
}