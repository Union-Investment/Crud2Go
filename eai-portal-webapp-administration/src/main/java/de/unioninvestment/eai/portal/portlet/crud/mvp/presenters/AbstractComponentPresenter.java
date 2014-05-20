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

import java.io.Serializable;

import org.springframework.util.Assert;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.support.vaadin.mvp.AbstractPresenter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

/**
 * Convenient base implementation of <code>ComponentPresenter</code>.
 * 
 * @author Jan Malcomess (codecentric AG)
 * @since 1.45
 */
public class AbstractComponentPresenter<C extends Component, V extends View>
		extends AbstractPresenter<V> implements ComponentPresenter {
	/**
	 * @see Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The model of the view presented by this <code>ComponentPresenter</code>.
	 */
	private final C model;

	/**
	 * @param view
	 *            The view controlled by this presenter.
	 * @param model
	 *            The model of the view presented by this
	 *            <code>ComponentPresenter</code>.
	 */
	public AbstractComponentPresenter(V view, C model) {
		super(view);
		Assert.notNull(model);
		this.model = model;
	}

	/**
	 * @return The model of the view presented by this
	 *         <code>ComponentPresenter</code>.
	 */
	protected C getModel() {
		return this.model;
	}

	@Override
	public int getExpandRatio() {
		if (model instanceof Component.ExpandableComponent) {
			return ((Component.ExpandableComponent) this.model)
					.getExpandRatio();
		}
		return 0;
	}

	protected String getConfiguredWidth() {
		return this.model.getWidth();
	}

	protected String getConfiguredHeight() {
		return this.model.getHeight();
	}

	@Override
	public void updateViewWidth() {
		String configuredWidth = getConfiguredWidth();
		if (configuredWidth != null) {
			getView().setWidth(configuredWidth);
		} else {
			getView().setWidth("100%");
		}
	}

	@Override
	public void updateViewHeight(boolean outerHeightDefined) {
		String componentHeight = getConfiguredHeight();
		if (componentHeight != null) {
			getView().setHeight(componentHeight);
		} else if (outerHeightDefined && getExpandRatio() > 0) {
			getView().setHeight("100%");
		} else {
			getView().setHeight(null);
		}
	}
}
