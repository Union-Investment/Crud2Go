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

package de.unioninvestment.eai.portal.support.vaadin.security;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class RichTextAreaSanitizerTest {

	@Test
	public void shouldPassThroughValidHTML() {
		String result = RichTextAreaSanitizer
				.sanitize("Hallo<strong>Welt</strong>");
		assertEquals("Hallo<strong>Welt</strong>", result);
	}

	@Test
	public void shouldConvertHTMLSymbolsThatAreNotXMLConformant() {
		String result = RichTextAreaSanitizer
				.sanitize("Hallo<strong>Welt&nbsp;&Uuml;&ggg;</strong>");
		assertEquals("Hallo<strong>Welt" + ((char) 160)
				+ "Ü&amp;ggg;</strong>", result);
	}

	@Test
	public void shouldRemoveUnknownXmlAttributes() {
		String result = RichTextAreaSanitizer
				.sanitize("Hallo<strong class=\"t\">Welt</strong>");
		assertEquals("Hallo<strong>Welt</strong>", result);
	}

	@Test
	public void shouldKeepKnownXmlAttributes() {
		String result = RichTextAreaSanitizer
				.sanitize("Hallo<span style=\"color:#ffffff\">Welt</span>");
		assertEquals("Hallo<span style=\"color:#ffffff\">Welt</span>", result);
	}

	@Test
	public void shouldConvertValidRichTextExample() throws IOException {
		String example = readResourceFromClasspath("valid-richtext-example.html");
		String expected = readResourceFromClasspath("valid-richtext-sanitized.html");
		String result = RichTextAreaSanitizer.sanitize(example);
		assertEquals(expected, result);
	}

	private String readResourceFromClasspath(String name) throws IOException {
		String example = IOUtils.toString(RichTextAreaSanitizerTest.class
				.getResourceAsStream(name));
		return example;
	}

}
