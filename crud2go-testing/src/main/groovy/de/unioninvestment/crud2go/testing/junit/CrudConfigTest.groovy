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
    public LiferayContext context = new LiferayContext()

    CrudTestConfig _instance

    void load(String name) {
        _instance = CrudTestContext.instance.configBuilder()
                .fromClasspath(this.getClass(), name)
                .build()
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
