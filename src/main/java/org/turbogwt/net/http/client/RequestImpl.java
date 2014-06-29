package org.turbogwt.net.http.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestCallbackWithProgress;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestProgress;
import com.google.gwt.http.client.Response;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.turbogwt.net.http.client.future.RequestProgressWrapper;
import org.turbogwt.net.http.client.future.RequestPromise;
import org.turbogwt.net.http.client.serialization.SerdesManager;
import org.turbogwt.net.http.client.serialization.SerializationContext;
import org.turbogwt.net.http.client.serialization.Serializer;

public class RequestImpl implements RequestDispatcher {

    private final Server server = GWT.create(Server.class);
    private final SerdesManager serdesManager;
    private final ContainerFactoryManager containerFactoryManager;
    private final UriBuilder uriBuilder = new UriBuilderImpl();
    private final FilterManager filterManager;
    private Headers headers;
    private String user;
    private String password;
    private int timeout;
    private String contentType;
    private AcceptHeader accept;
    private Object payload;

    public RequestImpl(String uri, SerdesManager serdesManager, ContainerFactoryManager containerFactoryManager,
                       FilterManager filterManager) {
        this.serdesManager = serdesManager;
        this.containerFactoryManager = containerFactoryManager;
        this.filterManager = filterManager;
        // TODO: parse URI
        uriBuilder.path(uri);
    }

    @Override
    public RequestDispatcher contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public RequestDispatcher accept(String contentType) {
        this.accept = new AcceptHeader(contentType);
        return this;
    }

    @Override
    public RequestDispatcher accept(AcceptHeader acceptHeader) {
        this.accept = acceptHeader;
        return this;
    }

    @Override
    public RequestDispatcher multivaluedParamStrategy(MultivaluedParamStrategy strategy)
            throws IllegalArgumentException {
        uriBuilder.multivaluedParamStrategy(strategy);
        return this;
    }

    @Override
    public RequestDispatcher header(String header, String value) {
        ensureHeaders().add(new SimpleHeader(header, value));
        return this;
    }

    @Override
    public RequestDispatcher header(Header header) {
        ensureHeaders().add(header);
        return this;
    }

    @Override
    public RequestDispatcher user(String user) {
//        uriBuilder.user(user);
        this.user = user;
        return this;
    }

    @Override
    public RequestDispatcher password(String password) {
//        uriBuilder.password(password);
        this.password = password;
        return this;
    }

    @Override
    public RequestDispatcher timeout(int timeoutMillis) {
        timeout = timeoutMillis;
        return this;
    }

    @Override
    public RequestDispatcher pathParam(String name, Object value) throws IllegalArgumentException {
        // TODO: implement
        return this;
    }

    @Override
    public RequestDispatcher matrixParam(String name, Object... values) throws IllegalArgumentException {
        uriBuilder.matrixParam(name, values);
        return this;
    }

    @Override
    public RequestDispatcher queryParam(String name, Object... values) throws IllegalArgumentException {
        uriBuilder.matrixParam(name, values);
        return this;
    }

    @Override
    public RequestDispatcher payload(Object object) throws IllegalArgumentException {
        payload = object;
        return this;
    }

    @Override
    public RequestPromise<Void> get() {
        return send(RequestBuilder.GET, Void.class);
    }

