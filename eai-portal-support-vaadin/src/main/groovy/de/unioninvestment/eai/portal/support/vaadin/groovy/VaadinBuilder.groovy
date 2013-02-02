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

import de.unioninvestment.eai.portal.support.vaadin.PortletApplication

class VaadinBuilder extends FactoryBuilderSupport {
	def components = [:]

	VaadinBuilder(PortletApplication application) {
		registerFactory('button', new ButtonFactory());
		registerFactory('panel', new PanelFactory());
		registerFactory('table', new TableFactory());
		registerFactory('checkBox', new CheckBoxFactory());
		registerFactory('link', new LinkFactory());
		registerFactory('label', new LabelFactory());
		registerFactory('externalResource', new ExternalResourceFactory());
		registerFactory('streamResource', new StreamResourceFactory(application));
		registerFactory('themeResource', new ThemeResourceFactory());
		registerFactory('embedded', new EmbeddedFactory());
		registerFactory('svgComponent', new SvgComponentFactory());
		registerFactory('verticalLayout', new VerticalLayoutFactory());
		registerFactory('horizontalLayout', new HorizontalLayoutFactory());
		registerFactory('downloadLink', new DownloadLinkFactory(application));
		registerFactory('xslt', new XSLTFactory());
		registerFactory('form', new FormFactory());
		registerFactory('textArea', new TextAreaFactory());
		registerFactory('contextMenu', new ContextMenuFactory(application));
		registerFactory('chart', new ChartFactory());
		registerFactory('select', new SelectFactory());
		registerFactory('tree', new TreeFactory());
		registerFactory('upload', new UploadFactory());
	}
}