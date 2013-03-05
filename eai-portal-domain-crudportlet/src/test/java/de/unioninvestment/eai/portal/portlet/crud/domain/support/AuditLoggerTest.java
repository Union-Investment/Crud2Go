package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.junit.rules.Log4jStub;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;
import de.unioninvestment.eai.portal.support.vaadin.LiferayApplicationMock;

public class AuditLoggerTest {

	@Rule
	public Log4jStub logging = new Log4jStub(
			"de.uit.eai.portal.crud.auditLogger.4711", Level.INFO);

	@Mock
	private CurrentUser currentUserMock;

	private AuditLogger auditLogger;

	private LiferayApplicationMock applicationMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		applicationMock = new LiferayApplicationMock(null, null, "portletId",
				4711);
		auditLogger = new AuditLogger(currentUserMock);
	}

	@After
	public void cleanUp() {
		applicationMock.onRequestEnd(null, null);
	}

	@Test
	public void shouldAuditRestRequestWithoutBody() {
		auditLogger.auditReSTRequest("POST", "http://test", "HTTP/1.1 200 OK");
		logging.assertInfo("Benutzer <Anonymous> - Method <POST> - URL <http://test> - Status <HTTP/1.1 200 OK>");
	}

	@Test
	public void shouldAuditRestRequestWithBody() {
		auditLogger.auditReSTRequest("POST", "http://test", "mybody",
				"HTTP/1.1 200 OK");
		logging.assertInfo("Benutzer <Anonymous> - Method <POST> - URL <http://test> - Status <HTTP/1.1 200 OK>\nmybody");
	}
}
