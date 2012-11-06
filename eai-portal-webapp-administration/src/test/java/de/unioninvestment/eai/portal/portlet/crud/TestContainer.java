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
package de.unioninvestment.eai.portal.portlet.crud;

import java.util.Date;

import com.vaadin.data.util.BeanContainer;

public class TestContainer extends BeanContainer<Integer, Row> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L;

	public TestContainer() {
		super(Row.class);
		for (int i = 1; i < 100; i++) {
			addItem(i,
					new Row(i, "Etwas längerer Text " + i, "Bla", new Date()));
		}
	}
}
