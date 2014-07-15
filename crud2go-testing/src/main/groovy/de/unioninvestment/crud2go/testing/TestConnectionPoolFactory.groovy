package de.unioninvestment.crud2go.testing;

import javax.naming.NamingException;
import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import de.unioninvestment.eai.portal.portlet.crud.domain.database.AbstractConnectionPool;
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory

class TestConnectionPoolFactory implements ConnectionPoolFactory {

	@Autowired @Qualifier("testDataSource")
	private DataSource testDataSource;

	@Autowired @Qualifier("eaiDataSource")
	private DataSource eaiDataSource;

	@Autowired @Qualifier("ikdatatoolDataSource")
	private DataSource ikdatatoolDataSource;

	@Autowired @Qualifier("meldReadDataSource")
	private DataSource meldReadDataSource;

	@Autowired @Qualifier("vcladezoneDataSource")
	private DataSource vcladezoneDataSource;

	ConnectionPool getPool(String name) {
		switch (name) {
			case "test": return new TestConnectionPool("test", testDataSource)
			case "eai": return new TestConnectionPool("eai", eaiDataSource)
			case "MELD_READ": return new TestConnectionPool("MELD_READ", meldReadDataSource)
			case "ikdatatool": return new TestConnectionPool("ikdatatool", ikdatatoolDataSource)
			case "vcladezone": return new TestConnectionPool("vcladezone", vcladezoneDataSource)
			default: throw new RuntimeException("Unknown datasource: " + name)
		}
	}
}

class TestConnectionPool extends AbstractConnectionPool {
	
		private DataSource dataSource
	
		TestConnectionPool(String name, DataSource dataSource) {
			super(name)
			this.dataSource = dataSource
		}
	
		DataSource lookupDataSource() throws NamingException {
			dataSource
		}
	}
	

