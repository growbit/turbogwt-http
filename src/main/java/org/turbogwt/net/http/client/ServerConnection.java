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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

import javax.annotation.Nullable;

/**
 * Represents a connection with server-side.
 *
 * @author Danilo Reinert
 */
public interface ServerConnection {

    void sendRequest(RequestBuilder.Method method, String url, String data, RequestCallback callback)
            throws RequestException;

    void sendRequest(int timeout, @Nullable String user, @Nullable String password, @Nullable Headers headers,
                     RequestBuilder.Method method, String url, String data, RequestCallback callback)
            throws RequestException;
}
