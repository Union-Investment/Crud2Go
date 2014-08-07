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

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.server.StreamResource.StreamSource;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.FreeformQueryEventWrapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * BLob Implementierung für das Domain Model. Während geänderte Daten im
 * Speicher gehalten werden, werden unveränderte Blobs immer aus der Datenbank
 * geladen und nicht zwischengespeichert, um den Speicherverbrauch gering zu
 * halten.
 * 
 * @author markus.bonsch
 * @author carsten.mjartan
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
		if (isInitialized) {
			return data;
		} else {
			return queryDelegate.getBLob(
					(RowId) containerRowId.getInternalId(), columnName);
		}
	}

	public void setValue(byte[] data) {
		this.data = data;
		isInitialized = true;
		isModified = true;
	}

	public boolean isEmpty() {
		if (isInitialized) {
			return data == null;
		} else {
			return !queryDelegate.hasBlobData(
					(RowId) containerRowId.getInternalId(), columnName);
		}
	}

	/**
	 * @return den Content als InputStream oder <code>null</code>
	 */
	public ByteArrayInputStream getInputStream() {
		byte[] result = getValue();
		return result == null ? null : new ByteArrayInputStream(result);
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
