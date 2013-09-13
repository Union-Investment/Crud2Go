package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.ui.ComboBox;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;

/**
 * Container for Option Lists. This class allows lazy initialization of the
 * backing {@link OptionList} by the vaadin framework. If lazy loading is
 * activated, the list is only loaded after the first filtering action (key
 * input on Vaadin {@link ComboBox}). Up to then, the container behaves like
 * empty.
 * 
 * @author carsten.mjartan
 */
public class OptionListContainer extends AbstractContainer implements
		Container, Filterable, Indexed, ItemSetChangeNotifier,
		OptionListChangeEventHandler {

	private static final long serialVersionUID = 1L;

	private static final List<String> PROPERTY_IDS = asList("title");

	private final Object lock = new Object();

	private final OptionList optionList;
	private SelectionContext context;

	private LinkedHashMap<String, Option> options = new LinkedHashMap<String, Option>();
	private ArrayList<Option> optionsAsList = new ArrayList<Option>();

	private List<Filter> filters = new LinkedList<Filter>();

	volatile boolean initialized = false;
	volatile boolean contentChanged = false;

	/**
	 * One Option {@link Item} of the list.
	 * 
	 * @author carsten.mjartan
	 */
	public static class Option implements Item {
		private static final long serialVersionUID = 1L;

		private final String key;
		private final String title;
		private final int index;

		/**
		 * Constructor.
		 * 
		 * @param key
		 *            the option key
		 * @param title
		 *            the display text
		 * @param index
		 *            the position inside the optionlist
		 */
		public Option(String key, String title, int index) {
			this.key = key;
			this.title = title;
			this.index = index;
		}

		/**
		 * @return the option key
		 */
		String getKey() {
			return key;
		}

		/**
		 * @return the display text
		 */
		String getTitle() {
			return title;
		}

		/**
		 * @return the position of the option
		 */
		int getIndex() {
			return index;
		}

		@Override
		public String toString() {
			return title;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Property getItemProperty(Object id) {
			if (id.equals("title")) {
				return new ObjectProperty(title, String.class);
			} else {
				return null;
			}
		}

		@Override
		public Collection<?> getItemPropertyIds() {
			return PROPERTY_IDS;
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Constructor. If lazy loading is disabled, the Optionlist is fetched
	 * immediately.
	 * 
	 * @param optionList
	 *            the backing OptionList
	 * @param context
	 *            the current usage context
	 */
	public OptionListContainer(OptionList optionList, SelectionContext context) {
		this.optionList = optionList;
		this.context = context;
		this.optionList.addChangeListener(this);
		if (!optionList.isLazy() || optionList.isInitialized()) {
			createFilteredOptionList();
			initialized = true;
		}
	}

	@Override
	public Item getItem(Object itemId) {
		refreshOptionListIfNeeded();

		String key = (String) itemId;
		return options.get(key);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return PROPERTY_IDS;
	}

	@Override
	public Collection<?> getItemIds() {
		refreshOptionListIfNeeded();
		return options.keySet();
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		int realCount = optionsAsList.size() - startIndex;
		if (realCount < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (realCount > numberOfItems) {
			realCount = numberOfItems;
		}
		List<String> results = new ArrayList<String>(realCount);
		List<Option> subList = optionsAsList.subList(startIndex, startIndex
				+ realCount);
		for (Option option : subList) {
			results.add(option.key);
		}
		return results;
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		refreshOptionListIfNeeded();
		Option option = options.get(itemId);
		if (option == null) {
			return null;
		} else {
			return option.getItemProperty(propertyId);
		}
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return String.class;
	}

	@Override
	public int size() {
		refreshOptionListIfNeeded();
		return options.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		refreshOptionListIfNeeded();
		return options.containsKey(itemId);
	}

	private void refreshOptionListIfNeeded() {
		if ((!initialized && (!optionList.isLazy())) || contentChanged) {
			contentChanged = false;
			createFilteredOptionList();

			if (!initialized) {
				initialized = true;
			}
		}
	}

	private boolean doesFilter() {
		return filters.size() > 0;
	}

	private void createFilteredOptionList() {
		// this has to be outside the lock to prevent a deadlock situation
		Map<String, String> newOptions = optionList.getOptions(context);

		synchronized (lock) {
			boolean unfiltered = !doesFilter();
			int index = 0;
			options = new LinkedHashMap<String, Option>();
			for (Entry<String, String> entry : newOptions.entrySet()) {
				String key = entry.getKey();
				Option item = new Option(key, entry.getValue(), index++);

				if (unfiltered || passesFilter(key, item)) {
					options.put(key, item);
				}

			}
			optionsAsList = new ArrayList<Option>(options.values());
		}
	}

	private boolean passesFilter(String key, Option item) {
		for (Filter filter : filters) {
			if (!filter.passesFilter(key, item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addContainerFilter(Filter filter)
			throws UnsupportedFilterException {
		this.filters.add(filter);
		fireItemSetChange();
	}

	@Override
	public void removeContainerFilter(Filter filter) {
		for (Iterator<Filter> it = filters.iterator(); it.hasNext();) {
			Filter next = it.next();
			if (next.equals(filter)) {
				it.remove();
				fireItemSetChange();
				return;
			}
		}
	}

	@Override
	public Collection<Filter> getContainerFilters() {
		return unmodifiableList(filters);
	}

	@Override
	protected void fireItemSetChange() {
		contentChanged = true;
		super.fireItemSetChange();
	}

	@Override
	public void removeAllContainerFilters() {
		if (filters.size() > 0) {
			filters.clear();
			fireItemSetChange();
		}
	}

	@Override
	public Object nextItemId(Object itemId) {
		refreshOptionListIfNeeded();
		int index = options.get(itemId).getIndex();
		if (index + 1 >= optionsAsList.size()) {
			return null;
		} else {
			return optionsAsList.get(index + 1).getKey();
		}
	}

	@Override
	public Object prevItemId(Object itemId) {
		refreshOptionListIfNeeded();
		int index = options.get(itemId).getIndex();
		return index == 0 ? null : optionsAsList.get(index - 1).getKey();
	}

	@Override
	public Object firstItemId() {
		refreshOptionListIfNeeded();
		return optionsAsList.size() > 0 ? optionsAsList.get(0).getKey() : null;
	}

	@Override
	public Object lastItemId() {
		refreshOptionListIfNeeded();
		return optionsAsList.size() > 0 ? optionsAsList.get(
				optionsAsList.size() - 1).getKey() : null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		refreshOptionListIfNeeded();
		return itemId.equals(firstItemId());
	}

	@Override
	public boolean isLastId(Object itemId) {
		refreshOptionListIfNeeded();
		return itemId.equals(lastItemId());
	}

	@Override
	public int indexOfId(Object itemId) {
		refreshOptionListIfNeeded();
		Option option = options.get(itemId);
		return option == null ? -1 : option.getIndex();
	}

	@Override
	public Object getIdByIndex(int index) {
		refreshOptionListIfNeeded();
		return optionsAsList.get(index).getKey();
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Item addItemAt(int index, Object newItemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(ItemSetChangeListener listener) {
		super.addItemSetChangeListener(listener);
	}

	@Override
	public void removeListener(ItemSetChangeListener listener) {
		super.removeItemSetChangeListener(listener);
	}

	@Override
	public void onOptionListChange(OptionListChangeEvent event) {
		contentChanged = true;
		if (event.isInitialized()) {
			fireItemSetChange();
		}
	}

	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
		super.addItemSetChangeListener(listener);
	}

	@Override
	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
		super.removeItemSetChangeListener(listener);
	}

	/**
	 * Unregister from {@link OptionList} events.
	 */
	public void close() {
		optionList.removeChangeListener(this);
		options = null;
		optionsAsList = null;
	}

}
