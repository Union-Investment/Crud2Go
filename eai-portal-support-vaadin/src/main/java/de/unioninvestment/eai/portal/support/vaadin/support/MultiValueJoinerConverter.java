/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.unioninvestment.eai.portal.support.vaadin.support;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.vaadin.data.util.converter.Converter;

@SuppressWarnings("rawtypes")
public class MultiValueJoinerConverter implements Converter<Set, String> {

	private static final long serialVersionUID = 1L;

	private String delimiter;

	public MultiValueJoinerConverter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public String convertToModel(Set value, Class<? extends String> targetType,
			Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null || value.isEmpty()) {
			return null;
		} else {
			return Joiner.on(delimiter).join(value);
		}
	}

	@Override
	public Set convertToPresentation(String value,
			Class<? extends Set> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null || value.isEmpty()) {
			return new HashSet<String>();
		}
		Iterable<String> elements = Splitter.on(delimiter).split(value);
		LinkedHashSet<Object> results = new LinkedHashSet<Object>();
		for (String element : elements) {
			results.add(element);
		}
		return results;
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<Set> getPresentationType() {
		return Set.class;
	}

}
