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

package org.turbogwt.net.serialization.client;

import java.util.Collection;

/**
 * Performs serialization of types.
 *
 * @param <T> The type it can serialize
 *
 * @author Danilo Reinert
 */
public interface Serializer<T> {

    /**
     * Method for accessing type of the Object this serializer can handle.
     *
     * @return The class which this serializer can serialize
     */
    Class<T> handledType();

    /**
     * Tells the content-type patterns which this serializer handles.
     * <p/>
     * 
     * E.g., a serializer for JSON can return {"application/json", "application/javascript"}.<br>
     * If you want to create a serializer for any content-type just return "*&#47;*".
     *
     * @return The content-type patterns handled by this serializer.
     */
    String[] contentType();

    /**
     * Serialize T to plain text.
     *
     * @param t         The object to be serialized
     * @param context   Context of the serialization
     *
     * @return The object serialized.
     */
    String serialize(T t, SerializationContext context);

    /**
     * Serialize a collection of T to plain text.
     *
     * @param c        The collection of the object to be serialized
     * @param context   Context of the serialization
     *
     * @return The object serialized.
     */
    String serializeFromCollection(Collection<T> c, SerializationContext context);
}
