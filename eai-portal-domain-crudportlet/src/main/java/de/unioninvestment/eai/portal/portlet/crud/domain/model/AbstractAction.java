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

import java.io.Serializable;

import org.springframework.util.Assert;

import de.unioninvestment.eai.portal.portlet.crud.config.AbstractActionConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEvent;
import de.unioninvestment.eai.portal.portlet.crud.domain.events.ExecutionEventHandler;
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventRouter;

/**
 * Oberklasse für Actions im Portlet-Modell.
 * 
 * @author carsten.mjartan
 */
public abstract class AbstractAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private Triggers triggers;

	protected final Portlet portlet;

	private EventRouter<ExecutionEventHandler, ExecutionEvent> executionEventRouter = new EventRouter<ExecutionEventHandler, ExecutionEvent>();

	private AbstractActionConfig config;

	/**
	 * Konstruktor.
	 * 
	 * @param portlet
	 *            Portletmodel
	 * @param triggers
	 *            Triggers
	 * @param config
	 *            Actionmodel
	 */
	public AbstractAction(Portlet portlet, AbstractActionConfig config,
			Triggers triggers) {
		Assert.notNull(portlet, "Portlet-Model required");
		Assert.notNull(config, "Configuration required");

		this.config = config;
		this.triggers = triggers;
		this.portlet = portlet;
	}

	public String getId() {
		return config.getId();
	}

	public String getTitle() {
		return config.getTitle();
	}

	/**
	 * Führt die an der Komponente hinterlegte Aktion durch und informiert die
	 * die registrierten {@link ExecutionEventHandler}.
	 */
	public void execute() {
		if (triggers == null) {
			return;
		}

		for (Trigger trigger : triggers.getTriggers()) {
			String actionId = trigger.getAction();

			Object object = portlet.getElementById(actionId);
			if (object instanceof AbstractAction) {
				AbstractAction action = (AbstractAction) object;
				action.execute();
			} else {
				throw new IllegalArgumentException(
						"Trigger referenziert keine Action.");
			}
		}
	}

	/**
	 * @param handler
	 *            ein zu registrierender {@link ExecutionEventHandler}
	 */
	public void addExecutionEventListener(ExecutionEventHandler handler) {
		executionEventRouter.addHandler(handler);
	}

	/**
	 * Führt die registrierten Events aus.
	 */
	protected void fireExecutionEvent() {
		executionEventRouter.fireEvent(new ExecutionEvent(this));
	}

	public Triggers getTriggers() {
		return triggers;
	}
}