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
package de.unioninvestment.eai.portal.support.scripting;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container.Filter;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptContainerBackend;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.vaadin.container.ContainerMetaDataBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.container.UpdateContext;

/**
 * 
 * @author max.hartmann
 * 
 */
public class ScriptContainerDelegate implements GenericDelegate {

    private final Closure delegateClosure;
	private final DataContainer container;

    private ScriptContainerBackend backend;

	public ScriptContainerDelegate(Closure delegateClosure,
			DataContainer container) {
		this.delegateClosure = delegateClosure;
		this.container = container;
	}

	@Override
	public MetaData getMetaData() {
		Closure<?> c = ((Closure<?>) getBackend().getMetaData());
		ContainerMetaDataBuilder b = new ContainerMetaDataBuilder();
		b.invokeMethod("cols", c);
		return b.createMetaData();
	}

	@Override
	public List<Object[]> getRows() {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (List<?> currentRow : getBackend().read()) {
			ret.add(currentRow.toArray());
		}
		return ret;
	}

	@Override
	public void setFilters(Filter[] filters) {
	}

	@Override
	public void update(List<GenericItem> items, UpdateContext context) {
		List<ScriptRow> rows = new ArrayList<ScriptRow>();
		for (GenericItem item : items) {
			GenericContainerRowId rowId = new GenericContainerRowId(
					item.getId(), container.getPrimaryKeyColumns());
			rows.add(new ScriptRow(new GenericContainerRow(rowId, item,
					container,
					false, true)));
		}
        getBackend().update(rows);
	}

    public ScriptContainerBackend getBackend() {
        if (backend == null) {
            backend = (ScriptContainerBackend) delegateClosure.call();
        }
        return backend;
    }
}
