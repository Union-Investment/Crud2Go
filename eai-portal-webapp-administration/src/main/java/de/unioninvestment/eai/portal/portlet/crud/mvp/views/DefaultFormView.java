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

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.LiferayTheme;

import de.unioninvestment.eai.portal.portlet.crud.domain.form.SearchFormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CheckBoxFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DateFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormActions;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FormFields;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.MultiOptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionListFormField;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.OptionListContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.FormSelectionContext;
import de.unioninvestment.eai.portal.support.vaadin.date.DateUtils;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;
import de.unioninvestment.eai.portal.support.vaadin.support.ConvertablePropertyWrapper;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

/**
 * {@link View} für Formularansicht. Bei einspaltiger Ansicht werden die Felder
 * untereinander, und links davon als Label jeweils die Titel angezeigt. Bei
 * mehrspaltiger Ansicht werden die Label über den Eingabefeldern angezeigt.
 * 
 * Unter den Eingabefeldern werden Aktions-Buttons horizontal angeordnet
 * dargestellt.
 * 
 * @author carsten.mjartan
 */
public class DefaultFormView extends Panel implements FormView, Handler {

	private static final long serialVersionUID = 1L;

	private static final Action ACTION_ENTER = new ShortcutAction("Enter",
			KeyCode.ENTER, null);
	private static final Action[] FORM_ACTIONS = { ACTION_ENTER };

	private Presenter presenter;
	private FormAction searchAction;

	private VerticalLayout rootLayout;
	private Layout fieldLayout;

	@Override
	public void initialize(Presenter presenter,
			de.unioninvestment.eai.portal.portlet.crud.domain.model.Form model) {
		// @since 1.45
		if (model.getWidth() != null) {
			setWidth(model.getWidth());
		}
		// @since 1.45
		if (model.getHeight() != null) {
			setHeight(model.getHeight());
		}

		this.presenter = presenter;

		int columns = model.getColumns();
		FormFields fields = model.getFields();
		FormActions actions = model.getActions();

		rootLayout = new VerticalLayout();
		setStyleName(LiferayTheme.PANEL_LIGHT);
		addStyleName("c2g-form");
		setContent(rootLayout);
		addActionHandler(this);

		if (columns > 1) {
			fieldLayout = layoutAsGrid(columns, fields.count());
		} else {
			fieldLayout = layoutAsForm();
		}
		populateFields(fields, columns);
		rootLayout.addComponent(fieldLayout);

		createFooterAndPopulateActions(actions);
	}

	private Layout layoutAsForm() {
		return new FormLayout();
	}

	private GridLayout layoutAsGrid(int columns, int fieldCount) {
		int rows = calculateRowCount(columns, fieldCount);

		GridLayout grid = new GridLayout(columns, rows);
		grid.setMargin(new MarginInfo(true, false, true, false));
		grid.setSpacing(true);
		grid.setWidth("100%");
		return grid;
	}

	private void populateFields(FormFields fields, int columns) {
		for (FormField field : fields) {

			Field<?> vaadinField;
			if (field instanceof MultiOptionListFormField) {
				vaadinField = createMultiSelect((MultiOptionListFormField) field);
			} else if (field instanceof OptionListFormField) {
				vaadinField = createSelect((OptionListFormField) field);
			} else if (field instanceof CheckBoxFormField) {
				vaadinField = createCheckBox(field, columns);
			} else if (field instanceof DateFormField) {
				vaadinField = createDateFormField(field);
			} else {
				vaadinField = createTextField(field);
			}

			applyValidators(field, vaadinField);

			if (!(field instanceof CheckBoxFormField)) {
				addFieldToLayout(field, vaadinField);
			}
		}
	}

	private Field<Date> createDateFormField(FormField field) {
		DateFormField dff = (DateFormField) field;

		PopupDateField datetime = new PopupDateField(field.getTitle());
		datetime.setInputPrompt(field.getInputPrompt());
		datetime.setDateFormat(dff.getDateFormat());
		datetime.setLocale(Locale.GERMAN);

		datetime.setResolution(DateUtils.getVaadinResolution(dff
				.getResolution()));

		ConvertablePropertyWrapper<Date, String> wrapper = new ConvertablePropertyWrapper<Date, String>(
				dff.getProperty(), dff.getConverter(), UI.getCurrent()
						.getLocale());
		datetime.setPropertyDataSource(wrapper);
		datetime.setImmediate(true);
		datetime.addStyleName(field.getName());

		return datetime;
	}

	private Field<String> createTextField(FormField field) {
		TextField textField = new TextField(field.getTitle(),
				field.getProperty());

		// applyValidators(field, textField);
		textField.setNullSettingAllowed(true);
		textField.setNullRepresentation("");
		textField.setImmediate(true);
		textField.addStyleName(field.getName());
		applyInputPrompt(field, textField);

		return textField;
	}

	private void addFieldToLayout(FormField field, Field<?> vaadinField) {

		fieldLayout.addComponent(vaadinField);

		if (fieldLayout instanceof GridLayout) {
			vaadinField.setWidth("100%");
			if (field.getTitle().length() > 15) {
				if (vaadinField instanceof AbstractComponent) {
					((AbstractComponent) vaadinField).setDescription(field
							.getTitle());
				}
			}
			((GridLayout) fieldLayout).setComponentAlignment(vaadinField,
					Alignment.BOTTOM_LEFT);
		}
	}

