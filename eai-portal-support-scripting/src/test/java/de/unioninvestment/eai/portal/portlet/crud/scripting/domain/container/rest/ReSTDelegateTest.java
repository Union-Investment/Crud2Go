package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import groovy.lang.Closure;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTDeleteConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTInsertConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTUpdateConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItemId;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.container.UpdateContext;

public class ReSTDelegateTest {

	private static final String JSON_STRING = "{ { a: 1}, {a: 2}, {a: 3} }";

	@Mock
	private HttpClient httpMock;

	@Mock
	private PayloadParser parserMock;

	@Captor
	private ArgumentCaptor<HttpUriRequest> requestCaptor;

	@Captor
	private ArgumentCaptor<HttpPost> postRequestCaptor;

	@Captor
	private ArgumentCaptor<HttpPut> putRequestCaptor;

	@Captor
	private ArgumentCaptor<HttpDelete> deleteRequestCaptor;

	@Mock
	private HttpResponse responseMock;

	@Mock
	private GenericItem itemMock;

	@Mock
	private GenericItemId itemIdMock;

	@Mock
	private HttpPost postRequestMock;

	@Mock
	private PayloadCreator creatorMock;

	@Mock
	private ScriptBuilder scriptBuilderMock;

	@Mock
	private Closure<Object> urlClosureMock;

	@Mock
	private ReSTContainer containerMock;

	@Mock
	private ContainerRow containerRowMock;

	private byte[] contents;

