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

/**
 * 
 * Model-Oberklasse für eine View-Komponente/Element.
 * 
 * @author markus.bonsch
 * 
 */
public abstract class Component {

	private Panel panel;

	public Panel getPanel() {
		return panel;
	}

	void setPanel(Panel panel) {
		this.panel = panel;
	}

	/**
	 * A component that can be configured to expand in size to take up all
	 * available space in the layout - that is: space, that is not taken up by
	 * other components. Expansion depends on the layout the component resides
	 * in: if vertical (which is most of the time) than expansion happens by
	 * increasing the components height; if horizontal (currently only possible
	 * when placed inside a correspondingly configured <code>Region</code>; @see
	 * <code>{@link Region#isHorizontalLayout()}</code>) then expansion happens
	 * by increasing the components width.<br>
	 * Please note, because not every component is expandable - for example, it
	 * makes not sense for a dialog or a page to provide an expand ratio -
	 * expandibility has been put into an interface.
	 * 
	 * @author Jan Malcomess (codecentric AG)
	 * @since 1.45
	 */
	public static interface ExpandableComponent {
		/**
		 * The expand ratio determines the amount of available space this
		 * component takes. For example, if two components define expand ratios
		 * 2 and 1, respectively, then the available space will be distributed
		 * 2/3 and 1/3 among the components. If expand ratio is 0 or less then
		 * the component will not expand. If only one
		 * <code>ExpandableComponent</code> defines an expand ratio, than it
		 * will take up all available space regardless of the defined value.
		 * 
		 * @return expand ratio defining the relative amount of available space
		 *         this component wants to take up. 0 or less to deactivate
		 *         expansion.
		 */
		public abstract int getExpandRatio();
	}
}
