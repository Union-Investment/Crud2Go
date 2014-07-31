package de.unioninvestment.crud2go.testing.spock
import de.unioninvestment.crud2go.testing.CrudTestConfig
import de.unioninvestment.crud2go.testing.CrudTestConfigBuilder
import de.unioninvestment.crud2go.testing.CrudTestContext
import de.unioninvestment.crud2go.testing.LiferayContext
import org.junit.Rule
import spock.lang.Specification
/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSpec extends Specification {

    @Rule
    LiferayContext context = new LiferayContext(CrudTestContext.TEST_PORTLET_ID, CrudTestContext.LIFERAY_COMMUNITY_ID)

    private CrudTestConfig _instance

    void load(String name) {
        _instance = CrudTestContext.instance.load(this.class, name)
    }

    void load(@DelegatesTo(CrudTestConfigBuilder) Closure params) {
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
