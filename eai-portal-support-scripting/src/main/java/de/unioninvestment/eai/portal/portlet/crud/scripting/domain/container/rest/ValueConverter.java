package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;

/**
 * Converts for ReST-Format attributes to match the configured datatypes.
 * 
 * @author carsten.mjartan
 */
public class ValueConverter {

	/**
	 * @param targetClass
	 *            the class to convert the value into
	 * @param format
	 *            an optional format
	 * @param locale
	 *            the locale
	 * @param value
	 *            the source value
	 * @return the converted value as instance of the targetClass or
	 *         <code>null</code>
	 */
	public Object convertValue(Class<?> targetClass, String format,
			Locale locale, Object value) {
		if (value == null) {
			return null;

		} else if (isCompatibleType(targetClass, value)) {
			return value;

		} else if (targetClass == String.class) {
			return value.toString();

		} else if (targetClass == Date.class) {
			return convertValueToDate(format, locale, value);

		} else {
			return value;
		}
	}

	private boolean isCompatibleType(Class<?> targetClass, Object value) {
		return targetClass.isAssignableFrom(value.getClass());
	}

	private Object convertValueToDate(String format, Locale locale, Object value) {
		if (value instanceof Number) {
			return new Date(((Number) value).longValue());

		} else if (value instanceof String) {
			return convertStringToDate(format, locale, (String) value);

		} else {
			throw new IllegalArgumentException("Cannot convert to date: "
					+ value);
		}
	}

	private Object convertStringToDate(String format, Locale locale,
			String value) {
		try {
			if (StringUtils.isBlank(value)) {
				return null;
			} else if (StringUtils.equalsIgnoreCase(format, "iso8601")) {
				Calendar calendar = DatatypeConverter.parseDateTime(value);
				return calendar.getTime();
			} else {
				// SimpleDateFormat
				return new SimpleDateFormat(format, locale).parse(value);
			}

		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"Cannot convert to date: "
							+ value, e);
		}
	}
}
