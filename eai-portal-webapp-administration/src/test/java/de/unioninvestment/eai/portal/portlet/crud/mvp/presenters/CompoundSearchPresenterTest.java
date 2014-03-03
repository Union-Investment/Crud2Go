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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.CompoundQueryChangedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.CompoundSearch;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.CompoundSearchView;

public class CompoundSearchPresenterTest {

	private CompoundSearchPresenter presenter;
	private Collection<String> searchableFields;
	private Collection<String> defaultFields;

	@Mock
	private CompoundSearchView viewMock;
	@Mock
	private CompoundSearch modelMock;
	@Mock
	private Query queryMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		searchableFields = asList("A", "B", "C");
		defaultFields = asList("A", "B");
		when(modelMock.getSearchableFields()).thenReturn(searchableFields);
		when(modelMock.getDefaultFields()).thenReturn(defaultFields);

		presenter = new CompoundSearchPresenter(viewMock, modelMock);
	}

	@Test
	public void shouldInitializeView() {
		verify(viewMock).setPresenter(presenter);
		verify(viewMock).initialize(searchableFields, defaultFields);
	}

	@Test
	public void shouldDelegateSearchToModel() {
		presenter.search(queryMock);
		verify(modelMock).search(queryMock);
	}

	@Test
	public void shouldUpdateViewOnQueryStringChange() {
		verify(modelMock).addQueryChangedEventHandler(presenter);
		expectSearchOnUpdateOfQueryStringInView();

		presenter
				.onQueryChange(new CompoundQueryChangedEvent(modelMock, "bla"));
		
		verify(viewMock).updateQueryString("bla");
		verify(modelMock, never()).search(Mockito.any(Query.class));
	}

	private void expectSearchOnUpdateOfQueryStringInView() {
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				presenter.search(queryMock);
				return null;
			}
		}).when(viewMock).updateQueryString("bla");
	}
}
