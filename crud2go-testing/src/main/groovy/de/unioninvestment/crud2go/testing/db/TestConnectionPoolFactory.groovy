package de.unioninvestment.crud2go.testing.db

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPool
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("testing")
class TestConnectionPoolFactory implements ConnectionPoolFactory {

    DatabaseSchemas schemas = new DatabaseSchemas()

    TestConnectionPoolFactory() {
        System.setProperty("oracle.jdbc.J2EE13Compliant", "true")
    }

	ConnectionPool getPool(String name) {
        def dataSource = schemas[name]
        assert dataSource : "Unknown DataSource $name"
        return new TestConnectionPool(dataSource)
	}
}



