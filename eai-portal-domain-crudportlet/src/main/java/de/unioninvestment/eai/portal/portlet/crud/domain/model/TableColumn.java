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
import java.util.List;

import org.springframework.util.Assert;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table.ColumnStyleRenderer;
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidator;

/**
 * Modell Klasse für die Tabellenspaltenkonfiguration zur Übergabe an die
 * Presenter.
 * 
 * @author markus.bonsch
 */
public class TableColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String title;
	private String longTitle;
	private Hidden hiddenStatus;
	private boolean editableDefault;
	private boolean multiline;
	private Integer rows;
	private boolean primaryKey;
	private Integer width;
	private String inputPrompt;
	private String displayFormat;
	private OptionList optionList;
	private FileMetadata fileMetadata;
	private CheckBoxTableColumn checkBox;

	private List<FieldValidator> validators;

	private ColumnStyleRenderer columnStyleRenderer;

	private FieldEditableChecker editableChecker;

	private CustomColumnGenerator customColumnGenerator;

	private GeneratedValueGenerator generatedValueGenerator;
	private Class<?> generatedType;

	/**
	 * Sichtbarkeitswerte der Spalten.
	 * 
	 */
	public static enum Hidden {
		TRUE, FALSE, IN_TABLE, IN_FORM;
	}

	/**
	 * @param name
	 *            der Name
	 * @param title
	 *            der im Header anzuzeigende Titel
	 * @param longtitle
	 *            der im Header bzw. Detailformular als Tooltip anzuzeigende
	 *            Text
	 * @param hiddenStatus
	 *            Sichtbarkeit der Spalte
	 * @param editableDefault
	 *            Default-Wert der von {@link #isEditable(ContainerRow)}
	 *            zurückgegeben wird falls kein {@link RowEditableChecker}
	 *            gesetzt ist.
	 * @param primaryKey
	 *            <code>true</code>, falls die Spalte zum Primärschlüssel gehört
	 * @param width
	 *            optionale Breite der Spalte in Pixeln
	 * @param multiline
	 *            <code>true</code>, falls die Tabellenzelle mehrzeilige Anzeige
	 *            und Eingabe unterstützen soll
	 * @param rows
	 *            Anzahl Zeilen
	 * @param inputPrompt
	 *            optionaler Anzeigetext bei leerem Eingabefeld
	 * @param validators
	 *            Liste von Validatoren
	 * @param optionList
	 *            Selection
	 * @param checkbox
	 *            Checkboxmodel
	 * 
	 */
	public TableColumn(String name, String title, String longtitle,
			Hidden hiddenStatus, boolean editableDefault, boolean primaryKey,
			boolean multiline, Integer rows, Integer width, String inputPrompt,
			List<FieldValidator> validators, OptionList optionList,
			CheckBoxTableColumn checkbox, String displayFormat,
			FileMetadata fileMetadata, Class<?> generatedType) {
		this.name = name;
		Assert.notNull(name, "TableColumn.name is mandatory");
		this.longTitle = longtitle;
		this.title = title;
		this.hiddenStatus = hiddenStatus;
		this.editableDefault = editableDefault;
		this.primaryKey = primaryKey;
		this.multiline = multiline;
		this.rows = rows;
		this.inputPrompt = inputPrompt;
		this.validators = validators;
		this.optionList = optionList;
		this.width = width;
		this.checkBox = checkbox;
		this.displayFormat = displayFormat;
		this.fileMetadata = fileMetadata;
		this.generatedType = generatedType;
	}

	/**
	 * @param builder
	 *            Builder-Klasse
	 */
	private TableColumn(Builder builder) {
		this.name = builder.name;
		Assert.notNull(name, "TableColumn.name is mandatory");
		this.title = builder.title;
		this.longTitle = builder.longTitle;
		this.hiddenStatus = builder.hiddenStatus;
		this.editableDefault = builder.editableDefault;
		this.multiline = builder.multiline;
		this.rows = builder.rows;
		this.primaryKey = builder.primaryKey;
		this.width = builder.width;
		this.inputPrompt = builder.inputPrompt;
		this.displayFormat = builder.displayFormat;
		this.optionList = builder.optionList;
		this.fileMetadata = builder.fileMetadata;
		this.checkBox = builder.checkBox;
		this.validators = builder.validators;
		this.columnStyleRenderer = builder.columnStyleRenderer;
		this.editableChecker = builder.editableChecker;
		this.customColumnGenerator = builder.customColumnGenerator;
		this.generatedValueGenerator = builder.generatedValueGenerator;
		this.generatedType = builder.generatedType;
	}

	/**
	 * @return den Namen der Tabellenspalte
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return der Titel, der in der Headerzeile und am Feld
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return
	 */
	public String getLongTitle() {
		return longTitle;
	}

	/**
	 * @return Angabe, in welchen Anzeigemodus die Spalte versteckt werden soll.
	 */
	public Hidden getHidden() {
		return hiddenStatus;
	}

	/**
	 * Prüft, ob dieses Feld in der übergebenen Zeile editiert werden darf. Dies
	 * kann aufgrund eines statischen oder dynamischen Schreibschutzes verboten
	 * sein.
	 * 
	 * @param row
	 *            die Zeile, die editiert wird
	 * @return ob das Feld in der Zeile {@code row} editiert werden darf
	 */
	public boolean isEditable(ContainerRow row) {
		return editableChecker != null ? editableChecker.isEditable(row)
				: editableDefault;
	}

	/**
	 * @return <code>true</code>, wenn die Spalte eine Primärschlüsselspalte ist
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return die Breite der Tabellenspalte in Pixeln oder <code>null</code>,
	 *         wenn nicht angegeben
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * @return Angabe ob die Feldwerte mehrzeilig anzuzeigen sind
	 */
	public boolean isMultiline() {
		return multiline;
	}

	/**
	 * @return Eingabehilfstext im Eingabefeld
	 */
	public String getInputPrompt() {
		return inputPrompt;
	}

	/**
	 * @return Validatoren für Felder dieser Spalte
	 */
	public List<FieldValidator> getValidators() {
		return validators;
	}

	/**
	 * @return <code>true</code>, wenn die Spalte als Checkbox dargestellt
	 *         werden soll
	 */
	public boolean isCheckable() {
		return checkBox != null;
	}

	/**
	 * @return <code>true</code>, wenn die Eingabe als Dropdown-Select-Box
	 *         erfolgen soll
	 */
	public boolean isSelectable() {
		return optionList != null;
	}

	/**
	 * @return Liste der Einträge in der Auswahlbox bei Dropdown-Eingabe
	 */
	public OptionList getOptionList() {
		return optionList;
	}

	/**
	 * @return die Checkboxdaten
	 */
	public CheckBoxTableColumn getCheckBox() {
		return checkBox;
	}

	/**
	 * @param optionList
	 *            die Optionsliste
	 */
	public void setOptionList(OptionList optionList) {
		this.optionList = optionList;
	}

	/**
	 * @return die Klasse, die den Column-Style liefert oder <code>null</code>
	 */
	public ColumnStyleRenderer getColumnStyleRenderer() {
		return columnStyleRenderer;
	}

	/**
	 * @return die Klasse, die den Column-Style liefert oder <code>null</code>
	 */
	public void setColumnStyleRenderer(ColumnStyleRenderer columnStyleRenderer) {
		this.columnStyleRenderer = columnStyleRenderer;
	}

	/**
	 * @return die Anzahl Zeilen, die bei mehrzeiligen Feldern dargestellt
	 *         werden soll
	 */
	public Integer getRows() {
		return rows;
	}

	/**
	 * @param checker
	 *            Klasse, die dynamisch auf Änderbarkeit einer Zelle prüft.
	 */
	public void setEditableChecker(FieldEditableChecker checker) {
		this.editableChecker = checker;
	}

	/**
	 * @param customColumnGenerator
	 *            Klasse, die für das Rendern einer Tabellenzelle zu verwenden
	 *            ist
	 */
	public void setCustomColumnGenerator(
			CustomColumnGenerator customColumnGenerator) {
		this.customColumnGenerator = customColumnGenerator;
	}

	/**
	 * @return Klasse, die für das Rendern einer Tabellenzelle zu verwenden ist
	 */
	public CustomColumnGenerator getCustomColumnGenerator() {
		return customColumnGenerator;
	}

	/**
	 * @return <code>true</code>, falls die Spalte generierte Inhalte liefert
	 */
	public boolean isGenerated() {
		return customColumnGenerator != null;
	}

	/**
	 * @return <code>true</code>, falls die Spalte editierbar sein soll. Diese
	 *         Default kann über einen hinterlegten {@link FieldEditableChecker}
	 *         angepasst werden.
	 */
	public boolean getDefaultEditable() {
		return editableDefault;
	}

	/**
	 * @return <code>true</code>, falls binärer Content in der Spalte
	 *         gespeichert ist
	 */
	public boolean isBinary() {
		return fileMetadata != null;
	}

	/**
	 * @return Zusatzangaben zu binären Daten
	 */
	public FileMetadata getFileMetadata() {
		return fileMetadata;
	}

	public Class<?> getGeneratedType() {
		return generatedType;
	}

	/**
	 * @return Anzeigeformat, abhängig vom Datentyp
	 */
	public String getDisplayFormat() {
		return displayFormat;
	}

	/**
	 * Builder-Klasse für {@link TableColumn}.
	 * 
	 * @author carsten.mjartan
	 */
	@SuppressWarnings("all")
	public static class Builder {
		private String name;
		private String title;
		private String longTitle;
		private Hidden hiddenStatus = Hidden.FALSE;
		private boolean editableDefault = false;
		private boolean multiline = false;
		private Integer rows;
		private boolean primaryKey = false;
		private Integer width;
		private String inputPrompt;
		private String displayFormat;
		private OptionList optionList;
		private FileMetadata fileMetadata;
		private CheckBoxTableColumn checkBox;
		private List<FieldValidator> validators;
		private ColumnStyleRenderer columnStyleRenderer;
		private FieldEditableChecker editableChecker;
		private CustomColumnGenerator customColumnGenerator;
		private GeneratedValueGenerator generatedValueGenerator;
		private Class<?> generatedType;

		/**
		 * @param name
		 * @return den builder
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * @param title
		 * @return den builder
		 */
		public Builder title(String title) {
			this.title = title;
			return this;
		}

		/**
		 * @param longTitle
		 * @return den builder
		 */
		public Builder longTitle(String longTitle) {
			this.longTitle = longTitle;
			return this;
		}

		/**
		 * @param hiddenStatus
		 * @return den builder
		 */
		public Builder hiddenStatus(Hidden hiddenStatus) {
			this.hiddenStatus = hiddenStatus;
			return this;
		}

		/**
		 * @param editableDefault
		 * @return den builder
		 */
		public Builder editableDefault(boolean editableDefault) {
			this.editableDefault = editableDefault;
			return this;
		}

		/**
		 * @param multiline
		 * @return den builder
		 */
		public Builder multiline(boolean multiline) {
			this.multiline = multiline;
			return this;
		}

		/**
		 * @param rows
		 * @return den builder
		 */
		public Builder rows(Integer rows) {
			this.rows = rows;
			return this;
		}

		/**
		 * @param primaryKey
		 * @return den builder
		 */
		public Builder primaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
			return this;
		}

		/**
		 * @param width
		 * @return den builder
		 */
		public Builder width(Integer width) {
			this.width = width;
			return this;
		}

		/**
		 * @param inputPrompt
		 * @return den builder
		 */
		public Builder inputPrompt(String inputPrompt) {
			this.inputPrompt = inputPrompt;
			return this;
		}

		/**
		 * @param displayFormat
		 * @return den builder
		 */
		public Builder displayFormat(String displayFormat) {
			this.displayFormat = displayFormat;
			return this;
		}

		/**
		 * @param optionList
		 * @return den builder
		 */
		public Builder optionList(OptionList optionList) {
			this.optionList = optionList;
			return this;
		}

		/**
		 * @param fileMetadata
		 * @return den builder
		 */
		public Builder fileMetadata(FileMetadata fileMetadata) {
			this.fileMetadata = fileMetadata;
			return this;
		}

		/**
		 * @param checkBox
		 * @return den builder
		 */
		public Builder checkBox(CheckBoxTableColumn checkBox) {
			this.checkBox = checkBox;
			return this;
		}

		/**
		 * @param validators
		 * @return den builder
		 */
		public Builder validators(List<FieldValidator> validators) {
			this.validators = validators;
			return this;
		}

		/**
		 * @param columnStyleRenderer
		 * @return den builder
		 */
		public Builder columnStyleRenderer(
				ColumnStyleRenderer columnStyleRenderer) {
			this.columnStyleRenderer = columnStyleRenderer;
			return this;
		}

		/**
		 * @param editableChecker
		 * @return den builder
		 */
		public Builder editableChecker(FieldEditableChecker editableChecker) {
			this.editableChecker = editableChecker;
			return this;
		}

		/**
		 * @param customColumnGenerator
		 * @return den builder
		 */
		public Builder customColumnGenerator(
				CustomColumnGenerator scriptColumnGenerator) {
			this.customColumnGenerator = scriptColumnGenerator;
			return this;
		}

		public Builder generatedValueGenerator(GeneratedValueGenerator generator) {
			this.generatedValueGenerator = generator;
			return this;
		}

		public Builder generatedType(Class<?> type) {
			this.generatedType = type;
			return this;
		}

		/**
		 * @return die erstellte Instanz
		 */
		public TableColumn build() {
			return new TableColumn(this);
		}
	}

	/**
	 * @return Generator für generierte Feldwerte - benötigt für Export
	 *         generierter Spalten
	 */
	public GeneratedValueGenerator getGeneratedValueGenerator() {
		return generatedValueGenerator;
	}

	/**
	 * Der Generator wird nachträglich aus dem Scripting-Layer gesetzt.
	 * 
	 * @param generatedValueGenerator
	 */
	public void setGeneratedValueGenerator(
			GeneratedValueGenerator generatedValueGenerator) {
		this.generatedValueGenerator = generatedValueGenerator;
	}

}
