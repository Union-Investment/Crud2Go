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

public class LiferayLibraryIntegrationTest {

	/**
	 * @throws Exception
	 */
	public void userShouldBeCreatedAndRemoved() throws Exception {
		LiferayLibrary liferayLibrary = new LiferayLibrary("localhost:8080",
				"test", "test");

		try {
			liferayLibrary.addLiferayUser("unittest", "unittest", "Unit",
					"Test", "unit.test@codecentric.de", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			liferayLibrary.removeLiferayUser("unittest");
		}

	}

	public static void main(String[] args) throws Exception {
		new LiferayLibraryIntegrationTest().userShouldBeCreatedAndRemoved();
	}
}
