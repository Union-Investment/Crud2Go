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

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.QuerySearchHandler;
import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchOptionsHandler;

/**
 * Darstellung der Compound-Suche. Falls eine Detailsuche konfiguriert ist, kann
 * diese auf- und zugeklappt werden.
 * 
 * @author cmj
 */
@SuppressWarnings("serial")
public class DefaultCompoundSearchView extends VerticalLayout implements
		CompoundSearchView, QuerySearchHandler {

	private static final int PAGE_SIZE = 20;
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
	public void initialize(Collection<String> searchableFields) {
		searchBox = new SearchBox(this);
		searchBox.setWidth("100%");
		searchBox.setOptionHandler(new SearchOptionsHandler(searchableFields));
		searchBox.focus();
		searchBox.setInputPrompt(getMessage("portlet.crud.compoundsearch.inputPrompt"));

		PopupView infoButton = new PopupView(new Content() {
			@Override
			public String getMinimizedValueAsHTML() {
				return "?";
			}

			@Override
			public Component getPopupComponent() {
				return createSearchInfo();
			}
		});
		infoButton.setHideOnMouseOut(false);

		searchBar = new HorizontalLayout(searchBox, infoButton);
		searchBar.setWidth("100%");
		searchBar.addStyleName("compsearchbar");
		searchBar.setExpandRatio(searchBox, 1);
		searchBar.setSpacing(true);
		searchBar.setComponentAlignment(infoButton, Alignment.MIDDLE_CENTER);

		addComponent(searchBar);
	}

	protected Component createSearchInfo() {
		ArrayList<TableColumn> columns = getOrderedColumnInfo();
		Table table = createColumnInfoTable(columns);

		Label syntaxReference = new Label(
				getMessage("portlet.crud.compoundsearch.syntaxReference"),
				ContentMode.HTML);

		VerticalLayout boxLayout = new VerticalLayout(table, syntaxReference);
		boxLayout.addStyleName("compsearchinfo");
		boxLayout.setWidth("300px");
		boxLayout.setSpacing(true);

		return boxLayout;
	}

	private Table createColumnInfoTable(ArrayList<TableColumn> columns) {
		Table table = new Table();
		table.setWidth("100%");
		table.setSortEnabled(false);
		table.addContainerProperty("field", String.class, null);
		table.addContainerProperty("title", String.class, null);
		table.addContainerProperty("default", Boolean.class, null);
		table.setColumnHeader("field",
				getMessage("portlet.crud.compoundsearch.field"));
		table.setColumnHeader("title",
				getMessage("portlet.crud.compoundsearch.title"));

		table.setPageLength(columns.size() < 20 ? columns.size() : PAGE_SIZE);
		table.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId,
					Object propertyId) {
				if (propertyId == null) {
					if (source.getItem(itemId).getItemProperty("default")
							.getValue().equals(Boolean.TRUE)) {
						return "defaultfield";
					}
				}
				return null;
			}
		});

		for (TableColumn column : columns) {
			table.addItem(new Object[] { //
					column.getName(), //
							column.getTitle(), //
							column.getSearchable() == Searchable.DEFAULT }, //
					column.getName());
		}

		table.setVisibleColumns("field", "title");
		return table;
	}

	private ArrayList<TableColumn> getOrderedColumnInfo() {
		Collection<TableColumn> searchableColumns = presenter
				.getSearchableColumns();
		ArrayList<TableColumn> list = new ArrayList<TableColumn>(
				searchableColumns);
		sort(list, new Comparator<TableColumn>() {
			@Override
			public int compare(TableColumn o1, TableColumn o2) {
				if (o1.getSearchable() != o2.getSearchable()) {
					return o1.getSearchable() == Searchable.DEFAULT ? -1 : 1;
				} else {
					return o1.getName().compareTo(o2.getName());
				}
			}
		});
		return list;
	}

	@Override
	public void updateQueryString(String queryString) {
		searchBox.setValue(queryString);
	}

	@Override
	public boolean isValidQuery(String queryString) {
		return presenter.isValidQuery(queryString);
	}

	@Override
	public void search(String queryString) {
		presenter.search(queryString);
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
			searchBar.setComponentAlignment(detailSwitch,
					Alignment.MIDDLE_RIGHT);
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
