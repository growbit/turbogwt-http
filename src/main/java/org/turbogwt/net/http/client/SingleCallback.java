/*
 * Copyright 2014 Grow Bit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.turbogwt.net.http.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestProgress;

/**
 * Callback to handle responses from request without error handling.
 *
 * @author Danilo Reinert
 */
public abstract class SingleCallback implements RequestCallback {

    /**
     * Called when a pending {@link com.google.gwt.http.client.Request} triggers a progress event.
     *
     * @param requestProgress the progress data
     */
    @Override
    @Deprecated
    public void onProgress(RequestProgress requestProgress) {
    }

    /**
     * This method is not used.
     *
     * @param request   the request object which has experienced the error condition, may be null if the request was
     *                  never generated
     * @param exception the error that was encountered
     */
    @Override
    @Deprecated
    public void onError(Request request, Throwable exception) {
    }
}
