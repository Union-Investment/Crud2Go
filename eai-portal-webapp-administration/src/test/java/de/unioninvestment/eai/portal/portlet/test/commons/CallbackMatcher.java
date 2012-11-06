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
package de.unioninvestment.eai.portal.portlet.test.commons;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Matcher, der es erlaubt den Matching-Algorithmus im Test mitzugeben
 * 
 * @param <T> Typ des zu match-enden Objektes
 *
 * @author max.hartmann
 * 
 * @see {@link Matcher}
 */
public abstract class CallbackMatcher<T> extends BaseMatcher<T> {
	
	private String descriptionText;
	
	public CallbackMatcher() {
		super();
	}

	public CallbackMatcher(String descriptionText) {
		this.descriptionText = descriptionText;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object obj) {
		return matchObject((T)obj);
	}
	
	public abstract boolean matchObject(T object);
	
	@Override
	public void describeTo(Description description) {
		if (descriptionText == null) {
			description.appendText("Object does not match");
		} else {
			description.appendText(descriptionText);
		}
	}
}
