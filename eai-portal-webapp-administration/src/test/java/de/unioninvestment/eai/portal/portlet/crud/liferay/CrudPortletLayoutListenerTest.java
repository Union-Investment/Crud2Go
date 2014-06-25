package de.unioninvestment.eai.portal.portlet.crud.liferay;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalService;

import de.unioninvestment.eai.portal.portlet.crud.domain.portal.LiferayTestHelper;
import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;

public class CrudPortletLayoutListenerTest {

	private static LiferayTestHelper helper = LiferayTestHelper.get();

	private LayoutLocalService layoutLocalServiceMock;
	private CrudPortletLayoutListener listener;

	@Mock
	private ConfigurationService configServiceMock;

	@Mock
	private Layout layoutMock;

	@Mock
	private Group groupMock;


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		CrudPortletLayoutListener.setConfigurationService(configServiceMock);
		listener = new CrudPortletLayoutListener();
		
		helper.resetMocks();
		layoutLocalServiceMock = helper.getLayoutLocalServiceMock();
	}

	@Test
	public void shouldRemovePortletConfigurationOnRemoveFromLayout() throws PortalException, SystemException {
		when(layoutLocalServiceMock.getLayout(3214L)).thenReturn(layoutMock);
		when(layoutMock.getGroup()).thenReturn(groupMock);
		when(groupMock.isRegularSite()).thenReturn(true);
		when(groupMock.getGroupId()).thenReturn(4711L);
		
		listener.onRemoveFromLayout("myPortletId", 3214L);
		
		verify(configServiceMock).removePortletInstanceData("myPortletId", 4711L);
	}
	
	@Test
	public void shouldNotRemoveAnythingIfCommunityIdNotDetected() throws PortalException, SystemException {
		when(layoutLocalServiceMock.getLayout(3214L)).thenReturn(layoutMock);
		when(layoutMock.getGroup()).thenReturn(groupMock);
		when(groupMock.isRegularSite()).thenReturn(false);
		
		listener.onRemoveFromLayout("myPortletId", 3214L);
		
		verifyZeroInteractions(configServiceMock);
	}
}
