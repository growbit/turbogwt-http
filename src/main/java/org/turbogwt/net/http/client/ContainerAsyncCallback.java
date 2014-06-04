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

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;

/**
 * Super class of all callbacks intended to receive result as collection of objects.
 *
 * @param <T>   The parametrized type of the collection
 * @param <C>   The type of the collection
 *
 * @author Danilo Reinert
 */
public abstract class ContainerAsyncCallback<C extends Collection<T>, T> implements AsyncCallback<C> {

    /**
     * Returns the collection type which this callback expects.
     * <p/>
     * IMPORTANT! You should override this method to return the specific class
     * you want to receive at #onSuccess.
     *
     * @return The collection type which this callback expects
     */
    public abstract Class<? super C> getContainerClass();
}
