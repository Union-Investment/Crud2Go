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

import com.google.common.collect.ImmutableSet;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Ordered;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.*;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.ExportWithExportSettings;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer.FilterPolicy;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.*;
import de.unioninvestment.eai.portal.support.vaadin.filter.AdvancedStringFilter;
import de.unioninvestment.eai.portal.support.vaadin.filter.NothingFilter;
import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.sql.SQLTimeoutException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public abstract class AbstractDataContainerTest<C extends AbstractDataContainer, V extends Ordered> {

	@Captor
	private ArgumentCaptor<com.vaadin.data.Container.Filter> filterCaptor;

	@Mock
	protected CreateEventHandler createEventHandlerMock;

	@Mock
	protected BeforeCommitEventHandler beforeCommitEventHandlerMock;

	@Mock
	protected InsertEventHandler insertEventHandlerMock;

	@Mock
	protected UpdateEventHandler updateEventHandlerMock;

	@Mock
	protected DeleteEventHandler deleteEventHandlerMock;

	@Mock
	protected CommitEventHandler commitEventHandlerMock;

	protected List<EditorSupport> editors = new ArrayList<EditorSupport>();

	protected C container;

	protected V vaadinContainerMock;

	@Mock
	protected HashMap<String, String> displayPatternMock;

	protected HashMap<String, String> displayPattern = new HashMap<String, String>();

	@Mock
	protected EditorSupport editorSupportMock;

	@Mock
	private ExportWithExportSettings exportMock;

    @Mock
    private ContainerRowId rowIdMock1, rowIdMock2;
    @Mock
    private Item itemMock1, itemMock2;
    @Mock
    private ContainerRow rowMock1, rowMock2;
    @Mock
    private DataContainer.EachRowCallback eachRowCallbackMock;
    @Mock
    private EventBus eventBusMock;

	@Rule
	public LiferayContext liferayContext = new LiferayContext();

    @Before
	public final void setUp() {
		MockitoAnnotations.initMocks(this);
		editors.add(editorSupportMock);

		vaadinContainerMock = createVaadinContainer();

		container = createDataContainer();

		container.setEditors(editors);

	}

	/**
	 * die zurückgelieferte Implementierung muss sowohl das {@code Container},
	 * als auch das {@code Filterable} Interface implementieren. Wenn es sich um
	 * ein Mock handelt, könnte es wie folgt aussehen:
	 * 
	 * <pre>
	 * Mockito.mock(Container.class,
	 * 		Mockito.withSettings().extraInterfaces(Filterable.class))
	 * </pre>
	 * 
	 * @return
	 */
	public abstract C createDataContainer();

	public abstract V createVaadinContainer();

	@Test
	public void shouldRegisterOnCreateEvents() {
		container.addCreateEventHandler(createEventHandlerMock);

		assertTrue(container.getOnCreateEventRouter().contains(
				createEventHandlerMock));
	}

	@Test
	public void shouldRegisterBeforeCommitEvents() {
		container.addBeforeCommitEventHandler(beforeCommitEventHandlerMock);

		assertTrue(container.getBeforeCommitEventRouter().contains(
				beforeCommitEventHandlerMock));
	}

	@Test
	public void shouldRegisterOnInsertEvents() {
		container.addInsertEventHandler(insertEventHandlerMock);

		assertTrue(container.getOnInsertEventRouter().contains(
				insertEventHandlerMock));
	}

	@Test
	public void shouldRegisterOnUpdateEvents() {
		container.addUpdateEventHandler(updateEventHandlerMock);

		assertTrue(container.getOnUpdateEventRouter().contains(
				updateEventHandlerMock));
	}

	@Test
	public void shouldRegisterOnDeleteEvents() {
		container.addDeleteEventHandler(deleteEventHandlerMock);

		assertTrue(container.getOnDeleteEventRouter().contains(
				deleteEventHandlerMock));
	}

	@Test
	public void shouldRegisterOnCommitEvents() {
		container.addCommitEventHandler(commitEventHandlerMock);

		assertTrue(container.getOnCommitEventRouter().contains(
				commitEventHandlerMock));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailWithoutTransactionOnAddRow() {
		container.addRow();
	}

	@Test
	public void shouldReturnContainerType() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType("test")).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation)
							throws Throwable {
						return Integer.class;
					}
				});

		Class<?> type = container.getType("test");

		assertTrue(type.isAssignableFrom(Integer.class));
	}

	@Test
	public void shouldNotRemoveDurableFilters() {
		container.setVaadinContainer(vaadinContainerMock);

		Filter equalFilter1 = new Equal("a", "b");
		Filter equalFilter2 = new Equal("c", "d", true);
		Filter equalFilter3 = new Equal("e", "f");

		container.addFilters(asList(equalFilter1, equalFilter2, equalFilter3));

		container.removeAllFilters();

		List<Filter> filterList = container.getFilterList();

		assertThat(filterList.size(), is(1));
		assertThat(filterList.contains(equalFilter2), is(true));

		verify((Filterable) vaadinContainerMock, times(2))
				.replaceContainerFilter(
						any(com.vaadin.data.Container.Filter.class));
	}

	@Test
	public void shouldReplaceFilters() {
		container.setVaadinContainer(vaadinContainerMock);

		List<Filter> newFilters = asList((Filter) new Equal("MYCOL", "MYVALUE"));
		com.vaadin.data.Container.Filter newVaadinFilter = container
				.buildVaadinFilter(new All(newFilters));
		container.replaceFilters(newFilters, true);
		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				newVaadinFilter);
	}

	@Test
	public void shouldRemoveAllFilters() {
		container.setVaadinContainer(vaadinContainerMock);

		Filter equalFilter1 = new Equal("a", "b");
		Filter equalFilter2 = new Equal("c", "d", true);
		Filter equalFilter3 = new Equal("e", "f");

		container.addFilters(asList(equalFilter1, equalFilter2, equalFilter3));

		container.removeAllFilters(true);

		List<Filter> filterList = container.getFilterList();

		assertThat(filterList.size(), is(0));

		verify((Filterable) vaadinContainerMock).removeAllContainerFilters();
	}

	@Test
	public void shouldAddEqualFilter() {
		shouldAddFilterType(new Equal("ID", 1), Compare.Equal.class);
	}

	@Test
	public void shouldAddGreaterFilter() {
		shouldAddFilterType(new Greater("ID", 1, false), Compare.Greater.class);
	}

	@Test
	public void shouldAddGreaterOrEqualFilter() {
		shouldAddFilterType(new Greater("ID", 1, true),
				Compare.GreaterOrEqual.class);
	}

	@Test
	public void shouldAddLessFilter() {
		shouldAddFilterType(new Less("ID", 1, false), Compare.Less.class);
	}

	@Test
	public void shouldAddLessOrEqualFilter() {
		shouldAddFilterType(new Less("ID", 1, true), Compare.LessOrEqual.class);
	}

	@Test
	public void shouldAddNothingFilter() {
		shouldAddFilterType(new Nothing(), NothingFilter.class);
	}

	@Test
	public void shouldAddIsNullFilter() {
		com.vaadin.data.util.filter.IsNull isNull = shouldAddFilterType(new IsNull("ID"), com.vaadin.data.util.filter.IsNull.class);
		assertThat(isNull.getPropertyId(), is((Object)"ID"));
	}

	@Test
	public void shouldAddWildcardFilter() {
		Like likeFilter = shouldAddFilterType(new Wildcard("ID", "Te*t", false), Like.class);
		assertThat(likeFilter.getValue(), is("Te%t"));
	}

	@Test
	public void shouldAddAnyFilter() {
		shouldAddFilterType(
				new Any(Arrays.asList(new Filter[] {
						new Contains("NAME", "abcdef", false),
						new Equal("ID", 1) })), Or.class);
	}

	@Test
	public void shouldAddNotFilterWithAllSubfilter() {
		container.setVaadinContainer(vaadinContainerMock);
		Not filter = new Not(Arrays.asList(new Filter[] {
				new Contains("NAME", "abcdef", false), new Equal("ID", 1) }));

		container.addFilters(asList((Filter) filter));

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				filterCaptor.capture());
		assertThat(container.getFilterList(), hasItems((Filter) filter));
		assertThat(filterCaptor.getValue(),
				instanceOf(com.vaadin.data.util.filter.Not.class));

		And subfilter = (And) ((com.vaadin.data.util.filter.Not) filterCaptor
				.getValue()).getFilter();
		Iterator<com.vaadin.data.Container.Filter> andIterator = subfilter
				.getFilters().iterator();
		assertThat(
				andIterator.next(),
				anyOf(instanceOf(AdvancedStringFilter.class),
						instanceOf(Like.class)));
		assertThat(andIterator.next(),
				instanceOf(com.vaadin.data.util.filter.Compare.Equal.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddFilter() {
		container.setVaadinContainer(vaadinContainerMock);
		container.addFilters(asList((Filter) new Filter(false) {
			private static final long serialVersionUID = 1L;
			//
		}));
	}

	@Test
	public void shouldAddNotFilterWithSingleSubfilter() {
		container.setVaadinContainer(vaadinContainerMock);
		Filter filter = new Not(Arrays.asList(new Filter[] { new Contains(
				"NAME", "abc%def", false) }));

		container.addFilters(asList(filter));

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				filterCaptor.capture());
		assertThat(container.getFilterList(), hasItems((Filter) filter));
		assertThat(filterCaptor.getValue(),
				instanceOf(com.vaadin.data.util.filter.Not.class));

		assertThat(
				((com.vaadin.data.util.filter.Not) filterCaptor.getValue())
						.getFilter(),
				anyOf(instanceOf(AdvancedStringFilter.class),
						instanceOf(Like.class)));
	}

	@Test
	public void shouldAddAllFilter() {
		container.setVaadinContainer(vaadinContainerMock);
		shouldAddFilterType(
				new All(Arrays.asList(new Filter[] {
						new Contains("NAME", "abc%def", false),
						new Equal("ID", 1) })), And.class);
	}

	@Test
	public void shouldNotAddEmptyAllFilter() {
		shouldNotAddFilterType(new All(new ArrayList<Filter>()));
	}

	@Test
	public void shouldNotAddEmptyAnyFilter() {
		shouldNotAddFilterType(new Any(new ArrayList<Filter>()));
	}

	@Test
	public void shouldNotAddEmptyNotFilter() {
		shouldNotAddFilterType(new Not(null));
	}

	@Test
	public void shouldGetCustomNumberFormat() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType(anyString())).thenAnswer(
				new Answer<Class<BigDecimal>>() {
					@Override
					public Class<BigDecimal> answer(InvocationOnMock invocation)
							throws Throwable {
						return BigDecimal.class;
					}
				});
		when(displayPatternMock.get(anyString())).thenReturn("#,##0.00");

		NumberFormat format = (NumberFormat) container.getFormat("testCol");
		assertThat(format.format(123123l), is("123,123.00"));
	}

	@Test
	public void shouldGetCustomDateFormat() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType(anyString())).thenAnswer(
				new Answer<Class<Date>>() {
					@Override
					public Class<Date> answer(InvocationOnMock invocation)
							throws Throwable {
						return Date.class;
					}
				});
		when(displayPatternMock.get(anyString()))
				.thenReturn("dd.MM.yyyy HH:mm");

		DateFormat format = (DateFormat) container.getFormat("testCol");
		assertThat(format.format(new GregorianCalendar(2013, 5, 25, 8, 31)
				.getTime()), is("25.06.2013 08:31"));
	}

	@Test
	public void shouldGetNullFormatByWrongType() {
		container.setVaadinContainer(vaadinContainerMock);
		when(vaadinContainerMock.getType(anyString())).thenAnswer(
				new Answer<Class<String>>() {
					@Override
					public Class<String> answer(InvocationOnMock invocation)
							throws Throwable {
						return String.class;
					}
				});
		when(displayPatternMock.get(anyString())).thenReturn("#,##0.00");

		NumberFormat format = (NumberFormat) container.getFormat("testCol");
		assertThat(format, nullValue());
	}

	private void shouldNotAddFilterType(Filter filter) {
		container.setVaadinContainer(vaadinContainerMock);
		container.addFilters(asList(filter));

		verify((Filterable) vaadinContainerMock, never()).addContainerFilter(
				any(com.vaadin.data.Container.Filter.class));
	}

	@SuppressWarnings("unchecked")
	protected <T extends com.vaadin.data.Container.Filter> T shouldAddFilterType(Filter filter,
			Class<T> vaadinClass) {
		container.setVaadinContainer(vaadinContainerMock);
		container.addFilters(asList(filter));

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				filterCaptor.capture());
		assertThat(container.getFilterList(), hasItems((Filter) filter));
		com.vaadin.data.Container.Filter vaadinFilter = filterCaptor.getValue();
		assertThat(vaadinFilter, instanceOf(vaadinClass));
		return (T) vaadinFilter;
	}

	public static void returnFollowingColumnType(DataContainer containerMock,
			String columnName, final Class<?> columnType) {
		when(containerMock.getType(columnName)).thenAnswer(
				new Answer<Class<Object>>() {
					@SuppressWarnings("unchecked")
					@Override
					public Class<Object> answer(InvocationOnMock invocation)
							throws Throwable {
						return (Class<Object>) columnType;
					}
				});
	}

	@Test
	public void addFilterShouldApplyNothingFilterIfTheNewFilterlistOnlyContainsDurableFilters() {
		container.setVaadinContainer(vaadinContainerMock);
		container.setFilterPolicy(FilterPolicy.NOTHING_AT_ALL);
		Filter equalFilter1 = new Equal("a", "b", true);

		container.addFilters(asList(equalFilter1));

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				isA(NothingFilter.class));
	}

	@Test
	public void replaceFilterShouldApplyNothingFilterIfTheNewFilterlistIsDurable() {
		container.setVaadinContainer(vaadinContainerMock);
		container.setFilterPolicy(FilterPolicy.NOTHING_AT_ALL);
		Filter equalFilter1 = new Equal("a", "b", true);

		container.replaceFilters(asList(equalFilter1), true);

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				isA(NothingFilter.class));
	}

	@Test
	public void replaceFilterShouldApplyNothingFilterIfTheNewFilterlistIsEmpty() {
		container.setVaadinContainer(vaadinContainerMock);
		container.setFilterPolicy(FilterPolicy.NOTHING_AT_ALL);
		Filter nonDurableFilter = new Equal("a", "b", false);
		container.addFilters(asList(nonDurableFilter));

		container.replaceFilters(new LinkedList<Filter>(), true);

		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				isA(Compare.Equal.class));
		verify((Filterable) vaadinContainerMock).replaceContainerFilter(
				isA(NothingFilter.class));
	}

	@Test
	public void replaceFilterShouldApplyNothingFilterOnTimeoutException() {
		container.setVaadinContainer(vaadinContainerMock);
		Filter nonDurableFilter = new Equal("a", "b", false);
		container.addFilters(asList(nonDurableFilter));
		doThrow(new RuntimeException(new SQLTimeoutException("Test"))).when(
				((Filterable) vaadinContainerMock)).removeAllContainerFilters();

		try {
			container.replaceFilters(new LinkedList<Filter>(), true);
			fail("Should throw timeout exception");

		} catch (RuntimeException e) {
			assertThat(container.getFilterList(),
					is(asList((Filter) new Nothing())));
		}
	}

	@Test
	public void addFilterShouldReApplyExistingDurableFiltersIfANonDurableFilterIsAdded() {
		container.setVaadinContainer(vaadinContainerMock);
		container.setFilterPolicy(FilterPolicy.NOTHING_AT_ALL);
		Filter durableFilter = new Equal("a", "b", true);
		Filter nonDurableFilter = new Equal("a", "b", false);
		container.addFilters(asList(durableFilter));

		container.addFilters(asList(nonDurableFilter));

		verify((Filterable) vaadinContainerMock, times(2))
				.replaceContainerFilter(filterCaptor.capture());
		assertThat(filterCaptor.getAllValues().get(0),
				instanceOf(NothingFilter.class));
		com.vaadin.data.Container.Filter allFilter = filterCaptor
				.getAllValues().get(1);
		assertThat(allFilter, instanceOf(And.class));
		Collection<com.vaadin.data.Container.Filter> subFilters = ((And) allFilter)
				.getFilters();
		assertThat(subFilters.size(), is(2));
	}

	@Test
	public void shouldCallExportCallbackWithExportSettings() {
		container.withExportSettings(exportMock);
		verify(exportMock).export();
	}

    @Test
    public void shouldIterateOverImmutableRowsByIds() {
        AbstractDataContainer containerMock = createPartialContainerMock();

        when(rowIdMock1.getInternalId()).thenReturn(1);
        when(rowIdMock2.getInternalId()).thenReturn(2);
        // spy stubbing
        doReturn(rowIdMock1).when(containerMock).convertInternalRowId(1);
        doReturn(rowIdMock2).when(containerMock).convertInternalRowId(2);
        doReturn(rowMock1).when(containerMock).getRow(rowIdMock1, false, true);
        doReturn(rowMock2).when(containerMock).getRow(rowIdMock2, false, true);

        containerMock.eachImmutableRow(ImmutableSet.of(rowIdMock1, rowIdMock2), eachRowCallbackMock);

        verify(eachRowCallbackMock).doWithRow(rowMock1);
        verify(eachRowCallbackMock).doWithRow(rowMock2);
    }

    @Test
    public void shouldIterateOverImmutableRowsByIdsIgnoringMissingRows() {
        AbstractDataContainer containerMock = createPartialContainerMock();

        when(rowIdMock1.getInternalId()).thenReturn(1);
        when(rowIdMock2.getInternalId()).thenReturn(2);
        // spy stubbing
        doReturn(rowIdMock1).when(containerMock).convertInternalRowId(1);
        doReturn(rowIdMock2).when(containerMock).convertInternalRowId(2);
        doReturn(rowMock1).when(containerMock).getRow(rowIdMock1, false, true);
        doReturn(null).when(containerMock).getRow(rowIdMock2, false, true);

        containerMock.eachImmutableRow(ImmutableSet.of(rowIdMock1, rowIdMock2), eachRowCallbackMock);

        verify(eachRowCallbackMock).doWithRow(rowMock1);
        verifyNoMoreInteractions(eachRowCallbackMock);
    }

    @Test
    public void shouldIterateOverRowsByIdsInsideATransaction() {
        AbstractDataContainer containerMock = createPartialContainerMock();

        when(rowIdMock1.getInternalId()).thenReturn(1);
        when(rowIdMock2.getInternalId()).thenReturn(2);
        // spy stubbing
        doReturn(rowIdMock1).when(containerMock).convertInternalRowId(1);
        doReturn(rowIdMock2).when(containerMock).convertInternalRowId(2);
        doReturn(rowMock1).when(containerMock).getRow(rowIdMock1, true, false);
        doReturn(rowMock2).when(containerMock).getRow(rowIdMock2, true, false);

        containerMock.eachRow(ImmutableSet.of(rowIdMock1, rowIdMock2), eachRowCallbackMock);

        InOrder inOrder = inOrder(containerMock, eachRowCallbackMock);

        inOrder.verify(containerMock).commit();
        inOrder.verify(eachRowCallbackMock).doWithRow(rowMock1);
        inOrder.verify(eachRowCallbackMock).doWithRow(rowMock2);
        inOrder.verify(containerMock).commit();
    }

    @Test
    public void shouldIterateOverRowsByIdsIgnoringMissingRows() {
        AbstractDataContainer containerMock = createPartialContainerMock();

        when(rowIdMock1.getInternalId()).thenReturn(1);
        when(rowIdMock2.getInternalId()).thenReturn(2);
        // spy stubbing
        doReturn(rowIdMock1).when(containerMock).convertInternalRowId(1);
        doReturn(rowIdMock2).when(containerMock).convertInternalRowId(2);
        doReturn(rowMock1).when(containerMock).getRow(rowIdMock1, true, false);
        doReturn(null).when(containerMock).getRow(rowIdMock2, true, false);

        containerMock.eachRow(ImmutableSet.of(rowIdMock1, rowIdMock2), eachRowCallbackMock);

        InOrder inOrder = inOrder(containerMock, eachRowCallbackMock);

        inOrder.verify(containerMock).commit();
        inOrder.verify(eachRowCallbackMock).doWithRow(rowMock1);
        inOrder.verify(containerMock).commit();
    }

    private AbstractDataContainer createPartialContainerMock() {
        AbstractDataContainer containerMock = spy(new AbstractDataContainer(eventBusMock, null, null, null) {

            @Override
            protected Container createVaadinContainer() {
                return null;
            }

            @Override
            public boolean isInsertable() {
                return false;
            }

            @Override
            public boolean isUpdateable() {
                return false;
            }

            @Override
            public boolean isDeleteable() {
                return false;
            }

            @Override
            public Class<?> getType(String name) {
                return null;
            }

            @Override
            public void rollback() {

            }

            @Override
            public void commit() {

            }

            @Override
            public List<String> getPrimaryKeyColumns() {
                return null;
            }

            @Override
            public List<String> getColumns() {
                return null;
            }

            @Override
            public ContainerRow convertItemToRow(Item item, boolean transactional, boolean immutable) {
                return null;
            }

            @Override
            public ContainerRowId convertInternalRowId(Object internalId) {
                return null;
            }

            @Override
            public void refresh() {

            }

            @Override
            public ContainerClob getCLob(ContainerRowId rowId, String columnName) {
                return null;
            }

            @Override
            public boolean isCLob(String columnName) {
                return false;
            }

            @Override
            public ContainerBlob getBLob(ContainerRowId rowId, String columnName) {
                return null;
            }

            @Override
            public boolean isBLobEmpty(ContainerRowId rowId, String columnName) {
                return false;
            }

            @Override
            public boolean isBLob(String columnName) {
                return false;
            }
        });
        containerMock.setVaadinContainer(vaadinContainerMock);
        return containerMock;
    }

}
