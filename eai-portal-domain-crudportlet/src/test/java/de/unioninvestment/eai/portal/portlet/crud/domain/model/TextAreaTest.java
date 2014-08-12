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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import javax.portlet.ReadOnlyException;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.TextAreaConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationMarshaller;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class TextAreaTest {

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	private static PortletConfig config;

	@BeforeClass
	public static void prepareConfig() throws JAXBException {
		InputStream stream = TextAreaTest.class.getClassLoader()
				.getResourceAsStream("validTextAreaConfig.xml");
		config = new PortletConfigurationMarshaller().unmarshal(stream);
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldProvideContent() {
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(0), false);
		assertThat(textarea.getContent(), is("\n\t\t\t\tTextarea 51\n\t\t\t"));
	}
	
	@Test
	public void shouldProvideContentFromPreferences() {
		when(liferayContext.getPortletPreferencesMock().getValue("test", null)).thenReturn("Stored Content");
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(1), true);
		assertThat(textarea.getContent(), is("Stored Content"));
	}
	
	@Test
	public void shouldAllowUpdatingContent() throws ReadOnlyException {
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(1), true);
		textarea.updateContent("Textarea 61");
		verify(liferayContext.getPortletPreferencesMock()).setValue("test", "Textarea 61");
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailUpdateIfReadonly() throws ReadOnlyException {
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(0), false);
		textarea.updateContent("Textarea 61");
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailUpdateIfPreferencesAreReadonly() throws ReadOnlyException {
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(1), true);
		doThrow(new ReadOnlyException("bla")).when(liferayContext.getPortletPreferencesMock()).setValue("test", "Textarea 61");
		textarea.updateContent("Textarea 61");
	}
	
	@Test
	public void shouldSanitizeUpdatedContent() throws ReadOnlyException {
		TextArea textarea = new TextArea((TextAreaConfig) config.getPage()
				.getElements().get(1), true);
		textarea.updateContent("Textarea 61<script/>");
		verify(liferayContext.getPortletPreferencesMock()).setValue("test", "Textarea 61");
	}
}
