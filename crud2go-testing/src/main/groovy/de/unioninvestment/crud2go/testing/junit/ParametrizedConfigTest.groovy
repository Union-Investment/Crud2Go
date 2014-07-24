package de.unioninvestment.crud2go.testing.junit;

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener

@RunWith(Parameterized.class)
@TestExecutionListeners( [DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class ])
abstract class ParametrizedConfigTest extends CrudConfigTest{

	protected static Collection<Object[]> toObjectCollection(pathes){
		return pathes.collect {
			[it] as Object[]
		}
	}
}