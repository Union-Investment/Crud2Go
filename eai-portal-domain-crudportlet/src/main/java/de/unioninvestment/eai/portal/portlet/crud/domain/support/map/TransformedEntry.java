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

import java.util.Map;
import java.util.Map.Entry;

/**
 * Ein {@link Entry} für die {@link TransformedEntryMap}.
 * 
 * @param <A>
 *            der Schlüsseltyp
 * @param <B>
 *            der Wertetyp des zu konvertierenden Wertes
 * @param <C>
 *            der Wertetyp
 * 
 * @author carsten.mjartan
 */
public class TransformedEntry<A, B, C> implements Map.Entry<A, C> {

	private static final long serialVersionUID = 1L;
	private final Entry<A, B> delegate;
	private final ValueTransformer<B, C> transformer;

	/**
	 * @param delegate
	 *            der Backing-Entry
	 * @param transformer
	 *            der Konverter
	 */
	public TransformedEntry(Entry<A, B> delegate,
			ValueTransformer<B, C> transformer) {
		this.delegate = delegate;
		this.transformer = transformer;
	}

	@Override
	public A getKey() {
		return delegate.getKey();
	}

	@Override
	public C getValue() {
		return transformer.transform(delegate.getValue());
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
		result = prime * result
				+ ((delegate == null) ? 0 : delegate.hashCode());
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
		TransformedEntry other = (TransformedEntry) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		if (transformer == null) {
			if (other.transformer != null)
				return false;
		} else if (!transformer.equals(other.transformer))
			return false;
		return true;
	}

}
