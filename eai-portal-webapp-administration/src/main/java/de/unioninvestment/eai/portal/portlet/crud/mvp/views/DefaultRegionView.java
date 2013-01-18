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

/**
 * Default-Implementierung für die View eines Bereichs.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class DefaultRegionView extends DefaultPanelContentView implements
		RegionView {

	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt eine Instanz eines Bereiches.
	 * 
	 * @param withMargin
	 *            ob der Margin gesetzt werden soll. (@since 1.45).
	 * @param useHorizontalLayout
	 *            when <code>true</code>, components are layed out horizontally.
	 *            (@since 1.45).
	 * @param width
	 *            The desired width of component (@since 1.45). Defaults to
	 *            "100%" when not specified.
	 * @param height
	 *            The desired height of component (@since 1.45). Defaults to
	 *            undefined when not specified.
	 */
	public DefaultRegionView(boolean withMargin, boolean useHorizontalLayout,
			String width, String height) {
		super(withMargin, useHorizontalLayout, width, height);
		this.addStyleName("crudRegion");
	}

	@Override
	public void setTitle(String title) {
		this.setCaption(title);
	}

	String getTitle() {
		return this.getCaption();
	}
}
