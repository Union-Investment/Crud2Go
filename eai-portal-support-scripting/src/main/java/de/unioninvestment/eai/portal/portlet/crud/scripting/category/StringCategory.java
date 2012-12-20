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
package de.unioninvestment.eai.portal.portlet.crud.scripting.category;

import groovy.lang.GString;

import java.text.MessageFormat;

import org.codehaus.groovy.runtime.GStringImpl;

/**
 * Erweitert die String Klasse um weitere Methoden.
 * 
 * @author eugen.melnichuk
 */
public class StringCategory {
	public static final int MAX_LENGTH = 5000;

	public static final String SHORTEN_PATTERN = "{0}... ({1} Zeichen)";

	/**
	 * Sollte der {@code s} länger {@code MAX_LENGTH} sein, werden nur die
	 * ersten {@code MAX_LENGTH} Zeichen zurück geliefert.
	 * 
	 * @param shortenedMessageFormat
	 * @param maxLength
	 */
	public static String shorten(String s, int maxLength,
			String shortenedMessageFormat) {
		if (s.length() > maxLength) {
			return MessageFormat.format(shortenedMessageFormat,
					s.substring(0, maxLength), s.length());
		} else {
			return s;
		}
	}

	public static GString toGString(String self) {
		return new GStringImpl(new Object[0], new String[] { self });
	}
}
