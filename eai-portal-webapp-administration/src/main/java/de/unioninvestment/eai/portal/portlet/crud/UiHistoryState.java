package de.unioninvestment.eai.portal.portlet.crud;

import java.io.Serializable;

import com.vaadin.ui.UI;

import de.unioninvestment.eai.portal.portlet.crud.UiHistory.HistoryAware;

/**
 * State needed by {@link UiHistory} that has to be stored by {@link HistoryAware} {@link UI}s.
 * 
 * @author cmj
 * 
 */
public class UiHistoryState implements Serializable {

	private static final long serialVersionUID = 1L;

	private int numberOfNewerDeletedUIs = 0;

	public int getNumberOfNewerDeletedUIs() {
		return numberOfNewerDeletedUIs;
	}

	public void addToNumberOfNewerDeletedUIs(int deletedUIs) {
		numberOfNewerDeletedUIs += deletedUIs;
	}

	public void resetNumberOfNewerDeletedUIs() {
		this.numberOfNewerDeletedUIs = 0;
	}
}
