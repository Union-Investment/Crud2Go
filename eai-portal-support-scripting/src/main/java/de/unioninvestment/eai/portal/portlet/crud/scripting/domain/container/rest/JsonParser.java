package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.json.JsonSlurper;

import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public class JsonParser extends AbstractParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonParser.class);

	public JsonParser(ReSTContainerConfig config, ScriptBuilder scriptBuilder) {
		super(config, scriptBuilder);
	}

	@Override
	protected Object parseData(Reader reader) {
		return new JsonSlurper().parse(reader);
	}

}
