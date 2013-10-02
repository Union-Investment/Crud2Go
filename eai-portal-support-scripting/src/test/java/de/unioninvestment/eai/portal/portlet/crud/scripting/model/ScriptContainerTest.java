package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import groovy.lang.Closure;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.EachRowCallback;

public class ScriptContainerTest {

	@Mock
	private DataContainer dataContainerMock;

	@Mock
	private Closure<?> closureMock;

	private ScriptContainer scriptContainer;

	@Mock
	private ContainerRow containerRowMock;

	@Captor
	private ArgumentCaptor<EachRowCallback> callbackCaptor;

	@Captor
	private ArgumentCaptor<ScriptRow> rowCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		scriptContainer = new ScriptContainer(dataContainerMock);
	}

	@Test
	public void shouldDelegateEachRowToContainer() {

		scriptContainer.eachRow(closureMock);
		verify(dataContainerMock).eachRow(callbackCaptor.capture());

		callbackCaptor.getValue().doWithRow(containerRowMock);
		verify(closureMock).call(rowCaptor.capture());
		assertThat(rowCaptor.getValue().getRow(),
				sameInstance(containerRowMock));
	}
}
