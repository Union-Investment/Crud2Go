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

	/**
	 * Permission Actions.
	 */
	public enum Permission {
		DISPLAY, EDIT
	}

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
	protected String displayFormat;
	private String excelFormat;
	private FileMetadata fileMetadata;

	private List<FieldValidator> validators;

	private ColumnStyleRenderer columnStyleRenderer;

	private FieldEditableChecker editableChecker;

	private CustomColumnGenerator customColumnGenerator;

	private GeneratedValueGenerator generatedValueGenerator;
	private Class<?> generatedType;
	private Searchable searchable;

	/**
	 * Sichtbarkeitswerte der Spalten.
	 * 
	 */
	public enum Hidden {
		TRUE, FALSE, IN_TABLE, IN_FORM;
	}

	public enum Searchable {
		DEFAULT, TRUE, FALSE
	}
	
	/**
	 * @param builder
	 *            Builder-Klasse
	 */
	protected TableColumn(Init<?> builder) {
		this.name = builder.name;
		Assert.notNull(name, "TableColumn.name is mandatory");

		this.title = builder.title;
		this.longTitle = builder.longTitle;
		this.hiddenStatus = builder.hiddenStatus;
		this.searchable = builder.searchable;
		this.editableDefault = builder.editableDefault;
		this.multiline = builder.multiline;
		this.rows = builder.rows;
		this.primaryKey = builder.primaryKey;
		this.width = builder.width;
		this.inputPrompt = builder.inputPrompt;
		this.displayFormat = builder.displayFormat;
		this.excelFormat = builder.excelFormat;
		this.fileMetadata = builder.fileMetadata;
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
	 * 
	 *         For Pattern, see: <a href=
	 *         "https://weblogs.java.net/blog/emcmanus/archive/2010/10/25/using-builder-pattern-subclasses"
	 *         >this</a>
	 */
	public static class Builder extends Init<Builder> {
		@Override
		protected Builder self() {
			return this;
		}
	}

	@SuppressWarnings("all")
	public static abstract class Init<T extends Init<T>> {
		protected String name;
		protected String title;
		protected String longTitle;
		protected Hidden hiddenStatus = Hidden.FALSE;
		protected Searchable searchable = Searchable.TRUE;
		protected boolean editableDefault = false;
		protected boolean multiline = false;
		protected Integer rows;
		protected boolean primaryKey = false;
		protected Integer width;
		protected String inputPrompt;
		protected String displayFormat;
		protected String excelFormat;
		protected FileMetadata fileMetadata;
		protected List<FieldValidator> validators;
		protected ColumnStyleRenderer columnStyleRenderer;
		protected FieldEditableChecker editableChecker;
		protected CustomColumnGenerator customColumnGenerator;
		protected GeneratedValueGenerator generatedValueGenerator;
		protected Class<?> generatedType;

		protected abstract T self();

		/**
		 * @param name
		 * @return den builder
		 */
		public T name(String name) {
			this.name = name;
			return self();
		}

		/**
		 * @param title
		 * @return den builder
		 */
		public T title(String title) {
			this.title = title;
			return self();
		}

		/**
		 * @param longTitle
		 * @return den builder
		 */
		public T longTitle(String longTitle) {
			this.longTitle = longTitle;
			return self();
		}

		/**
		 * @param hiddenStatus
		 * @return den builder
		 */
		public T hiddenStatus(Hidden hiddenStatus) {
			this.hiddenStatus = hiddenStatus;
			return self();
		}

		/**
		 * @param searchable
		 * @return den builder
		 */
		public T searchable(Searchable searchable) {
			this.searchable = searchable;
			return self();
		}
		
		/**
		 * @param editableDefault
		 * @return den builder
		 */
		public T editableDefault(boolean editableDefault) {
			this.editableDefault = editableDefault;
			return self();
		}

		/**
		 * @param multiline
		 * @return den builder
		 */
		public T multiline(boolean multiline) {
			this.multiline = multiline;
			return self();
		}

		/**
		 * @param rows
		 * @return den builder
		 */
		public T rows(Integer rows) {
			this.rows = rows;
			return self();
		}

		/**
		 * @param primaryKey
		 * @return den builder
		 */
		public T primaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
			return self();
		}

		/**
		 * @param width
		 * @return den builder
		 */
		public T width(Integer width) {
			this.width = width;
			return self();
		}

		/**
		 * @param inputPrompt
		 * @return den builder
		 */
		public T inputPrompt(String inputPrompt) {
			this.inputPrompt = inputPrompt;
			return self();
		}

		/**
		 * @param displayFormat
		 * @return den builder
		 */
		public T displayFormat(String displayFormat) {
			this.displayFormat = displayFormat;
			return self();
		}

		/**
		 * @param excelFormat
		 * @return den builder
		 */
		public T excelFormat(String excelFormat) {
			this.excelFormat = excelFormat;
			return self();
		}

		/**
		 * @param fileMetadata
		 * @return den builder
		 */
		public T fileMetadata(FileMetadata fileMetadata) {
			this.fileMetadata = fileMetadata;
			return self();
		}

		/**
		 * @param validators
		 * @return den builder
		 */
		public T validators(List<FieldValidator> validators) {
			this.validators = validators;
			return self();
		}

		/**
		 * @param columnStyleRenderer
		 * @return den builder
		 */
		public T columnStyleRenderer(ColumnStyleRenderer columnStyleRenderer) {
			this.columnStyleRenderer = columnStyleRenderer;
			return self();
		}

		/**
		 * @param editableChecker
		 * @return den builder
		 */
		public T editableChecker(FieldEditableChecker editableChecker) {
			this.editableChecker = editableChecker;
			return self();
		}

		/**
		 * @param customColumnGenerator
		 * @return den builder
		 */
		public T customColumnGenerator(
				CustomColumnGenerator scriptColumnGenerator) {
			this.customColumnGenerator = scriptColumnGenerator;
			return self();
		}

		public T generatedValueGenerator(GeneratedValueGenerator generator) {
			this.generatedValueGenerator = generator;
			return self();
		}

		public T generatedType(Class<?> type) {
			this.generatedType = type;
			return self();
		}

		/**
		 * @return die erstellte Instanz
		 */
		public TableColumn build() {
			return new TableColumn(self());
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

	/**
	 * @return das Excel-Format, falls angegeben
	 */
	public String getExcelFormat() {
		return excelFormat;
	}

	public Searchable getSearchable() {
		return searchable;
	}

}
