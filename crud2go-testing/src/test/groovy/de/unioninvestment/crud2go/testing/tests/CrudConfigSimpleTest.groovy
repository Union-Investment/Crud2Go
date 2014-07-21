package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.junit.CrudConfigTest
import org.junit.Test

/**
 * Created by cmj on 21.07.14.
 */
class CrudConfigSimpleTest extends CrudConfigTest {

    @Test
    void 'should load a crud2go configuration'() {
        load 'testingSimpleConfig.xml'

        assert instance != null
        assert instance.mainScript != null
        assert instance.portlet != null
        assert instance.config != null
    }

    @Test
    void 'should allow access to the mainScript via delegation'() {
        load 'testingSimpleConfig.xml'

        def data = backend.read()

        assert data.size() == 4000
    }
}
