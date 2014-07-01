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

/**
 * This class is a configurable {@link Request} factory.
 * Usually, you will use it as a singleton along your project.
 * <p/>
 *
 * It provides a convenience API for building/executing HTTP Requests.
 * <p/>
 *
 * You can register {@link org.turbogwt.net.http.client.RequestFilter}s with #registerRequestFilter, so the are executed over all your requests.
 * The same for {@link org.turbogwt.net.http.client.ResponseFilter}.
 * <p/>
 *
 * You can register custom {@link org.turbogwt.net.http.client.serialization.Serializer} with #registerSerializer.
 * The same for {@link org.turbogwt.net.http.client.serialization.Deserializer}.
 * If you want to support both serialization and deserialization for your custom object,
 * register a {@link org.turbogwt.net.http.client.serialization.Serdes} with #registerSerdes.
 * <p/>
 *
 * SerDes for {@link String}, {@link Number}, {@link Boolean}
 * and {@link com.google.gwt.core.client.JavaScriptObject} are already provided.
 * <p/>
 *
 * Define the way params with multiple values should be processed to form your URIs with #setDefaultStrategy.
 *
 * @author Danilo Reinert
 */
public class Requestor {

    private final SerdesManager serdesManager = new SerdesManager();
    private final FilterManager filterManager = new FilterManager();
    private final ContainerFactoryManager collectionFactoryManager = new ContainerFactoryManager();
    private String defaultContentType;
    private String defaultAccept;

    public Requestor() {
        defaultContentType = "application/json";
        defaultAccept = "application/json";
        serdesManager.registerSerdes(String.class, JsonStringSerdes.getInstance());
        serdesManager.registerSerdes(Number.class, JsonNumberSerdes.getInstance());
        serdesManager.registerSerdes(Boolean.class, JsonBooleanSerdes.getInstance());
        serdesManager.registerSerdes(Void.class, VoidSerdes.getInstance());
        serdesManager.registerSerdes(JavaScriptObject.class, OverlaySerdes.getInstance());
        serdesManager.registerDeserializer(String.class, TextDeserializer.getInstance());
        serdesManager.registerSerializer(FormParam.class, FormParamSerializer.getInstance());
    }

    //===================================================================
    // Request factory methods
    //===================================================================

      /**
     * Create a {@link Request} of no request/response content.
     *
     * @return The FluentRequest with void request and response contents.
     */
    public RequestDispatcher request(String uri) {
        return createRequest(uri);
    }


    //===================================================================
    // Requestor configuration
    //===================================================================

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public String getDefaultAccept() {
        return defaultAccept;
    }

    /**
     * Register a deserializer of the given type.
     *
     * @param type          The class of the deserializer's type.
     * @param deserializer  The deserializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public <T> Registration registerDeserializer(Class<T> type, Deserializer<T> deserializer) {
        return serdesManager.registerDeserializer(type, deserializer);
    }

    /**
     * Register a serializer of the given type.
     *
     * @param type          The class of the serializer's type.
     * @param serializer  The serializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public <T> Registration registerSerializer(Class<T> type, Serializer<T> serializer) {
        return serdesManager.registerSerializer(type, serializer);
    }

    /**
     * Register a serializer/deserializer of the given type.
     *
     * @param type      The class of the serializer/deserializer's type.
     * @param serdes    The serializer/deserializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public <T> Registration registerSerdes(Class<T> type, Serdes<T> serdes) {
        return serdesManager.registerSerdes(type, serdes);
    }

    /**
     * Register a request filter.
     *
     * @param requestFilter The request filter to be registered.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public Registration registerRequestFilter(RequestFilter requestFilter) {
        return filterManager.registerRequestFilter(requestFilter);
    }

    /**
     * Register a response filter.
     *
     * @param responseFilter The response filter to be registered.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public Registration registerResponseFilter(ResponseFilter responseFilter) {
        return filterManager.registerResponseFilter(responseFilter);
    }

    /**
     * Register a collection factory.
     *
     * @param collectionType    The class of the collection
     * @param factory           The factory of the collection
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    public <C extends Collection> Registration registerContainerFactoy(Class<C> collectionType, Factory<C> factory) {
        return collectionFactoryManager.registerFactory(collectionType, factory);
    }

    private RequestDispatcher createRequest(String uri) {
        final RequestImpl request = new RequestImpl(uri, serdesManager, collectionFactoryManager, filterManager);
        request.contentType(defaultContentType);
        request.accept(defaultAccept);
        return request;
    }
}
