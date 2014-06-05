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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.turbogwt.core.collections.client.JsArrayList;
import org.turbogwt.core.collections.client.JsMapInteger;

/**
 * Stores form params and values.
 *
 * @author Danilo Reinert
 */
public class FormData implements Collection<FormParam> {

    private final List<FormParam> params;
    private MultipleParamStrategy multipleParamStrategy;

    public FormData() {
        this.params = new JsArrayList<>();
    }

    public FormData(List<FormParam> paramList) {
        this.params = paramList;
    }

    public FormData(FormParam... params) {
        this.params = new JsArrayList<>(params);
    }

    /**
     * Returns a builder of FormData.
     *
     * @return The FormData builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public void setMultipleParamStrategy(MultipleParamStrategy multipleParamStrategy) {
        this.multipleParamStrategy = multipleParamStrategy;
    }

    @Nullable
    public MultipleParamStrategy getMultipleParamStrategy() {
        return multipleParamStrategy;
    }

    @Override
    public int size() {
        return params.size();
    }

    @Override
    public boolean isEmpty() {
        return params.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return params.contains(o);
    }

    @Override
    public Iterator<FormParam> iterator() {
        return params.iterator();
    }

    @Override
    public Object[] toArray() {
        return params.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return params.toArray(ts);
    }

    @Override
    public boolean add(FormParam formParam) {
        return params.add(formParam);
    }

    @Override
    public boolean remove(Object o) {
        return params.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return params.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends FormParam> formParams) {
        return params.addAll(formParams);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return params.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return params.retainAll(objects);
    }

    @Override
    public void clear() {
        params.clear();
    }

    /**
     * A builder of FormData.
     */
    public static class Builder {

        private final JsArrayList<FormParam> params = new JsArrayList<>();
        private final JsMapInteger indexes = JsMapInteger.create();
        private MultipleParamStrategy multipleParamStrategy;

        private Builder() {
        }

        public Builder put(String name, Object... values) {
            int i = indexes.get(name, -1);
            if (i > -1) {
                params.set(i, new FormParam(name, values));
            } else {
                indexes.set(name, params.size());
                params.add(new FormParam(name, values));
            }
            return this;
        }

        public Builder strategy(MultipleParamStrategy multipleParamStrategy) {
            this.multipleParamStrategy = multipleParamStrategy;
            return this;
        }

        public FormData build() {
            final FormData data = new FormData(params);
            data.setMultipleParamStrategy(multipleParamStrategy);
            return data;
        }
    }
}
