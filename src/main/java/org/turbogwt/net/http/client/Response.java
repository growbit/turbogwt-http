package org.turbogwt.net.http.client;

import com.google.gwt.http.client.Header;

/**
 * Represents an HTTP response.
 */
public interface Response {

    public static final int ACCEPTED = 202;
    public static final int BAD_GATEWAY = 502;
    public static final int BAD_REQUEST = 400;
    public static final int CONFLICT = 409;
    public static final int CONTINUE = 100;
    public static final int CREATED = 201;
    public static final int EXPECTATION_FAILED = 417;
    public static final int FORBIDDEN = 403;
    public static final int GATEWAY_TIMEOUT = 504;
    public static final int GONE = 410;
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int LENGTH_REQUIRED = 411;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int MOVED_PERMANENTLY = 301;
    public static final int MOVED_TEMPORARILY = 302;
    public static final int MULTIPLE_CHOICES = 300;
    public static final int NO_CONTENT = 204;
    public static final int NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int NOT_FOUND = 404;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int NOT_MODIFIED = 304;
    public static final int OK = 200;
    public static final int PARTIAL_CONTENT = 206;
    public static final int PAYMENT_REQUIRED = 402;
    public static final int PRECONDITION_FAILED = 412;
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int RESET_CONTENT = 205;
    public static final int SEE_OTHER = 303;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int SWITCHING_PROTOCOLS = 101;
    public static final int TEMPORARY_REDIRECT = 307;
    public static final int UNAUTHORIZED = 401;
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int USE_PROXY = 305;

    /**
     * Returns the value of the requested header or null if the header was not
     * specified.
     *
     * @param header the header to query for
     * @return the value of response header
     *
     * @throws IllegalArgumentException if the header name is empty
     * @throws NullPointerException if the header name is null
     */
    public abstract String getHeader(String header);

    /**
     * Returns an array of HTTP headers associated with this response.
     *
     * @return array of HTTP headers; returns zero length array if there are no
     *         headers
     */
    public abstract Header[] getHeaders();

    /**
     * Returns the HTTP status code that is part of this response.
     *
     * @return the HTTP status code
     */
    public abstract int getStatusCode();

    /**
     * Returns the HTTP status message text.
     *
     * @return the HTTP status message text
     */
    public abstract String getStatusText();

    /**
     * Returns the text associated with the response.
     *
     * @return the response text
     */
    public abstract String getText();
}
