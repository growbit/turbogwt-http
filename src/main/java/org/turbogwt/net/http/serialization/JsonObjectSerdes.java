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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import java.util.Collection;

import org.turbogwt.core.js.Overlays;

/**
 * Base class for all SerDes that manipulates serialized JSON objects.
 *
 * @param <T>   Type of the object to serialize/deserialize.
 *
 * @author Danilo Reinert
 */
public abstract class JsonObjectSerdes<T> extends JsonSerdes<T> {

    public JsonObjectSerdes(Class<T> handledType) {
        super(handledType);
    }

    /**
     * Recover an instance of T from deserialized JSON.
     *
     * @param reader    The evaluated response
     * @param context   Context of the deserialization
     *
     * @return The object deserialized
     */
    public abstract T readJson(JsonRecordReader reader, DeserializationContext context);

    /**
     * Build a JSON using {@link JsonRecordWriter}.
     * Later this JSON will be serialized using JSON#stringify.
     *
     * @param t         The object to be serialized
     * @param writer    The serializing JSON
     * @param context   Context of the serialization
     */
    public abstract void writeJson(T t, JsonRecordWriter writer, SerializationContext context);

    /**
     * Deserialize the plain text into an object of type T.
     *
     * @param response  Http response body content
     * @param context   Context of the deserialization
     *
     * @return The object deserialized
     */
    @Override
    public T deserialize(String response, DeserializationContext context) {
        if (!isObject(response))
            throw new UnableToDeserializeException("Response content is not an object");
        final JavaScriptObject deserialized = eval(response);
        return readJson((JsonRecordReader) deserialized, context);
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
        if (!isArray(response)) throw new UnableToDeserializeException("Response content is not an array.");

        C col = getCollectionInstance(context, collectionType);
        @SuppressWarnings("unchecked")
        JsArray<JavaScriptObject> jsArray = (JsArray<JavaScriptObject>) eval(response);
        for (int i = 0; i < jsArray.length(); i++) {
            JavaScriptObject jso = jsArray.get(i);
            col.add(readJson((JsonRecordReader) jso, context));
        }
        return col;
    }

    /**
     * Serialize T to plain text.
     *
     * @param t         The object to be serialized
     * @param context   Context of the deserialization
     *
     * @return The object serialized
     */
    @Override
    public String serialize(T t, SerializationContext context) {
        final JsonRecordWriter writer = JsonRecordWriter.create();
        writeJson(t, writer, context);
        return Overlays.stringify(writer);
    }

    /**
     * Verifies if the deserializer should evaluate the response safely.
     * <p/>
     * If this method returns <code>true</code>, then the deserializer will evaluate the response using
     * {@link com.google.gwt.core.client.JsonUtils#safeEval(String)}, otherwise it will use
     * {@link com.google.gwt.core.client.JsonUtils#unsafeEval(String)}.
     * <p/>
     * If you are completely sure you'll will always receive safe contents, then you can override it
     * to return <code>false</code> and you'll benefit a faster deserialization.
     * <p/>
     * The default implementation is <code>true</code>.
     *
     * @return  <code>true</code> if you want to evaluate response safely,
     *          or <code>false</code> to evaluate unsafely
     */
    public boolean useSafeEval() {
        return true;
    }

    protected boolean isObject(String text) {
        final String trim = text.trim();
        return trim.startsWith("{") && trim.endsWith("}");
    }

    protected JavaScriptObject eval(String response) {
        return useSafeEval() ? JsonUtils.safeEval(response) : JsonUtils.unsafeEval(response);
    }
}