	private ReSTDelegate delegate;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnAttributeAsMetadata() {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.getColumns().get(0), is(new Column("id",
				String.class, false, false, true, null)));
	}

	@Test
	public void shouldBeReadonlyByDefault() {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isInsertSupported(), is(false));
		assertThat(metaData.isUpdateSupported(), is(false));
		assertThat(metaData.isRemoveSupported(), is(false));
	}

	@Test
	public void shouldAllowInsertIfConfigured() {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.setInsert(new ReSTInsertConfig());
		ReSTDelegate delegate = newDelegate(config);

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isInsertSupported(), is(true));
	}

	@Test
	public void shouldAllowUpdateIfConfigured() {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.setUpdate(new ReSTUpdateConfig());
		ReSTDelegate delegate = newDelegate(config);

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isUpdateSupported(), is(true));
	}

	@Test
	public void shouldAllowDeleteIfConfigured() {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.setDelete(new ReSTDeleteConfig());
		ReSTDelegate delegate = newDelegate(config);

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isRemoveSupported(), is(true));
	}

	@Test
	public void shouldTellThatIsNotTransactional() {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isTransactional(), is(false));
	}

	@Test
	public void shouldTellThatBackendFilteringIsNotPossible() {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());

		MetaData metaData = delegate.getMetaData();

		assertThat(metaData.isFilterSupported(), is(false));
	}

	@Test
	public void shouldUseQueryUrlIfBaseUrlIsMissing()
			throws ClientProtocolException, IOException {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.setBaseUrl(null);
		config.getQuery().setUrl("http://test.de/path");
		ReSTDelegate delegate = newDelegate(config);
		assumeValidResponse();

		delegate.getRows();

		verify(httpMock).execute(requestCaptor.capture());
		assertThat(requestCaptor.getValue().getURI().toString(),
				is("http://test.de/path"));
	}

	@Test
	public void shouldConcatBaseUrlAndQueryUrl()
			throws ClientProtocolException, IOException {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.setBaseUrl("http://test.de/");
		config.getQuery().setUrl("path");
		ReSTDelegate delegate = newDelegate(config);
		assumeValidResponse();

		delegate.getRows();

		verify(httpMock).execute(requestCaptor.capture());
		assertThat(requestCaptor.getValue().getURI().toString(),
				is("http://test.de/path"));
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowReadableExceptionOnWrongResultCode()
			throws ClientProtocolException, IOException {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());
		assumeInvalidResponse(HttpStatus.NOT_FOUND, "NOT FOUND");

		delegate.getRows();
	}

	@Test(expected = InvalidConfigurationException.class)
	public void shouldThrowConfigurationExceptionIfUrlIsInvalid() {
		ReSTContainerConfig config = RestTestConfig.readonlyConfig();
		config.getQuery().setUrl("\\");
		ReSTDelegate delegate = newDelegate(config);

		delegate.getRows();
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowReadableExceptionOnIOErrors() throws IOException {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());
		assumeValidResponse();
		when(parserMock.getRows(responseMock)).thenThrow(
				new IOException("Connection problem"));

		delegate.getRows();
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowReadableExceptionOnProtocolErrors()
			throws IOException {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());
		when(httpMock.execute(any(HttpUriRequest.class))).thenThrow(
				new ClientProtocolException("bla"));

		delegate.getRows();
	}

	@Test
	public void shouldReturnResultFromParser() throws ClientProtocolException,
			IOException {
		ReSTDelegate delegate = newDelegate(RestTestConfig.readonlyConfig());
		assumeValidResponse();
		List<Object[]> expectedResult = asList(new Object[2], new Object[2]);
		when(parserMock.getRows(responseMock)).thenReturn(expectedResult);

		List<Object[]> result = delegate.getRows();

		assertThat(result, sameInstance(expectedResult));
	}

	@Test
	public void shouldSendInsertPostRequestToServer()
			throws ClientProtocolException, IOException {

		// given
		prepareValidInsertPostRequest();

		// when
		delegate.update(asList(itemMock), new UpdateContext());

		// then
		HttpPost postRequest = capturePostRequest();
		HttpEntity entity = postRequest.getEntity();

		assertThat(postRequest.getURI().toString(),
				is("http://test.de/insertpath/4711"));
		assertArrayEquals(contents, IOUtils.toByteArray(entity.getContent()));
		assertThat(ContentType.getOrDefault(entity).getCharset(),
				is(Charset.forName("UTF-8")));
		assertThat(ContentType.getOrDefault(entity).getMimeType(),
				is("text/json"));
	}

	@Test
	public void shouldSendUpdatePutRequestToServer()
			throws ClientProtocolException, IOException {

		// given
		prepareValidUpdatePutRequest();

		// when
		delegate.update(asList(itemMock), new UpdateContext());

		// then
		HttpPut request = capturePutRequest();
		HttpEntity entity = request.getEntity();

		assertThat(request.getURI().toString(),
				is("http://test.de/insertpath/4711"));
		assertArrayEquals(contents, IOUtils.toByteArray(entity.getContent()));
		assertThat(ContentType.getOrDefault(entity).getCharset(),
				is(Charset.forName("UTF-8")));
		assertThat(ContentType.getOrDefault(entity).getMimeType(),
				is("text/json"));
	}

	@Test
	public void shouldSendDeleteRequestToServer()
			throws ClientProtocolException, IOException {

		// given
		prepareValidDeleteRequest();

		// when
		delegate.update(asList(itemMock), new UpdateContext());

		// then
		HttpDelete request = captureDeleteRequest();

		assertThat(request.getURI().toString(),
				is("http://test.de/deletepath/4711"));
	}

	@Test
	public void shouldTriggerContainerRefreshOnUpdate()
			throws ClientProtocolException, IOException {

		// given
		prepareValidInsertPostRequest();
		UpdateContext context = new UpdateContext();

		// when
		delegate.update(asList(itemMock), context);

		assertThat(context.isRefreshRequired(), is(true));
	}

	@Test(expected = BusinessException.class)
	public void shouldThrowExceptionOnWrongResponseCode()
			throws ClientProtocolException, IOException {

		// given
		prepareValidInsertPostRequest();
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						HttpStatus.FORBIDDEN.value(), "FORBIDDEN"));

		// when
		delegate.update(asList(itemMock), new UpdateContext());
	}

	private void prepareValidInsertPostRequest()
			throws ClientProtocolException, IOException {
		ReSTContainerConfig config = RestTestConfig.readwriteConfig();
		delegate = newDelegate(config);

		// Input for URL
		when(scriptBuilderMock.buildClosure(config.getInsert().getUrl()))
				.thenReturn(urlClosureMock);
		when(urlClosureMock.call(any(ScriptRow.class))).thenReturn(
				"http://test.de/insertpath/4711");

		when(itemMock.isNewItem()).thenReturn(true);

		// fake for Script
		when(itemMock.getId()).thenReturn(itemIdMock);
		when(containerMock.getRowByInternalRowId(itemIdMock, false, true))
				.thenReturn(containerRowMock);

		contents = JSON_STRING.getBytes("UTF-8");
		when(creatorMock.getMimeType()).thenReturn("text/json");
		when(
				creatorMock.create(itemMock, config.getInsert().getValue(),
						"UTF-8")).thenReturn(contents);

		when(httpMock.execute(any(HttpUriRequest.class))).thenReturn(
				responseMock);
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						201, "CREATED"));
	}

	private void prepareValidUpdatePutRequest()
			throws ClientProtocolException, IOException {
		ReSTContainerConfig config = RestTestConfig.readwriteConfig();
		delegate = newDelegate(config);

		// Input for URL
		when(scriptBuilderMock.buildClosure(config.getUpdate().getUrl()))
				.thenReturn(urlClosureMock);
		when(urlClosureMock.call(any(ScriptRow.class))).thenReturn(
				"http://test.de/insertpath/4711");

		when(itemMock.isModified()).thenReturn(true);

		// fake for Script
		when(itemMock.getId()).thenReturn(itemIdMock);
		when(containerMock.getRowByInternalRowId(itemIdMock, false, true))
				.thenReturn(containerRowMock);

		contents = JSON_STRING.getBytes("UTF-8");
		when(creatorMock.getMimeType()).thenReturn("text/json");
		when(
				creatorMock.create(itemMock, config.getUpdate().getValue(),
						"UTF-8")).thenReturn(contents);

		when(httpMock.execute(any(HttpUriRequest.class))).thenReturn(
				responseMock);
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						200, "CREATED"));
	}

	private void prepareValidDeleteRequest()
			throws ClientProtocolException, IOException {
		ReSTContainerConfig config = RestTestConfig.readwriteConfig();
		delegate = newDelegate(config);

		// Input for URL
		when(scriptBuilderMock.buildClosure(config.getDelete().getUrl()))
				.thenReturn(urlClosureMock);
		when(urlClosureMock.call(any(ScriptRow.class))).thenReturn(
				"http://test.de/deletepath/4711");

		when(itemMock.isDeleted()).thenReturn(true);

		// fake for Script
		when(itemMock.getId()).thenReturn(itemIdMock);
		when(containerMock.getRowByInternalRowId(itemIdMock, false, true))
				.thenReturn(containerRowMock);

		when(httpMock.execute(any(HttpUriRequest.class))).thenReturn(
				responseMock);
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						HttpStatus.OK.value(), "DELETED"));
	}

	private HttpPost capturePostRequest() throws IOException,
			ClientProtocolException {
		verify(httpMock).execute(postRequestCaptor.capture());
		HttpPost postRequest = postRequestCaptor.getValue();
		return postRequest;
	}

	private HttpPut capturePutRequest() throws IOException,
			ClientProtocolException {
		verify(httpMock).execute(putRequestCaptor.capture());
		HttpPut putRequest = putRequestCaptor.getValue();
		return putRequest;
	}

	private HttpDelete captureDeleteRequest() throws IOException,
			ClientProtocolException {
		verify(httpMock).execute(deleteRequestCaptor.capture());
		HttpDelete request = deleteRequestCaptor.getValue();
		return request;
	}

	private void assumeValidResponse() throws ClientProtocolException,
			IOException {
		assumeValidQueryResponse(JSON_STRING, "text/json", "utf-8");
	}

	private void assumeInvalidResponse(HttpStatus status, String message)
			throws ClientProtocolException, IOException {
		when(httpMock.execute(any(HttpUriRequest.class))).thenReturn(
				responseMock);
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						status.value(), message));
	}

	private void assumeValidQueryResponse(String content,
			String mimeType, String charset)
			throws IOException, ClientProtocolException {
		when(httpMock.execute(any(HttpUriRequest.class))).thenReturn(
				responseMock);
		when(responseMock.getStatusLine()).thenReturn(
				new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),
						200, "OK"));
		when(responseMock.getEntity())
				.thenReturn(
						new StringEntity(content, ContentType.create(mimeType,
								charset)));
	}

	private ReSTDelegate newDelegate(ReSTContainerConfig config) {
		ReSTDelegate delegate = new ReSTDelegate(config, containerMock,
				scriptBuilderMock);
		delegate.http = httpMock;
		delegate.parser = parserMock;
		delegate.creator = creatorMock;
		return delegate;
	}

}
