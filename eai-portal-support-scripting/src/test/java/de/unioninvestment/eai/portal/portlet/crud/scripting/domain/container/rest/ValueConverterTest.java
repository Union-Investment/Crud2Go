package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Test;

public class ValueConverterTest {

	private ValueConverter converter = new ValueConverter();

	@Test
	public void shouldReturnNullOnNullValue() {
		assertThat(converter.convertValue(String.class, null, null, null),
				is(nullValue()));
	}

	@Test
	public void shouldReturnSameObjectIfTypeMatches() {
		Object expectedDate = new GregorianCalendar(2013, 02, 13).getTime();

		Object convertedValue = converter.convertValue(Date.class,
				null, null, expectedDate);

		assertThat(convertedValue, sameInstance(expectedDate));
	}

	@Test
	public void shouldReturnDateFromNumberInterpretedAsUnixTypeNumber() {
		Date expectedDate = new GregorianCalendar(2013, 02, 13).getTime();
		long value = expectedDate.getTime();

		Object convertedValue = converter.convertValue(Date.class,
				"dd. MMM yyyy", Locale.GERMANY, value);

		assertThat(convertedValue, is((Object) expectedDate));
	}

	@Test
	public void shouldReturnNullDateFromBlankValue() {

		Object convertedValue = converter.convertValue(Date.class,
				"dd. MMM yyyy", Locale.GERMANY, "");

		assertThat(convertedValue, is(nullValue()));
	}

	@Test
	public void shouldReturnDateFromStringUsingFormatAndLocale() {
		Object expectedDate = new GregorianCalendar(2013, 02, 13).getTime();

		Object convertedValue = converter.convertValue(Date.class,
				"dd. MMM yyyy", Locale.GERMANY, "13. März 2013");

		assertThat(convertedValue, is(expectedDate));
	}

	@Test
	public void shouldReturnDateFromIso8601String() {
		Object expectedDate = new GregorianCalendar(2013, 02, 13, 13, 42, 01)
				.getTime();

		Object convertedValue = converter.convertValue(Date.class,
				"iso8601", Locale.GERMANY, "2013-03-13T13:42:01.000");

		assertThat(convertedValue, is(expectedDate));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfDateStringIsNotParsable() {
		converter.convertValue(Date.class, "dd.MM.yyyy", Locale.GERMAN,
				"123456");
	}

	@Test
	public void shouldReturnStringFromAnythingUsingToStringMethod() {
		Object expectedString = "132.3";
		Object convertedValue = converter.convertValue(String.class, null,
				null, 132.3);

		assertThat(convertedValue, is(expectedString));
	}

}
