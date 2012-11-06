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
package de.unioninvestment.eai.portal.portlet.crud.domain.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.unioninvestment.eai.portal.portlet.crud.domain.test.commons.DomainSpringPortletContextTest;

@ContextConfiguration({ "/eai-portal-web-test-applicationcontext.xml" })
public class BusinessExceptionTest extends DomainSpringPortletContextTest {

	@Test
	public void shouldTranslateCodeAndArgs() {
		BusinessException exception = new BusinessException("error.with.args",
				1, "2");
		assertEquals("arg1: 1, arg2: 2", exception.getMessage());
	}

	@Test
	public void shouldTranslateCodeWithoutArgs() {
		BusinessException exception = new BusinessException(
				"error.without.args");
		assertEquals("test", exception.getMessage());
	}

	@Test
	public void shouldGetArgs() {
		BusinessException exception = new BusinessException("error.with.args",
				1, "2");
		assertNotNull(exception.getArgs());

	}

	@Test
	public void shouldGetCode() {
		BusinessException exception = new BusinessException("error.with.args",
				1, "2");
		assertEquals("error.with.args", exception.getCode());

	}

	@Test
	public void shouldSupportFreeformMessage() {
		BusinessException exception = BusinessException
				.withFreeformMessage("abcde");
		assertEquals("abcde", exception.getMessage());
	}
}
