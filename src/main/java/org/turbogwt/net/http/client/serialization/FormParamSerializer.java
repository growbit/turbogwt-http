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

import org.turbogwt.net.client.MultivaluedParamStrategy;
import org.turbogwt.net.http.client.FormData;
import org.turbogwt.net.http.client.FormParam;
import org.turbogwt.net.serialization.client.SerializationContext;
import org.turbogwt.net.serialization.client.Serializer;

/**
 * Serializer for FORM requests.
 *
 * @author Danilo Reinert
 */
public class FormParamSerializer implements Serializer<FormParam> {

    public static String[] CONTENT_TYPE_PATTERNS = new String[]{"application/x-www-form-urlencoded"};

    private static final FormParamSerializer INSTANCE = new FormParamSerializer();

    private MultivaluedParamStrategy multivaluedParamStrategy = MultivaluedParamStrategy.REPEATED_PARAM;

    public static FormParamSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public Class<FormParam> handledType() {
        return FormParam.class;
    }

    @Override
    public String[] contentType() {
        return CONTENT_TYPE_PATTERNS;
    }

    @Override
    public String serialize(FormParam formParam, SerializationContext context) {
        return multivaluedParamStrategy.asUriPart("&", formParam.getName(), formParam.getValues());
    }

    @Override
    public String serializeFromCollection(Collection<FormParam> c, SerializationContext context) {
        MultivaluedParamStrategy strategy = multivaluedParamStrategy;
        if (c instanceof FormData) {
            FormData data = (FormData) c;
            if (data.getMultivaluedParamStrategy() != null) strategy = data.getMultivaluedParamStrategy();
        }
        String serialized = "";
        String sep = "";
        for (FormParam formParam : c) {
            serialized += sep + strategy.asUriPart("&", formParam.getName(), formParam.getValues());
            sep = "&";
        }
        return serialized;
    }

    public MultivaluedParamStrategy getMultivaluedParamStrategy() {
        return multivaluedParamStrategy;
    }

    public void setMultivaluedParamStrategy(MultivaluedParamStrategy multivaluedParamStrategy) {
        this.multivaluedParamStrategy = multivaluedParamStrategy;
    }
}
