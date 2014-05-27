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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerBlob;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.FileMetadata;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;

/**
 * ColumnGenerator für Binär Spalten.
 * 
 * @author markus.bonsch
 * 
 */
public class BLobColumnGenerator implements ColumnGenerator {

	private static final long serialVersionUID = 1L;

	private final DataContainer container;

	private final TableColumns columns;

	public BLobColumnGenerator(DataContainer container, TableColumns columns) {
		this.container = container;
		this.columns = columns;
	}

	@Override
	public Component generateCell(Table source, Object itemId, Object columnId) {
		ContainerRow containerRow = container.getRow(
				container.convertInternalRowId(itemId), false, true);
		ContainerBlob containerBlob = (ContainerBlob) containerRow.getFields()
				.get(columnId).getValue();

		if (!containerBlob.isEmpty()) {
			return buildBlobLink(containerRow, columnId.toString(),
					containerBlob);
		}

		return null;
	}

	private Link buildBlobLink(ContainerRow row, String columnId,
			ContainerBlob containerBlob) {
		StreamSource streamSource = containerBlob.getStreamSource();
		FileMetadata metadata = columns.get(columnId).getFileMetadata();
		StreamResource resource = new StreamResource(streamSource,
				metadata.getFileName());
		
		String fileName = metadata.getCurrentDisplayname(row);
		String mimeType = metadata.getCurrentMimetype(row);
		
		resource.setMIMEType(mimeType);
		Link link = new Link();
		link.setCaption(fileName);
		link.setResource(resource);
		link.setTargetName("_blank");
		return link;
	}

}
