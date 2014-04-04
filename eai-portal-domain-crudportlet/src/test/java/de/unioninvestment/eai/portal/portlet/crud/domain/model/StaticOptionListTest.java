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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.OptionConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.SelectConfig;


public class StaticOptionListTest {
	private OptionList selection;
	
	private SelectConfig config;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		config = new SelectConfig();
	}
	
	@Test
	public void shouldProvideIteratorForEntries() {
		addConfigOption("key1", "value1");
		addConfigOption("key2", "value2");
		addConfigOption("key3", "value3");
		addConfigOption("key4", "value4");

		selection = new StaticOptionList(config);
		Iterator<Entry<String, String>> it = selection.getOptions(null).entrySet().iterator();
		assertThat(it.next().getKey(), is("key1"));
		assertThat(it.next().getKey(), is("key2"));
		assertThat(it.next().getKey(), is("key3"));
		assertThat(it.next().getKey(), is("key4"));
		assertThat(it.hasNext(), is(false));
	}
	
	@Test
	public void shouldFindOptionTitle() {
		addConfigOption("key1", "value1");
		addConfigOption("key2", "value2");
		addConfigOption("key3", "value3");
		addConfigOption("key4", "value4");

		selection = new StaticOptionList(config);

		assertThat(selection.getTitle("key3", null), is("value3"));
	}

	@Test
	public void shouldFindOptionKey() {
		addConfigOption("key1", "value1");
		addConfigOption("key2", "value2");
		addConfigOption("key3", "value3");
		addConfigOption("key4", "value4");

		selection = new StaticOptionList(config);

		assertThat(selection.getKey("value3", null), is("key3"));
	}

	private void addConfigOption(String key, String title) {
		OptionConfig opt = new OptionConfig();
		opt.setKey(key);
		opt.setValue(title);
		config.getOption().add(opt);
	}
}
