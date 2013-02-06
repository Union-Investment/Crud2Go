package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTAttributeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTFormatConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.BusinessException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.Column;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericDelegate;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;
import de.unioninvestment.eai.portal.support.vaadin.container.MetaData;
import de.unioninvestment.eai.portal.support.vaadin.container.UpdateContext;

public class ReSTDelegate implements GenericDelegate {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReSTDelegate.class);

	private ReSTContainerConfig config;
	private ScriptBuilder scriptBuilder;

	private MetaData metaData;

	HttpClient http = new DefaultHttpClient();
	PayloadParser parser;
	PayloadCreator creator;

	private ReSTContainer container;

	public ReSTDelegate(ReSTContainerConfig containerConfig,
			ReSTContainer container,
			ScriptBuilder scriptBuilder) {
		this.config = containerConfig;
		this.container = container;
		this.scriptBuilder = scriptBuilder;

		this.metaData = extractMetaData();
		this.parser = createParser();
		this.creator = createCreator();

	}

	private PayloadCreator createCreator() {
		if (config.getFormat() == ReSTFormatConfig.JSON) {
			return new JsonCreator(container, config, scriptBuilder);
		} else if (config.getFormat() == ReSTFormatConfig.XML) {
			return new XmlCreator(container, config, scriptBuilder);
		} else {
			throw new TechnicalCrudPortletException("Unknown ReST format: "
					+ config.getFormat());
		}
	}

	private PayloadParser createParser() {
		if (config.getFormat() == ReSTFormatConfig.JSON) {
			return new JsonParser(config, scriptBuilder);
		} else if (config.getFormat() == ReSTFormatConfig.XML) {
			return new XmlParser(config, scriptBuilder);
		} else {
			throw new TechnicalCrudPortletException("Unknown ReST format: "
					+ config.getFormat());
		}
	}

	private MetaData extractMetaData() {
		List<ReSTAttributeConfig> attributes = config.getQuery().getAttribute();
		List<Column> columns = new ArrayList<Column>(attributes.size());
		for (ReSTAttributeConfig attribute : attributes) {
			columns.add(new Column(
					attribute.getName(),
					attribute.getType(),
					attribute.isReadonly(),
					attribute.isRequired(),
					attribute.isPrimaryKey(),
					null));
		}
		boolean insertSupported = config.getInsert() != null;
		boolean updateSupported = config.getUpdate() != null;
		boolean deleteSupported = config.getDelete() != null;

		return new MetaData(columns, insertSupported, updateSupported,
				deleteSupported, false, false);
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public List<Object[]> getRows() {
		HttpGet request = createQueryRequest();
		try {
			HttpResponse response = http.execute(request);
			expectAnyStatusCode(response, HttpStatus.SC_OK);

			return parser.getRows(response);

		} catch (ClientProtocolException e) {
			LOGGER.error("Error during restful query", e);
			throw new BusinessException("portlet.crud.error.rest.io",
					e.getMessage());

		} catch (IOException e) {
			LOGGER.error("Error during restful query", e);
			throw new BusinessException("portlet.crud.error.rest.io",
					e.getMessage());

		} finally {
			request.releaseConnection();
		}
	}

	private HttpGet createQueryRequest() {
		URI uri = createURI(config.getQuery().getUrl());
		HttpGet request = new HttpGet(uri);

		String mimetype = creator.getMimeType();
		if (config.getMimetype() != null) {
			mimetype = config.getMimetype();
		}
		request.addHeader("Accept", mimetype);
		return request;
	}

	private URI createURI(String postfix) {
		String uri = postfix;
		if (config.getBaseUrl() != null) {
			uri = config.getBaseUrl() + postfix;
		}
		try {
			return URI.create(uri);

		} catch (IllegalArgumentException e) {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.invalidUrl", uri);
		}
	}

	@Override
	public void setFilters(Filter[] filters) {
		throw new UnsupportedOperationException(
				"Server side filtering is not supported by the ReST backend");
	}

	@Override
	public void update(List<GenericItem> items, UpdateContext context) {
		context.requireRefresh();
		try {
			for (GenericItem item : items) {

				if (item.isNewItem()) {
					sendInsertPostRequest(item);
				} else if (item.isModified()) {
					sendUpdatePutRequest(item);
				} else if (item.isDeleted()) {
					sendDeleteRequest(item);
				}
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("Error during restful operation", e);
			throw new BusinessException("portlet.crud.error.rest.io",
					e.getMessage());

		} catch (IOException e) {
			LOGGER.error("Error during restful operation", e);
			throw new BusinessException("portlet.crud.error.rest.io",
					e.getMessage());
		}
	}

	private void sendInsertPostRequest(GenericItem item) throws IOException,
			ClientProtocolException {

		byte[] content = creator.create(item, config.getInsert().getValue(),
				config.getCharset());
		URI uri = createURI(item, config.getInsert().getUrl());
		HttpPost request = new HttpPost(uri);
		ContentType contentType = createContentType();
		request.setEntity(new ByteArrayEntity(content, contentType));

		try {
			HttpResponse response = http.execute(request);
			expectAnyStatusCode(response, HttpStatus.SC_CREATED,
					HttpStatus.SC_NO_CONTENT);

		} finally {
			request.releaseConnection();
		}
	}

	private void expectAnyStatusCode(HttpResponse response,
			int... acceptedStatusCodes) {

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		for (int i = 0; i < acceptedStatusCodes.length; i++) {
			if (acceptedStatusCodes[i] == statusCode) {
				return;
			}
		}
		throw new BusinessException(
				"portlet.crud.error.rest.wrongStatus",
				statusCode, statusLine.getReasonPhrase());
	}

	private void sendUpdatePutRequest(GenericItem item)
			throws ClientProtocolException, IOException {
		byte[] content = creator.create(item, config.getUpdate().getValue(),
				config.getCharset());
		URI uri = createURI(item, config.getUpdate().getUrl());
		HttpPut request = new HttpPut(uri);
		ContentType contentType = createContentType();
		request.setEntity(new ByteArrayEntity(content, contentType));

		try {
			HttpResponse response = http.execute(request);
			expectAnyStatusCode(response, HttpStatus.SC_OK,
					HttpStatus.SC_NO_CONTENT);

		} finally {
			request.releaseConnection();
		}
	}

	private ContentType createContentType() {
		String mimetype = creator.getMimeType();
		if (config.getMimetype() != null) {
			mimetype = config.getMimetype();
		}
		return ContentType.create(
				mimetype, config.getCharset());
	}

	private void sendDeleteRequest(GenericItem item)
			throws ClientProtocolException, IOException {
		URI uri = createURI(item, config.getDelete().getUrl());
		HttpDelete request = new HttpDelete(uri);

		try {
			HttpResponse response = http.execute(request);
			expectAnyStatusCode(response, HttpStatus.SC_OK,
					HttpStatus.SC_ACCEPTED, // marked for deletion
					HttpStatus.SC_NO_CONTENT);

		} finally {
			request.releaseConnection();
		}
	}

	private URI createURI(GenericItem item, GroovyScript uriScript) {
		GenericContainerRowId containerRowId = new GenericContainerRowId(
				item.getId(), container.getPrimaryKeyColumns());
		GenericContainerRow containerRow = new GenericContainerRow(
				containerRowId, item, container, false, true);
		ScriptRow row = new ScriptRow(containerRow);
		Object postfix = scriptBuilder.buildClosure(uriScript).call(row);
		return createURI(postfix.toString());
	}
}
