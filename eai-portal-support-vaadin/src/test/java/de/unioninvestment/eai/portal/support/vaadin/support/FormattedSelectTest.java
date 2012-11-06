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
package de.unioninvestment.eai.portal.support.vaadin.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Label;

import de.unioninvestment.eai.portal.support.vaadin.support.FormattedSelect;

public class FormattedSelectTest {

	@SuppressWarnings("unchecked")
	public static class DummyPropertyFormatter extends PropertyFormatter {

		private static final long serialVersionUID = 42L;

		@Override
		public String format(Object value) {
			return "TEST-STRING";
		}

		@Override
		public Object parse(String formattedValue) throws Exception {
			return "TEST-OBJECT";
		}
		
	}
	
	@Test
	public void shouldSetInvalidAllowedFalse() {
		FormattedSelect select = new FormattedSelect(null);
		assertFalse(select.isInvalidAllowed());
	}

	@Test
	public void shouldSetPropertyFormatter() {
		FormattedSelect select = new FormattedSelect(new DummyPropertyFormatter());
		select.setPropertyDataSource(new Label());
		assertThat(select.getPropertyDataSource(), instanceOf(DummyPropertyFormatter.class));
	}

	@Test
	public void shouldFormat() {
		FormattedSelect select = new FormattedSelect(new DummyPropertyFormatter());
		select.setPropertyDataSource(new Label());
		select.setValue("test123");
		assertThat(select.getValue(), instanceOf(String.class));
		assertThat((String)select.getValue(), equalTo("TEST-STRING"));
	}
}
