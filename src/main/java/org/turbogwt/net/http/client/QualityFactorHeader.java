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

import java.util.Arrays;
import java.util.Iterator;

import org.turbogwt.core.js.collections.JsArrayIterator;

/**
 * HTTP Header with relative quality factors.
 *
 * @author Danilo Reinert
 */
public class QualityFactorHeader extends MultipleHeader implements Iterable<QualityFactorHeader.Value> {

    private final Value[] values;

    public QualityFactorHeader(String name, Value... values) {
        super(name, (Object[]) values);
        this.values = values;
    }

    public QualityFactorHeader(String name, String... values) {
        super(name, (String[]) values);
        this.values = new Value[values.length];
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            this.values[i] = new Value(value);
        }
        Arrays.sort(values);
    }

    public Value[] getQualityFactorValues() {
        return values;
    }

    @Override
    public Iterator<Value> iterator() {
        return new JsArrayIterator<>(values);
    }

    /**
     * Represents a HTTP Header value with relative quality factor associated.
     */
    public static class Value implements Comparable<Value> {

        private final double factor;
        private final String value;

        public Value(String value) {
            this(1, value);
        }

        public Value(double factor, String value) throws IllegalArgumentException {
            if (factor > 1.0 || factor < 0.0)
                throw new IllegalArgumentException("Factor must be between 0 and 1.");
            if (value == null || value.isEmpty())
                throw new IllegalArgumentException("Value cannot be empty or null.");
            this.factor = factor;
            this.value = value;
        }

        public double getFactor() {
            return factor;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            if (factor == 1) {
                return value;
            }
            return value + "; " + factor;
        }

        @Override
        public int compareTo(Value value) {
            return Double.compare(value.factor, factor);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Value)) {
                return false;
            }

            final Value value1 = (Value) o;

            if (Double.compare(value1.factor, factor) != 0) {
                return false;
            }
            if (!value.equals(value1.value)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(factor);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + value.hashCode();
            return result;
        }
    }
}
