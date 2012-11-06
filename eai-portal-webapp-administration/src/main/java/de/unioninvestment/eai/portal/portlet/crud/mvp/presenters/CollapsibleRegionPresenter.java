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

import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CollapsibleRegionView;

/**
 * Der Presenter für einen Bereich.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class CollapsibleRegionPresenter extends RegionPresenter implements
		CollapsibleRegionView.Presenter {

	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt eine neue Instanz diese Presenters.
	 * 
	 * @param view
	 *            die View.
	 * @param model
	 *            das Modell.
	 */
	public CollapsibleRegionPresenter(CollapsibleRegionView view, Region model) {
		super(view, model);
		view.initialize(this);
		view.setTitle(model.getTitle());
		this.registerEventHandlers();
		view.setCollapsed(model.isCollapsed());
	}

	@Override
	public CollapsibleRegionView getView() {
		return (CollapsibleRegionView) super.getView();
	}

	@Override
	public void regionHasBeenCollapsed(boolean collapsed) {
		this.getModel().setCollapsed(collapsed);
	}

	private void registerEventHandlers() {
		this.getModel().addCollapseEventHandler(new CollapseEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onCollapse(CollapseEvent event) {
				getView().setCollapsed(true);
			}
		});
		this.getModel().addExpandEventHandler(new ExpandEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onExpand(ExpandEvent event) {
				getView().setCollapsed(false);
			}
		});
	}
}
