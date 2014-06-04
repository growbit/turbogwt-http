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

package org.turbogwt.net.http.client;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.URL;

import org.turbogwt.core.js.Overlays;
import org.turbogwt.core.js.collections.JsMap;

/**
 * Default implementation of {@link UriBuilder}.
 *
 * @author Danilo Reinert
 */
public class UriBuilderImpl implements UriBuilder {

    private MultipleParamStrategy strategy = MultipleParamStrategy.REPEATED_PARAM;
    private String scheme;
    private String userInfo;
    private String host;
    private String port;
    private String path = "/";
    private JsArrayString segments;
    private String fragment;
    private JsMap<Object[]> queryParams;
    private JsMap<JsMap<Object[]>> matrixParams;

    /**
     * Set the strategy for appending parameters with multiple values.
     *
     * @param strategy the strategy.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if strategy is null
     */
    @Override
    public UriBuilder multipleParamStrategy(MultipleParamStrategy strategy) throws IllegalArgumentException {
        assertNotNull(strategy, "Strategy cannot be null.");
        this.strategy = strategy;
        return this;
    }

    /**
     * Set the URI scheme.
     *
     * @param scheme the URI scheme. A null value will unset the URI scheme.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if scheme is invalid
     */
    @Override
    public UriBuilder scheme(String scheme) throws IllegalArgumentException {
        // TODO: check scheme validity
        this.scheme = scheme;
        return this;
    }

    /**
     * Set the URI user-info.
     *
     * @param ui the URI user-info. A null value will unset userInfo component of the URI.
     *
     * @return the updated UriBuilder
     */
    @Override
    public UriBuilder userInfo(String ui) {
        // TODO: check userInfo validity
        this.userInfo = ui;
        return this;
    }

    /**
     * Set the URI host.
     *
     * @param host the URI host. A null value will unset the host component of the URI.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if host is invalid.
     */
    @Override
    public UriBuilder host(String host) throws IllegalArgumentException {
        // TODO: check host validity
        this.host = host;
        return this;
    }

    /**
     * Set the URI port.
     *
     * @param port the URI port, a negative value will unset an explicit port.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if port is invalid
     */
    @Override
    public UriBuilder port(int port) throws IllegalArgumentException {
        if (port > -1) {
            this.port = String.valueOf(port);
        } else {
            this.port = null;
        }
        return this;
    }

    /**
     * Set the URI path. This method will overwrite any existing path and associated matrix parameters. Existing '/'
     * characters are preserved thus a single value can represent multiple URI path segments.
     *
     * @param path the path. A null value will unset the path component of the URI.
     *
     * @return the updated UriBuilder
     */
    @Override
    public UriBuilder path(String path) {
        if (path == null || path.isEmpty()) {
            this.path = "/";
        } else {
            this.path = formatSegment(path);
        }
        return this;
    }

    /**
     * Append path segments to the existing path. When constructing the final path, a '/' separator will be inserted
     * between the existing path and the first path segment if necessary and each supplied segment will also be
     * separated by '/'. Existing '/' characters are encoded thus a single value can only represent a single URI path
     * segment.
     *
     * @param segments the path segment values
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if segments or any element of segments is null
     */
    @Override
    public UriBuilder segment(Object... segments) throws IllegalArgumentException {
        assertNotNull(segments, "Segments cannot be null.");
        if (this.segments == null)
            this.segments = (JsArrayString) JsArrayString.createArray();
        for (Object o : segments) {
            String segment = o.toString();
            assertNotNullOrEmpty(segment, "Segment cannot be null or empty.", false);
            this.segments.push(segment);
        }
        return this;
    }

