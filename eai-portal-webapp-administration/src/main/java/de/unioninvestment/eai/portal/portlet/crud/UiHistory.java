package de.unioninvestment.eai.portal.portlet.crud;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.portlet.util.PortletUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinRequest;

/**
 * Management of {@link HistoryAware} UI instances in a portlet session.
 * 
 * Normally, Vaadin portlet instances are kept in memory until a heartbeat
 * timeout occurs, or the HTTP session ends. This service tracks a history of
 * portlet instances per browser window/tab and eagerly closes "old" instances.
 * 
 * @author cmj
 */
@Component
public class UiHistory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UiHistory.class);

	static final String UIS_SESSION_ATTRIBUTE = "Crud2GoUIs";

	private final class OpenUIsFilter implements Predicate<HistoryAware> {
		@Override
		public boolean apply(HistoryAware ui) {
			return !ui.isClosing();
		}
	}

	public interface HistoryAware {
		String getId();

		Page getPage();

		long getLastHeartbeatTimestamp();

		int getHistoryLimit();

		void close();

		boolean isClosing();
	}

	private final class WindowNameFilter implements Predicate<HistoryAware> {
		private final String currentWindowName;

		private WindowNameFilter(String currentWindowName) {
			this.currentWindowName = currentWindowName;
		}

		@Override
		public boolean apply(HistoryAware ui) {
			return currentWindowName != null
					&& currentWindowName.equals(ui.getPage().getWindowName());
		}
	}

	private static final class HeartbeatTimestampComparator implements
			Comparator<HistoryAware> {
		@Override
		public int compare(HistoryAware o1, HistoryAware o2) {
			return new Long(o2.getLastHeartbeatTimestamp()).compareTo(o1
					.getLastHeartbeatTimestamp());
		}
	}


	public void add(HistoryAware ui) {
		PortletSession portletSession = getCurrentSession();
		Object mutex = PortletUtils.getSessionMutex(portletSession);
		synchronized (mutex) {
			addUiToSession(portletSession, ui);
			Collection<HistoryAware> allUIs = getUIsFromSession(portletSession);
			List<HistoryAware> uiPageHistory = filterRelevantUIs(allUIs, ui.getPage().getWindowName());
			LOGGER.debug("Checking UI history for window name "
					+ Page.getCurrent().getWindowName()
					+ ", having {} of {} UIs in Session", uiPageHistory.size(),
					allUIs.size());
			closeOldUIs(uiPageHistory);
		}
	}

	/**
	 * @param uis
	 *            the list of the UIs of the current session and page/tab sorted
	 *            descending by actuality
	 */
	private void closeOldUIs(List<HistoryAware> uis) {
		int pos = 0;
		for (HistoryAware ui : uis) {
			int maxHistoryLength = getMaxUiHistoryLength(ui);
			if (maxHistoryLength < pos) {
				LOGGER.info("Closing UI {}", ui.getId());
				// UIs have to inform UiHistory in detach() via
				// #handleDetach()
				ui.close();
			}
			pos++;
		}
	}

	private ImmutableList<HistoryAware> filterRelevantUIs(
			Collection<HistoryAware> allUIs, String currentWindowName) {
		ImmutableList<HistoryAware> uisOfPageSortedByLastHeartbeatTimestamp = FluentIterable
				.from(allUIs)
				//
				.filter(new WindowNameFilter(currentWindowName))
				.filter(new OpenUIsFilter())
				.toSortedList(new HeartbeatTimestampComparator());
		return uisOfPageSortedByLastHeartbeatTimestamp;
	}

	public void handleDetach(HistoryAware ui) {
		PortletSession portletSession = getCurrentSession();
		Object mutex = PortletUtils.getSessionMutex(portletSession);
		synchronized (mutex) {
			removeUiFromSession(portletSession, ui);
		}
	}

	/**
	 * @param portletSession2
	 * @return the UIs
	 */
	@SuppressWarnings("unchecked")
	private List<HistoryAware> getUIsFromSession(PortletSession portletSession) {
		List<HistoryAware> uis = (List<HistoryAware>) portletSession
				.getAttribute(UIS_SESSION_ATTRIBUTE,
						PortletSession.APPLICATION_SCOPE);
		if (uis == null) {
			uis = new LinkedList<HistoryAware>();
		}
		return uis;
	}

	private void addUiToSession(PortletSession portletSession,
			HistoryAware newUi) {
		List<HistoryAware> uis = getUIsFromSession(portletSession);
		uis.add(0, newUi);
		updateUIsInSession(uis);
	}

	private void removeUiFromSession(PortletSession portletSession,
			HistoryAware removedUi) {
		List<HistoryAware> uis = getUIsFromSession(portletSession);
		uis.remove(removedUi);
		updateUIsInSession(uis);
	}

	private void updateUIsInSession(List<HistoryAware> uis) {
		getCurrentSession().setAttribute(UIS_SESSION_ATTRIBUTE, uis,
				PortletSession.APPLICATION_SCOPE);
	}

	private PortletSession getCurrentSession() {
		PortletSession portletSession = VaadinPortletService
				.getCurrentPortletRequest().getPortletSession();
		return portletSession;
	}

	private int getMaxUiHistoryLength(HistoryAware ui) {
		return ui.getHistoryLimit();
	}

}
