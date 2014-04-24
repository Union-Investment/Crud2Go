package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.config.ScriptComponentConfig;

public class CustomComponentTest {

	private CustomComponent component;

	@Before
	public void setup() {
		ScriptComponentConfig config = new ScriptComponentConfig();
		component = new CustomComponent(config);
	}
	
	@Test
	public void shouldNotExpandByDefault() {
		assertThat(component.getExpandRatio(), is(0));
	}

	@Test
	public void shouldAllowCustomExpandRatio() {
		ScriptComponentConfig config = new ScriptComponentConfig();
		config.setExpandRatio(1);
		component = new CustomComponent(config);

		assertThat(component.getExpandRatio(), is(1));
	}

}
