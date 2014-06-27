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

package org.turbogwt.net.http.client.future;

import com.google.gwt.http.client.Response;

import org.turbogwt.core.future.shared.Promise;
import org.turbogwt.core.future.shared.impl.ContextImpl;

public class ResponseContextImpl extends ContextImpl implements ResponseContext {

    private final Response response;

    public ResponseContextImpl(Promise.State state, Response response) {
        super(state);
        this.response = response;
    }

    @Override
    public String getHeader(String header) {
        return response != null ? response.getHeader(header) : null;
    }

    @Override
    public int getStatusCode() {
        return response != null ? response.getStatusCode() : -1;
    }

    @Override
    public String getStatusText() {
        return response != null ? response.getStatusText() : null;
    }

    @Override
    public String getText() {
        return response != null ? response.getText() : null;
    }
}
