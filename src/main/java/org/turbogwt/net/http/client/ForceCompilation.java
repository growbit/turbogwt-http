package org.turbogwt.net.http.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

import org.turbogwt.net.http.client.serialization.Serializer;

/**
 * @author Danilo Reinert
 */
public class ForceCompilation implements EntryPoint {
    /**
     * The entry point method, called automatically by loading a module that declares an implementing class as an entry
     * point.
     */
    @Override
    public void onModuleLoad() {
        GwtJacksonSerializers serializers = GWT.create(GwtJacksonSerializers.class);

        for (Serializer<?> serializer : serializers) {
            Window.alert(serializer.contentType()[0]);
        }
    }
}
