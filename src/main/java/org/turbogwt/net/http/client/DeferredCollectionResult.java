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

import com.google.gwt.http.client.Response;

import java.util.Collection;

import org.turbogwt.net.http.client.future.RequestProgress;
import org.turbogwt.net.http.client.serialization.DeserializationContext;
import org.turbogwt.net.http.client.serialization.Deserializer;
import org.turbogwt.net.http.client.serialization.SerdesManager;

class DeferredCollectionResult<T> extends DeferredCollectionDecorator<T> implements DeferredRequestDecorator {

    private final Class<T> responseType;
    private final Class<? extends Collection> containerType;
    private final SerdesManager serdesManager;
    private final ContainerFactoryManager containerFactoryManager;

    public DeferredCollectionResult(Class<T> responseType, Class<? extends Collection> containerType,
                                    SerdesManager serdesManager, ContainerFactoryManager containerFactoryManager) {
        this.responseType = responseType;
        this.containerType = containerType;
        this.serdesManager = serdesManager;
        this.containerFactoryManager = containerFactoryManager;
    }

    @Override
    public void resolve(Response response) {
        final Headers headers = new Headers(response.getHeaders());
        final String responseContentType = headers.getValue("Content-Type");

        final Deserializer<T> deserializer = serdesManager.getDeserializer(responseType, responseContentType);
        final DeserializationContext context = DeserializationContext.of(headers, containerFactoryManager);
        @SuppressWarnings("unchecked")
        Collection<T> result = deserializer.deserializeAsCollection(containerType, response.getText(), context);

        // Set response before in order to correctly create the response context
        getDeferredDelegate().setResponse(response);
        getDeferredDelegate().resolve(result);
    }

    @Override
    public void notify(RequestProgress progress) {
        getDeferredDelegate().notify(progress);
    }

    @Override
    public void reject(Response response) {
        // Set response before in order to correctly create the response context
        getDeferredDelegate().setResponse(response);
        getDeferredDelegate().reject(new UnsuccessfulResponseException(response));
    }

    @Override
    public void reject(Throwable throwable) {
        getDeferredDelegate().reject(throwable);
    }
}
