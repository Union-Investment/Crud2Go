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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Field;

import de.unioninvestment.eai.portal.support.vaadin.test.commons.SupportVaadinSpringPortletContextTest;

@SuppressWarnings("unchecked")
public class FieldValidatorFactoryTest extends SupportVaadinSpringPortletContextTest {

	private FieldValidatorFactory factory;

	@ValidatorKeyword("dummy")
	public static class MyDummyValidator implements FieldValidator {
		public MyDummyValidator(String msg) {
			assertThat(msg, is("Fehler!"));
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("defaultMessage")
	public static class MyDefaultMessageValidator implements FieldValidator {
		public MyDefaultMessageValidator(String msg) {
			assertThat(msg, is("Die Feldeingabe ist ungültig"));
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("required")
	public static class MyRequiredValidator implements FieldValidator {
		public MyRequiredValidator(String msg) {
			assertThat(msg, is("Fehler!"));
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("regexp")
	public static class MyReqexpValidator implements FieldValidator {
		public MyReqexpValidator(String param, String msg) {
			assertThat(msg, is("Fehler!"));
			assertThat(param, is("[0-9]+"));
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("regexp")
	public static class MyReqexpWithCommaValidator implements FieldValidator {
		public MyReqexpWithCommaValidator(String param, String msg) {
			assertThat(msg, is("Fehler!"));
			assertThat(param, is("[0-9]+,[0-9]+"));
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("wrong")
	public static class MyWronglyConfiguredValidator implements FieldValidator {
		public MyWronglyConfiguredValidator(String param, String msg) {
			throw new ValidationException("wrong");
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@ValidatorKeyword("buggy")
	public static class MyBuggyValidator implements FieldValidator {
		public MyBuggyValidator(String msg) {
			throw new IllegalArgumentException("bug");
		}

		@Override
		public void apply(Field<?> field) {
			//
		}
	}

	@Before
	public void setup() {
		factory = new FieldValidatorFactory();
		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyDummyValidator.class,
				MyRequiredValidator.class));
	}

	@Test
	public void shouldDetectCorrectValidationClassByPrefix()
			throws ParseException {

		List<FieldValidator> validators = factory.createValidators("required",
				"Fehler!");
		assertThat(validators.size(), is(1));
		assertThat(validators.get(0), instanceOf(MyRequiredValidator.class));
	}

	@Test
	public void shouldApplyDefaultMessage() throws ParseException {

		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyDummyValidator.class,
				MyDefaultMessageValidator.class));

		List<FieldValidator> validators = factory.createValidators(
				"defaultMessage", null);
		assertThat(validators.size(), is(1));
		assertThat(validators.get(0),
				instanceOf(MyDefaultMessageValidator.class));
	}

	@Test
	public void shouldSupportEscapingOfComma() throws ParseException {

		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyDummyValidator.class,
				MyRequiredValidator.class, MyReqexpWithCommaValidator.class));

		List<FieldValidator> validators = factory.createValidators(
				"required,regexp=[0-9]+\\,[0-9]+,dummy", "Fehler!");
		assertThat(validators.size(), is(3));
		assertThat(validators.get(1),
				instanceOf(MyReqexpWithCommaValidator.class));
		assertThat(validators.get(2), instanceOf(MyDummyValidator.class));
	}

	@Test
	public void shouldAcceptRulesWithParameter() throws ParseException {
		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyDummyValidator.class,
				MyRequiredValidator.class, MyReqexpValidator.class));

		List<FieldValidator> validators = factory.createValidators(
				"required,regexp=[0-9]+,dummy", "Fehler!");
		assertThat(validators.size(), is(3));
		assertThat(validators.get(0), instanceOf(MyRequiredValidator.class));
		assertThat(validators.get(1), instanceOf(MyReqexpValidator.class));
		assertThat(validators.get(2), instanceOf(MyDummyValidator.class));
	}

	@Test
	public void shouldThrowErrorOnMissingParameter() throws ParseException {
		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyDummyValidator.class,
				MyReqexpValidator.class));

		try {
			factory.createValidators("regexp", "Fehler!");
			fail("should throw exception");

		} catch (ValidationException e) {
			assertEquals("portlet.crud.error.validation.missingParameter",
					e.getCode());
		}
	}

	@Test
	public void shouldThrowErrorOnUnsupportedParameter() throws ParseException {
		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyRequiredValidator.class,
				MyReqexpValidator.class));

		try {
			factory.createValidators("required=true", "Fehler!");
			fail("should throw exception");

		} catch (ValidationException e) {
			assertEquals("portlet.crud.error.validation.parameterNotSupported",
					e.getCode());
		}
	}

	@Test
	public void shouldThrowBusinessExceptionOnUnknownRule()
			throws ParseException {
		try {
			factory.createValidators("missing", "Fehler!");
			fail("should throw exception");

		} catch (ValidationException e) {
			assertEquals("portlet.crud.error.validation.unknownRule",
					e.getCode());
		}
	}

	@Test
	public void shouldThrowBusinessExceptionFromValidatorConstructor()
			throws ParseException {
		factory.setValidators(Arrays
				.asList((Class<? extends FieldValidator>) MyWronglyConfiguredValidator.class,
						MyReqexpValidator.class));

		try {
			factory.createValidators("wrong=x", "Fehler!");
			fail("should throw exception");

		} catch (ValidationException e) {
			assertEquals("wrong", e.getCode());
		}
	}

	@Test
	public void shouldThrowInternalErrorOnAnyUnknownExceptionTypes()
			throws ParseException {
		factory.setValidators(Arrays.asList(
				(Class<? extends FieldValidator>) MyBuggyValidator.class,
				MyReqexpValidator.class));

		try {
			factory.createValidators("buggy", "Fehler!");
			fail("should throw exception");

		} catch (ValidationException e) {
			assertEquals("portlet.crud.error.internal", e.getCode());
		}
	}

	@Test
	public void shouldReturnNullOnNullInput() throws ParseException {
		FieldValidatorFactory factory = new FieldValidatorFactory();
		assertNull(factory.createValidators(null, null));
		assertNull(factory.createValidators(null, "Bla"));
	}
}
