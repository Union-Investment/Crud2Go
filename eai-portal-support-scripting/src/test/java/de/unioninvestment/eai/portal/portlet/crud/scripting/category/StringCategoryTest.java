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
package de.unioninvestment.eai.portal.portlet.crud.scripting.category;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import groovy.lang.GString;
import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

public class StringCategoryTest {

	@Test
	public void shouldReturnStringUnchanged() {
		String str = RandomStringUtils.randomAlphabetic(20);
		String result = StringCategory.shorten(str, 20, "{0}... ({1} Zeichen)");

		Assert.assertEquals(str, result);
	}

	@Test
	public void shouldReturnShortString() {

		String result = StringCategory.shorten("123456789012345678901", 20,
				"{0}... ({1} Zeichen)");
		assertThat(result, is("12345678901234567890... (21 Zeichen)"));
	}

	@Test
	public void shouldConvertStringToGString() {
		GString gs = StringCategory.toGString("1,2,3");
		assertThat(asList(gs.getStrings()), equalTo(asList("1,2,3")));
		assertThat(gs.getValueCount(), is(0));
	}
}
