package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet

/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSimpleSpec extends CrudConfigSpec {

    def 'should load a crud2go configuration'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        instance != null
        instance.mainScript != null
        instance.portlet != null
        instance.config != null
    }

    def 'should allow direct access to the mainScript via delegation'() {
        given:
        load 'testingSimpleConfig.xml'

        when:
        def data = backend.read()

        then:
        data.size() == 4000
    }

    def 'should allow direct access to the script context'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        portlet != null
        portlet == instance.portlet
    }

    def 'should allow modification of current user'() {
    	when:
		load {
			 fromClasspath 'testingSimpleConfig.xml'
			 currentUserName 'carsten'
			 currentUserRoles(['admin'])
		}

		then:
		currentUser.name == 'carsten'
		currentUser.roles == ['all', 'admin', 'authenticated'] as Set
		portlet.elements.adminComponent != null
    }
    
}
