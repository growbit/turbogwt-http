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

package org.turbogwt.net.http;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;

/**
 * Represents a class capable of submitting requests with content and receiving responses with content.
 *
 * @param <RequestType> Type of data to be sent in the HTTP request body, when appropriate.
 * @param <ResponseType> Type of result from requests, when appropriate.
 *
 * @author Danilo Reinert
 */
public interface RequestSender<RequestType, ResponseType> {

    //===================================================================
    // GET
    //===================================================================
    Request get();

    Request get(AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>> Request get(A callback);

    //===================================================================
    // POST
    //===================================================================
    Request post();

    //-------------------------------------------------------------------
    // Post with DATA only
    //-------------------------------------------------------------------

    Request post(RequestType data);

    <C extends Collection<RequestType>> Request post(C dataCollection);

    //-------------------------------------------------------------------
    // Post with DATA and CALLBACK
    //-------------------------------------------------------------------

    Request post(RequestType data, AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request post(RequestType data, A callback);

    <C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request post(C dataCollection, A callback);

    <C extends Collection<RequestType>> Request post(C dataCollection, AsyncCallback<ResponseType> callback);

    //-------------------------------------------------------------------
    // Post with CALLBACK only
    //-------------------------------------------------------------------

    Request post(AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>> Request post(A callback);

    //===================================================================
    // PUT
    //===================================================================
    Request put();

    //-------------------------------------------------------------------
    // Put with DATA only
    //-------------------------------------------------------------------

    Request put(RequestType data);

    <C extends Collection<RequestType>> Request put(C dataCollection);

    //-------------------------------------------------------------------
    // Put with DATA and CALLBACK
    //-------------------------------------------------------------------

    Request put(RequestType data, AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>>
    Request put(RequestType data, A callback);

    <C extends Collection<RequestType>, B extends Collection<ResponseType>,
            A extends ContainerAsyncCallback<B, ResponseType>> Request put(C dataCollection, A callback);

    <C extends Collection<RequestType>> Request put(C dataCollection, AsyncCallback<ResponseType> callback);

    //-------------------------------------------------------------------
    // Put with CALLBACK only
    //-------------------------------------------------------------------

    Request put(AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>> Request put(A callback);

    //===================================================================
    // DELETE
    //===================================================================
    Request delete();

    Request delete(AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>> Request delete(A callback);

    //===================================================================
    // HEAD
    //===================================================================
    Request head();

    Request head(AsyncCallback<ResponseType> callback);

    <C extends Collection<ResponseType>, A extends ContainerAsyncCallback<C, ResponseType>> Request head(A callback);
}
