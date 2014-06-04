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

package org.turbogwt.net.http.client.serialization;

import org.turbogwt.net.http.client.ContainerFactoryManager;
import org.turbogwt.net.http.client.Headers;

/**
 * Context of deserialization.
 *
 * @author Danilo Reinert
 */
public class DeserializationContext {

    private final Headers headers;
    private final ContainerFactoryManager containerFactoryManager;

    private DeserializationContext(Headers headers, ContainerFactoryManager containerFactoryManager) {
        this.headers = headers;
        this.containerFactoryManager = containerFactoryManager;
    }

    public static DeserializationContext of(Headers headers, ContainerFactoryManager collectionFactoryManager) {
        return new DeserializationContext(headers, collectionFactoryManager);
    }

    public Headers getHeaders() {
        return headers;
    }

    public ContainerFactoryManager getContainerFactoryManager() {
        return containerFactoryManager;
    }
}
