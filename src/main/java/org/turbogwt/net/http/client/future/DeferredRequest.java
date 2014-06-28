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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.turbogwt.core.future.shared.AlwaysCallback;
import org.turbogwt.core.future.shared.Promise;
import org.turbogwt.core.future.shared.impl.AbstractDeferred;

public class DeferredRequest<T> extends AbstractDeferred<T, Throwable, RequestProgress, ResponseContext>
        implements RequestPromise<T> {

    private List<OnHolder> onCallbacks;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    protected ResponseContext getContext() {
        return new ResponseContextImpl(state, response);
    }

    @Override
    public Promise<T, Throwable, RequestProgress, ResponseContext> on(
            int statusCode, AlwaysCallback<T, Throwable, ResponseContext> callback) {
        ensureOnCallbacks().add(new OnHolder(statusCode, callback));
        return this;
    }

    public DeferredRequest<T> resolve(final T resolve) {
        if (!isPending())
            throw new IllegalStateException("Deferred object already finished, cannot resolve again");

        this.state = State.RESOLVED;
        this.resolveResult = resolve;

        try {
            triggerDone(resolve);
        } finally {
            triggerOn(getContext(), resolve, null);
            triggerAlways(getContext(), resolve, null);
        }
        return this;
    }

    public DeferredRequest<T> reject(final Throwable reject) {
        if (!isPending())
            throw new IllegalStateException("Deferred object already finished, cannot reject again");

        this.state = State.REJECTED;
        this.rejectResult = reject;

        try {
            triggerFail(reject);
        } finally {
            triggerOn(getContext(), null, reject);
            triggerAlways(getContext(), null, reject);
        }
        return this;
    }

    protected void triggerOn(ResponseContext context, T resolve, Throwable reject) {
        if (response != null) {
            for (OnHolder holder : onCallbacks) {
                final String responseCode = response.getStatusCode() + "";
                final String onCode = holder.code + "";
                if (responseCode.contains(onCode)) {
                    try {
                        triggerAlways(holder.callback, context, resolve, reject);
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "an uncaught exception occured in a AlwaysCallback", e);
                    }
                }
            }
        }
    }

    private List<OnHolder> ensureOnCallbacks() {
        return onCallbacks = onCallbacks == null ? new ArrayList<OnHolder>() : onCallbacks;
    }

    private class OnHolder {

        int code;
        AlwaysCallback<T, Throwable, ResponseContext> callback;

        private OnHolder(int code, AlwaysCallback<T, Throwable, ResponseContext> callback) {
            this.code = code;
            this.callback = callback;
        }
    }
}
