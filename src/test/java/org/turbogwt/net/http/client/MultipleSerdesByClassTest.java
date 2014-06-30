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

import com.google.gwt.junit.client.GWTTestCase;

import java.util.ArrayList;
import java.util.List;

import org.turbogwt.net.http.client.books.Book;
import org.turbogwt.net.http.client.books.BookJsonSerdes;
import org.turbogwt.net.http.client.books.BookXmlSerdes;
import org.turbogwt.net.http.client.mock.ResponseMock;
import org.turbogwt.net.http.client.mock.ServerStub;
import org.turbogwt.net.http.client.serialization.Serdes;

/**
 * @author Danilo Reinert
 */
public class MultipleSerdesByClassTest extends GWTTestCase {

    final String uri = "/book";

    final List<Book> bookList = new ArrayList<>(2);
    final Book firstBook = new Book(1, "RESTful Web Services", "Leonard Richardson");
    final Book secondBook = new Book(2, "Agile Software Development: Principles, Patterns and Practices",
            "Robert C. Martin");

    final String firstBookSerializedAsXml = "<book>" +
            "<id>1</id>" +
            "<title>RESTful Web Services</title>" +
            "<author>Leonard Richardson</author>" +
            "</book>";
    final String secondBookSerializedAsXml = "<book>" +
            "<id>2</id>" +
            "<title>Agile Software Development: Principles, Patterns and Practices</title>" +
            "<author>Robert C. Martin</author>" +
            "</book>";

    final String bookArraySerializedAsXml = "<books>" +
            firstBookSerializedAsXml + secondBookSerializedAsXml +
            "</books>";

    final String firstBookSerializedAsJson = "{\"id\":1,\"title\":\"RESTful Web Services\",\"author\":\"Leonard " +
            "Richardson\"}";

    final String secondBookSerializedAsJson = "{\"id\":2,\"title\":\"Agile Software Development: Principles, " +
            "Patterns and Practices\",\"author\":\"Robert C. Martin\"}";

    final String bookArraySerializedAsJson = "[" + firstBookSerializedAsJson + "," + secondBookSerializedAsJson + "]";

    final Serdes<Book> jsonSerdes = new BookJsonSerdes();
    final Serdes<Book> xmlSerdes = new BookXmlSerdes();

    @Override
    public String getModuleName() {
        return "org.turbogwt.net.http.HttpTest";
    }

    @Override
    public void gwtSetUp() throws Exception {
        bookList.add(firstBook);
        bookList.add(secondBook);
    }

//    public void testXmlDeserializingMatching() {
//        prepareStub("application/xml", firstBookSerializedAsXml);
//        final Requestor requestory = getRequestor();
//
//        final boolean[] callbackCalled = new boolean[3];
//
//        requestory.request(Void.class, Book.class).path(uri).get(new AsyncCallback<Book>() {
//            @Override
//            public void onFailure(Throwable caught) {
//                callbackCalled[0] = true;
//            }
//
//            @Override
//            public void onSuccess(Book result) {
//                callbackCalled[1] = true;
//                assertEquals(firstBook, result);
//            }
//        });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testJsonDeserializingMatching() {
//        prepareStub("application/json", firstBookSerializedAsJson);
//        final Requestor requestory = getRequestor();
//
//        final boolean[] callbackCalled = new boolean[3];
//
//        requestory.request(Void.class, Book.class).path(uri).get(new AsyncCallback<Book>() {
//            @Override
//            public void onFailure(Throwable caught) {
//                callbackCalled[0] = true;
//            }
//
//            @Override
//            public void onSuccess(Book result) {
//                callbackCalled[1] = true;
//                assertEquals(firstBook, result);
//            }
//        });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testXmlArrayDeserializingMatching() {
//        prepareStub("application/xml", bookArraySerializedAsXml);
//        final Requestor requestory = getRequestor();
//
//        final boolean[] callbackCalled = new boolean[3];
//
//            requestory.request(Void.class, Book.class).path(uri).get(new ListAsyncCallback<Book>() {
//                @Override
//                public void onFailure(Throwable caught) {
//                    callbackCalled[0] = true;
//                }
//
//                @Override
//                public void onSuccess(List<Book> result) {
//                    callbackCalled[1] = true;
//                    assertEquals(bookList, result);
//                }
//            });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testJsonArrayDeserializingMatching() {
//        prepareStub("application/json", bookArraySerializedAsJson);
//        final Requestor requestory = getRequestor();
//
//        final boolean[] callbackCalled = new boolean[3];
//
//        requestory.request(Void.class, Book.class).path(uri).get(new ListAsyncCallback<Book>() {
//            @Override
//            public void onFailure(Throwable caught) {
//                callbackCalled[0] = true;
//            }
//
//            @Override
//            public void onSuccess(List<Book> result) {
//                callbackCalled[1] = true;
//                assertEquals(bookList, result);
//            }
//        });
//
//        assertFalse(callbackCalled[0]);
//        assertTrue(callbackCalled[1]);
//    }
//
//    public void testXmlSerializingMatching() {
//        prepareStub("text/plain", "response ignored");
//        final Requestor requestory = getRequestor();
//
//        requestory.request(Book.class, Void.class).path(uri)
//                .contentType("application/xml").post(firstBook);
//
//        Assert.assertEquals(firstBookSerializedAsXml, ServerStub.getRequestData(uri).getData());
//    }
//
//    public void testXmlArraySerializingMatching() {
//        prepareStub("text/plain", "response ignored");
//        final Requestor requestory = getRequestor();
//
//        requestory.request(Book.class, Void.class).path(uri)
//                .contentType("application/xml").post(bookList);
//
//        Assert.assertEquals(bookArraySerializedAsXml, ServerStub.getRequestData(uri).getData());
//    }
//
//    public void testJsonSerializingMatching() {
//        prepareStub("text/plain", "response ignored");
//        final Requestor requestory = getRequestor();
//
//        requestory.request(Book.class, Void.class).path(uri)
//                .contentType("application/json").post(firstBook);
//
//        Assert.assertEquals(firstBookSerializedAsJson, ServerStub.getRequestData(uri).getData());
//    }
//
//    public void testJsonArraySerializingMatching() {
//        prepareStub("text/plain", "response ignored");
//        final Requestor requestory = getRequestor();
//
//        requestory.request(Book.class, Void.class).path(uri)
//                .contentType("application/json").post(bookList);
//
//        Assert.assertEquals(bookArraySerializedAsJson, ServerStub.getRequestData(uri).getData());
//    }

    private Requestor getRequestor() {
        final Requestor requestor = new Requestor();
        requestor.registerSerdes(Book.class, jsonSerdes);
        requestor.registerSerdes(Book.class, xmlSerdes);
        return requestor;
    }

    private void prepareStub(String responseContentType, String serializedResponse) {
        ServerStub.clearStub();
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader(responseContentType)));
    }
}
