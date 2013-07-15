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

import org.vaadin.peter.contextmenu.ContextMenu
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItemClickListener;

import com.vaadin.ui.UI



class ContextMenuFactory extends AbstractBeanFactory {

	ContextMenuFactory() {
	}

	@Override
	public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
		ContextMenu menu = new ContextMenu()
		menu.setAsContextMenuOf(UI.getCurrent())
		menu.setOpenAutomatically(false)
		return menu
	}

	void handleAttributeOnclick(FactoryBuilderSupport builder, ContextMenu component, Closure handler) {
		component.addItemClickListener(handler as ContextMenuItemClickListener)
	}

	void handleAttributeItems(FactoryBuilderSupport builder, ContextMenu component, Closure config) {
		config.delegate = new ContextMenuConfigurer(component) 
		config.resolveStrategy = Closure.DELEGATE_FIRST
		config.call()
	}
}

class ContextMenuConfigurer {
	private ContextMenu menu
	
	ContextMenuConfigurer(ContextMenu menu) {
		this.menu = menu
	}
	
	void item(String caption) {
		menu.addItem(caption)
	}
	
	void item(Map params, String caption) {
		def item = menu.addItem(caption)
		if (params.data) {
			item.data = params.data
		}
	}
}
