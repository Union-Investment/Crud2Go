package de.unioninvestment.crud2go.testing.db

import de.unioninvestment.eai.portal.portlet.crud.domain.database.AbstractConnectionPool

import javax.naming.NamingException
import javax.sql.DataSource

/**
 * Created by cmj on 17.07.14.
 */
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
