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

import com.google.gwt.http.client.Header;

class ResponseImpl implements Response {

    private final com.google.gwt.http.client.Response delegate;

    ResponseImpl(com.google.gwt.http.client.Response originalResponse) {
        this.delegate = originalResponse;
    }

    @Override
    public String getHeader(String header) {
        return delegate.getHeader(header);
    }

    @Override
    public Header[] getHeaders() {
        return delegate.getHeaders();
    }

    @Override
    public int getStatusCode() {
        return delegate.getStatusCode();
    }

    @Override
    public String getStatusText() {
        return delegate.getStatusText();
    }

    @Override
    public String getText() {
        return delegate.getText();
    }
}
