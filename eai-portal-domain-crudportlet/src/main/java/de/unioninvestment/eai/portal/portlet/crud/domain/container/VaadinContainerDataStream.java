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

package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Ordered;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

/**
 * "Generic" DataStream interface that uses a Vaadin-{@link Container} as
 * backing datasource. This is the default implementation returned by
 * {@link DataContainer#getStream()} and is meant to be overridden by more
 * performant {@link DataContainer} specific implementations.
 * 
 * @author cmj
 */
public class VaadinContainerDataStream implements DataStream {

	private Ordered container;

	private Object lastItemId = null;
	private Object nextItemId = null;

	public VaadinContainerDataStream(Ordered ordered) {
		this.container = ordered;
	}

	@Override
	public int open(boolean countEntries) {
		int size = countEntries ? container.size() : -1;
		lastItemId = null;
		return size;
	}

	@Override
	public boolean hasNext() {
		if (lastItemId == null) {
			nextItemId = container.firstItemId();
		} else {
			nextItemId = container.nextItemId(lastItemId);
		}
		return nextItemId != null;
	}

	@Override
	public StreamItem next() {
		StreamItem item = new VaadinContainerStreamItem(
				container.getItem(nextItemId));
		lastItemId = nextItemId;
		return item;
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
