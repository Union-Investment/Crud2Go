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

import com.vaadin.data.util.PropertyFormatter;

import de.unioninvestment.eai.portal.support.vaadin.PortletApplication;

/**
 * Formatierung und Parsing von SQL Number. Wird als Wrapper um eine
 * entsprechende PropertyDataSource verwendet.
 * 
 * @author carsten.mjartan
 */
@SuppressWarnings("unchecked")
public class NumberFormatter extends PropertyFormatter {

	private static final long serialVersionUID = 1L;
	private Class<? extends Number> targetClass;

	private NumberFormat format;
	/**
	 * @param numberClass
	 *            die Klasse für die eine Formatierung durchgeführt werden soll
	 */
	public NumberFormatter(Class<? extends Number> numberClass,
			NumberFormat format) {
		this.targetClass = numberClass;
		this.format = format;
	}

	/**
	 * Formatiert per toString().
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String format(Object value) {
		if (value == null || value == Boolean.FALSE) {
			return null;
		} else {
			if (format != null) {
				return format.format(value);
			} else {
				return getDefaultFormat().format(value);
			}
		}
	}

	/**
	 * Parst Zahlen und liefert eine sinnvolle Fehlermeldung.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Object parse(String formattedValue) throws Exception {
		return parseNumber(formattedValue, targetClass, format);
	}

	/**
	 * @param <T>
	 *            der Zieldatentyp
	 * @param formattedValue
	 *            der zu konvertierende Text
	 * @param targetClass
	 *            Klasse des Zieldatentypen
	 * @return Wert
	 * @throws NumberFormatException
	 *             bei Konvertierungsfehlern
	 */
	public static <T extends Number> T parseNumber(String formattedValue,
			Class<T> targetClass, NumberFormat format) {

		if (formattedValue == null || formattedValue.trim().length() == 0) {
			return null;
		}

		try {
			Number number;
			if (format != null) {
				number = format.parse(formattedValue);
			} else {
				number = getDefaultFormat().parse(formattedValue);
			}

			Constructor<T> constructor = targetClass
					.getConstructor(String.class);
			return (T) constructor.newInstance(number.toString());

		} catch (Exception e) {
			throw new NumberFormatException(
					"Bitte geben Sie einge gültige Zahl vom Typ '"
							+ targetClass.getSimpleName() + "' an.");
		}

	}

	private static NumberFormat getDefaultFormat() {
		Locale locale;
		if (PortletApplication.getCurrentRequest() != null
				&& PortletApplication.getCurrentRequest().getLocale() != null) {
			locale = PortletApplication.getCurrentRequest().getLocale();
		} else {
			locale = Locale.US;
		}
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setGroupingUsed(true);

		return nf;
	}

}
