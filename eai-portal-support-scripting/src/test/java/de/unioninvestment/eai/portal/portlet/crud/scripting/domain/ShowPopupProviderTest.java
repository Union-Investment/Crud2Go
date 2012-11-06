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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import groovy.lang.Closure;
import groovy.lang.Script;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowPopupEvent;
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;

public class ShowPopupProviderTest {

	private ShowPopupProvider showPopup = new ShowPopupProvider(this);

	@Mock
	private EventBus eventBusMock;

	@Captor
	ArgumentCaptor<ShowPopupEvent> argumentCaptor;

	@Mock
	private Closure<?> closureMock;

	private ScriptCompiler compiler = new ScriptCompiler();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		showPopup.setEventBus(eventBusMock);
	}

	@Test
	public void shouldFireShowPopupEventWithText() {
		String titel = "Titel";
		String content = "<div>Test</div>";

		showPopup.doCall(titel, content);
		verify(eventBusMock).fireEvent(any(ShowPopupEvent.class));
	}

	@Test
	public void shouldFireRightTextContent() {
		String titel = "Titel";
		String content = "<div>Test</div>";

		showPopup.doCall(titel, content);

		verify(eventBusMock).fireEvent(argumentCaptor.capture());

		assertThat(argumentCaptor.getAllValues().size(), is(1));
		assertThat(argumentCaptor.getValue().getSource().getBody(), is(content));
		assertThat(argumentCaptor.getValue().getSource().getTitle(), is(titel));
		assertThat(argumentCaptor.getValue().getSource().getContentType(),
				is(ShowPopupEvent.CONTENT_TYPE_PLAIN));
	}

	@Test
	public void shouldFireShowPopupEventWithClosure() {
		String title = "Titel";
		showPopup.doCall(title, closureMock);
		verify(eventBusMock).fireEvent(any(ShowPopupEvent.class));
	}

	@Test
	public void shouldFireRightClosureContent() throws InstantiationException,
			IllegalAccessException {
		String title = "Titel";
		String content = "<pre style='color:#ff0000'>errorMessage</pre>";

		Closure<?> scriptClosure = compileClosure("{ it -> pre(style:'color:#ff0000', 'errorMessage') }");
		showPopup.doCall(title, scriptClosure);

		verify(eventBusMock).fireEvent(argumentCaptor.capture());

		assertThat(argumentCaptor.getAllValues().size(), is(1));
		assertThat(argumentCaptor.getValue().getSource().getBody(),
				containsString(content));
		assertThat(argumentCaptor.getValue().getSource().getTitle(), is(title));
		assertThat(argumentCaptor.getValue().getSource().getContentType(),
				is(ShowPopupEvent.CONTENT_TYPE_XHTML));
	}

	private Closure<?> compileClosure(String script)
			throws InstantiationException, IllegalAccessException {

		Class<Script> compileScript = compiler.compileScript(script);
		Script instance = compileScript.newInstance();
		return (Closure<?>) instance.run();
	}
}
