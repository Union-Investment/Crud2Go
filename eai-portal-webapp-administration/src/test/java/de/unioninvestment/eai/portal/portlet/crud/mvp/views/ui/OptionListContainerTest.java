package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.filter.Compare;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;

@RunWith(MockitoJUnitRunner.class)
public class OptionListContainerTest {

	@Mock
	private SelectionContext contextMock;

	@Mock
	private OptionList optionListMock;

	@Mock
	private ItemSetChangeListener listenerMock;

	private OptionListContainer container;

	private LinkedHashMap<String, String> options;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(optionListMock.isLazy()).thenReturn(false);
		options = new LinkedHashMap<String, String>();
		options.put("KEY", "TITLE");
		options.put("OTHER", "OTHER");
		when(optionListMock.getOptions(contextMock)).thenReturn(options);
		when(optionListMock.getOptions(null)).thenReturn(options);

		container = new OptionListContainer(optionListMock, contextMock);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnTitleAsPropertyId() {
		assertThat((Collection<String>) container.getContainerPropertyIds(),
				hasItems("title"));
	}

	@Test
	public void shouldReturnItem() {
		assertThat(container.getItem("KEY"), notNullValue());
		assertThat(container.getItem("KEY").getItemProperty("title"),
				notNullValue());
		assertThat(
				container.getItem("KEY").getItemProperty("title").getValue(),
				is((Object) "TITLE"));
	}

	@Test
	public void shouldReturnSize() {
		assertThat(container.size(), is(2));
	}

	@Test
	public void shouldReturnItemProperty() {
		assertThat((String) container.getContainerProperty("KEY", "title")
				.getValue(), is("TITLE"));
	}

	@Test
	public void shouldReturnNullForNonExistingItem() {
		assertThat(container.getContainerProperty("UNKNOWN", "title"),
				nullValue());
	}

	@Test
	public void shouldReturnNullForNonExistingProperty() {
		assertThat(container.getContainerProperty("KEY", "unknown"),
				nullValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnItemIDs() {
		assertThat((Collection<String>) container.getItemIds(),
				hasItems("KEY", "OTHER"));
	}

	@Test
	public void shouldReturnStringTypeForTitleProperty() {
		assertEquals(String.class, container.getType("title"));
	}

	@Test
	public void shouldReturnIfIDIsContained() {
		assertThat(container.containsId("KEY"), is(true));
		assertThat(container.containsId("OTHER"), is(true));
		assertThat(container.containsId("UNKNOWN"), is(false));
	}

	@Test
	public void shouldFilterForKEY() {
		container.addContainerFilter(new Compare.Equal("title", "TITLE"));
		assertThat(container.containsId("KEY"), is(true));
		assertThat(container.containsId("OTHER"), is(false));
	}

	@Test
	public void shouldRemoveAllContainerFilters() {
		container.addContainerFilter(new Compare.Equal("title", "TITLE"));
		container.removeAllContainerFilters();
		assertThat(container.containsId("OTHER"), is(true));
	}

	@Test
	public void shouldFireChangeEventOnFilterUpdate() {
		container.addListener(listenerMock);
		container.addContainerFilter(new Compare.Equal("title", "TITLE"));
		verify(listenerMock).containerItemSetChange(
				isA(ItemSetChangeEvent.class));
	}

	@Test
	public void shouldFireChangeEventOnFilterRemoval() {
		container.addListener(listenerMock);
		container.addContainerFilter(new Compare.Equal("title", "TITLE"));
		container.removeAllContainerFilters();
		verify(listenerMock, times(2)).containerItemSetChange(
				isA(ItemSetChangeEvent.class));
	}

	@Test
	public void shouldRemoveSpecificContainerFilter() {
		Compare.Equal filter = new Compare.Equal("title", "TITLE");
		container.addContainerFilter(filter);
		container.removeContainerFilter(filter);
		assertThat(container.containsId("OTHER"), is(true));
	}

	@Test
	public void shouldReturnNextItemId() {
		assertThat((String) container.nextItemId("KEY"), is("OTHER"));
	}

	@Test
	public void shouldReturnNullAsNextItemId() {
		assertThat((String) container.nextItemId("OTHER"), nullValue());
	}

	@Test
	public void shouldReturnNullAsPreviousItemId() {
		assertThat((String) container.prevItemId("KEY"), nullValue());
	}

	@Test
	public void shouldReturnKEYAsPreviousItemId() {
		assertThat((String) container.prevItemId("OTHER"), is("KEY"));
	}

	@Test
	public void shouldReturnIfFirstId() {
		assertThat(container.isFirstId("KEY"), is(true));
		assertThat(container.isFirstId("OTHER"), is(false));
		assertThat(container.isFirstId("UNKNOWN"), is(false));
	}

	@Test
	public void shouldReturnIfLastId() {
		assertThat(container.isLastId("KEY"), is(false));
		assertThat(container.isLastId("OTHER"), is(true));
		assertThat(container.isLastId("UNKNOWN"), is(false));
	}

	@Test
	public void shouldReturnIndexOfId() {
		assertThat(container.indexOfId("KEY"), is(0));
		assertThat(container.indexOfId("OTHER"), is(1));
	}

	@Test
	public void shouldReturnIdOfIndex() {
		assertThat((String) container.getIdByIndex(0), is("KEY"));
		assertThat((String) container.getIdByIndex(1), is("OTHER"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldFailOnIndexTooSmall() {
		container.getIdByIndex(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void shouldFailOnIndexTooLarge() {
		container.getIdByIndex(2);
	}

	@Test
	public void shouldReturnFirstItemId() {
		assertThat((String) container.firstItemId(), is("KEY"));
	}

	@Test
	public void shouldReturnLastItemId() {
		assertThat((String) container.lastItemId(), is("OTHER"));
	}

	@Test
	public void shouldFireChangeOnInitializedEvent() {
		container.addListener(listenerMock);

		options.put("THIRD", "3RD");
		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				true);
		container.onOptionListChange(event);

		verify(listenerMock).containerItemSetChange(
				isA(ItemSetChangeEvent.class));
		assertThat(container.containsId("THIRD"), is(true));
	}

	@Test
	public void shouldDelayRefreshIfContainerIsNotInitialized() {
		assertThat(container.containsId("THIRD"), is(false));

		options.put("THIRD", "3RD");
		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				false);
		container.onOptionListChange(event);

		assertThat(container.contentChanged, is(true));
		assertThat(container.containsId("THIRD"), is(true));

		assertThat(container.contentChanged, is(false));
	}

	@Test
	public void shouldFireEventIfBackendRefreshes() {
		container.addListener(listenerMock);

		options.put("THIRD", "3RD");
		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				true);
		container.onOptionListChange(event);

		verify(listenerMock).containerItemSetChange(
				isA(ItemSetChangeEvent.class));
	}

	@Test
	@Ignore
	public void shouldFireEventLazilyIfBackendRefreshesUninitialized() {
		container.addListener(listenerMock);

		options.put("THIRD", "3RD");
		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				false);
		container.onOptionListChange(event);

		verifyZeroInteractions(listenerMock);

		container.containsId("KEY");
		verify(listenerMock).containerItemSetChange(
				isA(ItemSetChangeEvent.class));
	}

	@Test
	public void shouldDisconnectOnClose() {
		container.close();
		verify(optionListMock).removeChangeListener(container);
	}
}
