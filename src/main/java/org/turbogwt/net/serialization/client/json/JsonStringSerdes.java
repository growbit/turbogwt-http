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

package org.turbogwt.net.serialization.client.json;

import org.turbogwt.net.serialization.client.DeserializationContext;
import org.turbogwt.net.serialization.client.SerializationContext;

/**
 * De/Serializer of JSON strings.
 *
 * @author Danilo Reinert
 */
public class JsonStringSerdes extends JsonValueSerdes<String> {

    private static JsonStringSerdes INSTANCE = new JsonStringSerdes();

    public JsonStringSerdes() {
        super(String.class);
    }

    public static JsonStringSerdes getInstance() {
        return INSTANCE;
    }

    @Override
    public String deserialize(String response, DeserializationContext context) {
        return response.substring(1, response.length() - 1);
    }

    @Override
    public String serialize(String s, SerializationContext context) {
        return "\"" + s + "\"";
    }
}
