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

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Collection;

import org.turbogwt.core.util.shared.Factory;
import org.turbogwt.core.util.shared.Registration;
import org.turbogwt.net.http.client.serialization.Deserializer;
import org.turbogwt.net.http.client.serialization.FormParamSerializer;
import org.turbogwt.net.http.client.serialization.JsonBooleanSerdes;
import org.turbogwt.net.http.client.serialization.JsonNumberSerdes;
import org.turbogwt.net.http.client.serialization.JsonStringSerdes;
import org.turbogwt.net.http.client.serialization.OverlaySerdes;
import org.turbogwt.net.http.client.serialization.Serdes;
import org.turbogwt.net.http.client.serialization.Serializer;
import org.turbogwt.net.http.client.serialization.TextDeserializer;
import org.turbogwt.net.http.client.serialization.VoidSerdes;

public class RequestorImpl implements Requestor {

    private final SerdesManager serdesManager = new SerdesManager();
    private final FilterManager filterManager = new FilterManager();
    private final ContainerFactoryManager collectionFactoryManager = new ContainerFactoryManager();
    private String defaultContentType = "application/json";

    public RequestorImpl() {
        initSerdesManager();
    }

    //===================================================================
    // Request factory methods
    //===================================================================

    @Override
    public RequestDispatcher request(String uri) {
        return createRequest(uri);
    }

    //===================================================================
    // Requestor configuration
    //===================================================================

    @Override
    public void setDefaultContentType(String contentType) {
        this.defaultContentType = contentType;
    }

    @Override
    public String getDefaultContentType() {
        return defaultContentType;
    }

    @Override
    public <T> Deserializer<T> getDeserializer(Class<T> type, String contentType) {
        return serdesManager.getDeserializer(type, contentType);
    }

    @Override
    public <T> Serializer<T> getSerializer(Class<T> type, String contentType) {
        return serdesManager.getSerializer(type, contentType);
    }

    @Override
    public <T> Registration registerDeserializer(Class<T> type, Deserializer<T> deserializer) {
        return serdesManager.registerDeserializer(type, deserializer);
    }

    @Override
    public <T> Registration registerSerializer(Class<T> type, Serializer<T> serializer) {
        return serdesManager.registerSerializer(type, serializer);
    }

    @Override
    public <T> Registration registerSerdes(Class<T> type, Serdes<T> serdes) {
        return serdesManager.registerSerdes(type, serdes);
    }

    @Override
    public Registration registerRequestFilter(RequestFilter requestFilter) {
        return filterManager.registerRequestFilter(requestFilter);
    }

    @Override
    public Registration registerResponseFilter(ResponseFilter responseFilter) {
        return filterManager.registerResponseFilter(responseFilter);
    }

    @Override
    public <C extends Collection> Registration registerContainerFactoy(Class<C> collectionType, Factory<C> factory) {
        return collectionFactoryManager.registerFactory(collectionType, factory);
    }

    private RequestDispatcher createRequest(String uri) {
        final RequestImpl request = new RequestImpl(uri, serdesManager, collectionFactoryManager, filterManager);
        request.contentType(defaultContentType);
        request.accept(defaultContentType);
        return request;
    }

    private void initSerdesManager() {
        serdesManager.registerSerdes(String.class, JsonStringSerdes.getInstance());
        serdesManager.registerSerdes(Number.class, JsonNumberSerdes.getInstance());
        serdesManager.registerSerdes(Boolean.class, JsonBooleanSerdes.getInstance());
        serdesManager.registerSerdes(Void.class, VoidSerdes.getInstance());
        serdesManager.registerSerdes(JavaScriptObject.class, OverlaySerdes.getInstance());
        serdesManager.registerDeserializer(String.class, TextDeserializer.getInstance());
        serdesManager.registerSerializer(FormParam.class, FormParamSerializer.getInstance());
    }
}
