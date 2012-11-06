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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerClob;

public class OtherDataTypeTest {

	private OtherDataType type;

	@Before
	public void setUp() {
		type = new OtherDataType();
	}

	@Test
	public void shouldSupportsDisplayingAllObjectsExceptLobs() {
		assertTrue(type.supportsDisplaying(Object.class));
		assertFalse(type.supportsDisplaying(ContainerClob.class));
		assertFalse(type.supportsDisplaying(ContainerBlob.class));
	}

	@Test
	public void shouldFormatCorrectly() {
		assertThat(type.formatPropertyValue(new ObjectProperty<Object>(null,
				Object.class), null), is(""));
		assertThat(type.formatPropertyValue(new ObjectProperty<Integer>(4711),
				null), is("4711"));
		assertThat(type.formatPropertyValue(null, null), is(""));
	}

}
