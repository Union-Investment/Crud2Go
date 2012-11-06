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
package de.unioninvestment.eai.portal.portlet.crud.domain.events;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;
import de.unioninvestment.eai.portal.support.vaadin.mvp.SourceEvent;

/**
 * Event für das Expandieren eines Bereiches.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class ExpandEvent implements SourceEvent<ExpandEventHandler, Region> {

	private static final long serialVersionUID = 1L;

	private final Region source;

	/**
	 * @param source
	 *            die region die expandiert wurde.
	 */
	public ExpandEvent(Region source) {
		this.source = source;
	}

	@Override
	public void dispatch(ExpandEventHandler eventHandler) {
		eventHandler.onExpand(this);
	}

	@Override
	public Region getSource() {
		return this.source;
	}
}
