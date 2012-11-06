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
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Map die ein Backing-Set für das Auslesen von der Keys verwendet und diese
 * Keys über einen {@link ValueTransformer} in Map-Values konvertiert.
 * 
 * Die Klasse kann überschrieben werden, um Schreiboperationen auf existierenden
 * Entries zu ermöglichen.
 * 
 * @author carsten.mjartan
 * 
 * @param <A> Datentyp der Schlüssel dieser Map.
 * @param <C> Datantyp der Werte dieser Map.
 */
public class TransformedKeyMap<A, C> extends AbstractMap<A, C> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private Collection<A> keys;
	private ValueTransformer<A, C> transformer;

	/**
	 * @param keys
	 *            Die Schlüssel für's keySet. Die Einträge müssen unique sein.
	 * @param transformer
	 *            der Konverter von key zu value
	 */
	public TransformedKeyMap(Collection<A> keys,
			ValueTransformer<A, C> transformer) {
		this.keys = keys;
		this.transformer = transformer;
	}

	@Override
	public Set<Map.Entry<A, C>> entrySet() {
		return new TransformedKeySet<A, C>(keys, transformer);
	}

	@Override
	public C put(A key, C value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public C remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends A, ? extends C> m) {
		throw new UnsupportedOperationException();
	}

	protected ValueTransformer<A, C> getTransformer() {
		return transformer;
	}

}
