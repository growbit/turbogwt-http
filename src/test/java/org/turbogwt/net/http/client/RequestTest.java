package org.turbogwt.net.http.client;

import java.util.Collection;
import java.util.List;

import org.turbogwt.core.future.shared.AlwaysCallback;
import org.turbogwt.core.future.shared.DoneCallback;
import org.turbogwt.net.http.client.books.Book;
import org.turbogwt.net.http.client.future.RequestPromise;
import org.turbogwt.net.http.client.future.ResponseContext;

public class RequestTest {

    public static void main(String[] args) {
        RequestDispatcher r = null;
        final RequestPromise<Collection<Book>> listRequestPromise = r.get(Book.class, List.class);

        listRequestPromise.done(new DoneCallback<Collection<Book>>() {
            @Override
            public void onDone(Collection<Book> result) {
                List<Book> list = (List<Book>) result;

            }
        });

        listRequestPromise.always(new AlwaysCallback<Collection<Book>, Throwable, ResponseContext>() {
            @Override
            public void onAlways(ResponseContext context, Collection<Book> resolved, Throwable rejected) {

            }
        });
    }
}
