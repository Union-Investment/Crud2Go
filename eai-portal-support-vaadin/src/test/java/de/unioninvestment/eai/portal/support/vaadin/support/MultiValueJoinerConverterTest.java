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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.ListSelect;

public class MultiValueJoinerConverterTest {

	private ListSelect select;
	private ObjectProperty<String> property;

	@Before
	public void setup() {
		select = new ListSelect();
		select.setMultiSelect(true);
		select.setConverter((Converter) new MultiValueJoinerConverter(","));
		select.addItem("a");
		select.addItem("b");

		property = new ObjectProperty<String>("a", String.class);
		select.setPropertyDataSource(property);
	}

	@Test
	public void shouldJoinMultiSelectSingleValueToSameStringValue() {
		select.setValue(singleton("b"));
		assertThat(property.getValue(), is("b"));
	}
	
	@Test
	public void shouldJoinMultiSelectMultipleValues() {
		select.setValue(new LinkedHashSet<Object>(asList("a", "b")));
		assertThat(property.getValue(), is("a,b"));
	}
	
	@Test
	public void shouldSplitSingleValue() {
		assertThat(select.getValue(), equalTo((Object)singleton("a")));
	}
	
	@Test
	public void shouldSplitMultipleValues() {
		property.setValue("a,b");
		assertThat(select.getValue(), equalTo((Object)new LinkedHashSet<Object>(asList("a", "b"))));
	}
}
