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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
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

	/**
	 * <p>
	 * Replaces all occurrences of Strings within another String.
	 * </p>
	 * <p>
	 * For details and examples see
	 * {@link StringUtils#replaceEach(String, String[], String[])}.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> this method becomes obsolete when new groovy-version becomes
	 * available (2.1+) because then
	 * <code>String#collectReplacements(Closure)</code> should be used.
	 * 
	 * @param s
	 *            text to search and replace in, no-op if null
	 * @param searchList
	 *            the Strings to search for, no-op if null
	 * @param replacementList
	 *            the Strings to replace them with, no-op if null
	 * @return the text with any replacements processed, <code>null</code> if
	 *         null String input
	 * @throws IndexOutOfBoundsException
	 *             if the lengths of the arrays are not the same (null is ok,
	 *             and/or size 0)
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public static String replaceEach(String s, String[] searchList,
			String[] replacementList) {
		return StringUtils.replaceEach(s, searchList, replacementList);
	}

	/**
	 * <p>
	 * Converts all the whitespace separated words in a String into capitalized
	 * words, that is each word is made up of a titlecase character and then a
	 * series of lowercase characters.
	 * </p>
	 * <p>
	 * For details and examples see {@link WordUtils#capitalizeFully(String)}.
	 * </p>
	 * 
	 * @param s
	 *            the String to capitalize, may be null
	 * @return capitalized String, <code>null</code> if null String input
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public static String capitalizeFully(String s) {
		return WordUtils.capitalizeFully(s);
	}

	/**
	 * <p>
	 * Deletes all whitespaces from a String as defined by
	 * {@link Character#isWhitespace(char)}.
	 * </p>
	 * <p>
	 * For details and examples see {@link StringUtils#deleteWhitespace(String)}
	 * .
	 * </p>
	 * 
	 * @param s
	 *            the String to delete whitespace from, may be null
	 * @return the String without whitespaces, <code>null</code> if null String
	 *         input
	 * @since 1.46
	 * @author Jan Malcomess (codecentric AG)
	 */
	public static String deleteWhitespaces(String s) {
		return StringUtils.deleteWhitespace(s);
	}
}
