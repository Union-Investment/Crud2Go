package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mock;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.GenericVaadinContainerEventWrapper;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class ReSTContainerTest
		extends
		AbstractDataContainerTest<ReSTContainer, GenericVaadinContainerEventWrapper> {

	private EventBus eventBus = new EventBus();

	@Mock
	private ReSTDelegate delegateMock;

	@Mock
	private MetaData metaDataMock;

	@Override
	public ReSTContainer createDataContainer() {

		ReSTContainer container = new ReSTContainer(
				eventBus, displayPatternMock, new ArrayList<ContainerOrder>(),
				null);

		when(delegateMock.getMetaData()).thenReturn(metaDataMock);
		container.setDelegate(delegateMock);

		container.setMetaData(metaDataMock);
		container.setVaadinContainer(vaadinContainerMock);

		return container;
	}

	@Override
	public GenericVaadinContainerEventWrapper createVaadinContainer() {
		GenericVaadinContainerEventWrapper eventWrapper = mock(
				GenericVaadinContainerEventWrapper.class, withSettings()
						.extraInterfaces(Filterable.class));
		return eventWrapper;
	}

	@Test
	public void shouldDelegateChangingBaseUrlToDelegate() {
		container.setBaseUrl("newBaseUrl");
		verify(delegateMock).setBaseUrl("newBaseUrl");
	}

	@Test
	public void shouldRefreshIfBaseUrlChanges() {
		container.setBaseUrl("newBaseUrl");
		verify(vaadinContainerMock).refresh();
	}

	@Test
	public void shouldDelegateChangingQueryUrlToDelegate() {
		container.setQueryUrl("newUrl");
		verify(delegateMock).setQueryUrl("newUrl");
	}

	@Test
	public void shouldRefreshIfQueryUrlChanges() {
		container.setQueryUrl("newUrl");
		verify(vaadinContainerMock).refresh();
	}
}
