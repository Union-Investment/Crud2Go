package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.junit.rules.Log4jStub;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser;

public class AuditLoggerTest {

	@Rule
	public Log4jStub logging = new Log4jStub(
			"de.uit.eai.portal.crud.auditLogger.-1", Level.INFO);

	@Mock
	private CurrentUser currentUserMock;

	private AuditLogger auditLogger;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		auditLogger = new AuditLogger(currentUserMock);
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
