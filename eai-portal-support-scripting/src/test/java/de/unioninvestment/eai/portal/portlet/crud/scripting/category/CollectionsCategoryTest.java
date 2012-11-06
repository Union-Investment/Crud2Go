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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.unioninvestment.eai.portal.portlet.crud.scripting.category.CollectionsCategory;

public class CollectionsCategoryTest {

	@Test
	public void shouldPartitionWithoutRemainder() {
		List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

		List<List<Integer>> result = CollectionsCategory.partition(list, 3);

		assertThat(result.size(), is(3));
		assertThat(result.get(0), is(asList(0, 1, 2)));
		assertThat(result.get(1), is(asList(3, 4, 5)));
		assertThat(result.get(2), is(asList(6, 7, 8)));
	}

	@Test
	public void shouldPartitionWithRemainder() {
		List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
		List<List<Integer>> result = CollectionsCategory.partition(list, 3);

		assertThat(result.size(), is(3));
		assertThat(result.get(0), is(asList(0, 1, 2)));
		assertThat(result.get(1), is(asList(3, 4, 5)));
		assertThat(result.get(2), is(asList(6, 7)));
	}

	@Test
	public void shouldPartitionWithOnePartWithoutRemainder() {
		List<Integer> list = Arrays.asList(0, 1, 2);

		List<List<Integer>> result = CollectionsCategory.partition(list, 3);

		assertThat(result.size(), is(1));
		assertThat(result.get(0), is(asList(0, 1, 2)));
	}

	@Test
	public void shouldPartitionWithOnePartWithRemainder() {
		List<Integer> list = Arrays.asList(0);

		List<List<Integer>> result = CollectionsCategory.partition(list, 2);

		assertThat(result.size(), is(1));
		assertThat(result.get(0), is(asList(0)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnIllegalPartitionSize() {
		List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
		CollectionsCategory.partition(list, 0);
	}

	@Test
	public void shouldReturnEmptyListOnEmptyList() {
		List<Integer> list = Collections.emptyList();
		List<List<Integer>> result = CollectionsCategory.partition(list, 3);

		Assert.assertTrue(result.isEmpty());
	}
}
