package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TableFieldFactory;

public interface CrudFieldFactory extends TableFieldFactory {

	@Override
	Field<?> createField(Container container, Object itemId,
			Object propertyId, Component uiContext);

	/**
	 * Erstellung eines Formularfeldes.
	 * 
	 * @param item Tabellenzeile
	 * @param propertyId Feld-ID
	 * @return ein Editierfeld oder <code>null</code>
	 */
	Field<?> createField(Item item, Object propertyId);

}