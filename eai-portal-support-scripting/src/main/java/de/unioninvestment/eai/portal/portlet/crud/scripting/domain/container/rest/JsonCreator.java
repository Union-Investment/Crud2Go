package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.json.JsonBuilder;
import groovy.lang.Closure;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRow;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.GenericContainerRowId;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ReSTContainer;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;
import de.unioninvestment.eai.portal.support.vaadin.container.GenericItem;

public class JsonCreator implements PayloadCreator {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonCreator.class);

	private ReSTContainerConfig config;
	private ReSTContainer container;
	private ScriptBuilder scriptBuilder;

	public JsonCreator(ReSTContainer container, ReSTContainerConfig config,
			ScriptBuilder scriptBuilder) {
		this.container = container;
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	public String getMimeType() {
		return "application/json";
	}

	@Override
	public byte[] create(GenericItem item, GroovyScript conversionScript,
			String charset) {
		Closure<?> closure = scriptBuilder.buildClosure(conversionScript);
		ScriptRow scriptRow = scriptRow(item);
		Object data = closure.call(scriptRow);

		JsonBuilder builder = new JsonBuilder(data);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generating JSON:\n{}", builder.toPrettyString());
		}
		String json = builder.toString();

		return getBytes(json, charset);
	}

	private byte[] getBytes(String json, String charset) {
		try {
			return json.getBytes(charset);

		} catch (UnsupportedEncodingException e) {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.unknownEncoding", charset);
		}
	}

	private ScriptRow scriptRow(GenericItem item) {
		GenericContainerRowId containerRowId = new GenericContainerRowId(
				item.getId(), container.getPrimaryKeyColumns());
		GenericContainerRow containerRow = new GenericContainerRow(
				containerRowId, item, container, false, true);
		return new ScriptRow(containerRow);
	}

}
