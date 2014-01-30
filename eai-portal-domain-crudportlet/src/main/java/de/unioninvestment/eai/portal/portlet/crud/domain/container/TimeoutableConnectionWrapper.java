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

package de.unioninvestment.eai.portal.portlet.crud.domain.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

public class TimeoutableConnectionWrapper {

	public static JDBCConnectionPool wrapPool(final JDBCConnectionPool delegate) {
		return new TimeoutableConnectionPool(delegate);
	}

	public static Connection wrapConnection(final Connection delegate) {
		TimeoutableConnection wrapper = (TimeoutableConnection) Proxy
				.newProxyInstance(
						TimeoutableConnectionWrapper.class.getClassLoader(),
						new Class[] { TimeoutableConnection.class },
						new DefaultTimeoutInvocationHandler(delegate));
		return wrapper;
	}

	private static final class TimeoutableConnectionPool implements
			JDBCConnectionPool {
		private final JDBCConnectionPool delegate;
		private static final long serialVersionUID = 1L;

		private TimeoutableConnectionPool(JDBCConnectionPool delegate) {
			this.delegate = delegate;
		}

		@Override
		public Connection reserveConnection() throws SQLException {
			return wrapConnection(delegate.reserveConnection());
		}

		@Override
		public void releaseConnection(Connection conn) {
			if (conn instanceof TimeoutableConnection) {
				delegate.releaseConnection(((TimeoutableConnection) conn)
						.getWrappedConnection());
			} else {
				delegate.releaseConnection(conn);
			}
		}

		@Override
		public void destroy() {
			delegate.destroy();
		}
	}

	private static final class DefaultTimeoutInvocationHandler implements
			InvocationHandler {
		private final Connection delegate;
		private int queryTimeout = 0;

		private DefaultTimeoutInvocationHandler(Connection delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.getName().equals("setDefaultTimeout")) {
				this.queryTimeout = (Integer) args[0];
				return null;
			} else if (method.getName().equals("getWrappedConnection")) {
				return delegate;
			} else if (method.getName().equals("prepareStatement")
					|| method.getName().equals("createStatement")) {
				Statement stmt = (Statement) method.invoke(delegate, args);
				configureQueryTimeout(stmt);
				return stmt;

			} else {
				try {
					return method.invoke(delegate, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		}

		private void configureQueryTimeout(Statement statement)
				throws SQLException {
			if (queryTimeout > 0) {
				statement.setQueryTimeout(queryTimeout);
			}
		}
	}
}
