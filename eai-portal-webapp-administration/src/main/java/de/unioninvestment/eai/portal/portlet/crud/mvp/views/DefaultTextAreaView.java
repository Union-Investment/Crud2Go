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

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DefaultTextAreaView extends CustomComponent implements
		TextAreaView {

	private Presenter presenter;

	/**
	 * Initialisierung.
	 * 
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired width of component (@since 1.45). Defaults to
	 *            undefined when not specified.
	 */
	public DefaultTextAreaView(String width, String height) {
		addStyleName("c-textarea");
		setWidth(width != null ? width : "100%");
		if (height != null) {
			setHeight(height);
		}
		setCompositionRoot(new Label("Initializing..."));
	}

	@Override
	public void setPresenter(TextAreaView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void showContent(String xhtml) {
		Label label = new Label(xhtml, ContentMode.HTML);
		VerticalLayout clickLayout = new VerticalLayout(label);
		clickLayout.addLayoutClickListener(new LayoutClickListener() {
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (event.isDoubleClick()) {
					presenter.contentAreaClicked();
				}
			}
		});
		setCompositionRoot(clickLayout);
	}

	@Override
	public void showEditor(String xhtml) {
		
		final RichTextArea richTextArea = new RichTextArea();
		richTextArea.setNullRepresentation("");
		richTextArea.setWidth("100%");
		richTextArea.setValue(xhtml);
		
		Button saveButton = new Button("Speichern", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String changedContent = (String) richTextArea.getValue();
				presenter.contentChanged(changedContent);
			}
		});
		
		Button cancelButton = new Button("Abbrechen", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.cancelEditing();
			}
		});
		
		CssLayout buttons = new CssLayout(saveButton, cancelButton);
		VerticalLayout layout = new VerticalLayout(richTextArea, buttons);
		if (getHeight() >= 0f) {
			richTextArea.setHeight("100%");
			layout.setExpandRatio(richTextArea, 1f);
		}
		setCompositionRoot(layout);
	}
	
	@Override
	public void hide() {
		getCompositionRoot().setVisible(false);
	}
}
