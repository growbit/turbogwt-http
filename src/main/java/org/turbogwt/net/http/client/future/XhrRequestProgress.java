package org.turbogwt.net.http.client.future;

import com.google.gwt.core.client.JavaScriptObject;

public final class XhrRequestProgress extends JavaScriptObject {

    protected XhrRequestProgress() {
    }

    public native boolean lengthComputable() /*-{
        return lengthComputable;
    }-*/;

    public native double loaded() /*-{
        return loaded;
    }-*/;

    public native double total() /*-{
        return total;
    }-*/;
}
