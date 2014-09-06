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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that the type must have an auto-generated json serializer and deserializer
 * registered by default in any {@link org.turbogwt.net.http.client.Requestor} instance.
 * <br/>
 *
 * The implementation of the serializer is let to third-part projects.
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {

    /**
     * A list of content-type patterns.
     * These patterns will be used to match content-types of incoming responses and outgoing requests.
     * The patterns accept wildcards like "&#42;&#47;json".
     */
    String[] value() default "application/json";
}
