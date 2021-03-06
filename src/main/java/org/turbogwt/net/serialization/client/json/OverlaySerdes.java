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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import java.util.Collection;
import java.util.List;

import org.turbogwt.core.collections.client.JsArrayList;
import org.turbogwt.core.util.client.Overlays;
import org.turbogwt.net.serialization.client.DeserializationContext;
import org.turbogwt.net.serialization.client.Serdes;
import org.turbogwt.net.serialization.client.SerializationContext;

/**
 * Serializer/Deserializer of Overlay types.
 *
 * @param <T> The overlay type of the data to be serialized.
 *
 * @author Danilo Reinert
 */
public class OverlaySerdes<T extends JavaScriptObject> implements Serdes<T> {

    private static OverlaySerdes<JavaScriptObject> INSTANCE = new OverlaySerdes<>();

    @SuppressWarnings("unchecked")
    public static <O extends JavaScriptObject> OverlaySerdes<O> getInstance() {
        return (OverlaySerdes<O>) INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> handledType() {
        return (Class<T>) JavaScriptObject.class;
    }

    @Override
    public String[] contentType() {
        return JsonSerdes.CONTENT_TYPE_PATTERNS;
    }

    @Override
    public String[] accept() {
        return JsonSerdes.ACCEPT_PATTERNS;
    }

    @Override
    public T deserialize(String response, DeserializationContext context) {
        return JsonUtils.safeEval(response);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C extends Collection<T>> C deserializeAsCollection(Class<C> collectionType, String response,
                                                               DeserializationContext context) {
        JsArray<T> jsArray = JsonUtils.safeEval(response);
        if (collectionType.equals(List.class) || collectionType.equals(Collection.class)) {
            return (C) new JsArrayList(jsArray);
        } else {
            C col = context.getContainerInstance(collectionType);
            for (int i = 0; i < jsArray.length(); i++) {
                T t = jsArray.get(i);
                col.add(t);
            }
            return col;
        }
    }

    @Override
    public String serialize(T t, SerializationContext context) {
        return Overlays.stringify(t);
    }

    @Override
    public String serializeFromCollection(Collection<T> c, SerializationContext context) {
        if (c instanceof JsArrayList) {
            return Overlays.stringify(((JsArrayList<T>) c).asJsArray());
        }

        if (c instanceof JavaScriptObject) {
            return Overlays.stringify((JavaScriptObject) c);
        }

        @SuppressWarnings("unchecked")
        JsArray<T> jsArray = (JsArray<T>) JsArray.createArray();
        for (T t : c) {
            jsArray.push(t);
        }
        return Overlays.stringify(jsArray);
    }
}
