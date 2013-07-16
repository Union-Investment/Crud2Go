package de.unioninvestment.eai.portal.portlet.crud;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.vaadin.server.ErrorEvent;
import com.vaadin.ui.Notification;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;


public class CrudErrorHandlerTest {

	@Rule
	public LiferayContext liferayContext = new LiferayContext();
	
	private CrudErrorHandler errorHandler;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		errorHandler = new CrudErrorHandler();
	}
	
	
	@Test
	public void shouldDisplayErrorsAsNotifications() {
		errorHandler.error(new ErrorEvent(new RuntimeException("MyMessage")));
		
		liferayContext.shouldShowNotification("MyMessage", null,
				Notification.Type.ERROR_MESSAGE);
	}
	
	@Test
	public void shouldDisplayRootCauseErrorsAsNotifications() {
		RuntimeException rootCause = new RuntimeException("MyMessage");
		RuntimeException higherCause = new RuntimeException(rootCause);

		ErrorEvent event = new ErrorEvent(higherCause);

		errorHandler.error(event);

		liferayContext.shouldShowNotification("MyMessage", null,
				Notification.Type.ERROR_MESSAGE);
	}


}
