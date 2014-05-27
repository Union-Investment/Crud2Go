package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class FileMetadataTest {

	@Mock
	private ContainerRow rowMock;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnConfiguredFilenameIfNoColumnConfigured() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, null, null, null, null, null);
		assertThat(metadata.getCurrentFilename(rowMock), is("test.pdf"));
	}
	@Test
	public void shouldReturnConfiguredFilenameIfColumnContainsNull() {
		FileMetadata metadata = new FileMetadata("test.pdf", "FILENAME", null, null, null, null, null);
		when(rowMock.getValue("FILENAME")).thenReturn(null);
		assertThat(metadata.getCurrentFilename(rowMock), is("test.pdf"));
	}
	@Test
	public void shouldReturnColumnValueFilenameIfColumnConfigured() {
		FileMetadata metadata = new FileMetadata("test.pdf", "FILENAME", null, null, null, null, null);
		when(rowMock.getValue("FILENAME")).thenReturn("test.xml");
		assertThat(metadata.getCurrentFilename(rowMock), is("test.xml"));
	}

	@Test
	public void shouldReturnCurrentFilenameAsDisplayName() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, null, null, null, null, null);
		assertThat(metadata.getCurrentDisplayname(rowMock), is("test.pdf"));
	}
	
	@Test
	public void shouldReturnConfiguredDownloadCaptionAsDisplayName() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, null, null, "Datei herunterladen", null, null);
		assertThat(metadata.getCurrentDisplayname(rowMock), is("Datei herunterladen"));
	}
	
	@Test
	public void shouldReturnConfiguredMimetypeIfNoColumnConfigured() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, "test/pdf", null, null, null, null);
		assertThat(metadata.getCurrentMimetype(rowMock), is("test/pdf"));
	}
	@Test
	public void shouldReturnConfiguredMimetypeIfColumnContainsNull() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, "test/pdf", "MIMETYPE", null, null, null);
		when(rowMock.getValue("MIMETYPE")).thenReturn(null);
		assertThat(metadata.getCurrentMimetype(rowMock), is("test/pdf"));
	}
	@Test
	public void shouldReturnColumnValueMimetypeIfColumnConfigured() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, "test/pdf", "MIMETYPE", null, null, null);
		when(rowMock.getValue("MIMETYPE")).thenReturn("other/pdf");
		assertThat(metadata.getCurrentMimetype(rowMock), is("other/pdf"));
	}
	@Test
	public void shouldReturnMimetypeByExtensionIfNothingConfigured() {
		FileMetadata metadata = new FileMetadata("test.pdf", null, null, null, null, null, null);
		assertThat(metadata.getCurrentMimetype(rowMock), is("application/pdf"));
	}

	
}
