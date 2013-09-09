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
package de.unioninvestment.eai.portal.portlet.crud.datatypes;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.vaadin.tokenfield.TokenField;

import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.support.MultiValueJoinerConverter;

public class StringDataTypeTest extends AbstractDataTypeTest<StringDataType> {

	@Override
	protected StringDataType createDataType() {
		return new StringDataType();
	}

	@Test
	public void shouldSupportStringTypes() {
		assertTrue(type.supportsDisplaying(String.class));
		assertTrue(type.supportsEditing(String.class));
	}

	@Test
	public void shouldSupportWriting() {
		assertFalse(type.isReadonly());
	}
	
	@Test
	public void shouldProvideATokenField() {
		Field<?> field = type.createTokenField(String.class, "test", ";", null);

		assertThat(field, instanceOf(TokenField.class));
		TokenField tokens = (TokenField) field;
		assertThat(tokens.isNewTokensAllowed(), is(false));
		assertThat(tokens.isReadOnly(), is(false));
		assertThat(tokens.getFilteringMode(), is(FilteringMode.CONTAINS));
		assertThat(tokens.getConverter(), instanceOf(MultiValueJoinerConverter.class));
	}
}
