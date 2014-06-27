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

package org.turbogwt.net.http.client.future;

import com.google.gwt.core.client.JavaScriptObject;

public class RequestProgressImpl implements RequestProgress {

    private final XhrRequestProgress xhrRequestProgress;

    public RequestProgressImpl(JavaScriptObject xhrRequestProgress) {
        this.xhrRequestProgress = (XhrRequestProgress) xhrRequestProgress;
    }

    @Override
    public boolean isLengthComputable() {
        return xhrRequestProgress.lengthComputable();
    }

    @Override
    public Number loaded() {
        return xhrRequestProgress.loaded();
    }

    @Override
    public Number total() {
        return xhrRequestProgress.total();
    }
}