    @Override
    public <T> RequestPromise<T> get(Class<T> responseType) {
        return send(RequestBuilder.GET, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> get(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.GET, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> post() {
        return send(RequestBuilder.POST, Void.class);
    }

    @Override
    public <T> RequestPromise<T> post(Class<T> responseType) {
        return send(RequestBuilder.POST, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> post(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.POST, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> put() {
        return send(RequestBuilder.PUT, Void.class);
    }

    @Override
    public <T> RequestPromise<T> put(Class<T> responseType) {
        return send(RequestBuilder.PUT, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> put(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.PUT, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> delete() {
        return send(RequestBuilder.DELETE, Void.class);
    }

    @Override
    public <T> RequestPromise<T> delete(Class<T> responseType) {
        return send(RequestBuilder.DELETE, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> delete(Class<T> responseType,
                                                                          Class<C> containerType) {
        return send(RequestBuilder.DELETE, responseType, containerType);
    }

    @Override
    public RequestPromise<Void> head() {
        return send(RequestBuilder.HEAD, Void.class);
    }

    @Override
    public <T> RequestPromise<T> head(Class<T> responseType) {
        return send(RequestBuilder.HEAD, responseType);
    }

    @Override
    public <T, C extends Collection> RequestPromise<Collection<T>> head(Class<T> responseType, Class<C> containerType) {
        return send(RequestBuilder.HEAD, responseType, containerType);
    }

    private <T> RequestPromise<T> send(RequestBuilder.Method method, Class<T> responseType) {
        final DeferredSingleResult<T> deferred = new DeferredSingleResult<T>(responseType,
                serdesManager, containerFactoryManager);

        RequestCallback callback = createRequestCallback(deferred);

        dispatch(method, callback);

        return deferred;
    }

    private <T, C extends Collection> RequestPromise<Collection<T>> send(RequestBuilder.Method method,
                                                                         Class<T> responseType,
                                                                         Class<C> containerType) {
        final DeferredCollectionResult<T> deferred = new DeferredCollectionResult<T>(responseType, containerType,
                serdesManager, containerFactoryManager);

        RequestCallback callback = createRequestCallback(deferred);

        dispatch(method, callback);

        return deferred;
    }

    private RequestCallback createRequestCallback(final DeferredRequestDecorator deferred) {
        return new RequestCallbackWithProgress() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    // Execute filters on this response
                    final List<ResponseFilter> filters = filterManager.getResponseFilters();
                    for (ResponseFilter filter : filters) {
                        filter.filter(request, response);
                    }

                    if (response.getStatusCode()/100 == 2) {
                        deferred.resolve(response);
                    } else {
                        deferred.reject(response);
                    }
                }

                @Override
                public void onProgress(RequestProgress requestProgress) {
                    deferred.notify(new RequestProgressWrapper(requestProgress));
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    deferred.reject(exception);
                }
            };
    }

    private void dispatch(RequestBuilder.Method method, RequestCallback callback) {
        ensureHeaders();

        // Execute filters on this request
        // TODO: uncomment
//        final List<RequestFilter> filters = filterManager.getRequestFilters();
//        for (RequestFilter filter : filters) {
//            filter.filter(this);
//        }

        String body = serializePayload();

        ServerConnection connection = server.getConnection();

        try {
            final String url = uriBuilder.build().toString();
            connection.sendRequest(timeout, user, password, headers, method, url, body, callback);
        } catch (final RequestException e) {
            throw new RequestDispatchException("It was not possible to dispatch the request.", e);
        }
    }

    private String serializePayload() {
        String body = null;

        if (payload != null) {
            if (payload instanceof Collection) {
                Collection c = (Collection) payload;
                final Iterator iterator = c.iterator();
                Object item = null;
                while (iterator.hasNext() && item == null) {
                    item = iterator.next();
                }
                if (item == null) {
                    /* TODO: Create some EmptyCollectionSerializer Manager
                       for serialization of empty collections by content-type */
                    body = "[]";
                } else {
                    Serializer<?> serializer = serdesManager.getSerializer(item.getClass(), contentType);
                    body = serializer.serializeFromCollection(c, SerializationContext.of(ensureHeaders()));
                }
            } else {
                @SuppressWarnings("unchecked")
                Serializer<Object> serializer = (Serializer<Object>) serdesManager.getSerializer(payload.getClass(), contentType);
                body = serializer.serialize(payload, SerializationContext.of(ensureHeaders()));
            }
        }
        return body;
    }

    private Headers ensureHeaders() {
        if (headers == null) {
            headers = new Headers();
            headers.add(new ContentTypeHeader(contentType));
            headers.add(accept);
        }
        return headers;
    }
}