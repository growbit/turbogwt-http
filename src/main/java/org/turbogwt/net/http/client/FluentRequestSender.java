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

/**
 * This class provides fluent style request building and sending capabilities.
 *
 * @param <RequestType> Type of data to be sent in the HTTP request body, when appropriate.
 * @param <ResponseType> Type of result from requests, when appropriate.
 *
 * @author Danilo Reinert
 */
public interface FluentRequestSender<RequestType, ResponseType> extends FluentRequest<RequestType, ResponseType>,
        RequestSender<RequestType, ResponseType> {

    /**
     * Deserialize result to T.
     *
     * @param type The class from T.
     * @param <T> The type to be deserialized.
     * @return The new FluentRequest capable of deserializing T.
     * @throws IllegalArgumentException if no Deserializer is registered for type T.
     */
    <T> FluentRequestSender<RequestType, T> deserializeAs(Class<T> type) throws IllegalArgumentException;

    /**
     * Serialize request data from T.
     *
     * @param type The class from T.
     * @param <T> The type to be serialized.
     *
     * @return The new FluentRequest capable of serializing T.
     *
     * @throws IllegalArgumentException if no Serializer is registered for type T.
     */
    <T> FluentRequestSender<T, ResponseType> serializeAs(Class<T> type) throws IllegalArgumentException;

    /**
     * Serialize and Deserialize transmitting data from/to T.
     *
     * @param type The class from T.
     * @param <T> The type to be de/serialized.
     *
     * @return The new FluentRequest capable of de/serializing T.
     *
     * @throws IllegalArgumentException if no Deserializer or Serializer is registered for type T.
     */
    <T> FluentRequestSender<T, T> serializeDeserializeAs(Class<T> type) throws IllegalArgumentException;

    // TODO: Support directly setting serializer/deserializer?
}
