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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.LiferayTheme;

import de.unioninvestment.eai.portal.portlet.crud.mvp.presenters.PanelContentPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Default-Implementierung für {@link PanelContentView}, {@link View} für
 * {@link PanelContentPresenter} Elemente.
 * 
 * @author carsten.mjartan
 * @author Jan Malcomess (codecentric AG)
 * @see PanelContentView
 * @since 1.45 extending Panel
 */
public class DefaultPanelContentView extends Panel implements PanelContentView {

	private static final long serialVersionUID = 1L;

	/**
	 * Konstruktor.
	 * 
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll
	 * @param useHorizontalLayout
	 *            when <code>true</code>, components are layed out horizontally.
	 *            (@since 1.45).
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired height of component (@since 1.45). Defaults to
	 *            undefined when not specified.
	 */
	public DefaultPanelContentView(boolean withMargin,
			boolean useHorizontalLayout, String width, String height) {
		// will use VerticalLayout by default.
		super(useHorizontalLayout ? new HorizontalLayout() : null);
		AbstractOrderedLayout layout = getLayoutInternal();
		layout.setSpacing(true);
		layout.setMargin(withMargin);
		layout.setWidth(width != null ? width : "100%");
		if (height != null) {
			layout.setHeight(height);
		}
		this.setStyleName(LiferayTheme.PANEL_LIGHT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.45
	 */
	@Override
	public void setMargin(boolean enabled) {
		getLayoutInternal().setMargin(enabled);
	}

	/**
	 * @return MarginInfo containing the currently enabled margins.
	 * @since 1.45.
	 */
	public MarginInfo getMargin() {
		return getLayoutInternal().getMargin();
	}

	/**
	 * Gives convenient access to the layout of this panel -- either
	 * <code>{@link VerticalLayout}</code> (default) oder
	 * <code>{@link HorizontalLayout}</code>.
	 * 
	 * @return the layout this panel uses -- either
	 *         <code>{@link VerticalLayout}</code> (default) oder
	 *         <code>{@link HorizontalLayout}</code>.
	 * @since 1.45
	 */
	protected AbstractOrderedLayout getLayoutInternal() {
		return (AbstractOrderedLayout) getContent();
	}

}
