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

package org.turbogwt.net.serialization.client;

import java.util.Collection;

import org.turbogwt.core.util.shared.Factory;

/**
 * Context of deserialization.
 *
 * @author Danilo Reinert
 */
public abstract class DeserializationContext {

    private final ContainerFactoryManager containerFactoryManager;

    public DeserializationContext(ContainerFactoryManager containerFactoryManager) {
        this.containerFactoryManager = containerFactoryManager;
    }

    public <C extends Collection> C getContainerInstance(Class<C> type) {
        final Factory<C> factory = containerFactoryManager.getFactory(type);
        if (factory == null)
            throw new UnableToDeserializeException("Could not get container instance because there's no factory " +
                    "registered in the requestor.");
        return factory.get();
    }

    protected ContainerFactoryManager getContainerFactoryManager() {
        return containerFactoryManager;
    }
}
