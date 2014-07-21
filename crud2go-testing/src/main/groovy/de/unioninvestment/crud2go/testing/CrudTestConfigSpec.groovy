package de.unioninvestment.crud2go.testing

import org.junit.Rule
import spock.lang.Specification

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfigSpec extends Specification {

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
