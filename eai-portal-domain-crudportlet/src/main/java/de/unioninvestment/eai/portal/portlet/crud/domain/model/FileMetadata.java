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
package de.unioninvestment.eai.portal.portlet.crud.domain.model;

import java.io.Serializable;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import de.unioninvestment.eai.portal.portlet.crud.domain.util.MimetypeRegistry;

/**
 * Metadata Modell Objekt für binär Dateien.
 * 
 * @author markus.bonsch
 * 
 */
public class FileMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String fileName;
	private final String filenameColumn;

	private final String mimeType;
	private final String mimetypeColumn;

	public String getMimetypeColumn() {
		return mimetypeColumn;
	}

	private final String downloadCaption;
	private final String uploadCaption;
	private final Integer maxFileSize;

	public FileMetadata(String fileName, String filenameColumn,
			String mimeType, String mimetypeColumn, String downloadCaption,
			String uploadCaption, Integer maxFileSize) {
		this.fileName = fileName;
		this.filenameColumn = filenameColumn;
		this.mimeType = mimeType;
		this.mimetypeColumn = mimetypeColumn;
		this.downloadCaption = downloadCaption;
		this.uploadCaption = uploadCaption;
		this.maxFileSize = maxFileSize;

		Preconditions.checkArgument((fileName != null)
				|| (filenameColumn != null),
				"Either fileName or filename-column must be set");
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilenameColumn() {
		return filenameColumn;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getDownloadCaption() {
		return downloadCaption;
	}

	public Integer getMaxFileSize() {
		return maxFileSize;
	}

	public String getUploadCaption() {
		return uploadCaption;
	}

	public String getCurrentDisplayname(ContainerRow row) {
		if (downloadCaption != null) {
			return downloadCaption;
		} else {
			return getCurrentFilename(row);
		}
	}
	
	public String getCurrentFilename(ContainerRow row) {
		String result = null;
		if (filenameColumn != null) {
			result = (String) row.getValue(filenameColumn);
		}
		if (result == null) {
			result = fileName;
		}
		return result;
	}

	public String getCurrentMimetype(ContainerRow row) {
		String result = null;
		if (mimetypeColumn != null) {
			result = (String) row.getValue(mimetypeColumn);
		}
		if (result == null) {
			result = mimeType;
		}
		if (result == null) {
			String currentFileName = getCurrentFilename(row);
			result = new MimetypeRegistry().detectFromFilename(currentFileName);
		}
		return result;
	}

}
