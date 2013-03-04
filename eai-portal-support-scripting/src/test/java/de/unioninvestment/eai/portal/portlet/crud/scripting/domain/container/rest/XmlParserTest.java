package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public class XmlParserTest {

	@Mock
	private ScriptBuilder scriptBuilderMock;
	@Mock
	private Closure<Object> collectionClosureMock;

	private XmlParser parser;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldParseXml() throws IOException {

		ReSTContainerConfig config = new ReSTContainerConfig();
		parser = new XmlParser(config, scriptBuilderMock);

		StringReader reader = new StringReader(
				"<items><item>1</item><item>2</item></items>");

		Object items = parser.parseData(reader);

		GroovyShell shell = new GroovyShell(
				new Binding(singletonMap("items", items)));
		assertThat((Integer) shell.evaluate("items.item.size()"), is(2));
	}

	@Test
	public void shouldUseCollectionClosureToReturnCollectionFromData()
			throws IOException {
		// given
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.getQuery().setCollection(new GroovyScript("bla"));

		parser = new XmlParser(config, scriptBuilderMock);

		StringReader reader = new StringReader(
				"<items><item>1</item><item>2</item></items>");
		Object items = parser.parseData(reader);

		when(scriptBuilderMock.buildClosure(config.getQuery().getCollection()))
				.thenReturn(collectionClosureMock);
		GroovyShell shell = new GroovyShell(
				new Binding(singletonMap("items", items)));

		when(collectionClosureMock.call(items)).thenReturn(
				shell.evaluate("items.item"));

		// when
		Iterable<?> collection = parser.getCollection(items);

		// then
		Iterator<?> iterator = collection.iterator();
		assertThat(iterator.next(), notNullValue());
		assertThat(iterator.next(), notNullValue());
		assertThat(iterator.hasNext(), is(false));
	}

	@Test
	public void shouldReturnIterableRootIfCollectionClosureIsMissing()
			throws IOException {
		// given
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.getQuery().setCollection(null);

		parser = new XmlParser(config, scriptBuilderMock);

		StringReader reader = new StringReader(
				"<items><item>1</item><item>2</item></items>");
		Object items = parser.parseData(reader);

		// when
		Iterable<?> collection = parser.getCollection(items);

		// then
		Iterator<?> iterator = collection.iterator();
		assertThat(iterator.next(), notNullValue());
		assertThat(iterator.hasNext(), is(false));
	}

	@Test(expected = InvalidConfigurationException.class)
	public void shouldFailIfDataIsNotIterable()
			throws IOException {
		// given
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.getQuery().setCollection(null);

		parser = new XmlParser(config, scriptBuilderMock);

		// when
		parser.getCollection("notIterable");
	}

	@Test
	public void shouldReturnEmptyIterableIfResultIsNull()
			throws IOException {
		// given
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.getQuery().setCollection(null);

		parser = new XmlParser(config, scriptBuilderMock);

		// when
		Iterable<?> iterable = parser.getCollection(null);

		assertThat(iterable.iterator().hasNext(), is(false));
	}

	@Test
	public void shouldNotUnmarshalEverythingButGPathResults()
			throws IOException {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		parser = new XmlParser(config, scriptBuilderMock);

		Object unmarshalledValue = parser.unmarshalValue("1");
		assertThat(unmarshalledValue, is((Object) "1"));
	}

	@Test
	public void shouldExtractTextFromGPathResult()
			throws IOException {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		parser = new XmlParser(config, scriptBuilderMock);

		Object unmarshalledValue = parser.unmarshalValue(new GroovyShell()
				.evaluate("new XmlSlurper().parseText('<item>1</item>')"));
		assertThat(unmarshalledValue, is((Object) "1"));
	}
}
