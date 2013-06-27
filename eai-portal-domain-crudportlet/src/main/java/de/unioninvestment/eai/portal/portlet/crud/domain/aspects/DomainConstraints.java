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
package de.unioninvestment.eai.portal.portlet.crud.domain.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Constraints for the domain layer.
 * 
 * @author carsten.mjartan
 */
@Aspect
public class DomainConstraints {

	/**
	 * Methods with @DuringBuild annotation are only allowed to be called from within buildup classes
	 */
	@DeclareError("DomainArchitecture.inDomain() "
			+ "&& !DomainArchitecture.inBuildClass() "
			+ "&& accessMethodsForBuild()")
	static final String DISALLOW_RUNTIME_ACCESS_TO_METHODS_FOR_BUILD = "build operations are only allowed during build phase";

	/**
	 * Methods with @DuringBuild annotation
	 */
	@Pointcut("call(@DuringBuild * de.unioninvestment.eai.portal.portlet.crud.domain..*(..))")
	public void accessMethodsForBuild() {
		// pointcut specification
	}

}
