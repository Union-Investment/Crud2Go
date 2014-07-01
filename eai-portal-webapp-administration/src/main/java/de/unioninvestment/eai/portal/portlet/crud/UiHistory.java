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
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.ui.UI;

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
public class UiHistory implements DetachListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UiHistory.class);

	static final String UIS_SESSION_ATTRIBUTE = "Crud2GoUiHistory";
	static final String LIMIT_CORRECTOR_SESSION_ATTRIBUTE = "Crud2GoUiHistoryCorrector";

	private final class OpenUIsFilter implements Predicate<HistoryAware> {
		@Override
		public boolean apply(HistoryAware ui) {
			return !ui.isClosing();
		}
	}

	/**
	 * This interface has to be implemented by a HistoryAware UI. Please check
	 * the method JavaDocs for special requirements.
	 * 
	 * @author cmj
	 * 
	 */
	public interface HistoryAware {
		// already implemented by UI

		String getId();

		Page getPage();

		long getLastHeartbeatTimestamp();

		void close();

		boolean isClosing();

		public void addDetachListener(DetachListener listener);

		/**
		 * This method has to be overridden to additionally call
		 * {@link UiHistory#handleHeartbeat(HistoryAware)}.
		 * 
		 * @param lastHeartbeat
		 */
		public void setLastHeartbeatTimestamp(long lastHeartbeat);

		// new required methods

		/**
		 * @return the maximum position in history before removing the UI from
		 *         the session.
		 */
		int getHistoryLimit();

		/**
		 * @return the previously stored history state
		 */
		UiHistoryState getHistoryState();

		/**
		 * @param state
		 *            history state to be stored inside the {@link UI}.
		 */
		void setHistoryState(UiHistoryState state);
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
			ui.addDetachListener(this);
			ui.setHistoryState(new UiHistoryState());

			addUiToSession(ui, portletSession);

			closeOldUIs(ui, portletSession);
		}
	}

	/**
	 * @param ui
	 *            new UI instance
	 * @param portletSession
	 *            the session storing all ui instances including the new one
	 */
	private void closeOldUIs(HistoryAware ui, PortletSession portletSession) {
		Collection<HistoryAware> allUIs = getUIsFromSession(portletSession);
		List<HistoryAware> uiPageHistory = filterRelevantUIs(allUIs, ui
				.getPage().getWindowName());
		LOGGER.debug("Checking UI history for window name "
				+ Page.getCurrent().getWindowName()
				+ ", having {} of {} UIs in Session", uiPageHistory.size(),
				allUIs.size());
		closeOldUIs(uiPageHistory, portletSession);
	}

	/**
	 * @param uis
	 *            the list of the UIs of the current session and page/tab sorted
	 *            descending by actuality
	 */
	private void closeOldUIs(List<HistoryAware> uis, PortletSession session) {
		int pos = 0;
		int deletedUIs = 0;
		for (HistoryAware ui : uis) {
			UiHistoryState state = ui.getHistoryState();
			int correctedPos = pos + state.getNumberOfNewerDeletedUIs();
			int maxHistoryLength = getMaxUiHistoryLength(ui);
			if (maxHistoryLength < correctedPos) {
				LOGGER.info("Closing UI {}", ui.getId());
				ui.close();
				deletedUIs++;
			} else {
				state.addToNumberOfNewerDeletedUIs(deletedUIs);
			}
			pos++;
		}
	}

	public void handleHeartbeat(HistoryAware ui) {
		ui.getHistoryState().resetNumberOfNewerDeletedUIs();
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

	@Override
	public void detach(DetachEvent event) {
		PortletSession portletSession = getCurrentSession();
		Object mutex = PortletUtils.getSessionMutex(portletSession);
		synchronized (mutex) {
			removeUiFromSession(portletSession,
					(HistoryAware) event.getSource());
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

	private void addUiToSession(HistoryAware newUi,
			PortletSession portletSession) {
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
