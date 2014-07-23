package de.unioninvestment.crud2go.testing.junit

import de.unioninvestment.crud2go.testing.CrudTestConfig
import de.unioninvestment.crud2go.testing.CrudTestContext
import de.unioninvestment.crud2go.testing.LiferayContext
import org.junit.Rule

/**
 * Created by cmj on 21.07.14.
 */
class CrudConfigTest {
    @Rule
    public LiferayContext context = new LiferayContext(CrudTestContext.TEST_PORTLET_ID, CrudTestContext.LIFERAY_COMMUNITY_ID)

    CrudTestConfig _instance

    void load(String name) {
        _instance = CrudTestContext.instance.load(this.class) {
            fromClasspath name
        }
    }

    void load(Closure params) {
        _instance = CrudTestContext.instance.load(this.class, params)
    }

    CrudTestConfig getInstance() {
        assert _instance : 'Configuration not loaded'
        return _instance
    }

    def methodMissing(String name, args) {
        getInstance().mainScript."$name"(* args)
    }

    def propertyMissing(String name) {
        getInstance().mainScript."$name"
    }

    def propertyMissing(String name, value) {
        getInstance().mainScript."$name" = value
    }
}
