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

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.turbogwt.core.js.Overlays;
import org.turbogwt.core.js.collections.JsArray;
import org.turbogwt.core.js.collections.JsArrayList;
import org.turbogwt.net.http.mock.RequestMock;
import org.turbogwt.net.http.mock.ResponseMock;
import org.turbogwt.net.http.mock.ServerStub;
import org.turbogwt.net.http.model.Person;
import org.turbogwt.net.http.model.PersonJso;
import org.turbogwt.net.http.model.PersonSerdes;
import org.turbogwt.net.http.serialization.DeserializationContext;
import org.turbogwt.net.http.serialization.JsonObjectSerdes;
import org.turbogwt.net.http.serialization.JsonRecordReader;
import org.turbogwt.net.http.serialization.JsonRecordWriter;
import org.turbogwt.net.http.serialization.SerializationContext;

/**
 * @author Danilo Reinert
 */
public class FluentRequestImplTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.turbogwt.net.http.HttpTest";
    }

    public void testCustomObjectArraySerializationDeserialization() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();
        requestory.registerSerdes(Person.class, new JsonObjectSerdes<Person>(Person.class) {

            @Override
            public Person readJson(JsonRecordReader reader, DeserializationContext context) {
                return new Person(reader.readInteger("id"),
                        reader.readString("name"),
                        reader.readDouble("weight"),
                        new Date(reader.readLong("birthday")));
            }

            @Override
            public String serialize(Person person, SerializationContext context) {
                // Directly build json using string concatenation in order to increase performance.
                return "{" + "\"id\":" + person.getId() + ", \"name\":\"" + person.getName() + "\", " +
                        "\"weight\":" + person.getWeight() + ", \"birthday\":" + person.getBirthday().getTime() + "}";
            }

            @Override
            public void writeJson(Person person, JsonRecordWriter writer, SerializationContext context) {
                // Ignored, as #serialize was overridden in order to improve serialization performance.
            }
        });

        final String uri = "/person";

        final Person p1 = new Person(1, "John Doe", 6.3, new Date(329356800));
        final Person p2 = new Person(2, "Alice", 5.87, new Date(355343600));
        final List<Person> persons = Arrays.asList(p1, p2);

        final String serializedArray = "[{\"id\":1, \"name\":\"John Doe\", \"weight\":6.3, \"birthday\":329356800},"
                + "{\"id\":2, \"name\":\"Alice\", \"weight\":5.87, \"birthday\":355343600}]";

        ServerStub.responseFor(uri, ResponseMock.of(serializedArray, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.post(uri, Person.class, persons, Person.class, new ListAsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<Person> result) {
                assertTrue(Arrays.equals(persons.toArray(), result.toArray()));
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
        assertEquals(serializedArray, ServerStub.getRequestData(uri).getData());
    }

    public void testCustomObjectRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();
        requestory.registerSerdes(Person.class, new JsonObjectSerdes<Person>(Person.class) {

            @Override
            public Person readJson(JsonRecordReader reader, DeserializationContext context) {
                return new Person(reader.readInteger("id"),
                        reader.readString("name"),
                        reader.readDouble("weight"),
                        new Date(reader.readLong("birthday")));
            }

            @Override
            public void writeJson(Person person, JsonRecordWriter writer, SerializationContext context) {
                writer.writeInt("id", person.getId())
                        .writeString("name", person.getName())
                        .writeDouble("weight", person.getWeight())
                        .writeDouble("birthday", person.getBirthday().getTime());
            }
        });

        final String uri = "/person";

        final Person person = new Person(1, "John Doe", 6.3, new Date(329356800));
        final String serializedResponse = "{\"id\":1, \"name\":\"John Doe\", \"weight\":6.3, \"birthday\":329356800}";

        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, Person.class, new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Person result) {
                assertEquals(person, result);
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testCustomObjectSerialization() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();
        requestory.registerSerdes(Person.class, new PersonSerdes());

        final String uri = "/person";

        final Person person = new Person(1, "John Doe", 6.3, new Date(329356800));
        final String serializedRequest = "{\"id\":1,\"name\":\"John Doe\",\"weight\":6.3,\"birthday\":329356800}";

        ServerStub.responseFor(uri, ResponseMock.of(serializedRequest, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.post(uri, Person.class, person, Person.class, new AsyncCallback<Person>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Person result) {
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
        assertEquals(serializedRequest, ServerStub.getRequestData(uri).getData());
    }

    public void testAwaysCallbackExecutionOnFailure() {
        ServerStub.clearStub();

        final Requestor requestory = new Requestor();

        final String uri = "/failure";

        ServerStub.responseFor(uri,
                ResponseMock.of("not found", 404, "NOT FOUND", new ContentTypeHeader("plain/text")));
        ServerStub.setReturnSuccess(false);
        final MutableBoolean executed = new MutableBoolean();
        executed.setValue(false);

        requestory.request().path("/notValid").always(new SingleCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                executed.setValue(true);
            }
        }).get();
        assertTrue(executed.isTrue());
    }

    public void testAwaysCallbackExecutionOnSuccess() {

        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/success";

        ServerStub.responseFor(uri, ResponseMock.of("success", 200, "OK", new ContentTypeHeader("plain/text")));

        final MutableBoolean executed;
        executed = new MutableBoolean();
        executed.setValue(false);
        requestory.request().path(uri).always(new SingleCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                executed.setValue(true);
            }
        }).get();

        assertTrue(executed.isTrue());
    }

    public void testOnHttpCodeCallbackExecution() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/on";

        ServerStub.responseFor(uri, ResponseMock.of(null, 204, "Empty",
                new ContentTypeHeader("application/json")));

        final boolean[] callbacksCalled = new boolean[3];

        requestory.request().path(uri)
                .on(20, new SingleCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        callbacksCalled[0] = true;
                    }
                })
                .on(2, new SingleCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        callbacksCalled[1] = true;
                    }
                })
                .get(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                        callbacksCalled[2] = true;
                    }
                });

        // Most specific mapped code callback should be called
        assertTrue(callbacksCalled[0]);
        assertFalse(callbacksCalled[1]);
        assertFalse(callbacksCalled[2]);
    }

    public void testOverlayArrayRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/person-jso-array";

        final PersonJso p1 = PersonJso.create(1, "John Doe", 6.3, new Date(329356800));
        final PersonJso p2 = PersonJso.create(2, "Alice", 5.87, new Date(355343600));

        final JsArray<PersonJso> persons = JsArray.create();
        persons.push(p1);
        persons.push(p2);

        final String serializedResp = "[{\"id\": 1, \"name\": \"John Doe\", \"weight\": 6.3, \"birthday\": 329356800},"
                + "{\"id\": 2, \"name\": \"Alice\", \"weight\": 5.87, \"birthday\": 355343600}]";

        ServerStub.responseFor(uri, ResponseMock.of(serializedResp, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, PersonJso.class, new ListAsyncCallback<PersonJso>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<PersonJso> result) {
                JsArray<PersonJso> resultArray = ((JsArrayList<PersonJso>) result).asJsArray();
                assertEquals(Overlays.stringify(persons), Overlays.stringify(resultArray));
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testOverlayRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/person-jso";

        final PersonJso person = PersonJso.create(1, "John Doe", 6.3, new Date(329356800));
        final String serializedResp = "{ \"id\" : 1, \"name\":\"John Doe\",\"weight\" :6.3,  \"birthday\": 329356800}";

        ServerStub.responseFor(uri, ResponseMock.of(serializedResp, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, PersonJso.class, new AsyncCallback<PersonJso>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(PersonJso result) {
                assertEquals(Overlays.stringify(person), Overlays.stringify(result));
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testRequestHeaders() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/person-jso";

        final PersonJso person = PersonJso.create(1, "John Doe", 6.3, new Date(329356800));
        final String serializedResp = "{ \"id\" : 1, \"name\":\"John Doe\",\"weight\" :6.3,  \"birthday\": 329356800}";

        ServerStub.responseFor(uri, ResponseMock.of(serializedResp, 200, "OK",
                new ContentTypeHeader("application/json")));

        requestory.request(PersonJso.class, PersonJso.class).path(uri).post(person);

        // On #post execution, request mock should be set from Requestory
        final RequestMock requestMock = ServerStub.getRequestData(uri);
        assertNotNull(requestMock);
        assertNotNull(requestMock.getHeaders());

        boolean contentTypeHeaderOk = false;
        boolean acceptHeaderOk = false;
        for (Header header : requestMock.getHeaders()) {
            if (header.getName().equals("Content-Type")) {
                contentTypeHeaderOk = header.getValue().equals("application/json");
            } else if (header.getName().equals("Accept")) {
                acceptHeaderOk = header.getValue().equals("application/json");
            }
        }
        assertTrue(contentTypeHeaderOk);
        assertTrue(acceptHeaderOk);
    }

    public void testStringArrayRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/string-array";
        final String[] response = {"Some", "string", "array", "response"};
        final String irregularStringArray = " [ \"Some\", \"string\" ,  \"array\", \"response\" ]  ";
        ServerStub.responseFor(uri, ResponseMock.of(irregularStringArray, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, String.class, new ListAsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<String> result) {
                assertTrue(Arrays.equals(result.toArray(), response));
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testStringRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/string";
        final String response = "Some string response";
        final String serializedResponse = "\"Some string response\"";
        ServerStub.responseFor(uri, ResponseMock.of(serializedResponse, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, String.class, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(String result) {
                assertEquals(response, result);
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testVoidRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/void";
        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK", new ContentTypeHeader("application/json")));

        final boolean[] callbackSuccessCalled = new boolean[1];

        requestory.get(uri, Void.class, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                assertNull(result);
                callbackSuccessCalled[0] = true;
            }
        });

        assertTrue(callbackSuccessCalled[0]);
    }

    public void testFormDataRequest() {
        ServerStub.clearStub();
        final Requestor requestory = new Requestor();

        final String uri = "/form";
        ServerStub.responseFor(uri, ResponseMock.of(null, 200, "OK", new ContentTypeHeader("text/plain")));

        final FormData formData = FormData.builder().put("name", "John Doe").put("age", 1, 2, 3.5).build();
        final String serialized = "name=John+Doe&age=1&age=2&age=3.5";

        requestory.request(FormParam.class, Void.class).path(uri).contentType(FormParam.CONTENT_TYPE).post(formData);

        final RequestMock requestMock = ServerStub.getRequestData(uri);
        assertEquals(serialized, requestMock.getData());
    }

    class MutableBoolean {
        private boolean value;

        public void setValue(boolean value) {
            this.value = value;
        }

        public boolean isTrue() {
            return value;
        }

        public boolean isFalse() {
            return !value;
        }
    }
}
