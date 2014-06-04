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

/**
 * A type that supports uri progressive construction.
 *
 * @author Danilo Reinert
 */
public interface HasUriParts {

    /**
     * Set the strategy for appending parameters with multiple values.
     *
     * @param strategy the strategy.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if strategy is null
     */
    HasUriParts multipleParamStrategy(MultipleParamStrategy strategy) throws IllegalArgumentException;

    /**
     * Set the URI scheme.
     *
     * @param scheme the URI scheme. A null value will unset the URI scheme.
     *
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if scheme is invalid
     */
    HasUriParts scheme(String scheme) throws IllegalArgumentException;

    /**
     * Set the URI host.
     *
     * @param host the URI host. A null value will unset the host component of the URI.
     *
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if host is invalid.
     */
    HasUriParts host(String host) throws IllegalArgumentException;

    /**
     * Set the URI port.
     *
     * @param port the URI port, a negative value will unset an explicit port.
     *
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if port is invalid
     */
    HasUriParts port(int port) throws IllegalArgumentException;

    /**
     * Set the URI path. This method will overwrite any existing path and associated matrix parameters. Existing '/'
     * characters are preserved thus a single value can represent multiple URI path segments.
     *
     * @param path the path. A null value will unset the path component of the URI.
     *
     * @return the updated HasUri
     */
    HasUriParts path(String path);

    /**
     * Append path segments to the existing path. When constructing the final path, a '/' separator will be inserted
     * between the existing path and the first path segment if necessary and each supplied segment will also be
     * separated by '/'. Existing '/' characters are encoded thus a single value can only represent a single URI path
     * segment.
     *
     * @param segments the path segment values
     *
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if segments or any element of segments is null
     */
    HasUriParts segment(Object... segments) throws IllegalArgumentException;

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
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if name or values is null
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    HasUriParts matrixParam(String name, Object... values) throws IllegalArgumentException;

    /**
     * Append a query parameter to the existing set of query parameters. If multiple values are supplied the parameter
     * will be added once per value.
     *
     * @param name   the query parameter name
     * @param values the query parameter value(s), each object will be converted to a {@code String} using its {@code
     *               toString()} method.
     *
     * @return the updated HasUri
     *
     * @throws IllegalArgumentException if name or values is null
     */
    HasUriParts queryParam(String name, Object... values) throws IllegalArgumentException;

    /**
     * Set the URI fragment.
     *
     * @param fragment the URI fragment. A null value will remove any existing fragment.
     *
     * @return the updated HasUri
     */
    HasUriParts fragment(String fragment);
}
