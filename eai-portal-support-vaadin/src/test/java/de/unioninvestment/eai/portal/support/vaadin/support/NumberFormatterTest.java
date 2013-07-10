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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.junit.Test;

public class NumberFormatterTest {
	@Test
	public void shouldParseNumberWithNullValue() {
		assertThat(new NumberFormatter(null).convertToModel(null,
				Integer.class, null), is(nullValue()));
		assertThat(
				new NumberFormatter(null).convertToModel("", Float.class, null),
				is(nullValue()));
	}

	@Test
	public void shouldFormatCorrectly() {
		NumberFormatter formatter = new NumberFormatter(null);

		// assertFormatted(formatter, null, "");
		assertConversion(formatter, Integer.class, 4711, "4,711");
		assertConversion(formatter, Integer.class, -4711, "-4,711");
		assertConversion(formatter, Long.class, 4711L, "4,711");
		assertConversion(formatter, Short.class, (short) 4711, "4,711");
		assertConversion(formatter, Byte.class, (byte) 12, "12");
		assertConversion(formatter, Float.class, 123.12f, "123.12");
		assertConversion(formatter, Double.class, 123.12, "123.12");
		assertConversion(formatter, BigDecimal.class, new BigDecimal("123.12"),
				"123.12");
		assertConversion(formatter, BigDecimal.class,
				new BigDecimal("1421.121"), "1,421.121");
	}

	private <T extends Number> void assertConversion(NumberFormatter formatter,
			Class<T> modelType, T modelValue, String formattedValue) {

		assertFormatted(formatter, modelValue, formattedValue);
		assertParsed(formatter, formattedValue, modelType, modelValue);
	}

	@Test
	public void shouldFormatCorrectlyWithCustomPattern() {
		NumberFormat nf = new org.springframework.format.number.NumberFormatter(
				"#,##0.00").getNumberFormat(Locale.GERMANY);

		NumberFormatter formatter = new NumberFormatter(nf);

		assertConversion(formatter, BigDecimal.class, new BigDecimal(
				"421121.00"), "421.121,00");
		assertConversion(formatter, BigDecimal.class, new BigDecimal(
				"1114121.23"), "1.114.121,23");
		assertConversion(formatter, BigDecimal.class, new BigDecimal(
				"1114121.23"), "1.114.121,23");

		nf = new org.springframework.format.number.NumberFormatter("#,##0.00")
				.getNumberFormat(Locale.ENGLISH);
		formatter = new NumberFormatter(nf);

		assertConversion(formatter, BigDecimal.class, new BigDecimal(
				"1114121.23"), "1,114,121.23");

		nf = new org.springframework.format.number.NumberFormatter("#,##0%")
				.getNumberFormat(Locale.GERMAN);
		formatter = new NumberFormatter(nf);

		assertConversion(formatter, BigDecimal.class, new BigDecimal("-1234"),
				"-123.400%");

	}

	@Test
	public void shouldAcceptEmptyLineAsNull() {
		NumberFormatter formatter = new NumberFormatter(null);
		assertParsed(formatter, "", Integer.class, null);
	}

	private <T extends Number> void assertParsed(NumberFormatter formatter,
			String formattedValue, Class<T> targetType, T expectedValue) {
		assertThat(formatter.convertToModel(formattedValue, targetType, null),
				is((Number) expectedValue));
	}

	private void assertFormatted(NumberFormatter formatter, Number modelValue,
			String formattedValue) {
		assertThat(
				formatter.convertToPresentation(modelValue, String.class, null),
				is(formattedValue));
	}

}
