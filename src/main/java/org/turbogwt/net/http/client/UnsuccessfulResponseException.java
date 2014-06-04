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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Thrown to indicate that an HTTP request received a response with other status code then 2xx.
 * It provides access to the response in order to better handle the return.
 *
 * @author Danilo Reinert
 */
public class UnsuccessfulResponseException extends RequestException {

    private final Request request;
    private final Response response;

    /**
     * Constructs the exception with the request and respective response.
     *
     * @param request The request that originated the unsuccessful response.
     * @param response The response received from request.
     */
    public UnsuccessfulResponseException(Request request, Response response) {
        super("The response was received but the status code was not from 'Success' class (2xx).");
        this.request = request;
        this.response = response;
    }

    /**
     * Returns the request which received the unsuccessful response.
     *
     * @return The request which received the unsuccessful response.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Returns the unsuccessful response.
     *
     * @return The unsuccessful response.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Response's HTTP status code.
     *
     * @return The response's status code.
     */
    public int getStatusCode() {
        return response.getStatusCode();
    }

    /**
     * Response's HTTP body.
     *
     * @return The response's body text.
     */
    public String getReponseBody() {
        return response.getText();
    }

    /**
     * Response's HTTP body as JSON.
     *
     * The text is evaluated using {@link com.google.gwt.core.client.JsonUtils#safeEval(String)}.
     *
     * @return The response's body text.
     */
    public JavaScriptObject getReponseAsJson() {
        return JsonUtils.safeEval(response.getText());
    }
}
