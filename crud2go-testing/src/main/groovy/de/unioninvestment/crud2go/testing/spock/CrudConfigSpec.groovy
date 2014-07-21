package de.unioninvestment.crud2go.testing.spock

import de.unioninvestment.crud2go.testing.CrudTestConfig
import de.unioninvestment.crud2go.testing.CrudTestContext
import de.unioninvestment.crud2go.testing.LiferayContext
import org.junit.Rule
import spock.lang.Specification

/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSpec extends Specification {

    @Rule
    LiferayContext context = new LiferayContext()

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
