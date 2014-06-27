package org.turbogwt.net.http.client.future;

public interface RequestProgress {

    boolean isLengthComputable();

    Number loaded();

    Number total();
}
