package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.CustomComponent;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Component.ExpandableComponent;
import de.unioninvestment.eai.portal.support.vaadin.mvp.View;

public class AbstractComponentPresenterTest {

	static class DummyComponent extends Component implements ExpandableComponent {
		@Override
		public String getWidth() {
			return null;
		}

		@Override
		public String getHeight() {
			return null;
		}

		@Override
		public int getExpandRatio() {
			return 0;
		}
	}
	
	@SuppressWarnings("serial")
	static class DummyView extends CustomComponent implements View {

	}

	@SuppressWarnings("serial")
	static class DummyComponentPresenter extends
			AbstractComponentPresenter<DummyComponent, DummyView> {

		public DummyComponentPresenter(DummyView view, DummyComponent model) {
			super(view, model);
		}
	}

	@SuppressWarnings("serial")
	static class ExpandableDummyComponent extends DummyComponent implements ExpandableComponent {
		@Override
		public int getExpandRatio() {
			return 1;
		}
	}
	
	@Mock
	private DummyComponent modelMock;
	
	@Mock
	private DummyView viewMock;

	private DummyComponentPresenter presenter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		presenter = new DummyComponentPresenter(viewMock, modelMock);
	}
	
	@Test
	public void shouldUpdateViewWithFullWidthByDefault() {
		when(modelMock.getWidth()).thenReturn(null);
		presenter.updateViewWidth();
		verify(viewMock).setWidth("100%");
	}
	
	@Test
	public void shouldUpdateViewWithConfiguredWidth() {
		when(modelMock.getWidth()).thenReturn("50%");
		presenter.updateViewWidth();
		verify(viewMock).setWidth("50%");
	}
	
	@Test
	public void shouldUpdateViewWithUndefinedHeightByDefault() {
		when(modelMock.getHeight()).thenReturn(null);
		presenter.updateViewHeight(false);
		verify(viewMock).setHeight(null);
	}
	
	@Test
	public void shouldUpdateViewWithFullHeightForOuterDefinedHeightAndExpandRatio() {
		when(modelMock.getHeight()).thenReturn(null);
		when(modelMock.getExpandRatio()).thenReturn(1);
		presenter.updateViewHeight(true);
		verify(viewMock).setHeight("100%");
	}
	
	@Test
	public void shouldUpdateViewWithUndefindedHeightForOuterDefinedHeightAndWithoutExpandRatio() {
		when(modelMock.getHeight()).thenReturn(null);
		presenter.updateViewHeight(true);
		verify(viewMock).setHeight(null);
	}
	
	@Test
	public void shouldUpdateViewWithWithConfiguredHeightEvenIfOuterIsDefined() {
		when(modelMock.getHeight()).thenReturn("50%");
		presenter.updateViewHeight(true);
		verify(viewMock).setHeight("50%");
	}

	@Test
	public void shouldReturnNotExpandableIfModelNotExpandable() {
		assertThat(presenter.getExpandRatio(), is(0));
	}
	
	@Test
	public void shouldReturnConfiguredExpandRatioIfExpandable() {
		presenter = new DummyComponentPresenter(viewMock, new ExpandableDummyComponent());
		assertThat(presenter.getExpandRatio(), is(1));
	}
}
