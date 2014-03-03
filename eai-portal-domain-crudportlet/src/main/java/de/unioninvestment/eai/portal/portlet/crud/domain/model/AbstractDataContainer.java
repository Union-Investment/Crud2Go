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

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;

import java.sql.SQLTimeoutException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.number.NumberFormatter;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.Ordered;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;

import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport;
import de.unioninvestment.eai.portal.portlet.crud.domain.container.VaadinContainerDataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.BeforeCommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.BeforeCommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CommitEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CreateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.DeleteEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.InsertEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.PortletRefreshedEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.UpdateEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.All;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Any;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Contains;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.EndsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Equal;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Greater;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.IsNull;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Less;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Not;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Nothing;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.StartsWith;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Wildcard;
import de.unioninvestment.eai.portal.support.vaadin.context.Context;
import de.unioninvestment.eai.portal.support.vaadin.filter.AdvancedStringFilter;
import de.unioninvestment.eai.portal.support.vaadin.filter.NothingFilter;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport;

/**
 * Repräsentation eines Vaadin Containers als Backend für die Tabellenansicht.
 * 
 * @author siva.selvarajah
 */
@Configurable
public abstract class AbstractDataContainer implements DataContainer,
		PortletRefreshedEventHandler {

	private static final long serialVersionUID = 1L;

	private EventRouter<CreateEventHandler, CreateEvent> onCreateEventRouter = new EventRouter<CreateEventHandler, CreateEvent>();
	private EventRouter<InsertEventHandler, InsertEvent> onInsertEventRouter = new EventRouter<InsertEventHandler, InsertEvent>();
	private EventRouter<UpdateEventHandler, UpdateEvent> onUpdateEventRouter = new EventRouter<UpdateEventHandler, UpdateEvent>();
	private EventRouter<DeleteEventHandler, DeleteEvent> onDeleteEventRouter = new EventRouter<DeleteEventHandler, DeleteEvent>();
	private EventRouter<BeforeCommitEventHandler, BeforeCommitEvent> beforeCommitEventRouter = new EventRouter<BeforeCommitEventHandler, BeforeCommitEvent>();
	private EventRouter<CommitEventHandler, CommitEvent> onCommitEventRouter = new EventRouter<CommitEventHandler, CommitEvent>();

	private final Object lock = new Object();

	private List<Filter> filterList = new ArrayList<Filter>();

	private List<ContainerOrder> defaultOrder;

	private ThreadLocal<Boolean> isInTransaction = new ThreadLocal<Boolean>();

	@Resource(name = "editorSupport")
	protected transient List<EditorSupport> editors;

	@Resource(name = "displaySupport")
	protected transient List<DisplaySupport> displayers;

	protected Map<String, String> displayPattern;

	protected Container container;

	protected Map<ContainerRowId, Map<String, ContainerClob>> clobFields;

	protected Map<ContainerRowId, Map<String, ContainerBlob>> blobFields;

	private FilterPolicy filterPolicy;

	/**
	 * @param eventBus
	 *            der Event-Bus
	 * @param displayPattern
	 *            die Anzeige-Patterns je Spalte
	 * @param defaultOrder
	 *            die Default-Sortierung
	 * @param filterPolicy
	 *            das gewünschte Filter-Verhalten
	 */
	public AbstractDataContainer(EventBus eventBus,
			Map<String, String> displayPattern,
			List<ContainerOrder> defaultOrder, FilterPolicy filterPolicy) {
		this.displayPattern = displayPattern;
		this.defaultOrder = defaultOrder;
		this.filterPolicy = filterPolicy;
		clobFields = new HashMap<ContainerRowId, Map<String, ContainerClob>>();
		blobFields = new HashMap<ContainerRowId, Map<String, ContainerBlob>>();
		eventBus.addHandler(PortletRefreshedEvent.class, this);
	}

	/**
	 * 
	 * Erzeugt ein Container.
	 * 
	 * @return Container
	 */
	protected abstract Container createVaadinContainer();

	@Override
	public EditorSupport findEditor(String columnName) {
		Class<?> propertyType = getType(columnName);

		for (EditorSupport editor : editors) {
			if (editor.supportsEditing(propertyType)) {
				return editor;
			}
		}
		return null;
	}

	@Override
	public DisplaySupport findDisplayer(String columnName) {
		Class<?> propertyType = getType(columnName);
		for (DisplaySupport displayer : displayers) {
			if (displayer.supportsDisplaying(propertyType)) {
				return displayer;
			}
		}
		return null;
	}

	public void setEditors(List<EditorSupport> editors) {
		this.editors = editors;
	}

	/**
	 * Erzeugt beim ersten Aufruf eine neue {@link Container} Instanz auf Basis
	 * der im Constructor übergebenen Tabellendaten.
	 * 
	 * @return den zum Objekt zugehörigen {@link Container}
	 */
	protected Container getVaadinContainer() {
		synchronized (lock) {
			if (container == null) {
				container = createVaadinContainer();
				applyNothingFilterForFilterPoliciesNothing();
				applyDefaultOrder();
			}
			return container;
		}
	}

	private void applyNothingFilterForFilterPoliciesNothing() {
		if (filterPolicy == FilterPolicy.NOTHING
				|| filterPolicy == FilterPolicy.NOTHING_AT_ALL) {
			addVaadinFilters(asList((Filter) new Nothing()));
		}
	}

	/**
	 * Setzt eine Default-Sortierung aus den Angaben der Konfiguration.
	 */
	public void applyDefaultOrder() {
		if (defaultOrder != null && !defaultOrder.isEmpty()) {
			if (!(container instanceof Container.Sortable)) {
				throw new TechnicalCrudPortletException(
						"Error applying default order to container. Container is not sortable");
			} else {
				List<String> orderCols = new ArrayList<String>();
				List<Boolean> asc = new ArrayList<Boolean>();
				for (ContainerOrder order : defaultOrder) {
					orderCols.add(order.getColumnName());
					asc.add(order.isAscending());
				}
				boolean[] ascending = ArrayUtils.toPrimitive(asc
						.toArray(new Boolean[asc.size()]));

				((Container.Sortable) container).sort(orderCols.toArray(),
						ascending);
			}
		}
	}

	@Override
	public Container getDataSourceContainer() {
		return getVaadinContainer();
	}

	@Override
	public Format getFormat(String name) {
		String displayFormatPattern = displayPattern.get(name);
		if (displayFormatPattern == null) {
			return null;
		}

		Class<?> type = getType(name);
		Locale locale;

		if (Context.getLocale() != null) {
			locale = Context.getLocale();
		} else {
			locale = Locale.US;
		}

		if (Number.class.isAssignableFrom(type)) {
			return new NumberFormatter(displayFormatPattern)
					.getNumberFormat(locale);
		} else if (Date.class.isAssignableFrom(type)) {
			return new SimpleDateFormat(displayFormatPattern, locale);
		} else {
			return null;
		}

	}

	@Override
	public void removeAllFilters() {
		removeAllFilters(false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void removeAllFilters(boolean removeDurable) {
		replaceFilters(EMPTY_LIST, removeDurable);
	}

	private List<Filter> collectDurableFilters(List<Filter> filters) {
		List<Filter> durableFilters = new LinkedList<Filter>();
		for (Filter filter : filters) {
			if (filter.isDurable()) {
				durableFilters.add(filter);
			}
		}
		return durableFilters;
	}

	@Override
	public void replaceFilters(List<Filter> newFilters, boolean removeDurable) {
		replaceFilters(newFilters, removeDurable, false);
	}

	private void replaceFilters(List<Filter> newFilters, boolean removeDurable,
			boolean forceReplace) {
		List<Filter> replacementFilters = newFilters;
		if (!removeDurable) {
			replacementFilters = collectDurableFilters(filterList);
			replacementFilters.addAll(newFilters);
		}
		if (forceReplace || !replacementFilters.equals(filterList)) {
			if (shouldFilterNothing(replacementFilters)) {
				replaceVaadinFilters(asList((Filter) new Nothing()));
			} else {
				try {
					replaceVaadinFilters(replacementFilters);
				} catch (RuntimeException e) {
					if (e.getCause() instanceof SQLTimeoutException) {
						applyNothingFilter(removeDurable);
						throw new BusinessException(
								"portlet.crud.warn.searchQueryTimeout");
					} else {
						throw e;
					}
				}
			}
			filterList = replacementFilters;
		}
	}

	private void applyNothingFilter(boolean removeDurable) {
		replaceFilters(Arrays.asList(new Filter[] { new Nothing() }),
				removeDurable, true);
	}

	@Override
	public void replaceFilters(List<Filter> newFilters, boolean removeDurable,
			int timeout) {
		throw new UnsupportedOperationException("Timeout not supported!");
	}

	@Override
	public void addFilters(List<Filter> addedFilters) {
		LinkedList<Filter> replacementList = new LinkedList<Filter>(filterList);
		replacementList.addAll(addedFilters);
		replaceFilters(replacementList, true);
	}

	private boolean shouldFilterNothing(List<Filter> replacementFilters) {
		return filterPolicy == FilterPolicy.NOTHING_AT_ALL
				&& hasOnlyDurableOrNoFilters(replacementFilters);
	}

	private boolean hasOnlyDurableOrNoFilters(List<Filter> filters) {
		if (filters.size() == 0) {
			return true;
		} else if (filters.size() == collectDurableFilters(filters).size()) {
			return true;
		}
		return false;
	}

	private void replaceVaadinFilters(List<Filter> newFilters) {
		Container c = getVaadinContainer();

		if (!(c instanceof Filterable)) {
			throw new UnsupportedOperationException(
					"Container unterstützt keine Filter.");
		}

		Filterable filterable = (Filterable) c;
		if (newFilters.size() > 0) {
			com.vaadin.data.Container.Filter vaadinFilter = buildVaadinFilter(new All(
					newFilters));
			if (vaadinFilter != null) {
				filterable.replaceContainerFilter(vaadinFilter);
			} else {
				filterable.removeAllContainerFilters();
			}
		} else {
			filterable.removeAllContainerFilters();
		}
	}

	private void addVaadinFilters(List<Filter> addedFilters) {
		Container c = getVaadinContainer();

		if (!(c instanceof Filterable)) {
			throw new UnsupportedOperationException(
					"Container unterstützt keine Filter.");
		}
		Filterable filterable = (Filterable) c;
		if (addedFilters.size() > 0) {
			com.vaadin.data.Container.Filter vaadinFilter = buildVaadinFilter(new All(
					addedFilters));
			if (vaadinFilter != null) {
				filterable.addContainerFilter(vaadinFilter);
			}
		}
	}

	public List<Filter> getFilterList() {
		return Collections.unmodifiableList(filterList);
	}

	@Override
	public List<ContainerRowId> getRowIds() {
		List<ContainerRowId> result = new ArrayList<ContainerRowId>();
		for (Object o : getVaadinContainer().getItemIds()) {
			result.add(convertInternalRowId(o));
		}
		return result;
	}

	@Override
	public ContainerRowId previousRowId(ContainerRowId currentRowId) {
		Object itemId = currentRowId.getInternalId();
		Object prevItemId = ((Indexed) getVaadinContainer()).prevItemId(itemId);
		return prevItemId == null ? null : convertInternalRowId(prevItemId);
	}

	@Override
	public ContainerRowId nextRowId(ContainerRowId currentRowId) {
		Object itemId = currentRowId.getInternalId();
		Object nextItemId = ((Indexed) getVaadinContainer()).nextItemId(itemId);
		return nextItemId == null ? null : convertInternalRowId(nextItemId);
	}

	@Override
	public void removeAllRows() {
		withTransaction(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				getVaadinContainer().removeAllItems();
				return null;
			}
		});
	}

	@Override
	public void removeRows(final Set<ContainerRowId> ids) {
		withTransaction(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				for (ContainerRowId rowId : ids) {
					getVaadinContainer().removeItem(rowId.getInternalId());
				}
				return null;
			}
		});
	}

	@Override
	public ContainerRow getRow(ContainerRowId rowId, boolean transactional,
			boolean immutable) {
		Item item = getVaadinContainer().getItem(rowId.getInternalId());
		if (item == null) {
			return null;
		}
		return convertItemToRow(item, transactional, immutable);
	}

	@Override
	public ContainerRow getCachedRow(ContainerRowId rowId,
			boolean transactional, boolean immutable) {

		Container c = getVaadinContainer();

		if (!(c instanceof Filterable)) {
			throw new UnsupportedOperationException(
					"Container unterstützt keine Filter.");
		}

		Filterable filterable = (Filterable) c;

		Item item = filterable.getItemUnfiltered(rowId.getInternalId());
		if (item == null) {
			return null;
		}
		return convertItemToRow(item, transactional, immutable);
	}

	@Override
	public void eachRow(final Set<ContainerRowId> ids,
			final EachRowCallback eachRowCallback) {
		withTransaction(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				for (ContainerRowId rowId : ids) {
					Item item = getVaadinContainer().getItem(
							rowId.getInternalId());
					eachRowCallback.doWithRow(convertItemToRow(item, true,
							false));
				}
				return null;
			}
		});
	}

	@Override
	public DataStream getStream() {
		return new VaadinContainerDataStream((Ordered) getVaadinContainer());
	}

	@Override
	public void eachRow(final EachRowCallback eachRowCallback) {
		withTransaction(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction() {
				Ordered container = (Ordered) getVaadinContainer();
				Object itemId = container.firstItemId();
				while (itemId != null) {
					Item item = container.getItem(itemId);
					eachRowCallback.doWithRow(convertItemToRow(item, true,
							false));
					itemId = container.nextItemId(itemId);
				}
				return null;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer#addRow()
	 */
	@Override
	public ContainerRow addRow() {
		return withExistingTransaction(new TransactionCallback<ContainerRow>() {
			@Override
			public ContainerRow doInTransaction() {
				Object rowId = getVaadinContainer().addItem();
				Item item = getVaadinContainer().getItem(rowId);
				return convertItemToRow(item, true, false);
			}
		});
	}

	@Override
	public <R> R withTransaction(TransactionCallback<R> callback) {

		if (isInTransaction.get() == null || !isInTransaction.get()) {
			isInTransaction.set(Boolean.TRUE);

			// commit existing user transaction
			commit();

			try {
				R result = callback.doInTransaction();
				commit();

				return result;

			} catch (RuntimeException e) {
				rollback();
				throw e;

			} finally {
				isInTransaction.remove();
			}
		} else {
			return callback.doInTransaction();
		}
	}

	@Override
	public <T> T withExistingTransaction(TransactionCallback<T> callback) {
		if (isInTransaction.get() != null && isInTransaction.get()) {
			return callback.doInTransaction();
		} else {
			throw new IllegalStateException(
					"Expected existing container transaction");
		}
	}

	/**
	 * Erstellt aus den Filtern der Container-API Filter der Vaadin-API.
	 * 
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	com.vaadin.data.Container.Filter buildVaadinFilter(Filter filter) {
		if (filter instanceof Equal) {
			return buildEqualFilter(filter);

		} else if (filter instanceof Less) {
			return buildLessFilter(filter);

		} else if (filter instanceof Greater) {
			return buildGreaterFilter(filter);

		} else if (filter instanceof Any) {
			return buildAnyFilter(filter);

		} else if (filter instanceof All) {
			return buildAllFilter((All) filter);

		} else if (filter instanceof Not) {
			return buildNotFilter((Not) filter);

		} else if (filter instanceof StartsWith) {
			return buildStartsWithFilter(filter);

		} else if (filter instanceof EndsWith) {
			return buildEndsWithFilter(filter);

		} else if (filter instanceof Contains) {
			return buildContainsFilter(filter);

		} else if (filter instanceof Wildcard) {
			return buildWildcardFilter(filter);
			
		} else if (filter instanceof IsNull) {
			return buildIsNullFilter(filter);

		} else if (filter instanceof Nothing) {
			return buildNothingFilter(filter);
		}
		throw new IllegalArgumentException("Not supported Filtertype: "
				+ filter.getClass().getName());
	}

	/**
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	protected com.vaadin.data.Container.Filter buildIsNullFilter(Filter filter) {
		IsNull isNull = (IsNull) filter;
		return new com.vaadin.data.util.filter.IsNull(isNull.getColumn());
	}

	/**
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	protected com.vaadin.data.Container.Filter buildNothingFilter(Filter filter) {
		return new NothingFilter();
	}

	/**
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	protected com.vaadin.data.Container.Filter buildContainsFilter(Filter filter) {
		Contains containerFilter = (Contains) filter;
		return new AdvancedStringFilter(containerFilter.getColumn(),
				(String) containerFilter.getValue(),
				!containerFilter.isCaseSensitive(), false, false);
	}
	
	private com.vaadin.data.Container.Filter buildWildcardFilter(Filter filter) {
		Wildcard wildcard = (Wildcard) filter;
		String searchTerm = wildcard.getValue().replace('?', '_').replace('*', '%');
		return new Like(wildcard.getColumn(), searchTerm, false);
	}



	/**
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	protected com.vaadin.data.Container.Filter buildEndsWithFilter(Filter filter) {
		EndsWith containerFilter = (EndsWith) filter;
		return new AdvancedStringFilter(containerFilter.getColumn(),
				(String) containerFilter.getValue(),
				!containerFilter.isCaseSensitive(), false, true);
	}

	/**
	 * @param filter
	 *            Container-Filter
	 * @return Vaadin-Filter
	 */
	protected com.vaadin.data.Container.Filter buildStartsWithFilter(
			Filter filter) {
		StartsWith containerFilter = (StartsWith) filter;
		return new AdvancedStringFilter(containerFilter.getColumn(),
				(String) containerFilter.getValue(),
				!containerFilter.isCaseSensitive(), true, false);
	}

	private com.vaadin.data.Container.Filter buildNotFilter(Not notFilter) {
		int size = notFilter.getFilters().size();
		if (size == 0) {
			return null;
		} else if (size == 1) {
			return new com.vaadin.data.util.filter.Not(
					buildVaadinFilter(notFilter.getFilters().get(0)));
		} else {
			List<com.vaadin.data.Container.Filter> vaadinFilter = new ArrayList<Container.Filter>(
					size);
			for (Filter currentFilter : notFilter.getFilters()) {
				com.vaadin.data.Container.Filter vfilter = buildVaadinFilter(currentFilter);
				if (vfilter != null) {
					vaadinFilter.add(vfilter);
				}
			}
			return new com.vaadin.data.util.filter.Not(
					new And(
							vaadinFilter
									.toArray(new com.vaadin.data.Container.Filter[vaadinFilter
											.size()])));
		}
	}

	private com.vaadin.data.Container.Filter buildAllFilter(All allFilter) {
		int size = allFilter.getFilters().size();
		if (size == 0) {
			return null;
		} else if (size == 1) {
			return buildVaadinFilter(allFilter.getFilters().get(0));
		} else {
			List<com.vaadin.data.Container.Filter> vaadinFilter = new ArrayList<Container.Filter>(
					size);
			for (Filter currentFilter : allFilter.getFilters()) {
				com.vaadin.data.Container.Filter vfilter = buildVaadinFilter(currentFilter);
				if (vfilter != null) {
					vaadinFilter.add(vfilter);
				}
			}
			return new And(
					vaadinFilter
							.toArray(new com.vaadin.data.Container.Filter[vaadinFilter
									.size()]));
		}
	}

	private com.vaadin.data.Container.Filter buildAnyFilter(Filter filter) {
		com.vaadin.data.Container.Filter vadinAnyFilter = null;

		Any anyFilter = (Any) filter;
		int size = anyFilter.getFilters().size();
		if (size > 0) {
			List<com.vaadin.data.Container.Filter> vaadinFilter = new ArrayList<Container.Filter>(
					size);
			for (Filter currentFilter : anyFilter.getFilters()) {
				com.vaadin.data.Container.Filter vfilter = buildVaadinFilter(currentFilter);
				if (vfilter != null) {
					vaadinFilter.add(vfilter);
				}
			}
			vadinAnyFilter = new Or(
					vaadinFilter
							.toArray(new com.vaadin.data.Container.Filter[vaadinFilter
									.size()]));
		}

		return vadinAnyFilter;
	}

	private com.vaadin.data.Container.Filter buildGreaterFilter(Filter filter) {
		if (((Greater) filter).isInclusive()) {
			return new Compare.GreaterOrEqual(((Greater) filter).getColumn(),
					((Greater) filter).getValue());
		} else {
			return new Compare.Greater(((Greater) filter).getColumn(),
					((Greater) filter).getValue());
		}
	}

	private com.vaadin.data.Container.Filter buildLessFilter(Filter filter) {
		if (((Less) filter).isInclusive()) {
			return new Compare.LessOrEqual(((Less) filter).getColumn(),
					((Less) filter).getValue());
		} else {
			return new Compare.Less(((Less) filter).getColumn(),
					((Less) filter).getValue());
		}
	}

	private com.vaadin.data.util.filter.Compare.Equal buildEqualFilter(
			Filter filter) {
		return new Compare.Equal(((Equal) filter).getColumn(),
				((Equal) filter).getValue());
	}

	/**
	 * @param containerRowId
	 *            die ID der Zeile
	 * @param columnName
	 *            Der Spaltenname
	 * @param blob
	 *            das Binärobjekt
	 */
	protected void setBLob(ContainerRowId containerRowId, String columnName,
			ContainerBlob blob) {
		if (!blobFields.containsKey(containerRowId)) {
			blobFields
					.put(containerRowId, new HashMap<String, ContainerBlob>());
		}
		blobFields.get(containerRowId).put(columnName, blob);
	}

	/**
	 * @return <code>true</code>, falls Blob- oder Clob-Spalten der Zeile
	 *         modifiziert wurden.
	 */
	protected boolean isAdditionalColumnModified() {
		for (Map<String, ContainerClob> row : clobFields.values()) {
			for (ContainerClob clob : row.values()) {
				if (clob.isModified()) {
					return true;
				}
			}
		}

		for (Map<String, ContainerBlob> row : blobFields.values()) {
			for (ContainerBlob blob : row.values()) {
				if (blob.isModified()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Leert Informationen zu Änderungen an CLobs und BLobs
	 */
	protected void clearAdditionalFields() {
		clobFields.clear();
		blobFields.clear();
	}

	@Override
	public boolean isBLobModified(ContainerRowId containerRowId,
			String columnName) {
		Map<String, ContainerBlob> blobsInRow = blobFields.get(containerRowId);
		if (blobsInRow != null) {
			ContainerBlob blob = blobsInRow.get(columnName);
			if (blob != null) {
				return blob.isModified();
			}
		}
		return false;
	}

	@Override
	public boolean isCLobModified(ContainerRowId containerRowId,
			String columnName) {
		Map<String, ContainerClob> clobsInRow = clobFields.get(containerRowId);
		if (clobsInRow != null) {
			ContainerClob clob = clobsInRow.get(columnName);
			if (clob != null) {
				return clob.isModified();
			}
		}
		return false;
	}

	/**
	 * @param containerRowId
	 *            die ID der Zeile
	 * @param columnName
	 *            Der Spaltenname
	 * @param value
	 *            das CLob-Objekt
	 */
	protected void setCLob(ContainerRowId containerRowId, String columnName,
			ContainerClob value) {

		if (!clobFields.containsKey(containerRowId)) {
			clobFields
					.put(containerRowId, new HashMap<String, ContainerClob>());
		}
		clobFields.get(containerRowId).put(columnName, value);
	}

	@Override
	public void addInsertEventHandler(InsertEventHandler handler) {
		onInsertEventRouter.addHandler(handler);
	}

	@Override
	public void addDeleteEventHandler(DeleteEventHandler handler) {
		onDeleteEventRouter.addHandler(handler);
	}

	@Override
	public void addUpdateEventHandler(UpdateEventHandler handler) {
		onUpdateEventRouter.addHandler(handler);
	}

	@Override
	public void addCreateEventHandler(CreateEventHandler handler) {
		onCreateEventRouter.addHandler(handler);
	}

	@Override
	public void addBeforeCommitEventHandler(BeforeCommitEventHandler handler) {
		beforeCommitEventRouter.addHandler(handler);
	}

	@Override
	public void addCommitEventHandler(CommitEventHandler handler) {
		onCommitEventRouter.addHandler(handler);
	}

	protected EventRouter<InsertEventHandler, InsertEvent> getOnInsertEventRouter() {
		return onInsertEventRouter;
	}

	protected EventRouter<UpdateEventHandler, UpdateEvent> getOnUpdateEventRouter() {
		return onUpdateEventRouter;
	}

	protected EventRouter<DeleteEventHandler, DeleteEvent> getOnDeleteEventRouter() {
		return onDeleteEventRouter;
	}

	protected EventRouter<CommitEventHandler, CommitEvent> getOnCommitEventRouter() {
		return onCommitEventRouter;
	}

	protected EventRouter<CreateEventHandler, CreateEvent> getOnCreateEventRouter() {
		return onCreateEventRouter;
	}

	protected EventRouter<BeforeCommitEventHandler, BeforeCommitEvent> getBeforeCommitEventRouter() {
		return beforeCommitEventRouter;
	}

	void setVaadinContainer(Container vaadinContainer) {
		this.container = vaadinContainer;
	}

	@Override
	public ContainerRow getRowByInternalRowId(Object internalId,
			boolean transactional, boolean immutable) {
		ContainerRowId rowId = convertInternalRowId(internalId);
		return getRow(rowId, transactional, immutable);
	}

	/**
	 * nur von Tests aufzurufen (Änderung zur Laufzeit nicht vorgesehen)
	 * 
	 * @param filterPolicy
	 *            die Policy für den Container
	 */
	void setFilterPolicy(FilterPolicy filterPolicy) {
		this.filterPolicy = filterPolicy;
	}

	@Override
	public void withExportSettings(ExportWithExportSettings exportCallback) {
		exportCallback.export();
	}

	@Override
	public FilterPolicy getFilterPolicy() {
		return filterPolicy;
	}

	@Override
	public void onPortletRefresh(PortletRefreshedEvent event) {
		if (container != null) {
			refresh();
		}
	}

}
