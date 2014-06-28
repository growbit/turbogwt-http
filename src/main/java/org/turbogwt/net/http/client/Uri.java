package org.turbogwt.net.http.client;

public interface Uri {

    String getScheme();

    String getUser();

    String getPassword();

    String getHost();

    Integer getPort();

    String getPath();

    String getQuery();

    String getFragment();
}
