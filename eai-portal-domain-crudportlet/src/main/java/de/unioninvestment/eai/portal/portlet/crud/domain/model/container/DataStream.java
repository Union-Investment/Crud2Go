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

package de.unioninvestment.eai.portal.portlet.crud.domain.model.container;

import java.util.Iterator;

/**
 * Abstraction for streaming of container rows, that allows faster iteration
 * than using the standard container API.
 * 
 * @author cmj
 */
public interface DataStream extends Iterator<StreamItem> {

	/**
	 * This method has to be called before iterating over the {@link StreamItem}
	 * s.
	 * 
	 * @param countEntries
	 *            if <code>true</code>, the container counts the number of items
	 *            that will be returned
	 * @return the estimated number of entries that will be returned
	 */
	int open(boolean countEntries);

	/**
	 * Close the datastream and release resources. Has to be called finally{}
	 */
	void close();
}
