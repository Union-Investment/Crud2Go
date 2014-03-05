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

package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import java.util.Collection;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CompoundSearch;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CompoundSearchView;

/**
 * Presenter für {@link CompoundSearchView}.
 * 
 * @author cmj
 */
@SuppressWarnings("serial")
public class CompoundSearchPresenter extends PanelContentPresenter implements
		CompoundSearchView.Presenter, CompoundQueryChangedEventHandler {

	private boolean externalChange = false;

	public CompoundSearchPresenter(CompoundSearchView view, CompoundSearch model) {
		super(view, model);
		view.setPresenter(this);
		
		Collection<String> searchableFields = model.getSearchableFields();
		if (searchableFields.size() == 0) {
			throw new BusinessException("portlet.crud.error.compoundsearch.noSearchableFields");
		}
		
		Collection<String> defaultFields = model.getDefaultFields();
		if (defaultFields.size() == 0) {
			throw new BusinessException("portlet.crud.error.compoundsearch.noDefaultFields");
		}
		
		view.initialize(searchableFields);
		model.addQueryChangedEventHandler(this);
	}

	@Override
	public CompoundSearchView getView() {
		return (CompoundSearchView) super.getView();
	}

	@Override
	protected CompoundSearch getModel() {
		return (CompoundSearch) super.getModel();
	}

	@Override
	public boolean isValidQuery(String queryString) {
		return getModel().isValidQuery(queryString);
	}
	
	@Override
	public void search(String queryString) {
		if (!externalChange) {
			getModel().search(queryString);
		}
	}

	@Override
	public void onQueryChange(CompoundQueryChangedEvent event) {
		try {
			externalChange = true;
			getView().updateQueryString(event.getQueryString());
		} finally {
			externalChange = false;
		}
	}

	@Override
	public Collection<TableColumn> getSearchableColumns() {
		return getModel().getSearchableColumns().values();
	}
}
