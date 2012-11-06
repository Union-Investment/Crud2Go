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
package de.unioninvestment.eai.portal.support.vaadin.table;

import java.text.Format;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyFormatter;

public class ReadonlyFormatterTest {
	@Test
	public void shouldReturnTrueOnReadOnly() {
		PropertyFormatter formatter = new ReadonlyFormatter(null, null);
		Assert.assertTrue(formatter.isReadOnly());
	}

	@Test
	public void shouldNotChangeTheInput() throws Exception {
		String testInput = "test";

		PropertyFormatter formatter = new ReadonlyFormatter(null, null);
		Object result = formatter.parse(testInput);

		Assert.assertEquals(testInput, result);
	}

	@Test
	public void shouldUserDisplayerAndFormat() throws Exception {
		String testInput = "test";

		DisplaySupport displayerMock = Mockito.mock(DisplaySupport.class);
		Format formatMock = Mockito.mock(Format.class);

		PropertyFormatter formatter = new ReadonlyFormatter(displayerMock, formatMock);
		Object result = formatter.format(testInput);
		
		ArgumentCaptor<ObjectProperty> objectPropertyCaptor =ArgumentCaptor.forClass(ObjectProperty.class);
		ArgumentCaptor<Format> formatCaptor =ArgumentCaptor.forClass(Format.class);

		Mockito.verify(displayerMock).formatPropertyValue(objectPropertyCaptor.capture(), formatCaptor.capture());
		Assert.assertEquals(formatMock, formatCaptor.getValue());
		Assert.assertEquals(testInput, objectPropertyCaptor.getValue().getValue());
	}
}
