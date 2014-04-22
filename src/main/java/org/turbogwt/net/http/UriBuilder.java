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

package org.turbogwt.net.http;

/**
 * Utility class for building URIs from their components.
 * <p/>
 * It is NOT aware of templates.
 * <p/>
 * <p>Builder methods perform contextual encoding of characters not permitted in the corresponding URI component
 * following the rules of the
 * <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">application/x-www-form-urlencoded</a>
 * media type for query parameters and <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a> for all other components.
 * Note that only characters not permitted in a particular component are subject to encoding so, e.g., a path supplied
 * to one of the {@code path} methods may contain matrix parameters or multiple path segments since the separators are
 * legal characters and will not be encoded. Percent encoded values are also recognized where allowed and will not be
 * double encoded.</p>
 */
public interface UriBuilder extends HasUriParts {

    /**
     * Set the strategy for appending parameters with multiple values.
     *
     * @param strategy the strategy.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if strategy is null
     */
    UriBuilder multipleParamStrategy(MultipleParamStrategy strategy) throws IllegalArgumentException;

    /**
     * Set the URI user-info.
     *
     * @param ui the URI user-info. A null value will unset userInfo component of the URI.
     *
     * @return the updated UriBuilder
     */
    UriBuilder userInfo(String ui);

    /**
     * Set the URI scheme.
     *
     * @param scheme the URI scheme. A null value will unset the URI scheme.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if scheme is invalid
     */
    UriBuilder scheme(String scheme) throws IllegalArgumentException;

    /**
     * Set the URI host.
     *
     * @param host the URI host. A null value will unset the host component of the URI.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if host is invalid.
     */
    UriBuilder host(String host) throws IllegalArgumentException;

    /**
     * Set the URI port.
     *
     * @param port the URI port, a negative value will unset an explicit port.
     *
     * @return the updated UriBuilder
     *
     * @throws IllegalArgumentException if port is invalid
     */
    UriBuilder port(int port) throws IllegalArgumentException;

    /**
     * Set the URI path. This method will overwrite any existing path and associated matrix parameters. Existing '/'
     * characters are preserved thus a single value can represent multiple URI path segments.
     *
     * @param path the path. A null value will unset the path component of the URI.
     *
     * @return the updated UriBuilder
     */
    UriBuilder path(String path);

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
    UriBuilder segment(Object... segments) throws IllegalArgumentException;

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
    UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException;

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
    UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException;

    /**
     * Set the URI fragment.
     *
     * @param fragment the URI fragment. A null value will remove any existing fragment.
     *
     * @return the updated UriBuilder
     */
    UriBuilder fragment(String fragment);

    /**
     * Build a URI.
     *
     * @return the URI built from the UriBuilder as String
     */
    String build();
}
