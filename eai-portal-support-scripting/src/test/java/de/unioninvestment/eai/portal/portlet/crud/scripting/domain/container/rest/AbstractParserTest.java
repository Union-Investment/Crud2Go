package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public class AbstractParserTest {

	static class TestParser extends AbstractParser {

		private Reader reader;
		private Object result;

		public TestParser(ReSTContainerConfig config,
				ScriptBuilder scriptBuilder, Object result) {
			super(config, scriptBuilder);
			this.result = result;
		}

		@Override
		protected Object parseData(Reader reader) throws IOException {
			this.reader = reader;
			return result;
		}

		public Reader getReader() {
			return reader;
		}
	}

	private static final String REQUEST_CONTENT = "[{a:1},{a:2}]";

	@Mock
	private ScriptBuilder scriptBuilderMock;
	@Mock
	private HttpResponse responseMock;

	@Mock
	private Closure<Object> idClosureMock;
	@Mock
	private Closure<Object> aClosureMock;

	private ReSTContainerConfig config;

	private TestParser parser;

	@Mock
	private ValueConverter converterMock;

	@Mock
	private Closure<Object> collectionClosureMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private TestParser createParser(ReSTContainerConfig config, Object result) {
		return spy(new TestParser(config, scriptBuilderMock, result));
	}

	@Test
	public void shouldDelegateParsingToTheSubclass() throws IOException {
		StringReader reader = new StringReader(REQUEST_CONTENT);
		TestParser parser = createParser(RestTestConfig.readonlyConfig(),
				new ArrayList<Object[]>());
		when(responseMock.getEntity()).thenReturn(
				new StringEntity(REQUEST_CONTENT, "UTF-8"));

		List<Object[]> rows = parser.getRows(responseMock);

		assertThat(new BufferedReader(parser.getReader()).readLine(),
				is(REQUEST_CONTENT));
	}

	@Test
	public void shouldCollectValuesFromAttributeClosureForEveryAttributeAndRow()
			throws IOException {
		// given
		prepareForParsingOfRows();

		// when
		List<Object[]> rows = parser.getRows(responseMock);

		// then
		verify(idClosureMock).setDelegate("firstRow");
		verify(idClosureMock).setDelegate("secondRow");

		verify(aClosureMock).setDelegate("firstRow");
		verify(aClosureMock).setDelegate("secondRow");

		assertArrayEquals(new Object[] { "1", "3" }, rows.get(0));
		assertArrayEquals(new Object[] { "2", "4" }, rows.get(1));
	}

	@Test
	public void shouldReturnEmptyListOnNPEWhileRetrievingCollection()
			throws IOException {
		// given
		Iterable<Object> parsedData = asList((Object) "firstRow", "secondRow");

		config = RestTestConfig.readonlyConfig();
		// add different paths for mocking
		config.getQuery().setCollection(new GroovyScript("collection"));

		parser = createParser(config, parsedData);
		when(responseMock.getEntity()).thenReturn(
				new StringEntity(REQUEST_CONTENT, "UTF-8"));

		// return mock closures for each column
		when(
				scriptBuilderMock.buildClosure(config.getQuery()
						.getCollection())).thenReturn(collectionClosureMock);
		when(collectionClosureMock.call(parsedData)).thenThrow(
				new NullPointerException());

		// when
		List<Object[]> rows = parser.getRows(responseMock);

		assertThat(rows.size(), is(0));
	}

	@Test
	public void shouldAllowParserSpecificUnmarshalingOfValuesInSubclass()
			throws IOException {
		// given
		prepareForParsingOfRows();
		config.getQuery().getAttribute().get(0).setType(Integer.class);
		config.getQuery().getAttribute().get(1).setType(Integer.class);

		// disableTypeConversion
		parser.setConverter(new ValueConverter() {
			@Override
			public Object convertValue(Class<?> targetClass, String format,
					Locale locale, Object value) {
				return value;
			}
		});

		doReturn(1).when(parser).unmarshalValue("1");
		doReturn(2).when(parser).unmarshalValue("2");
		doReturn(3).when(parser).unmarshalValue("3");
		doReturn(4).when(parser).unmarshalValue("4");

		// when
		List<Object[]> rows = parser.getRows(responseMock);

		assertArrayEquals(new Object[] { 1, 3 }, rows.get(0));
		assertArrayEquals(new Object[] { 2, 4 }, rows.get(1));
	}

	@Test
	public void shouldConvertValuesToExpectedClass()
			throws IOException {
		// given
		prepareForParsingOfRows();

		config.getQuery().getAttribute().get(0).setType(Integer.class);
		config.getQuery().getAttribute().get(1).setType(Integer.class);

		parser.setConverter(converterMock);
		when(converterMock.convertValue(Integer.class, null, null, "1"))
				.thenReturn(1);
		when(converterMock.convertValue(Integer.class, null, null, "2"))
				.thenReturn(2);
		when(converterMock.convertValue(Integer.class, null, null, "3"))
				.thenReturn(3);
		when(converterMock.convertValue(Integer.class, null, null, "4"))
				.thenReturn(4);

		// when
		List<Object[]> rows = parser.getRows(responseMock);

		assertArrayEquals(new Object[] { 1, 3 }, rows.get(0));
		assertArrayEquals(new Object[] { 2, 4 }, rows.get(1));
	}

	private void prepareForParsingOfRows() throws UnsupportedEncodingException {
		Iterable<Object> parsedData = asList((Object) "firstRow", "secondRow");

		config = RestTestConfig.readonlyConfig();
		// add different paths for mocking
		config.getQuery().getAttribute().get(0).setPath(new GroovyScript("id"));
		config.getQuery().getAttribute().get(1).setPath(new GroovyScript("a"));

		parser = createParser(config, parsedData);
		when(responseMock.getEntity()).thenReturn(
				new StringEntity(REQUEST_CONTENT, "UTF-8"));

		// return mock closures for each column
		when(
				scriptBuilderMock.buildClosure(config.getQuery().getAttribute()
						.get(0).getPath())).thenReturn(idClosureMock);
		when(
				scriptBuilderMock.buildClosure(config.getQuery().getAttribute()
						.get(1).getPath())).thenReturn(aClosureMock);

		// return values of mocks
		when(idClosureMock.call("firstRow")).thenReturn("1");
		when(aClosureMock.call("firstRow")).thenReturn("3");
		when(idClosureMock.call("secondRow")).thenReturn("2");
		when(aClosureMock.call("secondRow")).thenReturn("4");
	}
}
