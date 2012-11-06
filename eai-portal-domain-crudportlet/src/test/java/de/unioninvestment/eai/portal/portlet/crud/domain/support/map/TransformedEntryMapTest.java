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
package de.unioninvestment.eai.portal.portlet.crud.domain.support.map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransformedEntryMapTest {

	@Mock
	private ValueTransformer<String, Integer> transformerMock;
	private TransformedEntryMap<String, String, Integer> map;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(transformerMock.transform("DEF")).thenReturn(5);
		map = new TransformedEntryMap<String, String, Integer>(singletonMap(
				"ABC", "DEF"), transformerMock);
	}

	@Test
	public void shouldReturnConvertedEntryOfDelegateMap() {
		assertThat(map.get("ABC"), is(5));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportPutByDefault() {
		map.put("ABC", 4);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportPutAllByDefault() {
		map.putAll(singletonMap("ABC", 4));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportRemoveByDefault() {
		map.remove("ABC");
	}

	@Test
	public void shouldReturnTransformerForSubclass() {
		assertThat(map.getTransformer(), is(transformerMock));
	}

}
