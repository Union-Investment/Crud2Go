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
package de.unioninvestment.eai.portal.portlet.crud.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.vaadin.addon.sqlcontainer.RowItem;
import com.vaadin.addon.sqlcontainer.TemporaryRowId;

/**
 * Aspekt der das Verhalten der private-Methode itemPassesFilters()
 * überschreibt, so dass temporäre (neue) Zeilen nicht weggefiltert werden.
 * 
 * @author carsten.mjartan
 */
@Aspect
public class SQLContainerPatchAspect {

	/**
	 * @param pjp
	 *            die eigentlich aufgerufene Methode
	 * @param windowId
	 *            die WindowID des aktuellen Portlets
	 * @return eine gecachete oder neu gelesene PortletConfig Instanz
	 * @throws Throwable
	 *             bei Fehler
	 */
	@Around(value = "execution(* com.vaadin.addon.sqlcontainer.SQLContainer.itemPassesFilters(..)) && args(item)")
	public Object doNotFilterTemporaryRows(ProceedingJoinPoint pjp, RowItem item)
			throws Throwable {
		if (item.getId() instanceof TemporaryRowId) {
			return true;
		} else {
			return pjp.proceed();
		}
	}
}
