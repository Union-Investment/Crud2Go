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
package de.unioninvestment.eai.portal.support.vaadin.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * Ein Filter der alles blockiert.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class NothingFilter implements Filter {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		return true;
	}
}
