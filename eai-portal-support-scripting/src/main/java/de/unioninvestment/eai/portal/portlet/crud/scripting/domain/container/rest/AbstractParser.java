package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

	protected abstract Object parseData(Reader reader) throws IOException;

	public AbstractParser(ReSTContainerConfig config,
			ScriptBuilder scriptBuilder) {
		this.config = config;
		this.scriptBuilder = scriptBuilder;
	}

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
		for (Object entry : (Iterable<?>) collection) {
			Object[] item = new Object[attributeClosures.length];
			for (int j = 0; j < item.length; j++) {
				ReSTAttributeConfig attr = attributes.get(j);

				Object value = convertValue(callClosureAgainstDelegate(
						attributeClosures[j], entry));
				Object formattedValue = parseValue(types[j],
						attr.getFormat(), locales[j], value);
				item[j] = formattedValue;
			}
			result.add(item);
		}
		return result;
	}

	protected Object convertValue(Object valueReturnedByClosure) {
		return valueReturnedByClosure;
	}

	protected Iterable<?> getCollection(Object parsedData) {

		Object collection = parsedData;

		GroovyScript collectionScript = config.getQuery()
				.getCollection();
		if (collectionScript != null && collectionScript.getClazz() != null) {
			Closure<?> closure = scriptBuilder.buildClosure(collectionScript);
			collection = callClosureAgainstDelegate(closure, parsedData);
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

	protected Reader getReader(HttpResponse response) throws IOException {
		ContentType contentType = ContentType.getOrDefault(response
				.getEntity());
		InputStream stream = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(stream,
				contentType.getCharset());
		return reader;
	}

	private Locale effectiveLocale(HttpResponse response,
			ReSTAttributeConfig attr) {
		Locale locale = response.getLocale();
		if (attr.getLocale() != null) {
			locale = LocaleUtils.toLocale(attr.getLocale());
		}
		return locale;
	}

	private Object parseValue(Class<?> type, String format, Locale locale,
			Object value) {
		if (value == null) {
			return null;
		} else if (type == Date.class) {
			if (value instanceof Date) {
				return value;
			} else if (value instanceof Number) {
				return new Date(((Number) value).longValue());
			} else if (value instanceof String) {
				try {
					return new SimpleDateFormat(format, locale)
							.parse((String) value);
				} catch (ParseException e) {
					throw new IllegalArgumentException(
							"Cannot convert to date: "
									+ value, e);
				}
			} else {
				throw new IllegalArgumentException("Cannot convert to date: "
						+ value);
			}
		} else {
			return value;
		}
	}

	protected Object callClosureAgainstDelegate(Closure<?> closure,
			Object delegate) {
		closure.setDelegate(delegate);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		return closure.call();
	}

}
