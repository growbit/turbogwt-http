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

import java.util.Collection;

import org.turbogwt.core.util.shared.Factory;
import org.turbogwt.core.util.shared.Registration;
import org.turbogwt.net.serialization.client.Deserializer;
import org.turbogwt.net.serialization.client.Serdes;
import org.turbogwt.net.serialization.client.Serializer;

/**
 * This interface is a configurable {@link Request} factory.
 * Usually, you will use it as a singleton along your project.
 * <p/>
 *
 * It provides a convenience API for managing/creating HTTP Requests.
 * <p/>
 *
 * You can register {@link RequestFilter}s with #registerRequestFilter, so the are executed over all your requests.
 * The same for {@link org.turbogwt.net.http.client.ResponseFilter}.
 * <p/>
 *
 * You can register custom {@link org.turbogwt.net.serialization.client.Serializer} with #registerSerializer.
 * The same for {@link org.turbogwt.net.serialization.client.Deserializer}.
 * If you want to support both serialization and deserialization for your custom object,
 * register a {@link org.turbogwt.net.serialization.client.Serdes} with #registerSerdes.
 * <p/>
 *
 * SerDes for {@link String}, {@link Number}, {@link Boolean}
 * and {@link com.google.gwt.core.client.JavaScriptObject} are already provided.
 *
 * @author Danilo Reinert
 */
public interface Requestor {

    //===================================================================
    // Requestor configuration
    //===================================================================

    void setDefaultContentType(String contentType);

    String getDefaultContentType();

    <T> Deserializer<T> getDeserializer(Class<T> type, String contentType);

    <T> Serializer<T> getSerializer(Class<T> type, String contentType);

    /**
     * Register a collection factory.
     *
     * @param collectionType    The class of the collection
     * @param factory           The factory of the collection
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    <C extends Collection> Registration registerContainerFactoy(Class<C> collectionType, Factory<C> factory);

    /**
     * Register a deserializer of the given type.
     *
     * @param type          The class of the deserializer's type.
     * @param deserializer  The deserializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    <T> Registration registerDeserializer(Class<T> type, Deserializer<T> deserializer);

    /**
     * Register a request filter.
     *
     * @param requestFilter The request filter to be registered.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    Registration registerRequestFilter(RequestFilter requestFilter);

    /**
     * Register a response filter.
     *
     * @param responseFilter The response filter to be registered.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    Registration registerResponseFilter(ResponseFilter responseFilter);

    /**
     * Register a serializer/deserializer of the given type.
     *
     * @param type      The class of the serializer/deserializer's type.
     * @param serdes    The serializer/deserializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    <T> Registration registerSerdes(Class<T> type, Serdes<T> serdes);

    /**
     * Register a serializer of the given type.
     *
     * @param type          The class of the serializer's type.
     * @param serializer  The serializer of T.
     *
     * @return  The {@link org.turbogwt.core.util.shared.Registration} object, capable of cancelling this registration.
     */
    <T> Registration registerSerializer(Class<T> type, Serializer<T> serializer);

    //===================================================================
    // Request factory methods
    //===================================================================

    /**
     * Create a {@link Request} of no request/response content.
     *
     * @return The FluentRequest with void request and response contents.
     */
    RequestDispatcher request(String uri);
}
