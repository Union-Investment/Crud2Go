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

import de.unioninvestment.eai.portal.portlet.crud.config.SelectDisplayType;

/**
 * 
 * Checkbox Modelobjekt für die Tabelleneditierung.
 * 
 * @author markus.bonsch
 * 
 */
public class SelectionTableColumn extends TableColumn {

	private static final long serialVersionUID = 1L;

	private OptionList optionList;
	private SelectDisplayType displayType;
	private String separator;
	private boolean multiselect;

	public static class Builder extends TableColumn.Init<Builder> {
		private SelectDisplayType displayType = SelectDisplayType.COMBOBOX;
		private String separator = ";";
		private boolean multiselect = false;
		private OptionList optionList;

		@Override
		protected Builder self() {
			return this;
		}

		public Builder displayType(SelectDisplayType displayType) {
			this.displayType = displayType;
			return self();
		}

		public Builder separator(String separator) {
			this.separator = separator;
			return self();
		}

		public Builder multiselect(boolean multiselect) {
			this.multiselect = multiselect;
			return self();
		}
		
		public Builder optionList(OptionList optionList) {
			this.optionList = optionList;
			return self();
		}

		@Override
		public SelectionTableColumn build() {
			return new SelectionTableColumn(this);
		}

	}

	public SelectionTableColumn(Builder builder) {
		super(builder);
		this.separator = builder.separator;
		this.multiselect = builder.multiselect;
		this.displayType = builder.displayType;
		this.optionList = builder.optionList;
	}

	public String getSeparator() {
		return separator;
	}

	public boolean isMultiselect() {
		return multiselect;
	}

	/**
	 * @return Liste der Einträge in der Auswahlbox bei Dropdown-Eingabe
	 */
	public OptionList getOptionList() {
		return optionList;
	}

	public boolean isComboBox() {
		return displayType == SelectDisplayType.COMBOBOX;
	}
	
	public boolean isTokenfield() {
		return displayType == SelectDisplayType.TOKENS;
	}
	
	/**
	 * @param optionList
	 *            die Optionsliste
	 */
	public void setOptionList(OptionList optionList) {
		this.optionList = optionList;
	}
	

}
