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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestProgress;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.turbogwt.core.collections.client.JsArray;
import org.turbogwt.core.collections.client.JsMap;
import org.turbogwt.core.util.client.Overlays;
import org.turbogwt.net.http.client.serialization.DeserializationContext;
import org.turbogwt.net.http.client.serialization.Deserializer;
import org.turbogwt.net.http.client.serialization.SerdesManager;
import org.turbogwt.net.http.client.serialization.SerializationContext;
import org.turbogwt.net.http.client.serialization.SerializationException;
import org.turbogwt.net.http.client.serialization.Serializer;
import org.turbogwt.net.http.client.serialization.UnableToDeserializeException;

/**
 * Default implementation of fluent request.
 *
 * @param <RequestType> Type of data to be sent in the HTTP request body, when appropriate.
 * @param <ResponseType> Type of result from requests, when appropriate.
 *
 * @author Danilo Reinert
 */
public class FluentRequestImpl<RequestType, ResponseType> implements FluentRequestSender<RequestType, ResponseType> {

    private final Server server = GWT.create(Server.class);
    // TODO: remove responsibility for manipulating SerdesManager
    private final SerdesManager serdesManager;
    // TODO: remove responsibility for manipulating FilterManager
    private final FilterManager filterManager;
    // TODO: remove responsibility for manipulating ContainerFactoryManager
    private final ContainerFactoryManager containerFactoryManager;
    private final Class<RequestType> requestType;
    private final Class<ResponseType> responseType;

    private UriBuilder uriBuilder;
    private String uri;
    private JsMap<SingleCallback> mappedCallbacks;
    private JsArray<SingleCallback> alwaysCallbacks;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private String contentType;
    private AcceptHeader accept;

    public FluentRequestImpl(FilterManager filterManager, SerdesManager serdesManager,
                             Class<RequestType> requestType, Class<ResponseType> responseType,
                             ContainerFactoryManager containerFactoryManager)
            throws NullPointerException, IllegalArgumentException {
        if (filterManager == null) throw new NullPointerException("FilterManager cannot be null.");
        this.filterManager = filterManager;

        if (containerFactoryManager == null) throw new NullPointerException("CollectionFactoryManager can't be null.");
        this.containerFactoryManager = containerFactoryManager;

        if (serdesManager == null) throw new NullPointerException("SerdesManager cannot be null.");
        this.serdesManager = serdesManager;

        this.requestType = requestType;
        this.responseType = responseType;

        this.uriBuilder = GWT.create(UriBuilder.class);
    }

