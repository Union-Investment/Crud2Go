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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implementierung des entrySet() für die {@link TransformedEntryMap}.
 * 
 * @param <A>
 *            der Schlüsseltyp
 * @param <B>
 *            der Wertetyp des zu konvertierenden Wertes
 * @param <C>
 *            der Wertetyp
 * 
 * 
 * @author carsten.mjartan
 */
class TransformedEntrySet<A, B, C> extends AbstractSet<Entry<A, C>> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private Set<Entry<A, B>> delegate;
	private ValueTransformer<B, C> transformer;

	/**
	 * @param delegate
	 * @param transformer
	 */
	public TransformedEntrySet(Set<Entry<A, B>> delegate,
			ValueTransformer<B, C> transformer) {
		this.delegate = delegate;
		this.transformer = transformer;
	}

	@Override
	public Iterator<Entry<A, C>> iterator() {
		final Iterator<Entry<A, B>> namesIterator = delegate.iterator();

		return new Iterator<Entry<A, C>>() {
			@Override
			public boolean hasNext() {
				return namesIterator.hasNext();
			}

			@Override
			public Entry<A, C> next() {
				Entry<A, B> next = namesIterator.next();
				return new TransformedEntry<A, B, C>(next, transformer);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return delegate.size();
	}

}
