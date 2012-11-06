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
import groovy.lang.GroovyCallable;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.runtime.GroovyCategorySupport;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.filter.Filter;

/**
 * 
 * Konfiguratinsklasse für einen Filterclosure.
 * 
 * @author siva.selvarajah
 */
public class FilterClosureCallable implements GroovyCallable<List<Filter>> {

	private Closure<?> filterClosure;

	/**
	 * 
	 * Klasse zum überschreiben des any-Closures.
	 * 
	 * @author siva.selvarajah
	 */
	public static class OverrideAny {
		/**
		 * Überschreibt das Any-Closure.
		 * 
		 * @param me
		 *            Closure
		 * @param cl
		 *            Closure
		 */
		public static void any(Closure<?> me, Closure<?> cl) {
			Closure<?> x = me;
			while (x.getDelegate() == null) {
				x = (Closure<?>) x.getOwner();
			}
			ScriptFilterFactory factory = (ScriptFilterFactory) me
					.getDelegate();
			factory.any(cl);
		}
	}

	/**
	 * Konfiguriert einen Filterclosure und ruft es dannach auf.
	 * 
	 * @param filterClosure
	 *            Zu konfigurierdes Closure
	 */
	public FilterClosureCallable(Closure<?> filterClosure) {
		this.filterClosure = filterClosure;
	}

	@Override
	public List<Filter> call() {
		List<Filter> filters = new ArrayList<Filter>();
		ScriptFilterFactory factory = new ScriptFilterFactory(filters);
		filterClosure.setDelegate(factory);
		filterClosure.setResolveStrategy(Closure.DELEGATE_ONLY);

		GroovyCategorySupport.use(OverrideAny.class, filterClosure);

		return filters;
	}

}
