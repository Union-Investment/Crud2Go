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

import java.util.Collection;

import org.apache.lucene.search.Query;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.QuerySearchHandler;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchOptionsHandler;

@SuppressWarnings("serial")
public class DefaultCompoundSearchView extends VerticalLayout implements
		CompoundSearchView, QuerySearchHandler {

	private Presenter presenter;
	private HorizontalLayout searchBar;
	private SearchBox searchBox;
	private Button detailSwitch;

	private boolean collapsed = true;

	@Override
	public void setPresenter(CompoundSearchView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void initialize(Collection<String> searchableFields,
			Collection<String> defaultSearchFields) {
		String[] defaultsArray = defaultSearchFields
				.toArray(new String[defaultSearchFields.size()]);
		searchBox = new SearchBox(defaultsArray, this);
		searchBox.setWidth("100%");
		searchBox.setOptionHandler(new SearchOptionsHandler(searchableFields));
		searchBox.focus();

		searchBar = new HorizontalLayout(searchBox);
		searchBar.setWidth("100%");
		searchBar.addStyleName("compsearchbar");
		searchBar.setExpandRatio(searchBox, 1);

		addComponent(searchBar);
	}

	@Override
	public void updateQueryString(String queryString) {
		searchBox.setValue(queryString);
	}

	@Override
	public void search(Query query) {
		presenter.search(query);
	}

	@Override
	public void addComponent(Component component) {
		if (component != searchBar) {
			component.setVisible(false);
			enableCollapsing();
		}
		super.addComponent(component);
	}

	private void enableCollapsing() {
		if (detailSwitch == null) {
			detailSwitch = new Button();
			detailSwitch.setStyleName(BaseTheme.BUTTON_LINK);
			detailSwitch.addStyleName("detailswitch");
			detailSwitch.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					switchDetails();
				}
			});
			searchBar.addComponent(detailSwitch, 0);
		}
	}

	private void switchDetails() {
		collapsed = !collapsed;
		updateDetailSwitch();
		updateDetailVisibility();
	}

	private void updateDetailVisibility() {
		for (Component child : this) {
			if (child != searchBar) {
				child.setVisible(!collapsed);
			}
		}
	}

	private void updateDetailSwitch() {
		if (collapsed) {
			detailSwitch.removeStyleName("open");
		} else {
			detailSwitch.addStyleName("open");
		}
	}

	@Override
	public Button addBackButton(String caption, ClickListener clickListener) {
		throw new UnsupportedOperationException(
				"Regions cannot have a back button");
	}
}
