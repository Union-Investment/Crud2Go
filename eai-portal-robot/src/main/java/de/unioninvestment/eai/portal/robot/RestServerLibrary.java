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
package de.unioninvestment.eai.portal.robot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServerLibrary {

	private static final Logger logger = LoggerFactory
			.getLogger(RestServerLibrary.class);

	private Server server;

	static class RestHandler extends AbstractHandler {

		private int dataA = 1;
		private String dataB = "Test";
		private Double dataC = 4.0;
		private boolean deleted = false;

		@Override
		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			if (target.equals("/query")) {
				handleQuery(baseRequest, request, response);
			} else if (target.equals("/secureQuery")) {
				handleSecureQuery(baseRequest, request, response);
			} else if (target.equals("/insert")) {
				handleInsert(baseRequest, request, response);
			} else if (target.equals("/update/1")) {
				handleUpdate(baseRequest, request, response);
			} else if (target.equals("/delete/1")) {
				handleDelete(baseRequest, request, response);
			}
		}

		private void handleSecureQuery(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			String auth = request.getHeader("Authorization");
			if (auth == null) {
				response.addHeader("WWW-Authenticate",
						"Basic realm=\"RestServerLibrary\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"Please login");
				baseRequest.setHandled(true);
			} else if (auth.equals("Basic YWRtaW46YWRtaW4=")) {
				// bas64 of admin:admin
				handleQuery(baseRequest, request, response);
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,
						"Wrong login");
				baseRequest.setHandled(true);
			}
		}

		private void handleQuery(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException {

			response.setContentType("text/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			if (deleted) {
				response.getWriter().println("[]");
			} else {
				response.getWriter().println(
						"[ {\"a\":" + dataA
								+ ", \"b\":\"" + dataB
								+ "\", \"c\":" + dataC + "} ]");
			}
		}

		private void handleInsert(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			try {
				String input = IOUtils.toString(request.getInputStream(),
						"UTF-8");
				// assertThat(input,
				// is("<row><a>2</a><b>Test2</b><c>5.0</c></row>"));
				assertThat(input, is("{\"a\":2,\"b\":\"Test2\",\"c\":5.0}"));
				dataA = 2;
				dataB = "Test2";
				dataC = 5.0;
				response.setStatus(HttpServletResponse.SC_CREATED);
				baseRequest.setHandled(true);
			} catch (Exception e) {
				handleException(baseRequest, request, response, e);
			}
		}

		private void handleUpdate(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			String input = IOUtils.toString(request.getInputStream(), "UTF-8");
			// assertThat(input,
			// is("<row><a>2</a><b>Test2</b><c>5.0</c></row>"));
			try {
				assertThat(input, is("{\"a\":3,\"b\":\"Test3\",\"c\":6.0}"));
				dataA = 3;
				dataB = "Test3";
				dataC = 6.0;
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				baseRequest.setHandled(true);
			} catch (Exception e) {
				handleException(baseRequest, request, response, e);
			}
		}

		private void handleException(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response,
				Exception e) throws IOException {
			logger.error("Error handling request", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
			e.printStackTrace(response.getWriter());
			baseRequest.setHandled(true);
		}

		private void handleDelete(Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			deleted = true;
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			baseRequest.setHandled(true);
		}
	}

	public RestServerLibrary(int port) {
		server = new Server(port);
	}

	public void startRestServer() throws Exception {
		server.setHandler(new RestHandler());
		if (!server.isStarted()) {
			server.start();
			while (!server.isStarted()) {
				Thread.sleep(100);
			}
		}
	}

	public void stopRestServer() throws Exception {
		server.stop();
		while (!server.isStopped()) {
			Thread.sleep(100);
		}

	}
}
