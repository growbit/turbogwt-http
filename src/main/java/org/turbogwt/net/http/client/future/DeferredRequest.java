package org.turbogwt.net.http.client.future;

import org.turbogwt.core.future.shared.impl.DeferredObject;

public class DeferredRequest<T> extends DeferredObject<T, Throwable, RequestProgress> implements RequestPromise<T> {
}
