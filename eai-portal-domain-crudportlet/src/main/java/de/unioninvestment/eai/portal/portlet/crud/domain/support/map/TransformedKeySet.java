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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Inhalt einer {@link TransformedKeyMap}.
 * 
 * @param <A>
 *            Datentyp des Schlüssels.
 * @param <C>
 *            Datentyp des Wertes.
 */
public class TransformedKeySet<A, C> extends AbstractSet<Entry<A, C>> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private Collection<A> keys;
	private ValueTransformer<A, C> transformer;

	public TransformedKeySet(Collection<A> keys,
			ValueTransformer<A, C> transformer) {
		this.keys = keys;
		this.transformer = transformer;
	}

	@Override
	public Iterator<Entry<A, C>> iterator() {
		final Iterator<A> keysIterator = keys.iterator();

		return new Iterator<Entry<A, C>>() {
			@Override
			public boolean hasNext() {
				return keysIterator.hasNext();
			}

			@Override
			public Entry<A, C> next() {
				return new TransformedKeyEntry<A, C>(keysIterator.next(),
						transformer);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return keys.size();
	}

}
