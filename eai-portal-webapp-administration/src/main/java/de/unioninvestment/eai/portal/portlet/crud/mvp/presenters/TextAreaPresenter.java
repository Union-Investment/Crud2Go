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

import static de.unioninvestment.eai.portal.support.vaadin.context.Context.getMessage;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TextArea;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TextAreaView;

public class TextAreaPresenter extends
		AbstractComponentPresenter<TextArea, TextAreaView> implements
		TextAreaView.Presenter, ShowEventHandler<Tab> {

	private static final long serialVersionUID = 1L;

	public TextAreaPresenter(TextAreaView view, TextArea model) {
		super(view, model);
		view.setPresenter(this);
		
		if (model.getContent() != null) {
			view.showContent(model.getContent());
		} else if (model.isEditable()) {
			view.showContent(getMessage("portlet.crud.textarea.emptyMessage"));
		} else { 
			view.hide();
		}
	}

	@Override
	public void onShow(ShowEvent<Tab> event) {
		// do nothing
	}

	@Override
	public void contentAreaClicked() {
		if (getModel().isEditable()) {
			getView().showEditor(getModel().getContent());
		}
	}

	@Override
	public void contentChanged(String changedContent) {
		getModel().updateContent(changedContent);
		getView().showContent(getModel().getContent());
	}

	@Override
	public void cancelEditing() {
		getView().showContent(getModel().getContent());
	}
}
