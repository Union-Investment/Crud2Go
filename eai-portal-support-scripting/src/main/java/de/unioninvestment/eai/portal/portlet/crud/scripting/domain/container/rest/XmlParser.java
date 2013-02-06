package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.lang.Closure;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public class XmlParser extends AbstractParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XmlParser.class);

	private ReSTContainerConfig config;
	private ScriptBuilder scriptBuilder;

	public XmlParser(ReSTContainerConfig config, ScriptBuilder scriptBuilder) {
		super(config, scriptBuilder);
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

	@Override
	protected Object parseData(Reader reader) throws IOException {
		try {
			return new XmlSlurper().parse(reader);

		} catch (SAXException e) {
			throw new TechnicalCrudPortletException("Invalid XML", e);

		} catch (ParserConfigurationException e) {
			throw new TechnicalCrudPortletException(
					"Serious parser configuration error", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Iterable<?> getCollection(Object parsedData) {

		Object collection = parsedData;

		GroovyScript collectionScript = config.getQuery()
				.getCollection();
		if (collectionScript != null && collectionScript.getClazz() != null) {
			Closure<?> closure = scriptBuilder.buildClosure(collectionScript);
			final Object closureResult = callClosureAgainstDelegate(closure,
					parsedData);
			if (closureResult instanceof GPathResult) {
				collection = new Iterable<Object>() {
					@Override
					public Iterator<Object> iterator() {
						return ((GPathResult) closureResult).iterator();
					}
				};
			}
		}

		if (collection instanceof Iterable) {
			return (Iterable<?>) collection;
		} else if (collection == null) {
			LOGGER.info("ReST Collection is null, assuming empty list");
			return new ArrayList<Object[]>(0);

		} else {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.wrongCollectionType",
					collection.getClass().getName());
		}
	}

	@Override
	protected Object unmarshalValue(Object valueReturnedByClosure) {
		if (valueReturnedByClosure instanceof GPathResult) {
			return ((GPathResult) valueReturnedByClosure).text();
		} else {
			return valueReturnedByClosure;
		}
	}
}
