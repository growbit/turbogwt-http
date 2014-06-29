package org.turbogwt.net.http.client.future;

public class RequestProgressWrapper implements RequestProgress {

    private final com.google.gwt.http.client.RequestProgress requestProgress;

    public RequestProgressWrapper(com.google.gwt.http.client.RequestProgress requestProgress) {
        this.requestProgress = requestProgress;
    }

    @Override
    public boolean isLengthComputable() {
        return requestProgress.isLengthComputable();
    }

    @Override
    public Number loaded() {
        return requestProgress.loaded();
    }

    @Override
    public Number total() {
        return requestProgress.total();
    }
}
