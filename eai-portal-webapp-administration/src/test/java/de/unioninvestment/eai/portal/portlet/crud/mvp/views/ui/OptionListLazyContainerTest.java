package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.util.filter.Compare;

import de.unioninvestment.eai.portal.portlet.crud.domain.events.OptionListChangeEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.OptionList;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.SelectionContext;

/**
 * Tests the behavior of {@link OptionListContainer} if the OptionList is
 * configured lazy.
 * 
 * @author carsten.mjartan
 */
@RunWith(MockitoJUnitRunner.class)
public class OptionListLazyContainerTest {

	@Mock
	private SelectionContext contextMock;

	@Mock
	private OptionList optionListMock;

	private OptionListContainer container;

	private LinkedHashMap<String, String> options;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(optionListMock.isLazy()).thenReturn(true);
		options = new LinkedHashMap<String, String>();
		options.put("KEY", "TITLE");
		options.put("OTHER", "OTHER");
		when(optionListMock.getOptions(contextMock)).thenReturn(options);

		container = new OptionListContainer(optionListMock, contextMock);
	}

	@Test
	public void shouldReturnNotItemBeforeInitialization() {
		assertThat(container.getItem("KEY"), nullValue());
	}

	@Test
	public void shouldReturnEmptyBeforeInitialization() {
		assertThat(container.size(), is(0));
	}

	@Test
	public void shouldNotReturnItemPropertyBeforeInitialization() {
		assertThat(container.getContainerProperty("KEY", "title"), nullValue());
	}

	@Test
	public void shouldNotReturnItemIDsBeforeInitialization() {
		assertThat(container.getItemIds().size(), is(0));
	}

	@Test
	public void shouldReturnThatNothingIsContained() {
		assertThat(container.containsId("KEY"), is(false));
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
	public void shouldRemoveSpecificContainerFilter() {
		Compare.Equal filter = new Compare.Equal("title", "TITLE");
		container.addContainerFilter(filter);
		container.removeContainerFilter(filter);
		assertThat(container.containsId("OTHER"), is(true));
	}

	@Test
	public void shouldRefreshOnInitializedEvent() {
		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				true);
		container.onOptionListChange(event);

		assertThat(container.contentChanged, is(false));
		assertThat(container.containsId("KEY"), is(true));
	}

	@Test
	public void shouldDelayRefreshIfContainerIsNotInitialized() {
		assertThat(container.containsId("KEY"), is(false));

		OptionListChangeEvent event = new OptionListChangeEvent(optionListMock,
				false);
		container.onOptionListChange(event);

		assertThat(container.contentChanged, is(true));
		assertThat(container.containsId("KEY"), is(true));

		assertThat(container.contentChanged, is(false));
	}
}
