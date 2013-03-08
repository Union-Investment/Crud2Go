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
package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;

/**
 * Parser interface. Implmementations extract tabular data from the HTTP
 * response of a ReST request.
 * 
 * @author carsten.mjartan
 */
public interface PayloadParser {
	/**
	 * @param response
	 *            the ReST response
	 * @return data that is needed by GenericContainer.
	 * @throws IOException
	 *             propagated if thrown by a subroutine
	 */
	List<Object[]> getRows(HttpResponse response) throws IOException;

}
