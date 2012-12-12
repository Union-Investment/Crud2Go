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
package de.unioninvestment.eai.portal.portlet.crud.domain.model.datasource;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;

/**
 * Das Modell der Datenquellen-Infoübersicht.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DatasourceInfos extends Component implements Serializable,
		Iterable<DatasourceInfo> {

	private static final long serialVersionUID = 1L;

	private final List<DatasourceInfo> infos = new LinkedList<DatasourceInfo>();
	private final BeanItemContainer<DatasourceInfo> container = new BeanItemContainer<DatasourceInfo>(
			DatasourceInfo.class);

	/**
	 * @return die Container-Instanz mit den Info-Objekten.
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Entfernt alle Einträge aus der Tabelle.
	 */
	public void clean() {
		infos.clear();
		container.removeAllItems();
	}

	@Override
	public Iterator<DatasourceInfo> iterator() {
		return infos.iterator();
	}

	/**
	 * @param info
	 *            das Info-Objekt das hinzugefügt werden soll.
	 */
	public void addInfo(DatasourceInfo info) {
		infos.add(info);
		container.addBean(info);
	}
}
