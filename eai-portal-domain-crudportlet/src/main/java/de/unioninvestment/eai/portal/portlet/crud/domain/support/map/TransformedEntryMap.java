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
import java.util.Map;
import java.util.Set;

/**
 * Map die eine Backing-Map für das Auslesen von von Werten verwendet und dessen
 * Werte über einen {@link ValueTransformer} konvertiert.
 * 
 * Die Klasse kann überschrieben werden, um Schreiboperationen auf existierenden
 * Entries zu ermöglichen.
 * 
 * @author carsten.mjartan
 * 
 * @param <A>
 *            Datentyp der Schlüssel dieser Map.
 * @param <B>
 *            Datentyp, aus dem konvertiert wird.
 * @param <C>
 *            Zieldatantyp der Konvertierung, und gleichzeitig der Datentyp der
 *            Werte dieser Map.
 */
public class TransformedEntryMap<A, B, C> extends AbstractMap<A, C> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private Map<A, B> delegate;
	private ValueTransformer<B, C> transformer;

	/**
	 * @param delegate
	 *            die Backing-Map
	 * @param transformer
	 *            der Konverter für die Werte der Backing-Map
	 */
	public TransformedEntryMap(Map<A, B> delegate,
			ValueTransformer<B, C> transformer) {
		super();
		this.delegate = delegate;
		this.transformer = transformer;
	}

	@Override
	public Set<Map.Entry<A, C>> entrySet() {
		return new TransformedEntrySet<A, B, C>(delegate.entrySet(),
				transformer);
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

	/**
	 * @return den Transformer, der in einer Unterklasse benötigt wird um bei
	 *         put() den vorherigen Wert auszuliefern.
	 */
	protected ValueTransformer<B, C> getTransformer() {
		return transformer;
	}

}
