package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;

public class JsonCreatorTest {

	@Mock
	private GenericItem itemMock;
	@Mock
	private GenericItemId itemIdMock;

	@Mock
	private ReSTContainer containerMock;

	@Mock
	private ContainerRow containerRowMock;

	@Mock
	private ContainerField aFieldMock;

	@Mock
	private ScriptBuilder scriptBuilderMock;

	@Mock
	private Closure<Object> closureMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnJsonMimetype() {
		JsonCreator creator = newJsonCreator(RestTestConfig.readwriteConfig());
		assertThat(creator.getMimeType(), is("text/json"));
	}

	private JsonCreator newJsonCreator(ReSTContainerConfig config) {
		JsonCreator creator = new JsonCreator(containerMock,
				config, scriptBuilderMock);
		return creator;
	}

	@Test
	public void shouldReturnJsonAsByteArray()
			throws UnsupportedEncodingException {
		ReSTContainerConfig config = RestTestConfig.readwriteConfig();
		JsonCreator creator = newJsonCreator(config);

		when(scriptBuilderMock.buildClosure(config
				.getInsert().getValue())).thenReturn(closureMock);
		when(closureMock.call(any(ScriptRow.class))).thenReturn(
				singletonMap("a", 3));

		fakeInfosForCreationOfScriptRow();

		byte[] content = creator.create(itemMock,
				config.getInsert().getValue(), "UTF-8");

		assertArrayEquals("{\"a\":3}".getBytes("UTF-8"), content);
	}

	private void fakeInfosForCreationOfScriptRow() {
		when(itemMock.getId()).thenReturn(itemIdMock);
		when(containerMock.getRowByInternalRowId(itemIdMock, false, true))
				.thenReturn(containerRowMock);
	}

	@Test(expected = InvalidConfigurationException.class)
	public void shouldThrowReadableMessageOnWrongCharset() {
		ReSTContainerConfig config = RestTestConfig.readwriteConfig();
		JsonCreator creator = newJsonCreator(config);

		when(scriptBuilderMock.buildClosure(config
				.getInsert().getValue())).thenReturn(closureMock);
		when(closureMock.call(any(ScriptRow.class))).thenReturn(
				singletonMap("a", 3));

		fakeInfosForCreationOfScriptRow();

		creator.create(itemMock, config.getInsert().getValue(), "unknown");
	}

}
