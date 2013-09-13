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
package de.unioninvestment.eai.portal.portlet.crud.domain.support.map;

import java.util.Map.Entry;

/**
 * Ein {@link Entry} für die {@link TransformedKeyMap}.
 * 
 * @param <A>
 *            der Schlüsseltyp
 * @param <C>
 *            der Wertetyp
 * 
 * @author carsten.mjartan
 */
public class TransformedKeyEntry<A, C> implements Entry<A, C> {

	private final A key;
	private final ValueTransformer<A, C> transformer;

	/**
	 * @param key
	 *            der Schlüsselwert
	 * @param transformer
	 *            der Konverter
	 */
	public TransformedKeyEntry(A key, ValueTransformer<A, C> transformer) {
		super();
		this.key = key;
		this.transformer = transformer;
	}

	@Override
	public A getKey() {
		return key;
	}

	@Override
	public C getValue() {
		return transformer.transform(key);
	}

	@Override
	public C setValue(C value) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((transformer == null) ? 0 : transformer.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransformedKeyEntry other = (TransformedKeyEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (transformer == null) {
			if (other.transformer != null)
				return false;
		} else if (!transformer.equals(other.transformer))
			return false;
		return true;
	}

}
