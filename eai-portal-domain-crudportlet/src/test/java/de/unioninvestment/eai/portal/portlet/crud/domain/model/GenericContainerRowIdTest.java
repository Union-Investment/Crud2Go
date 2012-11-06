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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;

public class GenericContainerRowIdTest {
	@Mock
	private GenericItemId itemId;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldConstructGenericContainerRowId() {
		// given
		String[] names = new String[] { "name-1", "name-2", "name-3" };
		String[] ids = new String[] { "id-1", "id-2", "id-3" };
		Mockito.when(itemId.getId()).thenReturn(ids);

		// when
		GenericContainerRowId result = new GenericContainerRowId(itemId,
				Arrays.asList(names));

		// then
		Assert.assertEquals(itemId, result.getInternalId());
		Map<String, Object> map = result.asMap();
		for (String name : names) {
			Assert.assertTrue(map.containsKey(name));
		}
	}

	@Test
	public void shouldCalculateKeyValueMap() {
		// given
		String[] names = new String[] { "name-1", "name-2", "name-3" };
		String[] ids = new String[] { "id-1", "id-2", "id-3" };
		Mockito.when(itemId.getId()).thenReturn(ids);

		// when
		Map<String, Object> result = GenericContainerRowId
				.calculateKeyValueMap(itemId, Arrays.asList(names));

		// then
		Mockito.verify(itemId).getId();

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			String id = ids[i];

			Assert.assertTrue(result.containsKey(name));
			Assert.assertEquals(id, result.get(name));
		}
	}

	@Test
	public void shouldCalculateKeyValueMapReturnNull() {
		// given
		String[] names = new String[] { "name-1", "name-2", "name-3" };
		Mockito.when(itemId.getId()).thenReturn(null);

		// when
		Map<String, Object> result = GenericContainerRowId
				.calculateKeyValueMap(itemId, Arrays.asList(names));

		// then
		Mockito.verify(itemId).getId();

		Assert.assertNull(result);
	}
}
