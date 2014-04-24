/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;

import de.unioninvestment.eai.portal.portlet.crud.config.PanelConfig;

/**
 * Model-Klasse für ein abstraktes Panel.
 * 
 * @author markus.bonsch
 * @author Jan Malcomess (codecentric AG)
 */
public class Panel extends Component implements Serializable {

	private static final long serialVersionUID = 1L;
	private Portlet portlet;
	private List<Component> elements = new ArrayList<Component>();

	private Presenter presenter;
	private String configuredHeight;

	/**
	 * The configuration of this Panel.
	 * 
	 * @since 1.45.
	 */
	private final PanelConfig config;
	private Integer minimumHeight;

	/**
	 * Definiert die Erwartungen des Models an den Presenter.
	 * 
	 * @author Bastian Krol
	 */
	public interface Presenter {

		/**
		 * Ersetzt die Hauptseite oder eine beliebige Unterseite durch die
		 * Unterseite ({@code dialog}) mit der übergebenen ID. Falls diese
		 * Unterseite bereits angezeigt wird, hat der Aufruf keine Auswirkungen.
		 * 
		 * @param id
		 *            die ID des dialog-Tags im XML
		 * @param withMargin
		 * @throws IllegalArgumentException
		 *             falls kein Dialog mit der gegebenen {@code dialogId}
		 *             existiert
		 */
		void attachDialog(String id, boolean withMargin);

		/**
		 * Ersetzt die Unterseite wieder durch die Hauptseite.
		 */
		void detachDialog();
	}

	/**
	 * Constructor.
	 * 
	 * @param config
	 *            The configuration of this Panel. <code>null</code> is not
	 *            allowed.
	 * @throws IllegalArgumentException
	 *             if <code>config == null</code>.
	 * @since 1.45.
	 */
	public Panel(PanelConfig config) {
		Validate.notNull(config);
		this.config = config;
		if (config != null) {
			this.configuredHeight = config.getHeight();
			this.minimumHeight = config.getMinimumHeight();
		}
	}

	void setConfiguredHeight(String configuredHeight) {
		this.configuredHeight = configuredHeight;
	}

	void setMinimumHeight(Integer minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	/**
	 * protected no-argument constructor if no configuration is provided.
	 * 
	 * @deprecated This constructor is only available for compatibility to
	 *             <code>{@link RowEditingFormDialog}</code>.
	 */
	protected Panel() {
		this.config = null;
	}

	/**
	 * @return The configuration of this Panel (or Region, Tab, Dialog etc.).
	 *         <code>null</code> if none was provided during construction.
	 * @since 1.45.
	 */
	protected PanelConfig getConfig() {
		return this.config;
	}

	/**
	 * @return <code>true</code> if components within this region are to be
	 *         layed out horizontally, <code>false</code> if vertically.
	 * @since 1.45
	 */
	public boolean isHorizontalLayout() {
		return (this.config == null ? false : this.config.isHorizontalLayout());
	}

	/**
	 * @since 1.45
	 */
	public String getWidth() {
		return this.config == null ? null : this.config.getWidth();
	}

	/**
	 * @since 1.45
	 * 
	 * @return the height, given the {@link #isHeightFitsScreen()} returns
	 *         <code>false</code>
	 */
	public String getHeight() {
		if (configuredHeight == null && isHeightDefined()) {
			return "100%";
		} else {
			return configuredHeight;
		}
	}

	/**
	 * @return <code>true</code>, if the view should try to adjust the height to
	 *         fill the browser window
	 */
	public boolean isHeightFitsScreen() {
		return configuredHeight != null && configuredHeight.equals("fit");
	}

	public boolean isHeightDefined() {
		if (configuredHeight != null) {
			return true;
		}
		if (getPanel() != null && getPanel().isHeightDefined()) {
			if (this instanceof ExpandableComponent) {
				if (((ExpandableComponent) this).getExpandRatio() != 0) {
					return true;
				}
			}
		}
		return false;
	}

	protected void setPortlet(Portlet portlet) {
		this.portlet = portlet;
	}

	public Portlet getPortlet() {
		return portlet;
	}

	/**
	 * Fügt der Portlet-Seite ein Element hinzu.
	 * 
	 * @param component
	 *            Element
	 */
	protected void addComponent(Component component) {
		elements.add(component);
		component.setPanel(this);
	}

	/**
	 * Liste aller Elemente im Panel.
	 * 
	 * @return List<Component> elements
	 */
	public List<Component> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * Gibt die Position des Elements imPanel zurück.
	 * 
	 * @param element
	 *            Elements
	 * @return Position des Elements im Panel
	 */
	public int indexOf(Component element) {
		return elements.indexOf(element);
	}

	/**
	 * Gibt das nächste Element zurück.
	 * 
	 * @param <T>
	 *            Typ
	 * @param pageElementType
	 *            ElementType
	 * @return Nächstes Element
	 */
	public <T extends Component> T findNextElement(Class<T> pageElementType) {
		return findNextElement(pageElementType, null);
	}

	/**
	 * Findet das nächste Seiten-Element für ein übergebenes Seiten-Element.
	 * 
	 * @param pageElementType
	 *            Typ des Elementes
	 * @param currentElement
	 *            aktuelles Element
	 * @param <T>
	 *            Typ des Elementes
	 * 
	 * @return das gefundene Element
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> T findNextElement(Class<T> pageElementType,
			Component currentElement) {
		int i = currentElement == null ? -1 : indexOf(currentElement);
		if (elements.size() > i + 1) {
			for (Component element : elements.subList(i + 1, elements.size())) {
				if (pageElementType.isInstance(element)) {
					return (T) element;
				}
			}
		}
		return null;
	}

	/**
	 * Durchläuft des Modelbaum und liefert alle für die Suche nötigen
	 * Tabellenkomponenten.
	 * 
	 * @param component
	 *            - die Root-Komponente für die rekusive Formularsuche
	 * @return - eine Liste aller relevanten Tabellen für die Suche.
	 */
	public List<Table> findSearchableTables(Component component) {
		List<Table> result = new ArrayList<Table>();
		if (Tabs.class.isInstance(component)) {
			for (Tab tab : ((Tabs) component).getElements()) {
				for (Component c : tab.getElements()) {
					if (Form.class.isInstance(c)) {
						break;
					}
					result.addAll(findSearchableTables(c));
				}
			}
		} else if (Table.class.isInstance(component)) {
			result.add((Table) component);
		}

		return result;
	}

	/**
	 * Gibt den Presenter zurück
	 * 
	 * @return den Presenter
	 */
	Presenter getPresenter() {
		return presenter;
	}

	/**
	 * Setzt den Presenter
	 * 
	 * @return den Presenter
	 */
	public void setPresenter(Presenter eventHandler) {
		this.presenter = eventHandler;
	}

	/**
	 * Ersetzt die Hauptseite oder eine beliebige Unterseite durch die
	 * Unterseite ({@code dialog}) mit der übergebenen ID. Falls diese
	 * Unterseite bereits angezeigt wird, hat der Aufruf keine Auswirkungen.
	 * 
	 * @param dialogId
	 *            die ID des dialog-Tags im XML
	 * @param b
	 * @throws IllegalArgumentException
	 *             falls kein Dialog mit der gegebenen {@code dialogId}
	 *             existiert
	 */
	public void attachDialog(String dialogId) {
		this.presenter.attachDialog(dialogId, false);
	}

	/**
	 * Ersetzt die Unterseite ({@code dialog}) durch die Hauptseite.
	 */
	public void detachDialog() {
		this.presenter.detachDialog();
	}

	public Integer getMinimumHeight() {
		return minimumHeight;
	}

}