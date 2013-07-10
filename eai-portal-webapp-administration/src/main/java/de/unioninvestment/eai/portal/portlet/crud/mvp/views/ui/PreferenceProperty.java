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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views.ui;

import javax.portlet.PortletPreferences;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.VaadinPortletService;

import de.unioninvestment.eai.portal.portlet.crud.domain.exception.InvalidConfigurationException;

public class PreferenceProperty extends AbstractProperty<String> {

	private static final long serialVersionUID = 1L;

	private String preferenceKey;

	public PreferenceProperty(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	@Override
	public String getValue() {
		return preferences().getValue(preferenceKey, null);
	}

	@Override
	public void setValue(String newValue) throws ReadOnlyException,
			ConversionException {
		try {
			if (StringUtils.isEmpty(newValue)) {
				preferences().reset(preferenceKey);
			} else {
				preferences().setValue(preferenceKey, newValue);
			}

		} catch (javax.portlet.ReadOnlyException e) {
			throw new InvalidConfigurationException("The preference '"
					+ preferenceKey + "' is readonly");
		}
	}

	private PortletPreferences preferences() {
		return VaadinPortletService.getCurrentPortletRequest().getPreferences();
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

}