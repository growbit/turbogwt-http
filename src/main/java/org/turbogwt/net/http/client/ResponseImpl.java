package org.turbogwt.net.http.client;

import com.google.gwt.http.client.Header;

class ResponseImpl implements Response {

    private final com.google.gwt.http.client.Response delegate;

    ResponseImpl(com.google.gwt.http.client.Response originalResponse) {
        this.delegate = originalResponse;
    }

    @Override
    public String getHeader(String header) {
        return delegate.getHeader(header);
    }

    @Override
    public Header[] getHeaders() {
        return delegate.getHeaders();
    }

    @Override
    public int getStatusCode() {
        return delegate.getStatusCode();
    }

    @Override
    public String getStatusText() {
        return delegate.getStatusText();
    }

    @Override
    public String getText() {
        return delegate.getText();
    }
}
