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

package de.unioninvestment.eai.portal.portlet.crud.ui.search;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

import de.unioninvestment.eai.portal.portlet.crud.ui.search.SearchBox.OptionHandler;

public class SearchOptionsHandler implements OptionHandler {

	private static final class DuplicateOperators implements Predicate<String> {
		private String head;

		public DuplicateOperators(String head) {
			this.head = head;
		}

		@Override
		public boolean apply(String input) {
			if (head != null) {
				if (head.endsWith(" AND ") || head.endsWith(" OR ")) {
					if (input.startsWith("AND ") || input.startsWith("OR ")) {
						return false;
					}
				}
			}
			return true;
		}
	}

	private static final class InvalidOperators implements Predicate<String> {
		private String filterString;

		public InvalidOperators(String filterString) {
			this.filterString = filterString;
		}

		@Override
		public boolean apply(String input) {
			if (input.startsWith("AND ") || input.startsWith("OR ")) {
				if (Strings.isNullOrEmpty(filterString)) {
					return false;
				} else if (filterString.trim().endsWith("(")) {
					return false;
				}
			}
			return true;
		}
	}

	private static final class AppendString implements Function<String, String> {
		private final String colon;

		private AppendString(String colon) {
			this.colon = colon;
		}

		@Override
		public String apply(String input) {
			return input + colon;
		}
	}

	private static final class PrependString implements
			Function<String, String> {
		private final String head;

		private PrependString(String head) {
			this.head = head;
		}

		@Override
		public String apply(String input) {
			return head + input;
		}
	}

	private static final class LowerStartsWith implements Predicate<String> {
		private final String lowerTail;

		private LowerStartsWith(String lowerTail) {
			this.lowerTail = lowerTail;
		}

		@Override
		public boolean apply(String input) {
			return input != null && input.toLowerCase().startsWith(lowerTail);
		}
	}

	final static List<String> keywords = asList("AND ", "OR ", "NOT ");
	private static final Pattern IN_RANGE_EXPR = Pattern.compile("[\\[\\{][^ \\)]+ $");
	Set<String> opsAndAttrsWithColon;

	public SearchOptionsHandler(Collection<String> attributes) {
		opsAndAttrsWithColon = new TreeSet<String>(keywords);
		opsAndAttrsWithColon.addAll( //
				FluentIterable.from(attributes)
						.transform(new AppendString(":")).toList());
	}

	@Override
	public Set<String> getOptions(String filterString) {
		Set<String> options = new TreeSet<String>();
		if (filterString == null) {
			filterString = "";
		}
		String head;
		String tail;
		if (filterString.endsWith("(")) {
			head = filterString;
			tail = "";
		} else {
			int lastSpaceIndex = filterString.lastIndexOf(' ');
			head = lastSpaceIndex == -1 ? "" : filterString.substring(0,
					lastSpaceIndex + 1);
			tail = filterString.substring(lastSpaceIndex + 1);
		}

		int lastColonIndex = tail.indexOf(':');
		if (IN_RANGE_EXPR.matcher(filterString).find()) {
			options.add(filterString + "TO ");
		} else if (filterString.endsWith(" TO ")) {
			// empty list
		} else if (lastColonIndex == -1) {
			String lowerTail = tail.toLowerCase();
			options.addAll(FluentIterable.from(opsAndAttrsWithColon) //
					.filter(new LowerStartsWith(lowerTail)) //
					.filter(new DuplicateOperators(head)) //
					.filter(new InvalidOperators(filterString)) //
					.transform(new PrependString(head)).toList());
		}
		return options;
	}
}