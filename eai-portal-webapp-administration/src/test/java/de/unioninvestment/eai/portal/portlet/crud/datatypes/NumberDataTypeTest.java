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

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class NumberDataTypeTest extends AbstractDataTypeTest<NumberDataType> {

	@Override
	protected NumberDataType createDataType() {
		return new NumberDataType();
	}

	@Test
	public void shouldSupportsNumbersForDisplayFilterAndEditing() {

		shouldSupportDataType(type, Byte.class);
		shouldSupportDataType(type, Short.class);
		shouldSupportDataType(type, Integer.class);
		shouldSupportDataType(type, Long.class);
		shouldSupportDataType(type, Double.class);
		shouldSupportDataType(type, Float.class);
		shouldSupportDataType(type, Byte.class);
		shouldSupportDataType(type, BigDecimal.class);
	}

	private void shouldSupportDataType(NumberDataType type,
			Class<? extends Number> clazz) {
		assertTrue(type.supportsDisplaying(clazz));
		assertTrue(type.supportsEditing(clazz));
	}

}
