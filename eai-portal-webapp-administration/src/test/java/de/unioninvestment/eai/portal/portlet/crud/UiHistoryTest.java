package de.unioninvestment.eai.portal.portlet.crud;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.portlet.PortletSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.UiHistory.HistoryAware;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class UiHistoryTest {

	static class HistoryAwareUI extends UI implements HistoryAware {
		private static final long serialVersionUID = 1L;
		
		private UiHistoryState state;

		@Override
		public int getHistoryLimit() {
			return 0;
		}

		@Override
		protected void init(VaadinRequest request) {
			
		}

		@Override
		public UiHistoryState getHistoryState() {
			return state;
		}

		@Override
		public void setHistoryState(UiHistoryState state) {
			this.state = state;
		}

	}
	
	@Spy
	private HistoryAwareUI ui1 = new HistoryAwareUI();
	@Spy
	private HistoryAwareUI ui2 = new HistoryAwareUI();
	@Spy
	private HistoryAwareUI ui3 = new HistoryAwareUI();
	@Spy
	private HistoryAwareUI ui4 = new HistoryAwareUI();

	@Mock
	private Page page1, page2, page3, page4;

	private UiHistory history = new UiHistory();

	@Rule
	public LiferayContext context = new LiferayContext();

	private List<HistoryAware> listInSession;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		stubSessionAttributeOperations();

		when(ui1.isClosing()).thenReturn(false);
		when(ui2.isClosing()).thenReturn(false);
		when(ui3.isClosing()).thenReturn(false);
		when(ui4.isClosing()).thenReturn(false);

		when(ui1.getLastHeartbeatTimestamp()).thenReturn(1L);
		when(ui2.getLastHeartbeatTimestamp()).thenReturn(2L);
		when(ui3.getLastHeartbeatTimestamp()).thenReturn(3L);
		when(ui4.getLastHeartbeatTimestamp()).thenReturn(4L);

		when(ui1.getPage()).thenReturn(page1);
		when(ui2.getPage()).thenReturn(page2);
		when(ui3.getPage()).thenReturn(page3);
		when(ui4.getPage()).thenReturn(page4);

		when(page1.getWindowName()).thenReturn("window1");
		when(page2.getWindowName()).thenReturn("window1");
		when(page3.getWindowName()).thenReturn("window1");
		when(page4.getWindowName()).thenReturn("window1");

		when(ui1.getHistoryLimit()).thenReturn(1);
		when(ui2.getHistoryLimit()).thenReturn(1);
		when(ui3.getHistoryLimit()).thenReturn(1);
		when(ui4.getHistoryLimit()).thenReturn(1);
	}

	private void stubSessionAttributeOperations() {
		when(
				context.getPortletSessionMock().getAttribute(
						UiHistory.UIS_SESSION_ATTRIBUTE,
						PortletSession.APPLICATION_SCOPE)).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return listInSession;
					}
				});
		doAnswer(new Answer<Object>() {
			@SuppressWarnings("unchecked")
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				listInSession = (List<HistoryAware>) invocation.getArguments()[1];
				return null;
			}
		}).when(context.getPortletSessionMock()).setAttribute(
				eq(UiHistory.UIS_SESSION_ATTRIBUTE), anyObject(),
				eq(PortletSession.APPLICATION_SCOPE));
	}

	@Test
	public void shouldRemoveOldUIOfSamePage() {
		history.add(ui1); // oldest heartbeat timestamp
		history.add(ui2);
		history.add(ui3);

		verify(ui1).close();
		verify(ui2, never()).close();
		verify(ui3, never()).close();
	}

	@Test
	public void shouldRemoveOldUIBasedOnHistoryLimitEvenIfNewerPagesAreRemovedBefore() {
		when(ui1.getHistoryLimit()).thenReturn(2);
		when(ui2.getHistoryLimit()).thenReturn(0);
		when(ui3.getHistoryLimit()).thenReturn(0);
		when(ui3.getHistoryLimit()).thenReturn(0);

		history.add(ui1); // oldest heartbeat timestamp
		
		history.add(ui2);
		history.add(ui3);
		verify(ui2).close();
		history.detach(new DetachEvent(ui2));
		
		history.add(ui4);
		verify(ui3).close();
		history.detach(new DetachEvent(ui3));
		
		verify(ui1).close();
	}
	
	@Test
	public void shouldResetNewerPageCounterOnHeartbeat() {
		when(ui1.getHistoryLimit()).thenReturn(2);
		when(ui2.getHistoryLimit()).thenReturn(0);
		when(ui3.getHistoryLimit()).thenReturn(0);
		when(ui3.getHistoryLimit()).thenReturn(0);
		
		history.add(ui1); // oldest heartbeat timestamp
		
		history.add(ui2);
		history.add(ui3);
		verify(ui2).close();
		history.detach(new DetachEvent(ui2));
		
		history.handleHeartbeat(ui1);
		
		history.add(ui4);
		verify(ui3).close();
		history.detach(new DetachEvent(ui3));
		
		verify(ui1, never()).close();
	}
	
	@Test
	public void shouldSelectOldUIsByLastHeartbeat() {
		history.add(ui2);
		history.add(ui1); // oldest heartbeat timestamp
		history.add(ui3);

		verify(ui1).close();
		verify(ui2, never()).close();
		verify(ui3, never()).close();
	}

	@Test
	public void shouldIgnoreUIsThatAreAlreadyClosing() {
		when(ui1.isClosing()).thenReturn(true);
		
		history.add(ui1); // oldest heartbeat timestamp
		history.add(ui2);
		history.add(ui3);
		
		verify(ui1, never()).close();
		verify(ui2, never()).close();
		verify(ui3, never()).close();
	}
	

	@Test
	public void shouldNotRemoveOldUIOfOtherPage() {
		when(page3.getWindowName()).thenReturn("other");
		
		history.add(ui1);
		history.add(ui2);
		history.add(ui3);

		verify(ui1, never()).close();
		verify(ui2, never()).close();
		verify(ui3, never()).close();
	}
	
	@Test
	public void shouldRemoveDetachedUIsFromSession() {
		history.add(ui1);

		history.detach(new DetachEvent(ui1));

		assertThat(listInSession, is(Collections.<HistoryAware>emptyList()));
	}

}
