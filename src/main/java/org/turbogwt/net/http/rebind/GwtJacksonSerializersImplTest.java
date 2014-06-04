package org.turbogwt.net.http.rebind;

import com.github.nmorel.gwtjackson.client.ObjectWriter;
import com.google.gwt.core.shared.GWT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.turbogwt.net.http.client.GwtJacksonSerializers;
import org.turbogwt.net.http.client.serialization.SerializationContext;
import org.turbogwt.net.http.client.serialization.Serializer;

/**
 * @author Danilo Reinert
 */
public class GwtJacksonSerializersImplTest implements GwtJacksonSerializers {

    interface StringWriter extends ObjectWriter<String> { }
    interface StringCollectionWriter extends ObjectWriter<Collection<String>> { }

    private final StringWriter stringWriter = GWT.create(StringWriter.class);
    private final StringCollectionWriter stringCollectionWriter = GWT.create(StringCollectionWriter.class);
    private final Serializer<String> serializer = new Serializer<String>() {
        @Override
        public Class<String> handledType() {
            return String.class;
        }

        @Override
        public String[] contentType() {
            return new String[]{"*/*", "dsa"};
        }

        @Override
        public String serialize(String s, SerializationContext context) {
            return stringWriter.write(s);
        }

        @Override
        public String serializeFromCollection(Collection<String> c, SerializationContext context) {
            return stringCollectionWriter.write(c);
        }
    };

    private final List<Serializer<?>> serializerList = new ArrayList<>();

    public GwtJacksonSerializersImplTest() {
        serializerList.add(serializer);
    }

    @Override
    public Iterator<Serializer<?>> iterator() {
        return serializerList.iterator();
    }
}
