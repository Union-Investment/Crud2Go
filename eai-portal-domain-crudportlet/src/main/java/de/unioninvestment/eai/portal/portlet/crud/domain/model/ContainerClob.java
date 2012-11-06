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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.ContainerException;

/**
 * Clob Implementierung für das Domain Model.
 * 
 * @author markus.bonsch
 * 
 */
public class ContainerClob {

	private ObjectProperty<String> property;

	private String data;

	private int size = 0;

	private boolean isModified = false;

	private boolean isInitialized = false;

	private FreeformQueryEventWrapper queryDelegate;

	private ContainerRowId containerRowId;

	private String columnName;

	/**
	 * Dient für neu angelegte CLobs
	 */
	ContainerClob() {
		isInitialized = true;
	}

	/**
	 * @param charBuffer
	 *            aktueller Wert
	 */
	ContainerClob(CharBuffer charBuffer) {
		isInitialized = true;
		this.data = charBuffer.toString();
	}

	/**
	 * Liest den Wert den CLobs 'lazy' nach.
	 * 
	 * @param queryDelegate
	 * @param containerRowId
	 * @param columnName
	 */
	ContainerClob(FreeformQueryEventWrapper queryDelegate,
			ContainerRowId containerRowId, String columnName) {
		this.queryDelegate = queryDelegate;
		this.containerRowId = containerRowId;
		this.columnName = columnName;
	}

	/**
	 * Liefert den Clob Inhalt.
	 * 
	 * @return value
	 * 
	 */
	public String getValue() {
		if (!isInitialized) {
			Reader reader = loadClobLazy();
			return toString(reader);
		}

		return data;
	}

	/**
	 * Die Zeichenlänge des CLob's.
	 * 
	 * @return size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param value
	 *            der neue CLob-Wert
	 */
	public void setValue(String value) {
		this.size = value.length();
		this.data = value;
		isInitialized = true;
		isModified = true;
	}

	/**
	 * Verpackt den CLob Wert in ein Vaadin Property.
	 * 
	 * @return ein ObjectProperty mit dem CLob String als Datasource
	 */
	public Property getPropertyValue() {

		property = new ObjectProperty<String>(getValue(), String.class);
		property.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object newValue = event.getProperty().getValue();
				if (newValue != null
						&& (getValue() == null || !newValue.equals(getValue()))) {
					setValue((String) newValue);
				}
			}
		});
		return property;
	}

	/**
	 * Liefert ob sich der Feldinhalt des CLobs gändert hat
	 * 
	 * @return true falls sich der Wert sich geändert hat.
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * Setzt den Modified Status zurück.
	 */
	public void commit() {
		isModified = false;
	}

	private String toString(Reader reader) {
		if (reader == null)
			return null;
		StringBuilder sb = new StringBuilder();
		Reader clobReader = new BufferedReader(reader);

		try {
			for (int c; (c = clobReader.read()) != -1;) {
				sb.append((char) c);
			}
		} catch (IOException e) {
			throw new ContainerException(
					"Exception while reading clob value from reader :"
							+ e.getMessage(), e);
		}
		return sb.toString();
	}

	/**
	 * @return Text als Stream
	 */
	public Reader getReader() {
		if (!isInitialized) {
			return loadClobLazy();
		}
		if (data != null) {
			return new StringReader(data);
		} else {
			return null;
		}
	}

	private Reader loadClobLazy() {
		String clobString = queryDelegate.getCLob(
				(RowId) containerRowId.getInternalId(), columnName);
		if (clobString != null) {
			this.size = clobString.length();
			return new StringReader(clobString);
		}
		return null;
	}

}
