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

package org.turbogwt.net.http.client.serialization;

import java.util.Collection;

/**
 * De/Serializer for Void type.
 * Returns null for every method.
 *
 * @author Danilo Reinert
 */
public class VoidSerdes implements Serdes<Void> {

    public static final String[] CONTENT_TYPE_PATTERNS = new String[] {"*/*"};

    private static VoidSerdes INSTANCE = new VoidSerdes();

    public static VoidSerdes getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<Void> handledType() {
        return Void.class;
    }

    @Override
    public String[] contentType() {
        return CONTENT_TYPE_PATTERNS;
    }

    @Override
    public String[] accept() {
        return CONTENT_TYPE_PATTERNS;
    }

    @Override
    public Void deserialize(String response, DeserializationContext context) {
        return null;
    }

    @Override
    public <C extends Collection<Void>> C deserializeAsCollection(Class<C> collectionType, String response,
                                                                  DeserializationContext context) {
        return null;
    }

    @Override
    public String serialize(Void v, SerializationContext context) {
        return null;
    }

    @Override
    public String serializeFromCollection(Collection<Void> c, SerializationContext context) {
        return null;
    }
}
