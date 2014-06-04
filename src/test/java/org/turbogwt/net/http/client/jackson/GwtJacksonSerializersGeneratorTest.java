package org.turbogwt.net.http.client.jackson;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import java.util.Arrays;
import java.util.Iterator;

import org.turbogwt.net.http.client.GwtJacksonSerializers;
import org.turbogwt.net.http.client.books.Book;
import org.turbogwt.net.http.client.serialization.Serializer;

/**
 * @author Danilo Reinert
 */
public class GwtJacksonSerializersGeneratorTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.turbogwt.net.http.HttpTest";
    }

    public void testGenerator() {
        GwtJacksonSerializers serializers = GWT.create(GwtJacksonSerializers.class);

        final Iterator<Serializer<?>> iterator = serializers.iterator();
        assertNotNull(iterator);

        final Book t = new Book(1, "Blah", "Aee");
        final Book t2 = new Book(2, "Uhaa", "sdsd");
        while (iterator.hasNext()) {
            Serializer<Book> next = (Serializer<Book>) iterator.next();
            System.out.println(next.handledType());
            System.out.println(Arrays.toString(next.contentType()));

            System.out.println(next.serialize(t, null));
            System.out.println(next.serializeFromCollection(Arrays.asList(t, t2), null));
        }
    }
}
