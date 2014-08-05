package de.unioninvestment.crud2go.testing.internal.db

import de.unioninvestment.eai.portal.portlet.crud.domain.database.AbstractConnectionPool
import groovy.transform.CompileStatic

import javax.naming.NamingException
import javax.sql.DataSource

/**
 * Created by cmj on 17.07.14.
 */
@CompileStatic
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
