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

import org.turbogwt.core.future.shared.AlwaysCallback;
import org.turbogwt.core.future.shared.Promise;

public interface RequestPromise<T> extends Promise<T, Throwable, RequestProgress, ResponseContext> {

    /**
     * This method will register {@link org.turbogwt.core.future.shared.AlwaysCallback} so that when
     * the response is received, if the code matches the status code of the response, the it is triggered.
     * <p/>
     * You can register multiple {@link org.turbogwt.core.future.shared.AlwaysCallback} by calling the method
     * multiple times. The order of callback
     * trigger is based on the order you call this method.
     * <p/>
     * <pre>
     * You can bind you callback execution to a complete code:
     * <code>
     * promise.on(404, new AlwaysCallback(){
     *   void onAlways(ResponseContext context, T result, Throwable rejection) {
     *     // context.getState() == Stated.REJECTED
     *     // result == null
     *     // rejection != null
     *   }
     * });
     * </code>
     * Or you can bind to a class of codes, like 40 or 4.
     * </pre>
     *
     * @param callback the callback to be executed
     *
     * @return the promise
     */
    Promise<T, Throwable, RequestProgress, ResponseContext> on(int statusCode,
                                                               AlwaysCallback<T, Throwable, ResponseContext> callback);
}
