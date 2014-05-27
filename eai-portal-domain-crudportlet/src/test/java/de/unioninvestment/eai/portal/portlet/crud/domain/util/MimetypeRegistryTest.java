package de.unioninvestment.eai.portal.portlet.crud.domain.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.domain.util.MimetypeRegistry;


public class MimetypeRegistryTest {

	@Test
	public void shouldDetectMimetypeFromFileExtension() {
		assertThat(new MimetypeRegistry().detectFromFilename("test.pdf"), is("application/pdf"));
	}
	
	@Test
	public void shouldReturnDefaultMimetypeForUnknownExtensions() {
		assertThat(new MimetypeRegistry().detectFromFilename("test.unknown"), is("application/octet-stream"));
	}
	
	@Test
	public void shouldReturnDefaultMimetypeForFileWithoutExtensions() {
		assertThat(new MimetypeRegistry().detectFromFilename("testunknown"), is("application/octet-stream"));
	}
}