    /**
     * Append a matrix parameter to the existing set of matrix parameters of the current final segment of the URI path.
     * If multiple values are supplied the parameter will be added once per value. Note that the matrix parameters are
     * tied to a particular path segment; subsequent addition of path segments will not affect their position in the URI
     * path.
     *
     * @param name   the matrix parameter name
     * @param values the matrix parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if name or values is null
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    @Override
    public UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNullOrEmpty(name, "Parameter name cannot be null or empty.", false);
        assertNotNull(values, "Parameter values cannot be null.");

        if (matrixParams == null) {
            matrixParams = JsMap.create();
        }

        // TODO: validate this assertion
        assertNotNull(segments, "There is no segment added to the URI. " +
                "There must be at least one segment added in order to bind matrix parameters");

        String segment = segments.get(segments.length() - 1);

        JsMap<Object[]> segmentParams = matrixParams.get(segment);
        if (segmentParams == null) {
            segmentParams = JsMap.create();
            matrixParams.set(segment, segmentParams);
        }
        // TODO: instead of setting the array, incrementally add to an existing one?
        segmentParams.set(name, values);

        return this;
    }

    /**
     * Append a query parameter to the existing set of query parameters. If multiple values are supplied the parameter
     * will be added once per value.
     *
     * @param name   the query parameter name
     * @param values the query parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if name or values is null
     */
    @Override
    public UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException {
        assertNotNull(name, "Parameter name cannot be null.");
        assertNotNull(values, "Parameter values cannot be null.");
        if (queryParams == null)
            queryParams = JsMap.create();
        queryParams.set(name, values);
        return this;
    }

    /**
     * Set the URI fragment.
     *
     * @param fragment the URI fragment. A null value will remove any existing fragment.
     *
     * @return the updated UriBuilder
     */
    @Override
    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Build a URI.
     *
     * @return the URI built from the UriBuilder as String
     */
    @Override
    public String build() {
        StringBuilder uri = new StringBuilder();

        if (scheme != null) {
            uri.append(URL.encode(scheme)).append("://");
        }

        if (userInfo != null) {
            uri.append(URL.encode(userInfo)).append('@');
        }

        if (host != null) {
            uri.append(URL.encode(host));
        }

        if (port != null) {
            uri.append(':').append(port);
        }

        uri.append(path);

        if (segments != null) {
            // Prevent doubling the char '/'
            if (uri.charAt(uri.length() - 1) == '/')
                uri.deleteCharAt(uri.length() - 1);

            for (int i = 0; i < segments.length(); i++) {
                String segment = segments.get(i);
                uri.append(formatSegment(segment));
                // Check if there are matrix params for this segment
                if (matrixParams != null) {
                    JsMap<Object[]> segmentParams = matrixParams.get(segment);
                    if (segmentParams != null) {
                        uri.append(";");
                        JsArrayString params = Overlays.getPropertyNames(segmentParams);
                        for (int j = 0; j < params.length(); j++) {
                            String param = params.get(j);
                            uri.append(strategy.asUriPart(";", param, segmentParams.get(param))).append(';');
                        }
                        uri.deleteCharAt(uri.length() - 1);
                    }
                }
            }
        }

        if (queryParams != null) {
            uri.append('?');

            JsArrayString params = Overlays.getPropertyNames(queryParams);
            for (int i = 0; i < params.length(); i++) {
                String param = params.get(i);
                uri.append(strategy.asUriPart("&", param, queryParams.get(param))).append('&');
            }
            uri.deleteCharAt(uri.length() - 1);
        }

        if (fragment != null) {
            uri.append('#').append(fragment);
        }

        return uri.toString();
    }

    /**
     * Assert that the value is not null.
     *
     * @param value   the value
     * @param message the message to include with any exceptions
     *
     * @throws IllegalArgumentException if value is null
     */
    private void assertNotNull(Object value, String message) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the value is not null or empty.
     *
     * @param value   the value
     * @param message the message to include with any exceptions
     * @param isState if true, throw a state exception instead
     *
     * @throws IllegalArgumentException if value is null
     * @throws IllegalStateException    if value is null and isState is true
     */
    private void assertNotNullOrEmpty(String value, String message, boolean isState) throws IllegalArgumentException {
        if (value == null || value.length() == 0) {
            if (isState) {
                throw new IllegalStateException(message);
            } else {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Performs initial e final checks over path segment assuring it starts with the char '/' and ends without it.
     *
     * @param segment the brute segment
     *
     * @return the processed segment
     */
    private String formatSegment(String segment) {
        String formattedSegment = segment;
        if (formattedSegment.endsWith("/"))
            formattedSegment = formattedSegment.substring(0, formattedSegment.length() - 1);
        if (!formattedSegment.startsWith("/"))
            formattedSegment = "/" + formattedSegment;
        return formattedSegment;
    }
}
