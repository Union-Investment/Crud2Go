package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import static java.util.Collections.emptyList;
import groovy.lang.Closure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTAttributeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder;

public abstract class AbstractParser implements PayloadParser {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractParser.class);

	private ReSTContainerConfig config;

	private ScriptBuilder scriptBuilder;

	private ValueConverter converter = new ValueConverter();

	protected abstract Object parseData(Reader reader) throws IOException;

	public AbstractParser(ReSTContainerConfig config,
			ScriptBuilder scriptBuilder) {
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object[]> getRows(HttpResponse response)
			throws IOException {

		Reader reader = getReader(response);

		Object parsedData = parseData(reader);
		Iterable<?> collection = getCollection(parsedData);

		List<ReSTAttributeConfig> attributes = config
				.getQuery().getAttribute();

		Closure<?>[] attributeClosures = new Closure<?>[attributes.size()];
		Locale[] locales = new Locale[attributes.size()];
		Class<?>[] types = new Class<?>[attributes.size()];

		int i = 0;
		for (ReSTAttributeConfig attr : attributes) {
			attributeClosures[i] = scriptBuilder.buildClosure(attr.getPath());
			locales[i] = effectiveLocale(response, attr);
			types[i] = attr.getType();
			i++;
		}

		List<Object[]> result = new LinkedList<Object[]>();
		int counter = 0;
		int notNullCounter = 0;
		for (Object entry : (Iterable<?>) collection) {
			if (entry != null) {
				Object[] item = new Object[attributeClosures.length];
				for (int j = 0; j < item.length; j++) {
					ReSTAttributeConfig attr = attributes.get(j);

					Object valueFromClosure = callClosureAgainstDelegate(
							attributeClosures[j], entry);
					Object unmarshaledValue = unmarshalValue(valueFromClosure);
					Object convertedValue = converter.convertValue(types[j],
							attr.getFormat(), locales[j], unmarshaledValue);
					item[j] = convertedValue;
				}
				result.add(item);
				notNullCounter++;
			}
			counter++;
		}
		if (notNullCounter == 0 && counter > 0) {
			LOGGER.warn("ReST Collection only contained NULL elements. The collection may be wrong");
		}
		return result;
	}

	/**
	 * Parser specific conversion/unmarshaling
	 * 
	 * @param valueReturnedByClosure
	 * @return the unmarshaled value (e.g. a simple Java type)
	 */
	protected Object unmarshalValue(Object valueReturnedByClosure) {
		return valueReturnedByClosure;
	}

	protected Iterable<?> getCollection(Object parsedData) {

		Object collection = parsedData;

		GroovyScript collectionScript = config.getQuery()
				.getCollection();
		if (collectionScript != null && collectionScript.getSource() != null) {
			Closure<?> closure = scriptBuilder.buildClosure(collectionScript);

			try {
				collection = callClosureAgainstDelegate(closure, parsedData);
			} catch (NullPointerException e) {
				LOGGER.warn("NPE while querying for ReST collection. Assuming empty collection.");
				return emptyList();
			}
		}

		if (collection instanceof Iterable) {
			return (Iterable<?>) collection;
		} else if (collection == null) {
			LOGGER.info("ReST Collection is null. Assuming empty collection");
			return emptyList();

		} else {
			throw new InvalidConfigurationException(
					"portlet.crud.error.config.rest.wrongCollectionType",
					collection.getClass().getName());
		}
	}

	protected Reader getReader(HttpResponse response) throws IOException {
		ContentType contentType = ContentType.getOrDefault(response
				.getEntity());
		InputStream stream = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(stream,
				getResponseCharset(contentType));
		return reader;
	}

	private Charset getResponseCharset(ContentType contentType) {
		Charset charset = contentType.getCharset();
		if (charset == null) {
			charset = Charset.forName(config.getCharset());
		}
		return charset;
	}

	private Locale effectiveLocale(HttpResponse response,
			ReSTAttributeConfig attr) {
		Locale locale = response.getLocale();
		if (attr.getLocale() != null) {
			locale = LocaleUtils.toLocale(attr.getLocale());
		}
		return locale;
	}

	protected Object callClosureAgainstDelegate(Closure<?> closure,
			Object delegate) {
		closure.setDelegate(delegate);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		return closure.call(delegate);
	}

	/**
	 * @param converter
	 *            for Testing
	 */
	void setConverter(ValueConverter converter) {
		this.converter = converter;
	}

}
