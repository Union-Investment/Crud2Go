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

package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.vaadin.data.util.sqlcontainer.query.OrderBy;

public class FreeformQueryEventWrapperTest {

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeys() {
		List<String> primaryKeys = asList("first", "second", "third");
		List<OrderBy> orderBys = asList(new OrderBy("second", false));
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(orderBys, primaryKeys);
		
		assertThat(newOrderBys.size(), is(3));
		assertThat(newOrderBys.get(0).getColumn(), is("second"));
		assertThat(newOrderBys.get(0).isAscending(), is(false));
		assertThat(newOrderBys.get(1).getColumn(), is("first"));
		assertThat(newOrderBys.get(1).isAscending(), is(true));
		assertThat(newOrderBys.get(2).getColumn(), is("third"));
		assertThat(newOrderBys.get(2).isAscending(), is(true));
	}

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeysWithNonPKOrder() {
		List<String> primaryKeys = asList("first", "second", "third");
		List<OrderBy> orderBys = asList(new OrderBy("fourth", false));
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(orderBys, primaryKeys);
		
		assertThat(newOrderBys.size(), is(4));
		assertThat(newOrderBys.get(0).getColumn(), is("fourth"));
		assertThat(newOrderBys.get(0).isAscending(), is(false));
		assertThat(newOrderBys.get(1).getColumn(), is("first"));
		assertThat(newOrderBys.get(1).isAscending(), is(true));
		assertThat(newOrderBys.get(2).getColumn(), is("second"));
		assertThat(newOrderBys.get(2).isAscending(), is(true));
		assertThat(newOrderBys.get(3).getColumn(), is("third"));
		assertThat(newOrderBys.get(3).isAscending(), is(true));
	}

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeysWithEmptyOrder() {
		List<String> primaryKeys = asList("first", "second", "third");
		List<OrderBy> orderBys = new LinkedList<OrderBy>();
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(orderBys, primaryKeys);
		
		assertDefaultPrimaryKeys(newOrderBys);
	}

	private void assertDefaultPrimaryKeys(List<OrderBy> newOrderBys) {
		assertThat(newOrderBys.size(), is(3));
		assertThat(newOrderBys.get(0).getColumn(), is("first"));
		assertThat(newOrderBys.get(0).isAscending(), is(true));
		assertThat(newOrderBys.get(1).getColumn(), is("second"));
		assertThat(newOrderBys.get(1).isAscending(), is(true));
		assertThat(newOrderBys.get(2).getColumn(), is("third"));
		assertThat(newOrderBys.get(2).isAscending(), is(true));
	}

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeysWithNullOrder() {
		List<String> primaryKeys = asList("first", "second", "third");
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(null, primaryKeys);
		
		assertDefaultPrimaryKeys(newOrderBys);
	}

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeysIfPrimaryKeysAreEmpty() {
		List<String> primaryKeys = new LinkedList<String>();
		List<OrderBy> orderBys = asList(new OrderBy("second", false));
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(orderBys, primaryKeys);
		
		assertThat(newOrderBys.size(), is(1));
		assertThat(newOrderBys.get(0).getColumn(), is("second"));
		assertThat(newOrderBys.get(0).isAscending(), is(false));
	}

	@Test
	public void shouldBuildOrderByIncludingPrimaryKeysIfPrimaryKeysAreNull() {
		List<OrderBy> orderBys = asList(new OrderBy("second", false));
		
		List<OrderBy> newOrderBys = FreeformQueryEventWrapper.createOrderBysIncludingPrimaryKeys(orderBys, null);

		assertThat(newOrderBys, sameInstance(orderBys));
	}
}
