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

package org.turbogwt.net.http.client.mock;

import com.google.gwt.http.client.Response;

import java.util.HashMap;
import java.util.Map;

import org.turbogwt.net.http.client.Server;
import org.turbogwt.net.http.client.ServerConnection;

/**
 * @author Danilo Reinert
 */
public class ServerStub implements Server {

    private static Map<String, Response> responseData = new HashMap<>();
    private static Map<String, RequestMock> requestData = new HashMap<>();

    private static boolean returnSuccess = true;

    public static boolean isReturnSuccess() {
        return returnSuccess;
    }

    public static void setReturnSuccess(boolean success) {
        returnSuccess = success;
    }

    public static void responseFor(String uri, Response response) {
        responseData.put(uri, response);
    }

    public static RequestMock getRequestData(String uri) {
        return requestData.get(uri);
    }

    public static void triggerPendingRequest() {
        ServerConnectionMock.triggerPendingRequest();
    }

    public static void clearStub() {
        responseData.clear();
        requestData.clear();
        returnSuccess = true;
    }

    static Response getResponseFor(String uri) {
        return responseData.get(uri);
    }

    static RequestMock setRequestData(String uri, RequestMock requestMock) {
        return requestData.put(uri, requestMock);
    }

    /**
     * Retrieve an instance of {@link org.turbogwt.net.http.client.ServerConnection}.
     *
     * @return The ServerConnection instance.
     */
    @Override
    public ServerConnection getConnection() {
        return new ServerConnectionMock();
    }
}
