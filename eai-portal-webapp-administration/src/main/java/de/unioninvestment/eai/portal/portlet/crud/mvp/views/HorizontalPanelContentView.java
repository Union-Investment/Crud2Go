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

import com.vaadin.ui.HorizontalLayout;

/**
 * Implementation of the <code>{@link PanelContentView}</code> that lays out its
 * content horizontally (the <code>{@link DefaultPanelContentView}</code> lays
 * out its components vertically).
 * 
 * @author Jan Malcomess (codecentric AG)
 * @since 1.45
 */
public class HorizontalPanelContentView extends HorizontalLayout implements
		PanelContentView {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct an new <code>HorizontalPanelContentView</code>.
	 * 
	 * @param withMargin
	 *            Enable layout margins (if <code>true</code>). Affects all four
	 *            sides of the layout. This will tell the client-side
	 *            implementation to leave extra space around the layout.
	 */
	public HorizontalPanelContentView(boolean withMargin) {
		super();
		setSpacing(true);
		setMargin(withMargin);
		setWidth("100%");
	}
}
