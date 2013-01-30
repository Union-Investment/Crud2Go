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
package de.unioninvestment.eai.portal.portlet.crud.aspects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

public class SqlContainerLoggingAspectTest {

	private SqlContainerLoggingAspect aspect;
	private Object expectedResult;

	@Mock
	private ProceedingJoinPoint pjpMock;
	@Mock
	private Logger loggerMock;

	@Before
	public void setUp() throws Throwable {
		MockitoAnnotations.initMocks(this);
		SqlContainerLoggingAspect.LOG = loggerMock;
		when(loggerMock.isInfoEnabled()).thenReturn(true);
		when(loggerMock.isDebugEnabled()).thenReturn(true);

		aspect = new SqlContainerLoggingAspect();
		expectedResult = new Object();

		when(pjpMock.toShortString()).thenReturn("execution");
		when(pjpMock.proceed()).thenReturn(expectedResult);
	}

	@Test
	public void shouldReturnCorrectReturnValue() throws Throwable {
		assertThat(aspect.logResultQueries(pjpMock), is(expectedResult));
	}

	@Test
	public void shouldLogStartOnDebugLevel() throws Throwable {
		when(loggerMock.isInfoEnabled()).thenReturn(true);
		when(loggerMock.isDebugEnabled()).thenReturn(true);

		aspect.logResultQueries(pjpMock);
		verify(pjpMock).proceed();
		verify(loggerMock).debug("Starting execution");
	}

	@Test
	public void shouldLogEndAndDurationOnDebugLevel() throws Throwable {
		aspect.logResultQueries(pjpMock);

		verify(pjpMock).proceed();
		verify(loggerMock).debug(matches("Leaving execution \\(\\d+ms\\)"));
	}

	@Test
	public void shouldLogEndAndDurationForLongExecutionsOnInfoLevel()
			throws Throwable {
		when(loggerMock.isDebugEnabled()).thenReturn(false);
		when(pjpMock.proceed()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(SqlContainerLoggingAspect.SLOW_CALL_DURATION_MS + 10);
				return null;
			}
		});
		aspect.logResultQueries(pjpMock);

		verify(pjpMock).proceed();
		verify(loggerMock).info(matches("Leaving execution \\(\\d+ms\\)"));
		verify(loggerMock, never()).debug(anyString());
	}

	@Test
	public void shouldNotLogAnythingBelowInfoLevel() throws Throwable {
		when(loggerMock.isInfoEnabled()).thenReturn(false);

		aspect.logResultQueries(pjpMock);

		verify(pjpMock).proceed();
		verify(loggerMock).isInfoEnabled();
		verifyNoMoreInteractions(loggerMock);
	}
}