    /**
     * Set the content type of this request.
     *
     * @param contentType The content type of this request
     *
     * @return the updated FluentRequest
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Set the content type accepted for the response.
     *
     * @param contentType The content type accepted for the response
     *
     * @return the updated FluentRequest
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> accept(String contentType) {
        this.accept = new AcceptHeader(contentType);
        return this;
    }

    /**
     * Set the Accept header of the request.
     *
     * @param acceptHeader The accept header of the request.
     *
     * @return the updated FluentRequest
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> accept(AcceptHeader acceptHeader) {
        this.accept = acceptHeader;
        return this;
    }

    /**
     * Set the strategy for appending parameters with multiple values.
     *
     * @param strategy the strategy
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if strategy is null
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> multivaluedParamStrategy(MultivaluedParamStrategy strategy)
            throws IllegalArgumentException {
        uriBuilder.multivaluedParamStrategy(strategy);
        return this;
    }

    /**
     * Set the URI scheme.
     *
     * @param scheme the URI scheme; a null value will unset the URI scheme
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if scheme is invalid
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> scheme(String scheme) throws IllegalArgumentException {
        uriBuilder.scheme(scheme);
        return this;
    }

    /**
     * Set the URI host.
     *
     * @param host the URI host; a null value will unset the host component of the URI
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if host is invalid
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> host(String host) throws IllegalArgumentException {
        uriBuilder.host(host);
        return this;
    }

    /**
     * Set the URI port.
     *
     * @param port the URI port; a negative value will unset an explicit port
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if port is invalid
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> port(int port) throws IllegalArgumentException {
        uriBuilder.port(port);
        return this;
    }

    /**
     * Set the URI path. This method will overwrite any existing path and associated matrix parameters.
     * Existing '/' characters are preserved thus a single value can represent multiple URI path segments.
     *
     * @param path the path; a null value will unset the path component of the URI
     *
     * @return the updated FluentRequestSender
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> path(String path) {
        uriBuilder.path(path);
        return this;
    }

    /**
     * Append path segments to the existing path. When constructing the final path, a '/' separator will be inserted
     * between the existing path and the first path segment if necessary and each supplied segment will also be
     * separated by '/'. Existing '/' characters are encoded thus a single value can only represent a single URI path
     * segment.
     *
     * @param segments the path segment values
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if segments or any element of segments is null
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> segment(Object... segments)
            throws IllegalArgumentException {
        uriBuilder.segment(segments);
        return this;
    }

    /**
     * Append a matrix parameter to the existing set of matrix parameters of the current final segment of the URI path.
     * If multiple values are supplied the parameter will be added once per value. Note that the matrix parameters are
     * tied to a particular path segment; subsequent addition of path segments will not affect their position in the URI
     * path.
     *
     * @param name   the matrix parameter name
     * @param values the matrix parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if name or values is null
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> matrixParam(String name, Object... values)
            throws IllegalArgumentException {
        uriBuilder.matrixParam(name, values);
        return this;
    }

    /**
     * Append a query parameter to the existing set of query parameters. If multiple values are supplied the parameter
     * will be added once per value.
     *
     * @param name   the query parameter name
     * @param values the query parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if name or values is null
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> queryParam(String name, Object... values)
            throws IllegalArgumentException {
        uriBuilder.queryParam(name, values);
        return this;
    }

    /**
     * Set the URI fragment.
     *
     * @param fragment the URI fragment. A null value will remove any existing fragment
     *
     * @return the updated FluentRequestSender
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> fragment(String fragment) {
        uriBuilder.fragment(fragment);
        return this;
    }

    /**
     * Deserialize result to T.
     *
     * @param type The class from T
     * @param <T> The type to be deserialized
     *
     * @return The new FluentRequest capable of deserializing T
     *
     * @throws IllegalArgumentException if no Deserializer is registered for type T
     */
    public <T> FluentRequestSender<RequestType, T> deserializeAs(Class<T> type) throws IllegalArgumentException {
        FluentRequestImpl<RequestType, T> newReq
                = new FluentRequestImpl<>(filterManager, serdesManager, requestType, type, containerFactoryManager);
        copyFieldsTo(newReq);
        return newReq;
    }

    /**
     * Serialize request data from T.
     *
     * @param type The class from T
     * @param <T> The type to be serialized
     *
     * @return The new FluentRequest capable of serializing T
     *
     * @throws IllegalArgumentException if no Serializer is registered for type T
     */
    public <T> FluentRequestSender<T, ResponseType> serializeAs(Class<T> type) throws IllegalArgumentException {
        FluentRequestImpl<T, ResponseType> newReq
                = new FluentRequestImpl<>(filterManager, serdesManager, type, responseType, containerFactoryManager);
        copyFieldsTo(newReq);
        return newReq;
    }

    /**
     * Serialize and Deserialize transmitting data from/to T.
     *
     * @param type The class from T
     * @param <T> The type to be de/serialized
     *
     * @return The new FluentRequest capable of de/serializing T
     *
     * @throws IllegalArgumentException if no Deserializer or Serializer is registered for type T
     */
    public <T> FluentRequestSender<T, T> serializeDeserializeAs(Class<T> type) throws IllegalArgumentException {
        FluentRequestImpl<T, T> newReq = new FluentRequestImpl<>(filterManager, serdesManager, type, type,
                containerFactoryManager);
        copyFieldsTo(newReq);
        return newReq;
    }

