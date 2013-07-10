package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.GString;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;

public class ScriptDatabaseQueryContainerTest {

	@Mock
	private DatabaseQueryContainer containerMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldConvertQueryHelperToGString() {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = ? ");
		queryHelper.addParameterValue(1);

		GString expectedResult = new GStringImpl(new Object[] { 1 },
				new String[] { "select * from TEST where x = ", " " });

		checkForValidResult(queryHelper, expectedResult);
	}

	@Test
	public void shouldPassPreserveOrderOption() {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = ? ");
		queryHelper.addParameterValue(1);

		when(containerMock.getCurrentQuery(true)).thenReturn(queryHelper);

		ScriptDatabaseQueryContainer scriptContainer = new ScriptDatabaseQueryContainer(
				containerMock);

		scriptContainer.getCurrentQuery(singletonMap("preserveOrder",
				(Object) true));
		verify(containerMock).getCurrentQuery(true);
	}

	@Test
	public void shouldNotPassPreserveOrderOptionByDefault() {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = ? ");
		queryHelper.addParameterValue(1);

		when(containerMock.getCurrentQuery(false)).thenReturn(queryHelper);

		ScriptDatabaseQueryContainer scriptContainer = new ScriptDatabaseQueryContainer(
				containerMock);

		scriptContainer.getCurrentQuery(null);
		verify(containerMock).getCurrentQuery(false);
	}

	@Test
	public void shouldHandleMultipleParameters() {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = ? and y=?");
		queryHelper.addParameterValue(1);
		queryHelper.addParameterValue(2);

		GString expectedResult = new GStringImpl(new Object[] { 1, 2 },
				new String[] { "select * from TEST where x = ", " and y=", "" });

		checkForValidResult(queryHelper, expectedResult);
	}

	@Test
	public void shouldIgnoreQuestionMarksInCharacterSequences() {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = '?' and y=?");
		queryHelper.addParameterValue(2);

		GString expectedResult = new GStringImpl(new Object[] { 2 },
				new String[] { "select * from TEST where x = '?' and y=", "" });

		checkForValidResult(queryHelper, expectedResult);
	}

	private void checkForValidResult(StatementHelper queryHelper,
			GString expectedResult) {
		when(containerMock.getCurrentQuery(false)).thenReturn(queryHelper);

		ScriptDatabaseQueryContainer scriptContainer = new ScriptDatabaseQueryContainer(
				containerMock);

		GString result = scriptContainer.getCurrentQuery(singletonMap(
				"preserveOrder", (Object) false));
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldHandleNullInteger() {
		checkSupportForNullParameterType(Integer.class);
	}

	@Test
	public void shouldHandleNullString() {
		checkSupportForNullParameterType(String.class);
	}

	@Test
	public void shouldHandleNullBoolean() {
		checkSupportForNullParameterType(Boolean.class);
	}

	@Test
	public void shouldHandleNullByte() {
		checkSupportForNullParameterType(Byte.class);
	}

	@Test
	@Ignore("Currently not handled by SQLContainer")
	public void shouldHandleNullDate() {
		checkSupportForNullParameterType(Date.class);
	}

	@Test
	public void shouldHandleNullDouble() {
		checkSupportForNullParameterType(Double.class);
	}

	@Test
	public void shouldHandleNullFloat() {
		checkSupportForNullParameterType(Float.class);
	}

	@Test
	public void shouldHandleNullLong() {
		checkSupportForNullParameterType(Long.class);
	}

	@Test
	public void shouldHandleNullShort() {
		checkSupportForNullParameterType(Short.class);
	}

	@Test
	public void shouldHandleNullBigDecimal() {
		checkSupportForNullParameterType(BigDecimal.class);
	}

	@Test
	public void shouldHandleNullSqlDate() {
		checkSupportForNullParameterType(java.sql.Date.class);
	}

	@Test
	public void shouldHandleNullTime() {
		checkSupportForNullParameterType(Time.class);
	}

	@Test
	public void shouldHandleNullTimestamp() {
		checkSupportForNullParameterType(Timestamp.class);
	}

	private void checkSupportForNullParameterType(Class<?> parameterType) {
		StatementHelper queryHelper = new StatementHelper();
		queryHelper.setQueryString("select * from TEST where x = ?");
		queryHelper.addParameterValue(null, parameterType);

		when(containerMock.getCurrentQuery(false)).thenReturn(queryHelper);

		ScriptDatabaseQueryContainer scriptContainer = new ScriptDatabaseQueryContainer(
				containerMock);

		GString expectedResult = new GStringImpl(new Object[] { null },
				new String[] { "select * from TEST where x = ", "" });
		assertThat(scriptContainer.getCurrentQuery(singletonMap("sorted",
				(Object) false)), equalTo(expectedResult));
	}
}
