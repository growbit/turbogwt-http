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
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
import org.turbogwt.net.http.client.serialization.SerdesManager;
import org.turbogwt.net.http.client.serialization.Serializer;
import org.turbogwt.net.http.client.serialization.TextDeserializer;
import org.turbogwt.net.http.client.serialization.VoidSerdes;

/**
 * This class is a configurable {@link FluentRequest} factory.
 * Usually, you will use it as a singleton along your project.
 * <p/>
 *
 * It provides a convenience API for building/executing HTTP Requests.
 * <p/>
 *
 * You can register {@link RequestFilter}s with #registerRequestFilter, so the are executed over all your requests.
 * The same for {@link ResponseFilter}.
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
    private MultipleParamStrategy defaultStrategy;
    private String defaultContentType;
    private String defaultAccept;

    public Requestor() {
        defaultStrategy = MultipleParamStrategy.REPEATED_PARAM;
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
    // FluentRequest factory methods
    //===================================================================

    /**
     * Create a {@link FluentRequest} of RequestType request content and ResponseType response content.
     *
     * @param requestType       The class of the request content type.
     * @param responseType      The class of the response content type.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return The FluentRequest with the specified request/response contents.
     */
    public <RequestType, ResponseType> FluentRequest<RequestType, ResponseType> request(Class<RequestType> requestType,
                                                                                     Class<ResponseType> responseType) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy);
    }

    /**
     * Create a {@link FluentRequest} of RequestType request content and ResponseType response content,
     * and set a custom {@link MultipleParamStrategy} for handling multiple params.
     *
     * @param requestType       The class of the request content type.
     * @param responseType      The class of the response content type.
     * @param strategy          The strategy for separating params with multiple values.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return The FluentRequest with the specified request/response contents.
     */
    public <RequestType, ResponseType> FluentRequest<RequestType, ResponseType> request(Class<RequestType> requestType,
                                                                                       Class<ResponseType> responseType,
                                                                                       MultipleParamStrategy strategy) {
        return createFluentRequestImpl(requestType, responseType, strategy);
    }

    /**
     * Create a {@link FluentRequest} of no request/response content.
     *
     * @return The FluentRequest with void request and response contents.
     */
    public FluentRequest<Void, Void> request() {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy);
    }

    /**
     * Create a {@link FluentRequest} of no request/response content,
     * and set a custom {@link MultipleParamStrategy} for handling multiple params.
     *
     * @param strategy  The strategy for separating params with multiple values.
     *
     * @return The FluentRequest with void request and response contents.
     */
    public FluentRequest<Void, Void> request(MultipleParamStrategy strategy) {
        return createFluentRequestImpl(Void.class, Void.class, strategy);
    }

    //===================================================================
    // Request shortcuts
    //===================================================================

    //-------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------

    /**
     * Performs simple a GET request on the specified URI.
     *
     * @param uri               The uri for the request.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public Request get(String uri) {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy).setUri(uri).get();
    }

    /**
     * Performs a GET request on the specified URI, returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType> Request get(String uri, Class<ResponseType> responseType,
                                      AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).get(callback);
    }

    /**
     * Performs a GET request on the specified URI, returning a collection of ResponseType.
     * You should use some subclass of {@link ContainerAsyncCallback} for retrieving the expected values.
     *
     * @see ListAsyncCallback
     * @see SetAsyncCallback
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <ResponseType>    The type of the response content.
     * @param <C>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType, C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request get(String uri, Class<ResponseType> responseType, A callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).get(callback);
    }

    //-------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------

    /**
     * Performs simple a POST request on the specified URI.
     *
     * @param uri               The uri for the request.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public Request post(String uri) {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy).setUri(uri).post();
    }

    /* Post with DATA only */

    /**
     * Performs a POST request on the specified URI, sending a single RequestType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType to send as the request content.
     * @param <RequestType>     The type of the request content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType> Request post(String uri, Class<RequestType> requestType, RequestType data) {
        return createFluentRequestImpl(requestType, Void.class, defaultStrategy).setUri(uri).post(data);
    }

    /**
     * Performs a POST request on the specified URI, sending a single RequestType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The collection of RequestType to send as the request content.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, C extends Collection<RequestType>> Request post(String uri, Class<RequestType> requestType,
                                                                         C dataCollection) {
        return createFluentRequestImpl(requestType, Void.class, defaultStrategy).setUri(uri).post(dataCollection);
    }

    /* Post with DATA and CALLBACK */

    /**
     * Performs a POST request on the specified URI, sending a single RequestType and returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType> Request post(String uri, Class<RequestType> requestType, RequestType data,
                                                    Class<ResponseType> responseType,
                                                    AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri).post(data, callback);
    }

    /**
     * Performs a POST request on the specified URI, sending a single instance of RequestType
     * and returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType instance to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     * @param <C>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType, C extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<C, ResponseType>> Request post(String uri,
                                                                             Class<RequestType> requestType,
                                                                             RequestType data,
                                                                             Class<ResponseType> responseType,
                                                                             A callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri).post(data, callback);
    }

    /**
     * Performs a POST request on the specified URI, sending a collection of RequestType
     * and returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The RequestType collection to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     * @param <ResponseType>    The type of the response content.
     * @param <B>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType, C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request post(String uri,
                                                                             Class<RequestType> requestType,
                                                                             C dataCollection,
                                                                             Class<ResponseType> responseType,
                                                                             A callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri)
                .post(dataCollection, callback);
    }

    /**
     * Performs a POST request on the specified URI, sending a collection of RequestType
     * and returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The RequestType collection to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, C extends Collection<RequestType>, ResponseType> Request post(String uri,
                                                                                       Class<RequestType> requestType,
                                                                                       C dataCollection,
                                                                                       Class<ResponseType> responseType,
                                                                                 AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri)
                .post(dataCollection, callback);
    }

    /* Post with CALLBACK only */

    /**
     * Performs a POST request on the specified URI, returning a single instance of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType> Request post(String uri, Class<ResponseType> responseType,
                                       AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).post(callback);
    }

    /**
     * Performs a POST request on the specified URI, returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType, C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request post(String uri, Class<ResponseType> responseType, A callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).post(callback);
    }

    //-------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------

    /**
     * Performs simple a PUT request on the specified URI.
     *
     * @param uri               The uri for the request.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public Request put(String uri) {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy).setUri(uri).put();
    }

    /* Put with DATA only */

    /**
     * Performs a PUT request on the specified URI, sending a single RequestType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType to send as the request content.
     * @param <RequestType>     The type of the request content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType> Request put(String uri, Class<RequestType> requestType, RequestType data) {
        return createFluentRequestImpl(requestType, Void.class, defaultStrategy).setUri(uri).put(data);
    }

    /**
     * Performs a PUT request on the specified URI, sending a single RequestType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The collection of RequestType to send as the request content.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, C extends Collection<RequestType>> Request put(String uri, Class<RequestType> requestType,
                                                                        C dataCollection) {
        return createFluentRequestImpl(requestType, Void.class, defaultStrategy).setUri(uri).put(dataCollection);
    }

    /* Put with DATA and CALLBACK */

    /**
     * Performs a PUT request on the specified URI, sending a single RequestType and returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType> Request put(String uri, Class<RequestType> requestType, RequestType data,
                                                   Class<ResponseType> responseType,
                                                   AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri).put(data, callback);
    }

    /**
     * Performs a PUT request on the specified URI, sending a single instance of RequestType
     * and returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param data              The RequestType instance to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <ResponseType>    The type of the response content.
     * @param <C>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType, C extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<C, ResponseType>> Request put(String uri,
                                                                            Class<RequestType> requestType,
                                                                            RequestType data,
                                                                            Class<ResponseType> responseType,
                                                                            A callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri).put(data, callback);
    }

    /**
     * Performs a PUT request on the specified URI, sending a collection of RequestType
     * and returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The RequestType collection to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     * @param <ResponseType>    The type of the response content.
     * @param <B>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, ResponseType, C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request put(String uri,
                                                                            Class<RequestType> requestType,
                                                                            C dataCollection,
                                                                            Class<ResponseType> responseType,
                                                                            A callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri)
                .put(dataCollection, callback);
    }

    /**
     * Performs a PUT request on the specified URI, sending a collection of RequestType
     * and returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param requestType       The class of the request type.
     * @param dataCollection    The RequestType collection to send as the request content.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <RequestType>     The type of the request content.
     * @param <C>               The collection type of the request content.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <RequestType, C extends Collection<RequestType>, ResponseType> Request put(String uri,
                                                                                      Class<RequestType> requestType,
                                                                                      C dataCollection,
                                                                                      Class<ResponseType> responseType,
                                                                                 AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(requestType, responseType, defaultStrategy).setUri(uri)
                .put(dataCollection, callback);
    }

    /* Put with CALLBACK only */

    /**
     * Performs a PUT request on the specified URI, returning a single instance of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType> Request put(String uri, Class<ResponseType> responseType,
                                      AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).put(callback);
    }

    /**
     * Performs a PUT request on the specified URI, returning a collection of ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType, C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request put(String uri, Class<ResponseType> responseType, A callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).put(callback);
    }

    //-------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------

    /**
     * Performs simple a DELETE request on the specified URI.
     *
     * @param uri               The uri for the request.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public Request delete(String uri) {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy).setUri(uri).delete();
    }

    /**
     * Performs a DELETE request on the specified URI, returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType> Request delete(String uri, Class<ResponseType> responseType,
                                         AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).delete(callback);
    }

    /**
     * Performs a DELETE request on the specified URI, returning a collection of ResponseType.
     * You should use some subclass of {@link ContainerAsyncCallback} for retrieving the expected values.
     *
     * @see ListAsyncCallback
     * @see SetAsyncCallback
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <ResponseType>    The type of the response content.
     * @param <C>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType, C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request delete(String uri, Class<ResponseType> responseType, A callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).delete(callback);
    }

    //-------------------------------------------------------------------
    // HEAD
    //-------------------------------------------------------------------

    /**
     * Performs simple a HEAD request on the specified URI.
     *
     * @param uri               The uri for the request.
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     *
     */
    public Request head(String uri) {
        return createFluentRequestImpl(Void.class, Void.class, defaultStrategy).setUri(uri).head();
    }

    /**
     * Performs a HEAD request on the specified URI, returning a single ResponseType.
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a single instance of ResponseType.
     * @param <ResponseType>    The type of the response content.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType> Request head(String uri, Class<ResponseType> responseType,
                                       AsyncCallback<ResponseType> callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).head(callback);
    }

    /**
     * Performs a HEAD request on the specified URI, returning a collection of ResponseType.
     * You should use some subclass of {@link ContainerAsyncCallback} for retrieving the expected values.
     *
     * @see ListAsyncCallback
     * @see SetAsyncCallback
     *
     * @param uri               The uri for the request.
     * @param responseType      The class of the response type.
     * @param callback          The callback for retrieving a collection of ResponseType.
     * @param <ResponseType>    The type of the response content.
     * @param <C>               The collection type to accumulate the response.
     *
     * @return  The launched {@link com.google.gwt.http.client.Request}.
     */
    public <ResponseType, C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request head(String uri, Class<ResponseType> responseType, A callback) {
        return createFluentRequestImpl(Void.class, responseType, defaultStrategy).setUri(uri).head(callback);
    }

    //===================================================================
    // Requestory configuration
    //===================================================================

    public MultipleParamStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    /**
     * Set the default strategy to separate params with multiple values.
     * You can use one of the constants provided at {@link MultipleParamStrategy} or implement a customized one.
     *
     * @param defaultStrategy   The {@link MultipleParamStrategy} to be initially set
     *                          in all {@link FluentRequest}s created.
     */
    public void setDefaultStrategy(MultipleParamStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

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
     * @return  The {@link Registration} object, capable of cancelling this registration.
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
     * @return  The {@link Registration} object, capable of cancelling this registration.
     */
    public <T> Registration registerSerdes(Class<T> type, Serdes<T> serdes) {
        return serdesManager.registerSerdes(type, serdes);
    }

    /**
     * Register a request filter.
     *
     * @param requestFilter The request filter to be registered.
     *
     * @return  The {@link Registration} object, capable of cancelling this registration.
     */
    public Registration registerRequestFilter(RequestFilter requestFilter) {
        return filterManager.registerRequestFilter(requestFilter);
    }

    /**
     * Register a response filter.
     *
     * @param responseFilter The response filter to be registered.
     *
     * @return  The {@link Registration} object, capable of cancelling this registration.
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
     * @return  The {@link Registration} object, capable of cancelling this registration.
     */
    public <C extends Collection> Registration registerCollectionFactoy(Class<C> collectionType, Factory<C> factory) {
        return collectionFactoryManager.registerFactory(collectionType, factory);
    }

    private <RequestType, ResponseType> FluentRequestImpl<RequestType, ResponseType>
            createFluentRequestImpl(Class<RequestType> requestType,
                                    Class<ResponseType> responseType,
                                    MultipleParamStrategy strategy) {
        final FluentRequestImpl<RequestType, ResponseType> request = new FluentRequestImpl<>(filterManager,
                serdesManager, requestType, responseType, collectionFactoryManager);
        request.multipleParamStrategy(strategy);
        request.contentType(defaultContentType);
        request.accept(defaultAccept);
        return request;
    }
}
