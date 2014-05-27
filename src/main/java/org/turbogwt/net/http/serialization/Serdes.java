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

package org.turbogwt.net.http.serialization;

/**
 * Performs serialization and deserialization of a type to/from plain text.
 *
 * @param <T> Type of serialization
 *
 * @author Danilo Reinert
 */
public interface Serdes<T> extends Deserializer<T>, Serializer<T> {

    /**
     * Method for accessing type of the Object this de/serializer can handle.
     *
     * @return The class which this de/serializer can de/serialize
     */
    @Override
    Class<T> handledType();
}
