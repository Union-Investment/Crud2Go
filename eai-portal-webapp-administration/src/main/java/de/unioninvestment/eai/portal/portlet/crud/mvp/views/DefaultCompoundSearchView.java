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

import java.util.Date;
import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
import com.vaadin.ui.themes.LiferayTheme;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionTableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn.Searchable;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
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
	private TableColumns fields;

	@Override
	public void setPresenter(CompoundSearchView.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void initialize(boolean useHorizontalLayout) {
		Preconditions.checkState(!useHorizontalLayout,
				"Only vertical layout supported");
	}

	@Override
	public void setHeightToFitScreen(Integer minimumHeight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize(TableColumns fields) {
		this.fields = fields;
		searchBox = new SearchBox(this);
		searchBox.setWidth("100%");
		searchBox.setOptionHandler(new SearchOptionsHandler(fields));
		searchBox.focus();
		searchBox
				.setInputPrompt(getMessage("portlet.crud.compoundsearch.inputPrompt"));

		Button resetButton = new Button(
				getMessage("portlet.crud.compoundsearch.reset"));
		resetButton.setStyleName(LiferayTheme.BUTTON_LINK);
		resetButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				presenter.reset();
			}
		});

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

		searchBar = new HorizontalLayout(searchBox, resetButton, infoButton);
		searchBar.setWidth("100%");
		searchBar.addStyleName("compsearchbar");
		searchBar.setExpandRatio(searchBox, 1);
		searchBar.setSpacing(true);
		searchBar.setComponentAlignment(resetButton, Alignment.MIDDLE_CENTER);
		searchBar.setComponentAlignment(infoButton, Alignment.MIDDLE_CENTER);

		addComponent(searchBar);
	}

	protected Component createSearchInfo() {
		Table table = createColumnInfoTable();

		Label syntaxReference = new Label(
				getMessage("portlet.crud.compoundsearch.syntaxReference"),
				ContentMode.HTML);

		VerticalLayout boxLayout = new VerticalLayout(table, syntaxReference);
		boxLayout.addStyleName("compsearchinfo");
		boxLayout.setWidth("350px");
		boxLayout.setSpacing(true);

		return boxLayout;
	}

	private Table createColumnInfoTable() {
		Table table = new Table();
		table.setWidth("100%");
		table.setSortEnabled(false);
		table.addContainerProperty("field", String.class, null);
		table.addContainerProperty("title", String.class, null);
		table.addContainerProperty("description", String.class, null);
		table.addContainerProperty("default", Boolean.class, null);
		table.setColumnHeader("field",
				getMessage("portlet.crud.compoundsearch.field"));
		table.setColumnHeader("title",
				getMessage("portlet.crud.compoundsearch.title"));
		table.setColumnHeader("description",
				getMessage("portlet.crud.compoundsearch.description"));

		table.setPageLength(fields.size() < 20 ? fields.size() : PAGE_SIZE);
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

		for (TableColumn column : fields) {
			table.addItem(new Object[] { //
					column.getName(), //
							column.getTitle(), //
							createColumnDescription(column), //
							column.getSearchable() == Searchable.DEFAULT }, //
					column.getName());
		}

		table.setVisibleColumns("field", "title", "description");
		return table;
	}

	private String createColumnDescription(TableColumn column) {
		LinkedList<String> results = Lists.newLinkedList();
		if (column instanceof SelectionTableColumn) {
			results.add(getMessage("portlet.crud.compoundsearch.selection"));
		} else if (column instanceof CheckBoxTableColumn) {
			CheckBoxTableColumn checkbox = (CheckBoxTableColumn) column;
			results.add(checkbox.getCheckedValue() + "/"
					+ checkbox.getUncheckedValue());
		} else if (column instanceof DateTableColumn) {
			results.add(getMessage("portlet.crud.compoundsearch.date"));
		} else if (Number.class.isAssignableFrom(column.getType())) {
			results.add(getMessage("portlet.crud.compoundsearch.numeric"));
		} else if (Date.class.isAssignableFrom(column.getType())) {
			results.add(getMessage("portlet.crud.compoundsearch.date"));
		}
		if (column.getSearchable() == Searchable.DEFAULT) {
			results.add(getMessage("portlet.crud.compoundsearch.default"));
		}
		return Joiner.on(", ").join(results);
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
