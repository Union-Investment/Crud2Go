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
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.CollapseEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExpandEventHandler;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Region;

/**
 * Repräsentiert einen Bereich.
 * 
 * @author Frank Hardy (Codecentric AG)
 */
public class ScriptRegion extends ScriptPanel {

	private final Region region;

	private Closure<?> onExpand;
	private Closure<?> onCollapse;

	ScriptRegion(Region region) {
		super(region);
		this.region = region;
		this.registerEventHandlers();
	}

	/**
	 * @return die ID des Bereiches.
	 */
	public String getId() {
		return this.region.getId();
	}

	/**
	 * @return der Titel des Bereiches.
	 */
	public String getTitle() {
		return this.region.getTitle();
	}

	/**
	 * @return die Closure, die beim Aufklappen des Bereiches aufgerufen wird.
	 */
	public Closure<?> getOnExpand() {
		return this.onExpand;
	}

	/**
	 * @param onExpand
	 *            eine Closure die beim Aufklappen des Bereiches aufgerufen
	 *            wird.
	 */
	public void setOnExpand(Closure<?> onExpand) {
		this.onExpand = onExpand;
	}

	/**
	 * @return die Closure, die beim Zuklappen des Bereiches aufgerufen wird.
	 */
	public Closure<?> getOnCollapse() {
		return this.onCollapse;
	}

	/**
	 * @param onCollapse
	 *            eine Closure die beim Zuklappen des Bereiches aufgerufen wird.
	 */
	public void setOnCollapse(Closure<?> onCollapse) {
		this.onCollapse = onCollapse;
	}

	/**
	 * @return <code>true</code> wenn der Bereich zusammengeklappt ist.
	 */
	public boolean isCollapsed() {
		return this.region.isCollapsed();
	}

	/**
	 * Klappt den Bereich auf oder zu.
	 * 
	 * @param collapsed
	 *            <code>true</code> um den Bereich zusammen zu klappen.
	 */
	public void setCollapsed(boolean collapsed) {
		this.region.setCollapsed(collapsed);
	}

	private void registerEventHandlers() {
		this.region.addExpandEventHandler(new ExpandEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onExpand(ExpandEvent event) {
				if (onExpand != null) {
					onExpand.call(ScriptRegion.this);
				}
			}
		});
		this.region.addCollapseEventHandler(new CollapseEventHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onCollapse(CollapseEvent event) {
				if (onCollapse != null) {
					onCollapse.call(ScriptRegion.this);
				}
			}
		});
	}
}