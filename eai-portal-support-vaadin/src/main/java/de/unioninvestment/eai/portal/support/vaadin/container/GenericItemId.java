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
package de.unioninvestment.eai.portal.support.vaadin.container;

import java.io.Serializable;
import java.util.Arrays;

/**
 * ID für {@link GenericItem}.
 * 
 * @author carsten.mjartan
 */
public class GenericItemId implements Serializable {

	private static final long serialVersionUID = 1L;
	protected final Object[] id;

	public GenericItemId(Object[] id) {
		this.id = id;
	}

	public boolean isNewId() {
		return false;
	}

	public Object[] getId() {
		return id;
	}

	@Override
	@SuppressWarnings("all")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(id);
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
		GenericItemId other = (GenericItemId) obj;
		if (!Arrays.equals(id, other.id))
			return false;
		return true;
	}

}
