package de.unioninvestment.eai.portal.portlet.crud.scripting.category;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateCategoryTest {

	@Test
	public void shouldFormatDatesAsIso8601String() {
		GregorianCalendar calendar = new GregorianCalendar(2013, 02, 13, 13,
				42, 01);
		calendar.setTimeZone(TimeZone.getTimeZone("CET"));
		Date date = calendar.getTime();
		String expectedString = "2013-03-13T13:42:01+01:00";

		assertThat(DateCategory.getIso8601String(date), is(expectedString));
	}
}
