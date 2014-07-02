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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.turbogwt.core.util.shared.Registration;

/**
 * A manager for {@link RequestFilter} and {@link ResponseFilter}.
 *
 * @author Danilo Reinert
 */
class FilterManager {

    private final List<RequestFilter> requestFilters = new ArrayList<>();
    private final List<ResponseFilter> responseFilters = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private List<RequestFilter> requestFiltersCopy = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private List<ResponseFilter> responseFiltersCopy = Collections.EMPTY_LIST;

    /**
     * Register a request filter.
     *
     * @param requestFilter The request filter to be registered.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration
     *          to the {@link FilterManager}.
     */
    public Registration registerRequestFilter(final RequestFilter requestFilter) {
        addRequestFilter(requestFilter);

        return new Registration() {
            @Override
            public void removeHandler() {
                removeRequestFilter(requestFilter);
            }
        };
    }

    /**
     * Register a response filter.
     *
     * @param responseFilter The response filter to be registered.
     *
     * @return  The {@link Registration} object, capable of cancelling this registration
     *          to the {@link FilterManager}.
     */
    public Registration registerResponseFilter(final ResponseFilter responseFilter) {
        addResponseFilter(responseFilter);

        return new Registration() {
            @Override
            public void removeHandler() {
                removeResponseFilter(responseFilter);
            }
        };
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The request filters.
     */
    public List<RequestFilter> getRequestFilters() {
        return requestFiltersCopy;
    }

    /**
     * Returns an immutable copy of the filters.
     *
     * @return The response filters.
     */
    public List<ResponseFilter> getResponseFilters() {
        return responseFiltersCopy;
    }

    private void addRequestFilter(RequestFilter requestFilter) {
        requestFilters.add(requestFilter);
        updateRequestFiltersCopy();
    }

    private void addResponseFilter(ResponseFilter responseFilter) {
        responseFilters.add(responseFilter);
        updateResponseFiltersCopy();
    }

    private void removeRequestFilter(RequestFilter requestFilter) {
        requestFilters.remove(requestFilter);
        updateRequestFiltersCopy();
    }

    private void removeResponseFilter(ResponseFilter responseFilter) {
        responseFilters.remove(responseFilter);
        updateResponseFiltersCopy();
    }

    private void updateRequestFiltersCopy() {
        requestFiltersCopy = Collections.unmodifiableList(requestFilters);
    }

    private void updateResponseFiltersCopy() {
        responseFiltersCopy = Collections.unmodifiableList(responseFilters);
    }
}
