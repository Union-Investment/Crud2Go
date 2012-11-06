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

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CustomComponent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.LazyInitializable;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CustomComponentView;

/**
 * Presenter für Custom Components. Diese werden Lazy generiert und gerendert,
 * d. h. erst wenn z. B. ein Tab sichtbar wird
 * 
 * @author carsten.mjartan
 */
public class CustomComponentPresenter implements ComponentPresenter,
		ShowEventHandler<Tab> {

	private static final long serialVersionUID = 1L;

	private final CustomComponentView view;

	private boolean initialized = false;

	private CustomComponent model;

	/**
	 * @param view
	 *            die View
	 * @param model
	 *            das Model
	 */
	@SuppressWarnings("unchecked")
	public CustomComponentPresenter(CustomComponentView view,
			CustomComponent model) {
		this.view = view;
		this.model = model;

		if (model.getPanel() instanceof LazyInitializable) {
			((LazyInitializable) model.getPanel()).addShowEventListener(this);
		} else {
			onShow(null);
		}
	}

	@Override
	public CustomComponentView getView() {
		return view;
	}

	@Override
	public void onShow(ShowEvent<Tab> event) {
		if (!initialized) {
			view.setComponent(model.getGenerator().generate());
			initialized = true;
		}
	}

}
