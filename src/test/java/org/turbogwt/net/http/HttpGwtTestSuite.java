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

import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

import org.turbogwt.net.http.client.ContentTypeAcceptPatternsTest;
import org.turbogwt.net.http.client.MultipleHeaderTest;
import org.turbogwt.net.http.client.QualityFactorHeaderTest;
import org.turbogwt.net.http.client.RequestTest;
import org.turbogwt.net.http.client.SimpleHeaderWithParameterTest;
import org.turbogwt.net.http.client.UriBuilderTest;
import org.turbogwt.net.http.client.books.RestTest;

/**
 * @author Danilo Reinert
 */
public class HttpGwtTestSuite {

    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("Http Test Suite");

        suite.addTestSuite(MultipleHeaderTest.class);
        suite.addTestSuite(QualityFactorHeaderTest.class);
        suite.addTestSuite(SimpleHeaderWithParameterTest.class);

        suite.addTestSuite(UriBuilderTest.class);
        suite.addTestSuite(RequestTest.class);
        suite.addTestSuite(ContentTypeAcceptPatternsTest.class);
//        suite.addTestSuite(SerializerAndDeserializerMatchTest.class);
//        suite.addTestSuite(SerializerAndDeserializerPrecedenceTest.class);
//        suite.addTestSuite(MultipleSerdesByClassTest.class);

        suite.addTestSuite(RestTest.class);

        return suite;
    }
}
