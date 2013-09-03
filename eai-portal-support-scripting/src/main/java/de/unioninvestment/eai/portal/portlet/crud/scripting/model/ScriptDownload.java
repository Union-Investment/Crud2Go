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

package de.unioninvestment.eai.portal.portlet.crud.scripting.model;

import groovy.lang.Closure;

import java.io.InputStream;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download;

/**
 * Implementation of {@link Download} that wraps the status and delegates
 * execution to a Groovy closure.
 * 
 * @author cmj
 */
public class ScriptDownload implements Download {

	/**
	 * The download status accessor that can be used for progress bar status
	 * updates.
	 */
	public class Status {

		private de.unioninvestment.eai.portal.portlet.crud.domain.model.Download.Status delegate;
		private int progressPos = 0;

		public Status(Download.Status status) {
			this.delegate = status;
		}

		/**
		 * Call to update a progress bar informing the user on the file creation
		 * progress. Advances the progress bar 1 unit.
		 */
		public void advanceProgress() {
			this.progressPos++;
			delegate.updateProgress(((float) progressPos / totalProgressSize));
		}
	}

	private String filename;
	private String mimeType;
	private Integer totalProgressSize;
	private Closure<InputStream> closure;

	/**
	 * Initializes download meta data.
	 * 
	 * @param filename
	 * @param mimeType
	 * @param totalProgressSize
	 *            number of steps to perform to be done.
	 * @param closure
	 */
	public ScriptDownload(String filename, String mimeType,
			Integer totalProgressSize, Closure<InputStream> closure) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.totalProgressSize = totalProgressSize;
		this.closure = closure;
	}

	@Override
	public InputStream build(Download.Status status) {
		Status wrappedStatus = new Status(status);
		closure.setDelegate(wrappedStatus);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		return closure.call(wrappedStatus);
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

}
