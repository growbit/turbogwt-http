package org.turbogwt.net.http.client.future;

import org.turbogwt.core.future.shared.Promise;

public interface RequestPromise<T> extends Promise<T, Throwable, RequestProgress, ResponseContext> {
}
