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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.turbogwt.core.future.shared.AlwaysCallback;
import org.turbogwt.core.future.shared.DoneCallback;
import org.turbogwt.core.future.shared.FailCallback;
import org.turbogwt.core.future.shared.ProgressCallback;
import org.turbogwt.net.http.client.future.RequestProgress;
import org.turbogwt.net.http.client.future.ResponseContext;
import org.turbogwt.net.http.client.header.ContentTypeHeader;
import org.turbogwt.net.http.client.mock.ResponseMock;
import org.turbogwt.net.http.client.mock.ServerStub;
import org.turbogwt.net.http.client.model.Person;
import org.turbogwt.net.http.client.serialization.DeserializationContext;
import org.turbogwt.net.http.client.serialization.JsonObjectSerdes;
import org.turbogwt.net.http.client.serialization.JsonRecordReader;
import org.turbogwt.net.http.client.serialization.JsonRecordWriter;
import org.turbogwt.net.http.client.serialization.SerializationContext;

/**
 * @author Danilo Reinert
 */
public class RequestorTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.turbogwt.net.http.HttpTest";
    }

    public void testCustomObjectArraySerializationDeserialization() {
        ServerStub.clearStub();
        final Requestor requestor = new Requestor();
        requestor.registerSerdes(Person.class, new JsonObjectSerdes<Person>(Person.class) {

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

        final boolean[] callbackDoneCalled = new boolean[1];
        final boolean[] callbackFailCalled = new boolean[1];
        final boolean[] callbackAlwaysCalled = new boolean[1];
        final boolean[] callbackProgressCalled = new boolean[1];

        requestor.request(uri).payload(persons).post(Person.class, List.class)
                .progress(new ProgressCallback<RequestProgress>() {
                    @Override
                    public void onProgress(RequestProgress progress) {
                        callbackProgressCalled[0] = true;
                    }
                }).done(new DoneCallback<Collection<Person>>() {
                    @Override
                    public void onDone(Collection<Person> result) {
                        assertTrue(Arrays.equals(persons.toArray(), result.toArray()));
                        callbackDoneCalled[0] = true;
                    }
                }).fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        callbackFailCalled[0] = true;
                    }
                }).always(new AlwaysCallback<Collection<Person>, Throwable, ResponseContext>() {
                    @Override
                    public void onAlways(ResponseContext context, Collection<Person> resolved,  Throwable rejected) {
                        callbackAlwaysCalled[0] = true;
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);
        assertFalse(callbackFailCalled[0]);
        assertTrue(callbackAlwaysCalled[0]);
        assertTrue(callbackProgressCalled[0]);
        assertEquals(serializedArray, ServerStub.getRequestData(uri).getData());
    }

}
