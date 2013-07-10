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


abstract class AbstractBeanFactory extends AbstractFactory {
	List ignoredAttributes

	public boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object component, Map attributes ) {
		attributes.each {name, value ->
			if (ignoredAttributes == null || !ignoredAttributes.contains(name)) {
				Object[] args = [builder, component, value]
				MetaMethod method = this.metaClass.getMetaMethod("handleAttribute${name.capitalize()}", args)
				if (method) {
					method.invoke(this, args)
				} else {
					MetaProperty p = component.metaClass.hasProperty(component, name);
					if (p) {
						p.setProperty(component, value);
					} else {
						handleCustomProperty(builder, component, name, value);
					}
				}
			}
		}

		return false;
	}

	protected handleCustomProperty(builder, component, name, value) {
		throw new BuilderException("Property $name of class ${component.class.name} not found")
	}
}
