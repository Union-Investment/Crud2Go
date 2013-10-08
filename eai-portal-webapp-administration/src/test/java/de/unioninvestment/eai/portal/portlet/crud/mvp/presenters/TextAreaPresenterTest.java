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

package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TextArea;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TextAreaView;
import de.unioninvestment.eai.portal.support.vaadin.junit.ContextMock;

public class TextAreaPresenterTest {

	@Mock
	private TextAreaView viewMock;
	@Mock
	private TextArea modelMock;
	private TextAreaPresenter presenter;

	@Rule
	public ContextMock contextMock = new ContextMock();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldDisplayDefaultMessageIfEditableAndContentIsEmpty() {
		when(modelMock.getContent()).thenReturn(null);
		when(modelMock.isEditable()).thenReturn(true);
		when(contextMock.getProvider().getMessage("portlet.crud.textarea.emptyMessage")).thenReturn("msg");
		
		presenter = new TextAreaPresenter(viewMock, modelMock);
		
		verify(viewMock).showContent("msg");
	}
	
	@Test
	public void shouldBeHiddenIfNotEditableAndContentIsEmpty() {
		when(modelMock.getContent()).thenReturn(null);
		when(modelMock.isEditable()).thenReturn(false);

		presenter = new TextAreaPresenter(viewMock, modelMock);
		
		verify(viewMock).hide();
	}
	
	@Test
	public void shouldDisplayContentOnOpen() {
		when(modelMock.getContent()).thenReturn("Content");
		
		presenter = new TextAreaPresenter(viewMock, modelMock);
		
		verify(viewMock).showContent("Content");
	}
	
	@Test
	public void shouldSwitchToEditorIfContentIsClickedAndEditable() {
		presenter = new TextAreaPresenter(viewMock, modelMock);
		when(modelMock.getContent()).thenReturn("Content");
		when(modelMock.isEditable()).thenReturn(true);
		
		presenter.contentAreaClicked();
		
		verify(viewMock).showEditor("Content");
	}

	@Test
	public void shouldIgnoreContentClickIfNotEditable() {
		presenter = new TextAreaPresenter(viewMock, modelMock);
		when(modelMock.isEditable()).thenReturn(false);
		
		presenter.contentAreaClicked();
		
		verify(viewMock, never()).showEditor(null);
	}
	
	@Test
	public void shouldSaveContent() {
		presenter = new TextAreaPresenter(viewMock, modelMock);
		
		presenter.contentChanged("NewContent");
		
		verify(modelMock).updateContent("NewContent");
	}
	
	@Test
	public void shouldDisplaySanitizedChangedContentInViewMode() {
		presenter = new TextAreaPresenter(viewMock, modelMock);
		when(modelMock.getContent()).thenReturn("New Sanitized Content");

		presenter.contentChanged("NewContent");
		
		verify(viewMock).showContent("New Sanitized Content");
	}
}
