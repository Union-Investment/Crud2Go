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
package de.unioninvestment.eai.portal.portlet.crud.mvp.events;

import de.unioninvestment.eai.portal.support.vaadin.mvp.Event;

/**
 * Event das bei einem erfolgreichen Update der Portletkonfiguration gefeuert
 * wird.
 * 
 * @author markus.bonsch
 * 
 */
public class ConfigurationUpdatedEvent implements
		Event<ConfigurationUpdatedEventHandler> {

	private static final long serialVersionUID = 1L;
	private boolean configurable;

	public ConfigurationUpdatedEvent(boolean configurable) {
		this.configurable = configurable;
	}

	@Override
	public void dispatch(ConfigurationUpdatedEventHandler eventHandler) {
		eventHandler.onConfigurationUpdated(this);
	}

	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (configurable ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigurationUpdatedEvent other = (ConfigurationUpdatedEvent) obj;
		if (configurable != other.configurable)
			return false;
		return true;
	}

}
