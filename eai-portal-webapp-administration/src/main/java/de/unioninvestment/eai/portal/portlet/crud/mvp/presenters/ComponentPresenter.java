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
package de.unioninvestment.eai.portal.portlet.crud.mvp.presenters;

import de.unioninvestment.eai.portal.support.vaadin.mvp.Presenter;

/**
 * Ein Presenter für eine Komponente.
 * 
 * @author markus.bonsch
 * @author Jan Malcomess (codecentric AG)
 */
public interface ComponentPresenter extends Presenter {
	/**
	 * The expand ratio determines the amount of available space this
	 * presenter's component takes. For example, if two components define expand
	 * ratios 2 and 1, respectively, then the available space will be
	 * distributed 2/3 and 1/3 among the components. If expand ratio is 0 or
	 * less then the component will not expand. If only one component defines an
	 * expand ratio, than it will take up all available space regardless of the
	 * defined value.
	 * 
	 * @return expand ratio defining the relative amount of available space this
	 *         presenter's component wants to take up. 0 or less to deactivate
	 *         expansion.
	 * @since 1.45
	 */
	int getComponentExpandRation();
}
