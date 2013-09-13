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

import com.vaadin.ui.Component
import com.vaadin.ui.Select

@Deprecated
class SelectFactory extends AbstractFieldFactory {

	@Override
	public Component createComponent() {
		return new Select();
	}
	
	void handleAttributeValue(FactoryBuilderSupport builder, Select select, String value) {
		select.value = value;
	}
	
	void handleAttributeItems(FactoryBuilderSupport builder, Select select, Map items) {
		items.each {key, value ->
			select.addItem(key);
			select.setItemCaption(key, value);
		}
	}
}
