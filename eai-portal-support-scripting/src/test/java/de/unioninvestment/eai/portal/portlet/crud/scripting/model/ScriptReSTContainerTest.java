package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;

public class ScriptReSTContainerTest {

	@Mock
	private ReSTContainer containerMock;

	private ScriptReSTContainer scriptContainer;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.scriptContainer = new ScriptReSTContainer(containerMock);
	}

	@Test
	public void shouldDelegateSetBaseUrl() {
		scriptContainer.setBaseUrl("test");
		verify(containerMock).setBaseUrl("test");
	}

	@Test
	public void shouldDelegateSetQueryUrl() {
		scriptContainer.setQueryUrl("test");
		verify(containerMock).setQueryUrl("test");
	}
}
