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
package de.unioninvestment.eai.portal.portlet.crud

def xml = new XmlParser().parse(new File("test.xml"))

def scriptText = xml.script.text()

ClassLoader parent = getClass().getClassLoader();
GroovyClassLoader loader = new GroovyClassLoader(parent);

Class scriptClass = loader.parseClass(scriptText, "PortletScript.groovy")

println "-- Compiling Closure"
def button = xml.button[0]
def buttonScript = "{ it -> "+button.'@onclick'+"} as Closure"
println "-- $buttonScript"
def buttonClosureFactory = loader.parseClass(buttonScript, "ButtonOnClick.groovy")

// buttonClosureFactory.getMethods().each { m -> println m }

println "-- Running Script"
def script = scriptClass.newInstance()
script.run()

println "-- Instantiating Closure"
Closure buttonClosure = buttonClosureFactory.newInstance().run()
buttonClosure.delegate = script


println "-- Running Closure"
buttonClosure(button)
buttonClosure(button)
