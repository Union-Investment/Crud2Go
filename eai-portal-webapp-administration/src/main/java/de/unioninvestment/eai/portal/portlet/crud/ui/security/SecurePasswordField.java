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
package de.unioninvestment.eai.portal.portlet.crud.ui.security;

import org.springframework.util.Assert;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.PasswordField;

/**
 * This is a more secure variant of {@link PasswordField}. The Password is not
 * sent to the client, but will be sent in plaintext to the server if changed,
 * so securing the connection via encryption is recommended.
 * 
 * @author carsten.mjartan
 */
public class SecurePasswordField extends PasswordField {

	private static final long serialVersionUID = 1L;

	private static final String XXX = "xxxxxxxx";

	private Property realDataSource;

	private Object realValue;

	public SecurePasswordField(String caption, Property dataSource) {
		super(caption);
		Assert.isAssignable(String.class, dataSource.getType(),
				"SecurePasswordField only works for String properties");
		this.realDataSource = dataSource;

		realValue = realDataSource.getValue();
		setPropertyDataSource(new ObjectProperty(
				placeholder(realValue), String.class));
	}

	private String placeholder(Object realValue) {
		return realValue == null ? null : XXX;
	}

	@Override
	protected void setValue(Object newValue, boolean repaintIsNotNeeded)
			throws ReadOnlyException, ConversionException {
		if (newValue == null) {
			realValue = newValue;

		} else if (realValue == null || !newValue.equals(XXX)) {
			realValue = newValue;
		}
		super.setValue(placeholder(newValue), repaintIsNotNeeded);
		if (isWriteThrough() && realDataSource != null) {
			realDataSource.setValue(realValue);
		}
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		super.commit();
		realDataSource.setValue(realValue);
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
		realValue = realDataSource.getValue();
	}
}
