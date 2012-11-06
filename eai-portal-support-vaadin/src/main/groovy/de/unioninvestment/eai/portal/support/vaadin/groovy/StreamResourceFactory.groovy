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


import com.vaadin.Application
import com.vaadin.terminal.StreamResource
import com.vaadin.terminal.StreamResource.StreamSource

class StreamResourceFactory extends AbstractBeanFactory {
	private Application application

	StreamResourceFactory(Application application) {
		ignoredAttributes = ['stream', 'filename']
		this.application = application
	}

	Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map args)  {
		return new StreamResource(args.stream as StreamSource, args.filename as String, application)
	}

	void handleAttributeMimetype(builder, StreamResource component, Object value) {
		component.setMIMEType(value)
	}
}
