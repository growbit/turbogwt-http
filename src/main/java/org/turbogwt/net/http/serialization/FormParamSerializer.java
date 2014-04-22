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

import org.turbogwt.net.http.FormData;
import org.turbogwt.net.http.FormParam;
import org.turbogwt.net.http.MultipleParamStrategy;

/**
 * @author Danilo Reinert
 */
public class FormParamSerializer implements Serializer<FormParam> {

    public static String[] CONTENT_TYPE_PATTERNS = new String[]{"application/x-www-form-urlencoded"};

    private static final FormParamSerializer INSTANCE = new FormParamSerializer();

    public static FormParamSerializer getInstance() {
        return INSTANCE;
    }

    private MultipleParamStrategy multipleParamStrategy = MultipleParamStrategy.REPEATED_PARAM;

    /**
     * Method for accessing type of Objects this deserializer can handle.
     *
     * @return The class which this serializer can serialize
     */
    @Override
    public Class<FormParam> handledType() {
        return FormParam.class;
    }

    /**
     * Informs the content type this serializer serializes.
     *
     * @return The content type serialized.
     */
    @Override
    public String[] contentType() {
        return CONTENT_TYPE_PATTERNS;
    }

    /**
     * Serialize T to plain text.
     *
     * @param formParam The object to be serialized
     * @param context   Context of the serialization
     *
     * @return The object serialized.
     */
    @Override
    public String serialize(FormParam formParam, SerializationContext context) {
        return multipleParamStrategy.asUriPart("&", formParam.getName(), formParam.getValues());
    }

    /**
     * Serialize a collection of T to plain text.
     *
     * @param c       The collection of the object to be serialized
     * @param context Context of the serialization
     *
     * @return The object serialized.
     */
    @Override
    public String serializeFromCollection(Collection<FormParam> c, SerializationContext context) {
        MultipleParamStrategy strategy = multipleParamStrategy;
        if (c instanceof FormData) {
            FormData data = (FormData) c;
            if (data.getMultipleParamStrategy() != null) strategy = data.getMultipleParamStrategy();
        }
        String serialized = "";
        String sep = "";
        for (FormParam formParam : c) {
            serialized += sep + strategy.asUriPart("&", formParam.getName(), formParam.getValues());
            sep = "&";
        }
        return serialized;
    }

    public MultipleParamStrategy getMultipleParamStrategy() {
        return multipleParamStrategy;
    }

    public void setMultipleParamStrategy(MultipleParamStrategy multipleParamStrategy) {
        this.multipleParamStrategy = multipleParamStrategy;
    }
}
