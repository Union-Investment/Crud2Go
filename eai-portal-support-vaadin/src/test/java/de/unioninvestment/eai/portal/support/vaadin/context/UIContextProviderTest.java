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

package de.unioninvestment.eai.portal.support.vaadin.context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;

import de.unioninvestment.eai.portal.support.vaadin.junit.LiferayContext;

public class UIContextProviderTest {
	@Rule
	public LiferayContext vaadinMocks = new LiferayContext();

	@Mock
	protected ApplicationContext springContextMock;
	@Mock
	protected MessageSource messageSourceMock;

	private UIContextProvider provider;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		provider = new UIContextProvider();
		provideSpringApplicationContext();
	}

	private void provideSpringApplicationContext() {
		when(
				vaadinMocks
						.getPortletContextMock()
						.getAttribute(
								WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(springContextMock);
	}

	@Test
	public void shouldProvideSpringBeans() {
		Object testBean = new Object();
		when(springContextMock.getBean(Object.class)).thenReturn(testBean);

		assertThat(provider.getBean(Object.class), sameInstance(testBean));
	}

	@Test
	public void shouldReturnMessageFromSpringMessageSource() {
		when(springContextMock.getBean(MessageSource.class)).thenReturn(
				messageSourceMock);
		when(
				messageSourceMock.getMessage("mycode", new Object[] { 1, 2 },
						null)).thenReturn("my code 1,2");

		assertThat(provider.getMessage("mycode", 1, 2), is("my code 1,2"));
	}

	@Test
	public void shouldProvideMessageFallbackForTesting() {
		assertThat(provider.getMessage("mycode"), is("#mycode"));
		assertThat(provider.getMessage("mycode", 1, 2), is("#mycode[1, 2]"));
	}

}
