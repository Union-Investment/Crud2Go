/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package de.unioninvestment.eai.portal.support.vaadin.validation;

import static de.unioninvestment.eai.portal.support.vaadin.PortletUtils.getMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Erzeugt Feld-Validatoren aufgrund von kommaseparierten Validierungsregeln.
 * 
 * @author max.hartmann
 */
public class FieldValidatorFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(FieldValidatorFactory.class);

	private Map<String, Class<? extends FieldValidator>> validatorClassesByName = new HashMap<String, Class<? extends FieldValidator>>();

	/**
	 * Registriert default-Validatoren.
	 */
	public FieldValidatorFactory() {
		List<Class<? extends FieldValidator>> defaultValidators = new LinkedList<Class<? extends FieldValidator>>();
		defaultValidators.add(RequiredValidator.class);
		defaultValidators.add(MaxlenValidator.class);
		defaultValidators.add(RegexpValidator.class);
		setValidators(defaultValidators);
	}

	/**
	 * @param validatorClasses
	 *            registriert die angegebenen Validatoren anhand der Annotation
	 *            {@link ValidatorKeyword}
	 */
	public final void setValidators(
			List<Class<? extends FieldValidator>> validatorClasses) {

		validatorClassesByName.clear();

		for (Class<? extends FieldValidator> clazz : validatorClasses) {
			ValidatorKeyword keywordAnnotation = clazz
					.getAnnotation(ValidatorKeyword.class);
			if (keywordAnnotation != null) {
				validatorClassesByName.put(keywordAnnotation.value(), clazz);
			} else {
				throw new IllegalArgumentException("Validator Klasse " + clazz
						+ " hat keine @Validator Annotation");
			}
		}
	}

	/**
	 * @param commaSeparatedRules
	 *            komma-separierte Liste an Regeln
	 * @param validationMessage
	 *            Fehlermeldung, die im Falle eines Validierungsfehlers
	 *            angezeigt werden soll
	 * @return Liste von Validatoren, die gemäß der Regeln konfiguriert wurden
	 */
	public List<FieldValidator> createValidators(String commaSeparatedRules,
			String validationMessage) {
		if (commaSeparatedRules == null) {
			return null;
		}
		String msg = validationMessage;
		if (msg == null) {
			msg = getMessage("portlet.crud.error.validation.defaultMessage");
		}

		String[] ruleList = commaDelimitedListToStringArray(
				commaSeparatedRules, "\\");

		List<FieldValidator> validators = new ArrayList<FieldValidator>();
		for (String rule : ruleList) {
			parseAndAddRule(validators, msg, rule);
		}
		return validators;
	}

	private void parseAndAddRule(List<FieldValidator> validators,
			String validationMessage, String rule) {
		String[] ruleParts = rule.split("=", 2);
		Class<? extends FieldValidator> validatorClass = validatorClassesByName
				.get(ruleParts[0]);
		if (validatorClass != null) {
			try {
				if (ruleParts.length == 2) {
					addValidatorWithParam(validators, validatorClass,
							validationMessage, ruleParts[1]);
				} else {
					addValidatorWithoutParam(validators, validatorClass,
							validationMessage);
				}

			} catch (ValidationException e) {
				throw e;

			} catch (InvocationTargetException e) {
				throwOriginalError(e);

			} catch (Exception e) {
				throwInternalError(e);
			}
		} else {
			throw new ValidationException(
					"portlet.crud.error.validation.unknownRule", rule);
		}
	}

	private void throwOriginalError(InvocationTargetException e) {
		if (e.getTargetException() instanceof ValidationException) {
			throw (ValidationException) e.getTargetException();
		} else {
			throwInternalError(e);
		}
	}

	private void addValidatorWithoutParam(List<FieldValidator> validators,
			Class<? extends FieldValidator> validatorClass,
			String validationMessage) throws InstantiationException,
			IllegalAccessException, InvocationTargetException {
		try {
			Constructor<? extends FieldValidator> constructor = validatorClass
					.getConstructor(String.class);
			validators.add(constructor.newInstance(validationMessage));

		} catch (NoSuchMethodException e) {
			LOG.info("Error calling validator constructor", e);
			throw new ValidationException(
					"portlet.crud.error.validation.missingParameter",
					validatorClass.getSimpleName());
		}

	}

	private void addValidatorWithParam(List<FieldValidator> validators,
			Class<? extends FieldValidator> validatorClass,
			String validationMessage, String parameter)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		try {
			Constructor<? extends FieldValidator> constructor = validatorClass
					.getConstructor(String.class, String.class);
			validators.add(constructor
					.newInstance(parameter, validationMessage));

		} catch (NoSuchMethodException e) {
			LOG.info("Error calling validator constructor", e);
			throw new ValidationException(
					"portlet.crud.error.validation.parameterNotSupported",
					validatorClass.getSimpleName());
		}
	}

	private void throwInternalError(Exception e) {
		LOG.error("Fehler beim Instanzieren eines FieldValidators", e);
		throw new ValidationException("portlet.crud.error.internal");
	}

	/**
	 * 
	 * @param str
	 *            der kommaseparierte String von Validierungsregeln
	 * @param escapeChar
	 *            das Escape Zeichen.
	 * @return Ein Array mit den gespliteten Regeln.
	 */
	static String[] commaDelimitedListToStringArray(String str,
			String escapeChar) {
		// these characters need to be escaped in a regular expression
		String regularExpressionSpecialChars = "/.*+?|()[]{}\\";

		String escapedEscapeChar = escapeChar;

		// if the escape char for our comma separated list needs to be escaped
		// for the regular expression, escape it using the \ char
		if (regularExpressionSpecialChars.indexOf(escapeChar) != -1) {
			escapedEscapeChar = "\\" + escapeChar;
		}
		// see
		// http://stackoverflow.com/questions/820172/how-to-split-a-comma-separated-string-while-ignoring-escaped-commas
		String[] temp = str.split("(?<!" + escapedEscapeChar + "),", -1);

		// remove the escapeChar for the end result
		String[] result = new String[temp.length];
		for (int i = 0; i < temp.length; i++) {
			result[i] = temp[i].replaceAll(escapedEscapeChar + ",", ",");
		}

		return result;
	}

}
