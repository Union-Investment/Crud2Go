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

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.ShowEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Page;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Tab;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.TableView;

public class AbstractTablePresenterTest {

	TablePresenter presenter;

	@Mock
	TableView viewMock;

	@Mock
	Table modelMock;

	@Mock
	Page pageMock;

	@Mock
	DataContainer containerMock;

	@Mock
	TableColumns tableColumns;

	@Mock
	List<TableAction> tableActionsMock;

	@Mock
	ShowEvent<Tab> eventMock;

	@Mock
	Tab tabMock;

	@Mock
	Item itemMock;

	@Mock
	Object itemIdMock;

	
	@Mock
	ContainerRowId containerRowIdMock;

	@Mock
	ContainerRow containerRowMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(modelMock.getContainer()).thenReturn(containerMock);
		when(modelMock.getPanel()).thenReturn(pageMock);
		when(modelMock.getRowHeight()).thenReturn(10);
		when(modelMock.getPageLength()).thenReturn(15);
		when(modelMock.getCacheRate()).thenReturn(2.0);
		when(modelMock.isSortingEnabled()).thenReturn(true);
		when(modelMock.getColumns()).thenReturn(tableColumns);
		when(modelMock.getActions()).thenReturn(tableActionsMock);
		
		when(containerRowMock.getId()).thenReturn(containerRowIdMock);
	}
}
