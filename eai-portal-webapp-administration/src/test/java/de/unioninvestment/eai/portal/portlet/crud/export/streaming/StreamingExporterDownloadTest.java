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

package de.unioninvestment.eai.portal.portlet.crud.export.streaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DataContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download.Status;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.DataStream;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.container.StreamItem;

public class StreamingExporterDownloadTest {

	private StreamingExporterDownload download;

	@Mock
	private Table tableMock;
	@Mock
	private Exporter exporterMock;
	@Mock
	private DataContainer containerMock;
	@Mock
	private DataStream streamMock;
	@Mock
	private StreamItem itemMock;
	@Mock
	private Status statusMock;
	@Mock
	private InputStream inputStreamMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(tableMock.getContainer()).thenReturn(containerMock);

		download = new StreamingExporterDownload("myfile.txt", "text/plain",
				tableMock, exporterMock);
	}

	@Test
	public void shouldReturnMetadata() {
		assertThat(download.getFilename(), is("myfile.txt"));
		assertThat(download.getMimeType(), is("text/plain"));
	}

	@Test
	public void shouldPassTableDataStreamToExporter() {
		download.statusUpdateInterval = 2;
		when(tableMock.getStream()).thenReturn(streamMock);
		when(streamMock.open(true)).thenReturn(2);
		when(streamMock.hasNext()).thenReturn(true, true, false);
		when(streamMock.next()).thenReturn(itemMock);
		when(exporterMock.getInputStream()).thenReturn(inputStreamMock);

		InputStream inputStream = download.build(statusMock);

		verify(exporterMock).begin(isA(StreamingExportInfo.class));
		verify(exporterMock, times(2)).addRow(isA(StreamItemInfoWrapper.class));
		verify(statusMock, times(1)).updateProgress(1.0f);
		assertThat(inputStream, sameInstance(inputStreamMock));
	}

	@Test
	public void shouldCloseStreamOnError() {
		when(tableMock.getStream()).thenReturn(streamMock);
		when(streamMock.open(true)).thenReturn(2);
		when(streamMock.hasNext()).thenReturn(true);
		when(streamMock.next()).thenThrow(new RuntimeException("Error"));
		try {
			download.build(statusMock);
			fail();
		} catch (RuntimeException e) {
			verify(streamMock).close();
		}

	}
}
