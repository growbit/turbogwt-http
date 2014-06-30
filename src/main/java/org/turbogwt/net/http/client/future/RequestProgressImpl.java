package org.turbogwt.net.http.client.future;

public class RequestProgressImpl implements RequestProgress {

    private final com.google.gwt.http.client.RequestProgress requestProgress;

    public RequestProgressImpl(com.google.gwt.http.client.RequestProgress requestProgress) {
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
