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
package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Component;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomColumnGenerator;
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder;

public class CustomColumnGeneratorImplTest {

	@Mock
	@SuppressWarnings("rawtypes")
	private Closure closureMock;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldRunClosureWithArguments() {
		// given
		ContainerRowId containerRowIdMock = Mockito.mock(ContainerRowId.class);

		ContainerRow rowMock = Mockito.mock(ContainerRow.class);
		Mockito.when(rowMock.getId()).thenReturn(containerRowIdMock);

		Component componentMock = Mockito.mock(Component.class);
		Mockito.when(
				closureMock.call(Mockito.any(ScriptRow.class),
						Mockito.any(VaadinBuilder.class))).thenReturn(
				componentMock);

		// when
		CustomColumnGenerator columnGenerator = new CustomColumnGeneratorImpl(
				closureMock);
		Component result = columnGenerator.generate(rowMock);

		// then
		Assert.assertNotNull(result);
		Assert.assertEquals(componentMock, result);
	}
}
