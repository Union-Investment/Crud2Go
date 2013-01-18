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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

/**
 * Container für eine Custom Component.
 * 
 * @author carsten.mjartan
 */
public class DefaultCustomComponentView extends CustomComponent implements
		CustomComponentView {

	private static final long serialVersionUID = 1L;

	/**
	 * Initialisierung.
	 * 
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired width of component (@since 1.45). Defaults to
	 *            undefined when not specified.
	 */
	public DefaultCustomComponentView(String width, String height) {
		setWidth(width != null ? width : "100%");
		if (height != null) {
			setHeight(height);
		}
		super.setCompositionRoot(new Label("Initializing..."));
	}

	@Override
	public void setComponent(Component c) {
		super.setCompositionRoot(c);
	}
}
