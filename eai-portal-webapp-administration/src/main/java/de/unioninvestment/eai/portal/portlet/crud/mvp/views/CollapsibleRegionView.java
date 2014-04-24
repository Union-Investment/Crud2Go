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
package de.unioninvestment.eai.portal.portlet.crud.mvp.views;

/**
 * Die Schnittstellendefinition einer Bereichs-View die auf- zu zuklappbar ist.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public interface CollapsibleRegionView extends RegionView {

	/**
	 * Anforderungen an den Presenter.
	 * 
	 * @author Frank Hardy (Codecentric AG)
	 */
	interface Presenter {

		/**
		 * Signalisiert eine Änderung des Zustands (auf- oder zugeklappt)
		 * 
		 * @param collapsed
		 *            <code>true</code> wenn der Bereich zugeklappt wurde.
		 */
		void regionHasBeenCollapsed(boolean collapsed);
	}

	/**
	 * Initialisiert die View mit dem Presenter.
	 * 
	 * @param presenter
	 *            der Presenter.
	 */
	void setPresenter(CollapsibleRegionView.Presenter presenter);

	/**
	 * @return <code>true</code> wenn der Bereich zusammengeklappt ist.
	 */
	boolean isCollapsed();

	/**
	 * @param collapsed
	 *            <code>true</code> um den Bereich zusammen zu klappen.
	 */
	void setCollapsed(boolean collapsed);
}