    /**
     * Sets a request header with the given name and value. If a header with the
     * specified name has already been set then the new value overwrites the
     * current value.
     *
     * @param header the name of the header
     * @param value the value of the header
     *
     * @return the updated FluentRequestSender
     *
     * @throws NullPointerException if header or value are null
     * @throws IllegalArgumentException if header or value are the empty string
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> header(String header, String value) {
        ensureHeaders();
        headers.add(new SimpleHeader(header, value));
        return this;
    }

    /**
     * Sets a request header. If a header with the specified name has already been set then the new value overwrites the
     * current value.
     *
     * @param header the header instance
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> header(Header header) {
        ensureHeaders();
        headers.add(header);
        return this;
    }

    /**
     * Sets the user name that will be used in the request URL.
     *
     * @param user user name to use
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if the user is empty
     * @throws NullPointerException if the user is null
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Sets the password to use in the request URL. This is ignored if there is no
     * user specified.
     *
     * @param password password to use in the request URL
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if the password is empty
     * @throws NullPointerException if the password is null
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Sets the number of milliseconds to wait for a request to complete. Should
     * the request timeout, the
     * {@link com.google.gwt.http.client.RequestCallback#onError(Request, Throwable)}
     * method will be called on the callback instance given to the
     * {@link com.google.gwt.http.client.RequestBuilder#sendRequest(String, RequestCallback)}
     * method. The callback method will receive an instance of the
     * {@link com.google.gwt.http.client.RequestTimeoutException} class as its
     * {@link Throwable} argument.
     *
     * @param timeoutMillis number of milliseconds to wait before canceling the
     *          request, a value of zero disables timeouts
     *
     * @return the updated FluentRequestSender
     *
     * @throws IllegalArgumentException if the timeout value is negative
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> timeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
        return this;
    }

    /**
     * Set a callback to handle specific HTTP status code response.
     * <p/>
     * The informed code can represent a group of codes, e.g. 4 will handle any code in [400,499].
     * Similarly, 20 will handle any code in [200,209].
     * <p/>
     * The codes have priority for specificity, e.g. 201 has a higher priority than 20,
     * which has a higher priority than 2.
     *
     * @param statusCode    the unit, dozen or hundred expected on response's status code.
     * @param callback      the callback to handle informed code
     *
     * @return the updated FluentRequestSender
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> on(int statusCode, SingleCallback callback) {
        if (mappedCallbacks == null) mappedCallbacks = JsMap.create();
        mappedCallbacks.put(String.valueOf(statusCode), callback);
        return this;
    }

    /**
     * @see FluentRequest#always(SingleCallback) always(callback)
     */
    @Override
    public FluentRequestSender<RequestType, ResponseType> always(SingleCallback callback) {
        if (alwaysCallbacks == null) alwaysCallbacks = JsArray.create();
        alwaysCallbacks.push(callback);
        return this;
    }

    @Override
    public Request get(AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.GET, (String) null, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request get(A callback) {
        return send(RequestBuilder.GET, (String) null, callback);
    }

    @Override
    public Request get() {
        return send(RequestBuilder.GET, (String) null, null);
    }

    @Override
    public Request post(RequestType data) {
        return send(RequestBuilder.POST, data, null);
    }

    @Override
    public <C extends Collection<RequestType>> Request post(C dataCollection) {
        return send(RequestBuilder.POST, dataCollection, null);
    }

    @Override
    public Request post(RequestType data, AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.POST, data, callback);
    }

