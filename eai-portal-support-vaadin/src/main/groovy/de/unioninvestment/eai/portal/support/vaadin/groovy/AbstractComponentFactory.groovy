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
package de.unioninvestment.eai.portal.support.vaadin.groovy

import groovy.util.FactoryBuilderSupport

import java.util.Map

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.vaadin.ui.Component

abstract class AbstractComponentFactory extends AbstractBeanFactory {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentFactory.class);

	void setParent(FactoryBuilderSupport builder,  parent,  child) {
		parent.addComponent child
	}

	@Override
	public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
		def component = createComponent();
		if (value) {
			builder.components[value] = component
		}
		return component
	}

	void handleAttributeWidth(FactoryBuilderSupport builder, Component component, String value) {
		component.class.getMethod("setWidth", String.class).invoke(component, value)
	}

	void handleAttributeHeight(FactoryBuilderSupport builder, Component component, String value) {
		component.class.getMethod("setHeight", String.class).invoke(component, value)
	}

	abstract Component createComponent();
}
