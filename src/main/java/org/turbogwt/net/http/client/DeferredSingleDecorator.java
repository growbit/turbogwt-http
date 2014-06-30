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

import org.turbogwt.core.future.shared.AlwaysCallback;
import org.turbogwt.core.future.shared.DoneCallback;
import org.turbogwt.core.future.shared.FailCallback;
import org.turbogwt.core.future.shared.ProgressCallback;
import org.turbogwt.core.future.shared.Promise;
import org.turbogwt.net.http.client.future.DeferredRequest;
import org.turbogwt.net.http.client.future.RequestProgress;
import org.turbogwt.net.http.client.future.RequestPromise;
import org.turbogwt.net.http.client.future.ResponseContext;

abstract class DeferredSingleDecorator<T> implements RequestPromise<T> {

    private final DeferredRequest<T> deferredDelegate = new DeferredRequest<T>();

    @Override
    public State state() {
        return deferredDelegate.state();
    }

    @Override
    public Promise<T, Throwable, RequestProgress, ResponseContext> done(DoneCallback<T> callback) {
        return deferredDelegate.done(callback);
    }

    @Override
    public Promise<T, Throwable, RequestProgress, ResponseContext> fail(FailCallback<Throwable> callback) {
        return deferredDelegate.fail(callback);
    }

    @Override
    public Promise<T, Throwable, RequestProgress, ResponseContext> always(AlwaysCallback<T, Throwable,
            ResponseContext> callback) {
        return deferredDelegate.always(callback);
    }

    @Override
    public Promise<T, Throwable, RequestProgress, ResponseContext> progress(ProgressCallback<RequestProgress> callback) {
        return deferredDelegate.progress(callback);
    }

    @Override
    public boolean isPending() {
        return deferredDelegate.isPending();
    }

    @Override
    public boolean isResolved() {
        return deferredDelegate.isResolved();
    }

    @Override
    public boolean isRejected() {
        return deferredDelegate.isRejected();
    }

    public Promise<T, Throwable, RequestProgress, ResponseContext> promise() {
        return this;
    }

    protected DeferredRequest<T> getDeferredDelegate() {
        return deferredDelegate;
    }
}