    @Override
    public <C extends Collection<RequestType>> Request post(C dataCollection, AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.POST, dataCollection, callback);
    }

    @Override
    public <C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request post(C dataCollection, A callback) {
        return send(RequestBuilder.POST, dataCollection, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request post(RequestType data, A callback) {
        return send(RequestBuilder.POST, data, callback);
    }

    @Override
    public Request post(AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.POST, (String) null, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request post(A callback) {
        return send(RequestBuilder.POST, (String) null, callback);
    }

    @Override
    public Request post() {
        return send(RequestBuilder.POST, (String) null, null);
    }

    @Override
    public Request put(RequestType data) {
        return send(RequestBuilder.PUT, data, null);
    }

    @Override
    public <C extends Collection<RequestType>> Request put(C dataCollection) {
        return send(RequestBuilder.PUT, dataCollection, null);
    }

    @Override
    public Request put(RequestType data, AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.PUT, data, callback);
    }

    @Override
    public <C extends Collection<RequestType>> Request put(C dataCollection, AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.PUT, dataCollection, callback);
    }

    @Override
    public <C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request put(C dataCollection, A callback) {
        return send(RequestBuilder.PUT, dataCollection, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request put(RequestType data, A callback) {
        return send(RequestBuilder.PUT, data, callback);
    }

    @Override
    public Request put(AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.PUT, (String) null, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request put(A callback) {
        return send(RequestBuilder.PUT, (String) null, callback);
    }

    @Override
    public Request put() {
        return send(RequestBuilder.PUT, (String) null, null);
    }

    @Override
    public Request delete(AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.DELETE, (String) null, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request delete(A callback) {
        return send(RequestBuilder.DELETE, (String) null, callback);
    }

    @Override
    public Request delete() {
        return send(RequestBuilder.DELETE, (String) null, null);
    }

    @Override
    public Request head(AsyncCallback<ResponseType> callback) {
        return send(RequestBuilder.HEAD, (String) null, callback);
    }

    @Override
    public <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request head(A callback) {
        return send(RequestBuilder.HEAD, (String) null, callback);
    }

    @Override
    public Request head() {
        return send(RequestBuilder.HEAD, (String) null, null);
    }

    /**
     * Directly set URI for request.
     * It will override any uri in construction.
     *
     * @param uri The URI for requesting
     *
     * @return the updated FluentRequestSender
     */
    protected FluentRequestSender<RequestType, ResponseType> setUri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Build request and send it.
     * If the request could not be sent then the returned {@link Request} is null and resultCallback#onError is called.
     *
     * @param dataCollection    The data collection to be serialized and sent into the request body
     * @param resultCallback    The user callback
     *
     * @return the sent request.
     */
    private <C extends Collection<RequestType>> Request send(RequestBuilder.Method method,
                                                             @Nullable C dataCollection,
                                                             @Nullable AsyncCallback resultCallback) {
        String body = null;
        if (dataCollection != null) {
            // Serializer init was verified on construction
            body = getSerializer().serializeFromCollection(dataCollection, SerializationContext.of(headers));
        }
        return send(method, body, resultCallback);
    }

    /**
     * Build request and send it.
     * If the request could not be sent then the returned {@link Request} is null and resultCallback#onError is called.
     *
     * @param data              The data to be serialized and sent into the request body
     * @param resultCallback    The user callback
     *
     * @return the sent request
     */
    private Request send(RequestBuilder.Method method, @Nullable RequestType data,
                         @Nullable AsyncCallback resultCallback) {
        String body = null;
        if (data != null) {
            // Serializer init was verified on construction
            body = getSerializer().serialize(data, SerializationContext.of(headers));
        }
        return send(method, body, resultCallback);
    }

    /**
     * Build request and send it.
     * If the request could not be sent then the returned {@link Request} is null and resultCallback#onError is called.
     *
     * @param body              The content to be sent in the request body
     * @param resultCallback    The user callback
     *
     * @return the sent request
     */
    @SuppressWarnings("unchecked")
    private Request send(RequestBuilder.Method method, @Nullable String body,
                         @Nullable final AsyncCallback resultCallback) {
        // Prepare callback for following request builder
        RequestCallback callback = new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                final String code = String.valueOf(response.getStatusCode());

                // Execute filters on this response
                final List<ResponseFilter> filters = filterManager.getResponseFilters();
                for (ResponseFilter filter : filters) {
                    filter.filter(request, response);
                }

                // Check if the response matches any mapped status code
                if (mappedCallbacks != null) {
                    JsArrayString codes = getMappedCodes();
                    for (int i = 0; i < codes.length(); i++) {
                        String mappedCode = codes.get(i);
                        if (code.startsWith(mappedCode)) {
                            // Status code matched. Execute callback and finish
                            SingleCallback singleCallback = mappedCallbacks.get(mappedCode);
                            singleCallback.onResponseReceived(request, response);
                            return;
                        }
                    }
                }

                // Successful response
                if (code.startsWith("2")) {
                    final String body = response.getText();
                    if (resultCallback != null) {
                        if (body != null && !body.isEmpty()) {
                            Headers responseHeaders = new Headers(response.getHeaders());
                            // Check AsyncCallback type in order to correctly deserialize
                            deserializeAndCallOnSuccess(body, responseHeaders, resultCallback);
                        } else {
                            resultCallback.onSuccess(null);
                        }
                    }
                    executeAwaysCallbacks(request, response);
                    return;
                }

                // Unsuccessful response
                if (resultCallback != null)
                    resultCallback.onFailure(new UnsuccessfulResponseException(request, response));

                executeAwaysCallbacks(request, response);
            }

            @Override
            public void onProgress(RequestProgress requestProgress) {
                // do nothing
            }

            @Override
            public void onError(Request request, Throwable exception) {
                if (resultCallback != null) resultCallback.onFailure(exception);

                executeAwaysCallbacks(request, null);
            }

            private void executeAwaysCallbacks(Request request, Response response) {
                if (alwaysCallbacks == null || alwaysCallbacks.length() <= 0)
                    return;
                int length = alwaysCallbacks.length();
                for (int i = 0; i < length; i++) {
                    alwaysCallbacks.get(i).onResponseReceived(request, response);
                }
            }
        };

        // If the uri was not set via #setUri, then build it.
        if (uri == null) uri = uriBuilder.build().toString();

        // Ensure required fields
        ensureHeaders();

        // Execute filters on this request
        final List<RequestFilter> filters = filterManager.getRequestFilters();
        for (RequestFilter filter : filters) {
            filter.filter(this);
        }

        ServerConnection connection = server.getConnection();

        try {
            connection.sendRequest(timeout, user, password, headers, method, uri, body, callback);
        } catch (RequestException e) {
            if (resultCallback != null) resultCallback.onFailure(e);
        } finally {
            uri = null; // Avoid caching problems. TODO: add caching capabilities to UriBuilderImpl
        }

        return null;
    }

    /**
     * Get the HTTP codes registered with special callbacks in order of priority from the most specific to the least.
     *
     * @return The registered codes as an array of String
     */
    private JsArrayString getMappedCodes() {
        JsArrayString codes = Overlays.getPropertyNames(mappedCallbacks, true);
        // Reverse order to check from most specific to generic
        codes = reverse(codes);
        return codes;
    }

    /**
     * Performs deserialization of the HTTP body checking whether it should be deserialized into a Collection or a
     * single Object.
     *
     * @param body The content from HTTP response
     * @param responseHeaders The headers from HTTP response
     * @param resultCallback The user callback
     */
    @SuppressWarnings("unchecked")
    private void deserializeAndCallOnSuccess(String body, Headers responseHeaders,
                                             AsyncCallback resultCallback) {
        final String responseContentType = responseHeaders.getValue("Content-Type");

        Deserializer<ResponseType> deserializer;
        try {
            deserializer = serdesManager.getDeserializer(responseType, responseContentType);
        } catch (SerializationException e) {
            resultCallback.onFailure(new UnableToDeserializeException("Could not deserialize response of content-type *"
                    + responseContentType + "*.", e));
            return;
        }

        if (resultCallback instanceof ContainerAsyncCallback) {
            ContainerAsyncCallback<Collection<ResponseType>, ResponseType> cac =
                    (ContainerAsyncCallback<Collection<ResponseType>, ResponseType>) resultCallback;
            Class<Collection<ResponseType>> collectionType = (Class<Collection<ResponseType>>) cac.getContainerClass();
            cac.onSuccess(deserializer.deserializeAsCollection(collectionType, body,
                    DeserializationContext.of(responseHeaders, containerFactoryManager)));
        } else {
            ResponseType result = deserializer.deserialize(body,
                    DeserializationContext.of(responseHeaders, containerFactoryManager));
            resultCallback.onSuccess(result);
        }
    }

    /**
     * Copies all internal fields to a new {@link FluentRequestImpl} instance.
     *
     * @param newReq The new instance
     */
    private void copyFieldsTo(FluentRequestImpl<?, ?> newReq) {
        newReq.uriBuilder = uriBuilder;
        newReq.uri = uri;
        newReq.mappedCallbacks = mappedCallbacks;
        newReq.headers = headers;
        newReq.user = user;
        newReq.password = password;
        newReq.timeout = timeout;
    }

    private void ensureHeaders() {
        if (headers == null) {
            headers = new Headers();
            headers.add(new ContentTypeHeader(contentType));
            headers.add(accept);
        }
    }

    private Serializer<RequestType> getSerializer() {
        try {
            return serdesManager.getSerializer(requestType, contentType);
        } catch (SerializationException e) {
            throw new SerializationException("Serializer of RequestType *" + requestType.getName() +
                    "* and content type *" + contentType + "* was not registered.", e);
        }
    }

    private static native JsArrayString reverse(JsArrayString array) /*-{
        return array.reverse();
    }-*/;
}
