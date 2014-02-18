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

package de.unioninvestment.eai.portal.support.vaadin;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RequestProcessingInfoIntegrationTest {

	private RequestProcessingInfo info = new RequestProcessingInfo();

	@Test
	public void shouldMeasureTimeSinceInstantiation()
			throws InterruptedException {
		Thread.sleep(30L);

		long duration = info.getTimeSinceRequestStart();

		assertThat(duration >= 25, is(true));
		assertThat(duration < 35, is(true));
	}

	@Test
	public void shouldMeasureATaggedInterval() throws InterruptedException {
		measureForDB(10L);

		long duration = info.getMeasuredTime("db");
		assertThat(duration >= 5, is(true));
		assertThat(duration < 15, is(true));
	}

	@Test
	public void shouldMeasureATaggedIntervalTwoTimes()
			throws InterruptedException {
		measureForDB(15L);
		measureForDB(15L);
		long duration = info.getMeasuredTime("db");

		assertThat(duration >= 25, is(true));
		assertThat(duration < 35, is(true));
	}

	@Test
	public void shouldAllowReentrantMeasurement() throws InterruptedException {
		info.startMeasuring("db");
		Thread.sleep(15);
		info.startMeasuring("db");
		Thread.sleep(15);
		info.stopMeasuring("db");
		info.stopMeasuring("db");

		long duration = info.getMeasuredTime("db");
		assertThat(duration >= 25, is(true));
		assertThat(duration < 35, is(true));
	}

	@Test
	public void shouldReturnZeroIfNeverMeasured() throws InterruptedException {
		assertThat(info.getMeasuredTime("db"), is(0L));
	}

	private void measureForDB(long millis) throws InterruptedException {
		info.startMeasuring("db");
		Thread.sleep(millis);
		info.stopMeasuring("db");
	}

	@Test
	public void shouldRememberSqlStatements() throws InterruptedException {
		info.addSqlStatement("select * from TEST");

		assertThat(info.getSqlStatements(), is("select * from TEST"));
	}

	@Test
	public void shoulSeparateRememberedSqlStatementsByMinusRows()
			throws InterruptedException {
		info.addSqlStatement("select * from TEST");
		info.addSqlStatement("select * from TEST");
		
		assertThat(info.getSqlStatements(),
				is("select * from TEST\n----\nselect * from TEST"));
	}
	
	@Test
	public void shouldCountRememberedSqlStatements()
			throws InterruptedException {

		info.addSqlStatement("select * from TEST");
		assertThat(info.getCountOfSqlStatements(), is(1));

		info.addSqlStatement("select * from TEST");
		assertThat(info.getCountOfSqlStatements(), is(2));
		
		info.addSqlStatement("select * from TEST");
		assertThat(info.getCountOfSqlStatements(), is(3));
	}

}
