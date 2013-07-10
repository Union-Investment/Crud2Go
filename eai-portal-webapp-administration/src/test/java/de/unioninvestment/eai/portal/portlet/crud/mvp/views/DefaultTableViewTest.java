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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import de.unioninvestment.eai.portal.portlet.crud.config.TableActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.datatypes.OtherDataType;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.AbstractDataContainerTest;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableAction;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.DomainSpringPortletContextTest;
import de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui.CrudTable;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class DefaultTableViewTest extends DomainSpringPortletContextTest {

	private DefaultTableView view;

	// @Mock
	// private ApplicationContext applicationContextMock;

	@Mock
	private Portlet portletMock;
	@Mock
	private TableView.Presenter presenterMock;
	@Mock
	private DataContainer databaseContainerMock;
	@Mock
	private Table modelTableMock;
	@Mock
	CrudTable crudTableMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

	@Mock
	private Styles stylesMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		liferayContext.initialize();
		when(liferayContext.getPageMock().getStyles()).thenReturn(stylesMock);

		initializeView();
	}

	private void initializeView() {
		view = new DefaultTableView();
		when(databaseContainerMock.findDisplayer(Mockito.anyString()))
				.thenReturn(new OtherDataType());
		view.initialize(presenterMock, databaseContainerMock, modelTableMock,
				10, 1D);
	}

	@Test
	public void shouldNotCommitDuringSelectItemForEditing() throws Exception {
		Set<Object> selection = Collections.singleton((Object) "uncommitted");

		// Simuliert das Feuern des Selection-Change-Events bei view.unselectAll
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				view.onSelectionChanged();
				return null;
			}
		}).when(crudTableMock).setValue(null);

		when(crudTableMock.getValue()).thenReturn(Collections.singleton("id"));
		when(crudTableMock.isEditable()).thenReturn(true);
		// Sorgt dafür, dass sich die View so verhält, als ob momentan ein nicht
		// committete Zeile editierbar ist
		view.updateUncommittedItemId(selection);

		// Wenn eine Zeile zum editieren ausgewählt wird mit suppressCommit =
		// true
		view.selectItemForEditing("id", true);

		// dann wird kein Commit ausgeführt
		verify(databaseContainerMock, never()).commit();
	}

	/**
	 * Regressionstest für Bug TableAction-Button-Zuordnung in EAI-873
	 */
	@Test
	public void shouldCallCorrectClosureWhenTableActionButtonIsClicked() {
		// Gegeben eine TableView mit einigen Actions,
		List<TableAction> actionList = new ArrayList<TableAction>();
		TableAction action1 = addTableAction(actionList, "1");
		TableAction action2 = addTableAction(actionList, "2");
		TableAction action3 = addTableAction(actionList, "3");
		when(modelTableMock.getActions()).thenReturn(actionList);
		initializeView();

		// und den dazugehörigen Buttons
		Map<String, Button> actionButtons = view.getActionButtons();

		// wenn Button 1 angeklickt wird
		clickButton("1", actionButtons);
		// wird die dazugehörige Aktion ausgelöst
		verify(presenterMock).callClosure(action1);

		// wenn Button 2 angeklickt wird
		clickButton("2", actionButtons);
		// wird die dazugehörige Aktion ausgelöst
		verify(presenterMock).callClosure(action2);

		// wenn Button 1 angeklickt wird
		clickButton("3", actionButtons);
		// wird die dazugehörige Aktion ausgelöst
		verify(presenterMock).callClosure(action3);
	}

	private TableAction addTableAction(List<TableAction> actionList, String id) {
		TableActionConfig tableActionConfig = new TableActionConfig();
		tableActionConfig.setId(id);
		TableAction action = new TableAction(portletMock, tableActionConfig,
				modelTableMock, null);
		actionList.add(action);
		return action;
	}

	private void clickButton(String id, Map<String, Button> actionButtons) {
		Button button1 = actionButtons.get(id);
		Button.ClickListener clickListener1 = (ClickListener) button1
				.getListeners(Button.ClickEvent.class).iterator().next();
		clickListener1.buttonClick(null);
	}

	@Test
	public void shouldReturnNameAsHeaderIfNoTitleIsGiven() {

		TableColumn col1 = new TableColumn.Builder().name("Name").build();
		TableColumns tableColumns = new TableColumns(asList(col1));
		when(modelTableMock.getColumns()).thenReturn(tableColumns);
		AbstractDataContainerTest.returnFollowingColumnType(
				databaseContainerMock, "Name", String.class);

		initializeView();

		assertThat(view.getTable().getColumnHeader("Name"), is("Name"));
	}

	@Test
	public void shouldReturnTitleAsHeaderIfExisting() {

		TableColumn col1 = new TableColumn.Builder().name("Name")
				.title("Title").build();
		TableColumns tableColumns = new TableColumns(asList(col1));
		when(modelTableMock.getColumns()).thenReturn(tableColumns);
		AbstractDataContainerTest.returnFollowingColumnType(
				databaseContainerMock, "Name", String.class);

		initializeView();

		assertThat(view.getTable().getColumnHeader("Name"), is("Title"));
	}

	@Test
	public void shouldAddLongTitleAsTooltipToHeader() {

		TableColumn col1 = new TableColumn.Builder().name("Name")
				.title("Title").longTitle("Long Title").build();
		TableColumns tableColumns = new TableColumns(asList(col1));
		when(modelTableMock.getColumns()).thenReturn(tableColumns);
		AbstractDataContainerTest.returnFollowingColumnType(
				databaseContainerMock, "Name", String.class);

		initializeView();

		assertThat(view.getTable().getColumnHeader("Name"),
				is("<span title=\"Long Title\">Title</span>"));
	}
}
