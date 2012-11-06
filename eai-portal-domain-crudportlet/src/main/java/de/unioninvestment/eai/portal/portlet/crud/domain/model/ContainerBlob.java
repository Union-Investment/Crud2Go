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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.addon.sqlcontainer.RowId;
import com.vaadin.terminal.StreamResource.StreamSource;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;

/**
 * BLob Implementierung für das Domain Model.
 * 
 * @author markus.bonsch
 * 
 */
public class ContainerBlob {

	private boolean isInitialized = false;

	private boolean isModified = false;

	private FreeformQueryEventWrapper queryDelegate;

	private ContainerRowId containerRowId;

	private String columnName;

	private byte[] data;

	public ContainerBlob() {
		isInitialized = true;
	}

	public ContainerBlob(byte[] data) {
		this.data = data;
		isInitialized = true;
	}

	public ContainerBlob(FreeformQueryEventWrapper queryDelegate,
			ContainerRowId containerRowId, String columnName) {
		this.queryDelegate = queryDelegate;
		this.containerRowId = containerRowId;
		this.columnName = columnName;
	}


	public boolean isModified() {
		return isModified;
	}

	/**
	 * Liefert den Clob Inhalt.
	 * 
	 * @return value
	 * 
	 */
	public byte[] getValue() {
		return toArray(getInputStream());
	}

	private byte[] toArray(ByteArrayInputStream bais) {
		byte[] array = new byte[bais.available()];
		try {
			bais.read(array);
		} catch (IOException e) {
			throw new TechnicalCrudPortletException(
					"Error while reading blob data.", e);
		}
		return array;
	}

	public void setValue(byte[] data) {
		this.data = data;
		isInitialized = true;
		isModified = true;
	}

	public boolean hasData() {
		if (isInitialized) {
			return data != null;
		} else {
			return queryDelegate.hasBlobData(
					(RowId) containerRowId.getInternalId(),
					columnName);
		}
	}

	public ByteArrayInputStream getInputStream() {
		if (!isInitialized) {
			return new ByteArrayInputStream(queryDelegate.getBLob(
					(RowId) containerRowId.getInternalId(), columnName));
		}
		return new ByteArrayInputStream(data);
	}

	public StreamSource getStreamSource() {
		return new StreamSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public InputStream getStream() {
				return getInputStream();
			}
		};
	}

	public void commit() {
		isModified = false;
	}

}
