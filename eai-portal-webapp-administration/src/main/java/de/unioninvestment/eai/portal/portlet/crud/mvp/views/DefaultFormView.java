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

import java.util.Locale;
import java.util.Set;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
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
import de.unioninvestment.eai.portal.portlet.crud.domain.util.DateUtils;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.OptionListContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.domain.FormSelectionContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;
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
public class DefaultFormView extends Form implements FormView {

	@SuppressWarnings("unused")
	private static class IgnoreErrorHandler implements ComponentErrorHandler {

		private static final long serialVersionUID = 42L;

		@Override
		public boolean handleComponentError(ComponentErrorEvent event) {
			// do nothing
			return true;
		}

	}

	private static final long serialVersionUID = 1L;
	private Presenter presenter;
	private FormAction searchAction;
	private GridLayout grid;

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
		setValidationVisibleOnCommit(true);

		int columns = model.getColumns();
		FormFields fields = model.getFields();
		FormActions actions = model.getActions();

		if (columns > 1) {
			layoutAsGrid(columns, fields.count());
		}
		populateFields(fields, columns);
		populateActions(actions);
	}

	private void layoutAsGrid(int columns, int fieldCount) {
		int rows = calculateRowCount(columns, fieldCount);

		grid = new GridLayout(columns, rows);
		grid.setMargin(true, false, true, false);
		grid.setSpacing(true);
		grid.setWidth(100, UNITS_PERCENTAGE);

		setLayout(grid);
	}

	private void populateFields(FormFields fields, int columns) {
		for (FormField field : fields) {

			Field vaadinField;
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

	private Field createDateFormField(FormField field) {
		DateFormField dff = (DateFormField) field;

		PopupDateField datetime = new PopupDateField(field.getTitle());
		datetime.setInputPrompt(field.getInputPrompt());
		datetime.setDateFormat(dff.getDateFormat());
		datetime.setLocale(Locale.GERMAN);

		datetime.setValue(dff.getDefaultValue());

		datetime.setResolution(DateUtils.getVaadinResolution(dff
				.getResolution()));

		datetime.setPropertyDataSource(dff.getTimestampProperty());
		datetime.setImmediate(true);
		datetime.addStyleName(field.getName());

		return datetime;
	}

	private Field createTextField(FormField field) {
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

	private void addFieldToLayout(FormField field, Field vaadinField) {
		addField(field.getName(), vaadinField);
		if (grid != null) {
			vaadinField.setWidth("100%");
			if (field.getTitle().length() > 15) {
				vaadinField.setDescription(field.getTitle());
			}
			grid.setComponentAlignment(vaadinField, Alignment.BOTTOM_LEFT);
		}
	}

	private void applyValidators(FormField field, Field textField) {
		if (field.getValidators() != null) {
			for (FieldValidator validator : field.getValidators()) {
				validator.apply(textField);
			}
		}
	}

	private Field createSelect(OptionListFormField field) {
		AbstractSelect select;
		if (field.getVisibleRows() <= 1) {
			select = new Select(field.getTitle());
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

	private Field createMultiSelect(MultiOptionListFormField field) {
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
		select.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
		select.setItemCaptionPropertyId("title");
		reapplyCurrentValue(select, currentValue);
	}

	private void reapplyCurrentValue(AbstractSelect select, Object currentValue) {
		if (currentValue != null) {
			if (select.isMultiSelect()) {
				for (Object element : (Set) currentValue) {
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

	private void addOptionListChangeListener(OptionListFormField field,
			final AbstractSelect select, final FormSelectionContext ctx) {
		field.getOptionList().addChangeListener(
				new OptionListChangeEventHandler() {

					private static final long serialVersionUID = 42L;

					@Override
					public void onOptionListChange(OptionListChangeEvent event) {
						fillOptions(event.getSource(), select, ctx);
					}
				});
	}

	private Field createCheckBox(FormField field, int columns) {
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

		checkBoxField.setPropertyDataSource(checkBoxFormField
				.getCheckboxProperty());
		checkBoxField.setImmediate(true);
		checkBoxField.addStyleName(field.getName());

		// addField(field.getName(), checkBoxField);
		getLayout().addComponent(checkboxLayout);

		return checkBoxField;
	}

	private void applyInputPrompt(FormField field, TextField textField) {
		String inputPrompt = field.getInputPrompt();
		if (inputPrompt != null) {
			textField.setInputPrompt(inputPrompt);
		}
	}

	private void populateActions(FormActions actions) {
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);

		boolean allHidden = true;
		boolean first = true;
		for (final FormAction action : actions) {
			Button button;
			if (action.getActionHandler() instanceof SearchFormAction) {
				searchAction = action;
				button = new Button(action.getTitle(), this, "commit");
				if (first) {
					button.setClickShortcut(KeyCode.ENTER);
					first = false;
				}
				// button.setErrorHandler(new IgnoreErrorHandler());
				button.addListener(new ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						presenter.executeAction(action);
					}
				});
			} else {
				button = new Button(action.getTitle());
				button.addListener(new ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						DefaultFormView.this.setValidationVisible(false);
						presenter.executeAction(action);
					}
				});
			}

			if (action.isHidden()) {
				button.setVisible(false);
			} else {
				allHidden = false;
			}
			buttons.addComponent(button);
		}
		if (!allHidden) {
			getFooter().addComponent(buttons);
			getFooter().setMargin(false, true, true, false);
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

}
