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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class DefaultPanelContentViewTest {

	DefaultPanelContentView view = new DefaultPanelContentView();

	@Before
	public void setup() {
		view.initialize(false);
	}
	
	@Test
	public void shouldUseMarginByDefault() {
		Assert.assertTrue(view.getMargin().getBitMask() > 0);
	}

	@Test
	public void shouldUseVerticalLayout() {
		Assert.assertTrue(view.getLayoutInternal() instanceof VerticalLayout);
	}

	@Test
	public void shouldUseMarginWithHorizontalLayout() {
		view = new DefaultPanelContentView();
		view.initialize(true);
		Assert.assertTrue(view.getLayoutInternal() instanceof HorizontalLayout);
	}
}
