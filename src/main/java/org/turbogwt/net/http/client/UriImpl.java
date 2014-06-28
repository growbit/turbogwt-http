package org.turbogwt.net.http.client;

public class UriImpl implements Uri {

    private String scheme;
    private String user;
    private String password;
    private String host;
    private Integer port;
    private String path;
    private String query;
    private String fragment;
    private String uriString;

    public UriImpl(String scheme, String user, String password, String host, Integer port, String path, String query,
                   String fragment) {
        // TODO: validate
        this.scheme = scheme;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public String getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        if (uriString == null) {
            StringBuilder uri = new StringBuilder();

            if (scheme != null) {
                uri.append(scheme).append("://");
            }

            if (user != null) {
                uri.append(user);
                if (password != null) {
                    uri.append(':').append(password);
                }
                uri.append('@');
            }

            if (host != null) {
                uri.append(host);
            }

            if (port != null) {
                uri.append(':').append(port);
            }

            uri.append('/');

            if (path != null) {
                uri.append(path);
            }

            if (query != null) {
                uri.append('?').append(query);
            }

            if (fragment != null) {
                uri.append('#').append(fragment);
            }

            uriString = uri.toString();
        }

        return uriString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UriImpl)) {
            return false;
        }

        final UriImpl uri = (UriImpl) o;

        if (fragment != null ? !fragment.equals(uri.fragment) : uri.fragment != null) {
            return false;
        }
        if (host != null ? !host.equals(uri.host) : uri.host != null) {
            return false;
        }
        if (password != null ? !password.equals(uri.password) : uri.password != null) {
            return false;
        }
        if (path != null ? !path.equals(uri.path) : uri.path != null) {
            return false;
        }
        if (port != null ? !port.equals(uri.port) : uri.port != null) {
            return false;
        }
        if (query != null ? !query.equals(uri.query) : uri.query != null) {
            return false;
        }
        if (scheme != null ? !scheme.equals(uri.scheme) : uri.scheme != null) {
            return false;
        }
        if (user != null ? !user.equals(uri.user) : uri.user != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (fragment != null ? fragment.hashCode() : 0);
        return result;
    }
}
