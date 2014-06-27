package org.turbogwt.net.http.client.future;

import com.google.gwt.core.client.JavaScriptObject;

public class RequestProgressImpl implements RequestProgress {

    private final XhrRequestProgress xhrRequestProgress;

    public RequestProgressImpl(JavaScriptObject xhrRequestProgress) {
        this.xhrRequestProgress = (XhrRequestProgress) xhrRequestProgress;
    }

    @Override
    public boolean isLengthComputable() {
        return xhrRequestProgress.lengthComputable();
    }

    @Override
    public Number loaded() {
        return xhrRequestProgress.loaded();
    }

    @Override
    public Number total() {
        return xhrRequestProgress.total();
    }
}
