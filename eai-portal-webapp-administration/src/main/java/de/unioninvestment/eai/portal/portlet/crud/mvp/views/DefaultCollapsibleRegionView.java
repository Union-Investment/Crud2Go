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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationEvent;
import org.vaadin.jouni.animator.AnimatorProxy.AnimationListener;
import org.vaadin.jouni.animator.Disclosure;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

/**
 * Default-Implementierung für die View eines Bereichs.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DefaultCollapsibleRegionView extends Disclosure implements
		CollapsibleRegionView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultCollapsibleRegionView.class);

	private static final long serialVersionUID = 1L;

	private final ComponentContainer content = this.createContent();

	private Presenter presenter;

	/**
	 * Erzeugt eine Instanz eines auf- und zuklappbaren Bereiches.
	 * 
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired height of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 */
	public DefaultCollapsibleRegionView(String width, String height) {
		super("");
		setWidth(width != null ? width : "100%");
		setHeight(height != null ? height : "100%");
		this.setContent(this.content);
		this.ap.addListener(new AnimationListener() {
			@Override
			public void onAnimation(AnimationEvent event) {
				if (presenter != null) {
					presenter.regionHasBeenCollapsed(!isOpen());
				}
			}
		});
	}

	@Override
	public void initialize(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setTitle(String title) {
		this.setDisclosureCaption(title);
	}

	@Override
	public boolean isCollapsed() {
		return !this.isOpen();
	}

	@Override
	public void setCollapsed(boolean collapsed) {
		// Do only something if the state changes
		if (this.isOpen() != !collapsed) {
			if (collapsed) {
				this.close();
			} else {
				this.open();
			}
			if (this.presenter != null) {
				this.presenter.regionHasBeenCollapsed(collapsed);
			}
		}
	}

	@Override
	public void addComponent(Component c) {
		this.content.addComponent(c);
	}

	@Override
	public void removeAllComponents() {
		this.content.removeAllComponents();
	}

	String getTitle() {
		return this.getDisclosureCaption();
	}

	private ComponentContainer createContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setSpacing(true);

		return layout;
	}

	@Override
	public void setMargin(boolean enabled) {
		// FIXME
		LOGGER.warn("Fixme Margin on CollapsibleRegion");
	}

	@Override
	public Button addBackButton(String caption, ClickListener clickListener) {
		throw new UnsupportedOperationException(
				"Regions cannot have a back button");
	}

}