	private void applyValidators(FormField field, Field<?> textField) {
		if (field.getValidators() != null) {
			for (FieldValidator validator : field.getValidators()) {
				validator.apply(textField);
			}
		}
	}

	private Field<?> createSelect(OptionListFormField field) {
		AbstractSelect select;
		if (field.getVisibleRows() <= 1) {
			select = new ComboBox(field.getTitle());
		} else {
			select = new ListSelect(field.getTitle());
			((ListSelect) select).setRows(field.getVisibleRows());
		}

		fillOptions(field.getOptionList(), select, new FormSelectionContext(
				field));
		// addOptionListChangeListener(field, select, new FormSelectionContext(
		// field));

		select.setPropertyDataSource(field.getProperty());
		select.setInvalidAllowed(false);
		select.setImmediate(true);
		select.setMultiSelect(false);
		select.addStyleName(field.getName());

		return select;
	}

	private Field<?> createMultiSelect(MultiOptionListFormField field) {
		ListSelect select = new ListSelect(field.getTitle());

		fillOptions(field.getOptionList(), select, new FormSelectionContext(
				field));
		// addOptionListChangeListener(field, select, new FormSelectionContext(
		// field));

		select.setMultiSelect(true);
		select.setPropertyDataSource(field.getListProperty());

		select.setInvalidAllowed(false);
		select.setImmediate(true);
		select.setRows(field.getVisibleRows());
		select.addStyleName(field.getName());

		return select;
	}

	private void fillOptions(OptionList optionList, AbstractSelect select,
			FormSelectionContext ctx) {
		Object currentValue = select.getValue();
		select.setContainerDataSource(new OptionListContainer(optionList, ctx));
		select.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		select.setItemCaptionPropertyId("title");
		reapplyCurrentValue(select, currentValue);
	}

	private void reapplyCurrentValue(AbstractSelect select, Object currentValue) {
		if (currentValue != null) {
			if (select.isMultiSelect()) {
				for (Object element : (Set<?>) currentValue) {
					if (select.containsId(element)) {
						select.select(element);
					}
				}
			} else {
				if (select.containsId(currentValue)) {
					select.setValue(currentValue);
				}
			}
		}
	}

	private Field<Boolean> createCheckBox(FormField field, int columns) {
		CheckBoxFormField checkBoxFormField = (CheckBoxFormField) field;

		Layout checkboxLayout = null;
		if (columns == 1) {
			checkboxLayout = new HorizontalLayout();
			checkboxLayout.setCaption(field.getTitle());
		} else {
			checkboxLayout = new VerticalLayout();
			Label checkBoxLabel = new Label(field.getTitle());
			checkboxLayout.addComponent(checkBoxLabel);
		}

		CheckBox checkBoxField = new CheckBox();
		checkboxLayout.addComponent(checkBoxField);

		checkBoxField.setConverter(checkBoxFormField.getConverter());
		checkBoxField.setPropertyDataSource(checkBoxFormField.getProperty());
		checkBoxField.setImmediate(true);
		checkBoxField.addStyleName(field.getName());

		// addField(field.getName(), checkBoxField);
		fieldLayout.addComponent(checkboxLayout);

		return checkBoxField;
	}

	private void applyInputPrompt(FormField field, TextField textField) {
		String inputPrompt = field.getInputPrompt();
		if (inputPrompt != null) {
			textField.setInputPrompt(inputPrompt);
		}
	}

	@SuppressWarnings("serial")
	private void createFooterAndPopulateActions(FormActions actions) {
		CssLayout buttons = new CssLayout();
		buttons.setStyleName("actions");

		boolean allHidden = true;
		for (final FormAction action : actions) {
			final Button button = new Button(action.getTitle());
			button.setDisableOnClick(true);
			button.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					try {
						presenter.executeAction(action);
					} finally {
						button.setEnabled(true);
					}
				}
			});

			if (action.getActionHandler() instanceof SearchFormAction) {
				if (searchAction == null) {
					searchAction = action;
					// button.setClickShortcut(KeyCode.ENTER);
				}
			}
			if (action.isHidden()) {
				button.setVisible(false);
			} else {
				allHidden = false;
			}
			buttons.addComponent(button);
		}
		if (!allHidden) {
			rootLayout.addComponent(buttons);
		}
	}

	/**
	 * Berechnet die Anzahl der Zeilen.
	 * 
	 * @param columns
	 *            Spalten
	 * @param fieldCount
	 *            Anzahl Felder
	 * @return Anzahl Zeilen
	 */
	static int calculateRowCount(int columns, int fieldCount) {
		return ((fieldCount - 1) / columns) + 1;
	}

	@Override
	public FormAction getSearchAction() {
		return searchAction;
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		if (searchAction != null && sender == this) {
			return FORM_ACTIONS;
		}
		return null;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (action == ACTION_ENTER) {
			presenter.executeAction(searchAction);
		}
	}

}
