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

import java.util.Collection;

/**
 * Base class for all SerDes that manipulates serialized JSON simple values.
 *
 * @param <T>   Type of the object to serialize/deserialize
 *
 * @author Danilo Reinert
 */
public abstract class JsonValueSerdes<T> extends JsonSerdes<T> {

    public JsonValueSerdes(Class<T> handledType) {
        super(handledType);
    }

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param collectionType The class of the collection
     * @param response       Http response body content
     * @param context        Context of the deserialization
     *
     * @return The object deserialized
     */
    @Override
    public <C extends Collection<T>> C deserializeAsCollection(Class<C> collectionType, String response,
                                                               DeserializationContext context) {
        final String trimmedResponse = response.trim();
        if (!isArray(trimmedResponse)) throw new UnableToDeserializeException("Response content is not an array.");

        C col = getCollectionInstance(context, collectionType);

        int initialIndex = 1;
        final int lastIndex = trimmedResponse.length() - 1;
        while (initialIndex < lastIndex) {
            int finalIndex = trimmedResponse.indexOf(",", initialIndex);
            if (finalIndex == -1) finalIndex = trimmedResponse.indexOf("]", initialIndex);
            final String trimmedValue = trimmedResponse.substring(initialIndex, finalIndex).trim();
            col.add(deserialize(trimmedValue, context));
            initialIndex = finalIndex + 1;
        }

        return col;
    }
}
