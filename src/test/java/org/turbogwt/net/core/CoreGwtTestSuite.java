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

package org.turbogwt.net.core;

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

import org.turbogwt.net.core.client.UriBuilderTest;

/**
 * @author Danilo Reinert
 */
public class CoreGwtTestSuite {

    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("Net Core Test Suite");

        suite.addTestSuite(UriBuilderTest.class);

        return suite;
    }
}
