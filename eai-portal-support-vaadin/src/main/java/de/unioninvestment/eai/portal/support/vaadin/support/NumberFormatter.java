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

import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Formatierung und Parsing von SQL Number. Wird als Wrapper um eine
 * entsprechende PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
public class NumberFormatter implements Converter<String, Number> {

	private static final long serialVersionUID = 1L;

	private NumberFormat format;

	/**
	 * @param format
	 *            the formatting class
	 */
	public NumberFormatter(NumberFormat format) {
		this.format = format;
	}

	@Override
	public Number convertToModel(String value,
			Class<? extends Number> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return parseNumber(value, targetType, format, locale);
	}

	@Override
	public String convertToPresentation(Number value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
			return null;
		} else {
			if (format != null) {
				return format.format(value);
			} else {
				return getDefaultFormat(locale).format(value);
			}
		}
	}

	@Override
	public Class<Number> getModelType() {
		return Number.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

	/**
	 * @param <T>
	 *            der Zieldatentyp
	 * @param formattedValue
	 *            der zu konvertierende Text
	 * @param targetClass
	 *            Klasse des Zieldatentypen
	 * @param locale
	 * @return Wert
	 * @throws NumberFormatException
	 *             bei Konvertierungsfehlern
	 */
	public static <T extends Number> T parseNumber(String formattedValue,
			Class<T> targetClass, NumberFormat format, Locale locale) {

		if (formattedValue == null || formattedValue.trim().length() == 0) {
			return null;
		}

		try {
			Number number;
			if (format != null) {
				number = format.parse(formattedValue);
			} else {
				number = getDefaultFormat(locale).parse(formattedValue);
			}

			Constructor<T> constructor = targetClass
					.getConstructor(String.class);
			return (T) constructor.newInstance(number.toString());

		} catch (Exception e) {
			throw new ConversionException(
					"Bitte geben Sie eine gültige Zahl vom Typ '"
							+ targetClass.getSimpleName() + "' an.");
		}

	}

	private static NumberFormat getDefaultFormat(Locale locale) {
		NumberFormat nf = NumberFormat.getInstance(locale != null ? locale
				: Locale.US);
		nf.setGroupingUsed(true);
		return nf;
	}

}
