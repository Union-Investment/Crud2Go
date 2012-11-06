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

import java.util.ArrayList;
import java.util.List;

/**
 * Erweitert alle Klasse, die von {@code java.util.List} ableiten um die Methode
 * {@code partition()}.
 * 
 * @author bastian.krol
 * @author eugen.melnichuk
 */
public class CollectionsCategory {

	/**
	 * Teilt eine Liste in mehrere Liste von der Grösse {@code partitionSize}
	 * oder kleiner.
	 * 
	 * @param partitionSize
	 *            Die maximalle Grösse der Teillisten.
	 * 
	 */
	static public <T> List<List<T>> partition(List<T> list, int partitionSize) {
		if (partitionSize < 1) {
			throw new IllegalArgumentException(
					"size of partition must be greater than 0.");
		}

		List<List<T>> partitions = new ArrayList<List<T>>();
		int partitionCount = list.size() / partitionSize;

		for (int i = 0; i < partitionCount; i++) {
			int from = partitionSize * i;
			int to = from + partitionSize;

			partitions.add(list.subList(from, to));
		}

		int remainder = list.size() % partitionSize;
		if (remainder != 0) {
			partitions.add(list.subList(list.size() - remainder, list.size()));
		}

		return partitions;
	}
}
