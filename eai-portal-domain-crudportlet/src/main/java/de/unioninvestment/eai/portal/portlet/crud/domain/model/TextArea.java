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

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.vaadin.server.VaadinPortletService;

import de.unioninvestment.eai.portal.portlet.crud.config.TextAreaConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.validation.ConfigurationException;
import de.unioninvestment.eai.portal.support.vaadin.security.RichTextAreaSanitizer;

/**
 * Model-Klasse für &lt;textarea&gt;-Tag.
 */
public class TextArea extends Component implements
		Component.ExpandableComponent {

	private final TextAreaConfig config;
	private String content;
	private boolean editable;

	public enum Permission {
		BUILD, EDIT
	}

	/**
	 * @param config
	 *            the component's configuration.
	 * @param editable
	 */
	public TextArea(TextAreaConfig config, boolean editable) {
		this.config = config;
		this.editable = editable;

		if (config.getPreferenceKey() != null) {
			content = VaadinPortletService.getCurrentPortletRequest()
					.getPreferences().getValue(config.getPreferenceKey(), null);
		}
		if (content == null) {
			if (config.getContent() != null) {
				if (config.getContent().getContent().size() > 0) {
					content = convertNodeListToString(config.getContent()
							.getContent());
				}
			}
		}
	}

	private String convertNodeListToString(List<Object> domList) {
		try {
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			StringWriter buffer = new StringWriter();
			for (Object entry : domList) {
				if (entry instanceof String) {
					buffer.append((String) entry);
				} else if (entry instanceof Node) {
					transformer.transform(new DOMSource((Node) entry),
							new StreamResult(buffer));
				}
			}
			return buffer.toString();

		} catch (TransformerConfigurationException e) {
			throw new ConfigurationException(
					"Error deserializing textarea content", e);
		} catch (TransformerException e) {
			throw new ConfigurationException(
					"Error deserializing textarea content", e);
		}
	}

	@Override
	public int getExpandRatio() {
		return this.config.getExpandRatio() == null ? 0 : this.config.getExpandRatio();
	}

	public String getWidth() {
		return this.config.getWidth();
	}

	public String getHeight() {
		return this.config.getHeight();
	}

	public boolean isEditable() {
		return this.config.isEditable()
				&& this.config.getPreferenceKey() != null;
	}

	/**
	 * @return the XHTML content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Bereinigt und speichert den XHTML-Content.
	 * 
	 * @param changedContent
	 */
	public void updateContent(String changedContent) {
		if (isEditable()) {
			String sanitizedContent = RichTextAreaSanitizer
					.sanitize(changedContent);
			storeToPreferences(sanitizedContent);
			this.content = sanitizedContent;
		} else { 
			throw new IllegalStateException("Text area is not editable!");
		}
	}

	private void storeToPreferences(String changedContent) {
		try {
			PortletPreferences preferences = VaadinPortletService
					.getCurrentPortletRequest().getPreferences();

			preferences.setValue(config.getPreferenceKey(), changedContent);
			preferences.store();

		} catch (ReadOnlyException e) {
			throw new IllegalStateException("Error updating content", e);
		} catch (ValidatorException e) {
			throw new IllegalStateException("Error updating content", e);
		} catch (IOException e) {
			throw new IllegalStateException("Error updating content", e);
		}
	}
}
