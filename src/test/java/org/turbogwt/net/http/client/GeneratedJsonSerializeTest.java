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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

import org.turbogwt.core.future.shared.DoneCallback;
import org.turbogwt.net.http.client.header.ContentTypeHeader;
import org.turbogwt.net.http.client.mock.ResponseMock;
import org.turbogwt.net.http.client.mock.ServerStub;
import org.turbogwt.net.http.shared.serialization.JsonSerialize;

/**
 * @author Danilo Reinert
 */
public class GeneratedJsonSerializeTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.turbogwt.net.http.HttpTest";
    }

    public void testGeneratedSerialization() {
        final Animal animal = new Animal();
        animal.setName("Stuart");
        animal.setAge(3);

        final Requestor requestor = getRequestor();

        final String uri = "/animal";

        final String serialized = "{\"name\":\"Stuart\",\"age\":3}";

        ServerStub.responseFor(uri, ResponseMock.of(serialized, 200, "OK",
                new ContentTypeHeader("application/json")));

        final boolean[] callbackDoneCalled = new boolean[1];

        requestor.request(uri).payload(animal).post()
                .done(new DoneCallback<Void>() {
                    @Override
                    public void onDone(Void ignored) {
                        callbackDoneCalled[0] = true;
                    }
                });

        ServerStub.triggerPendingRequest();

        assertTrue(callbackDoneCalled[0]);

        assertEquals(serialized, ServerStub.getRequestData(uri).getData());
    }

    private Requestor getRequestor() {
        ServerStub.clearStub();
        return GWT.create(Requestor.class);
    }

    /**
     * @author Danilo Reinert
     */
    @JsonSerialize
    public class Animal {

        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
