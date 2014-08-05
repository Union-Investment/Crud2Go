package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptCurrentUser

/**
 * Created by cmj on 17.07.14.
 */
class GroovyCategoryConfigSpec extends CrudConfigSpec {

    def 'config script should support class extensions'() {
        when:
        load 'testingGroovyCategoryConfig.xml'
        GString d = instance.mainScript.d

        then:
        d.values[0] == "1"
        d.values[1] == 2
    }
}
