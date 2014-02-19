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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessingInfo {

	private static class Category {
		int depth = 0;
		long duration = 0;
		long lastStart = 0;
	}

	private long startTime;
	private Map<String, Category> categories = new HashMap<String, Category>();
	private StringBuilder sqlStatements = null;
	private int countOfSqlStatements = 0;

	public RequestProcessingInfo() {
		startTime = System.currentTimeMillis();
	}

	public long getTimeSinceRequestStart() {
		return System.currentTimeMillis() - startTime;
	}

	public void startMeasuring(String category) {
		Category cat = categories.get(category);
		if (cat == null) {
			cat = new Category();
			categories.put(category, cat);
		}
		if (cat.depth++ == 0) {
			cat.lastStart = System.currentTimeMillis();
		}
	}

	public boolean isPartOfMeasurement(String category) {
		Category cat = categories.get(category);
		return cat != null && cat.depth > 0;
	}
	
	public void stopMeasuring(String category) {
		Category cat = categories.get(category);
		if (--cat.depth == 0) {
			cat.duration += (System.currentTimeMillis() - cat.lastStart);
		}
	}

	public long getMeasuredTime(String category) {
		Category cat = categories.get(category);
		return cat == null ? 0 : cat.duration;
	}

	public void addSqlStatement(String newStatement) {
		if (sqlStatements == null) {
			sqlStatements = new StringBuilder(newStatement);
		} else {
			sqlStatements.append("\n----\n").append(newStatement);
		}
		countOfSqlStatements++;
	}

	public String getSqlStatements() {
		return sqlStatements == null ? "" : sqlStatements.toString();
	}

	public int getCountOfSqlStatements() {
		return countOfSqlStatements ;
	}

	public Date getStartDate() {
		return new Date(startTime);
	}

}
