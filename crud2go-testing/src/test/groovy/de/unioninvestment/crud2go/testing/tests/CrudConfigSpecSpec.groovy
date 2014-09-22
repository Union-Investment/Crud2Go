package de.unioninvestment.crud2go.testing.tests
import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptCurrentUser
/**
 * Created by cmj on 17.07.14.
 */
class CrudConfigSpecSpec extends CrudConfigSpec {

    def 'should load a crud2go configuration from classpath by relative path'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        instance != null
        instance.mainScript != null
        instance.portlet != null
        instance.xml != null
    }

    def 'should load a config from classpath by absolute path'() {
        when:
        load '/de/unioninvestment/crud2go/testing/tests/testingSimpleConfig.xml'

        then:
        instance != null
    }

    def 'should load combined file relative to testclass if existing'() {
        when:
        load {
            preferCombinedFiles()
            from 'testingCombinedConfig.xml'
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        instance.mainScript.type == 'combined'
    }

    def 'should load combined file relative to combined.path if existing'() {
        when:
        load {
            preferCombinedFiles()
            from '/de/unioninvestment/crud2go/testing/tests/testingCombinedConfig.xml'
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        instance.mainScript.type == 'combined'
    }

    def 'should combine files on-the-fly'() {
        when:
        load {
            fromClasspath 'testingCombinedConfig.xml'
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        instance.mainScript.type == 'classpath'
    }

    def 'should load a config from file by path relative to maven project root'() {
        when:
        load {
            fromFile 'src/test/resources/de/unioninvestment/crud2go/testing/tests/testingSimpleConfig.xml'
        }

        then:
        instance != null
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

    def 'should use anonymous user by default'() {
        when:
        load 'testingSimpleConfig.xml'
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == null
        currentUser.roles == ['all', 'anonymous'] as Set
        currentUser.authenticated == false
        portlet.elements.adminComponent == null
    }

    def 'should switch to named user if roles are set'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            // currentUserName 'carsten'
            currentUserRoles('admin')
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == 'unnamed'
        currentUser.isAuthenticated()
    }

    def 'should allow modification of current user'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            currentUserName 'carsten'
            currentUserRoles 'admin'
            currentUserPortalRoles 'EAI-AV'
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == 'carsten'
        currentUser.roles == ['all', 'admin', 'authenticated'] as Set
        currentUser.portalRoles == ['EAI-AV'] as Set
        currentUser.authenticated == true
        portlet.elements.adminComponent != null
    }

    def 'should allow setting preferences'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            preference 'pref1', 'value1'
            preference 'pref2', 'newValue2'
        }

        then:
        portlet.preferences.pref1 == 'value1'
        portlet.preferences.pref2 == 'newValue2'
        portlet.preferences.pref3 == 'defaultValue3'
    }

    def 'should allow setting parameters'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            parameter 'param', 'value'
            parameter 'multiParam', ['value1', 'value2']
        }

        then:
        portal.parameters['param'] == ['value'] as String[]
        portal.parameters['multiParam'] == ['value1', 'value2'] as String[]
    }

    def 'should provide access to the configuration XML'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        instance.xml.contains('backend =')
    }

    def 'should allow modification of the config XML'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            modifyXml { it.replace('backend =', 'modified = true\nbackend =')}
        }

        then:
        instance.mainScript.modified == true
    }

    def 'should allow optional additional validation'() {
        when:
        load {
            fromClasspath 'testingValidationConfig.xml'
            validate()
        }

        then:
        thrown(IllegalArgumentException)
    }
	
	def 'should allow optional validation of form field'() {
		when:
		load {
			fromClasspath 'testingValidationConfig_FormFields.xml'
			validate()
		}

		then:
		thrown(IllegalArgumentException)
	}


}