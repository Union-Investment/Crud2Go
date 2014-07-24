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

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import de.unioninvestment.eai.portal.support.vaadin.SVGEntityResolver

class XSLTFactory extends AbstractBeanFactory {
	
	private static final int INITIAL_OUTPUTSTREAM_SIZE = 1024
	
	private DocumentBuilderFactory dbf
	private TransformerFactory tf
	
	XSLTFactory() {
		ignoredAttributes = ['input', 'xslt']
		
		dbf = DocumentBuilderFactory.newInstance()
		dbf.setNamespaceAware(true)
		
		tf = TransformerFactory.newInstance()
	}
	
	def newInstance(FactoryBuilderSupport builder, name,value, Map args) throws InstantiationException ,IllegalAccessException {
		InputStream input = args.input as InputStream
		InputStream xslt = args.xslt as InputStream
		
		DocumentBuilder db = dbf.newDocumentBuilder()
		db.setEntityResolver(new SVGEntityResolver())
		
		DOMSource inSource = new DOMSource(db.parse(input))
		ByteArrayOutputStream out = new ByteArrayOutputStream(INITIAL_OUTPUTSTREAM_SIZE)
		StreamResult outSource = new StreamResult(out)
		
		Transformer t = tf.newTransformer(new StreamSource(xslt))
		t.transform(inSource, outSource)
		
		return new ByteArrayInputStream(out.toByteArray())
	}
}