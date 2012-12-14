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

import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;

/**
 * Model-Klasse für Komponenten, die zur Laufzeit durch den
 * {@code CustomComponentGenerator} erzeugt werden.
 */
public class CustomComponent extends Component implements
		Component.ExpandableComponent {

	/**
	 * The component's configuration.
	 * 
	 * @since 1.45
	 */
	private final ScriptComponentConfig config;

	private CustomComponentGenerator generator;

	/**
	 * @param config
	 *            the component's configuration.
	 * @since 1.45
	 */
	public CustomComponent(ScriptComponentConfig config) {
		this.config = config;
	}

	public String getId() {
		return this.config.getId();
	}

	public CustomComponentGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(CustomComponentGenerator generator) {
		this.generator = generator;
	}

	/**
	 * {@inheritDoc} since 1.45
	 */
	@Override
	public int getExpandRatio() {
		return this.config.getExpandRatio();
	}
}
