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

import java.util.List;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

/**
 * Wrapper over an existing stream that adds generated column values.
 * 
 * @author cmj
 */
public class GeneratedColumnsDataStreamWrapper implements DataStream {

	private final DataStream delegate;
	private final List<String> columnNames;
	private final TableColumns columns;

	public GeneratedColumnsDataStreamWrapper(DataStream delegate,
			List<String> columnNames, TableColumns columns) {
		this.delegate = delegate;
		this.columnNames = columnNames;
		this.columns = columns;
	}

	@Override
	public int open(boolean countEntries) {
		return delegate.open(countEntries);
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public StreamItem next() {
		return new GeneratedColumnsStreamItemWrapper(delegate.next(),
				columnNames, columns);
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void remove() {
		delegate.remove();
	}

}
