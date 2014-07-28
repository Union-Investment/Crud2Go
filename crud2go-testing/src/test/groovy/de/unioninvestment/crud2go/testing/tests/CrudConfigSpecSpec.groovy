package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptCurrentUser
import de.unioninvestment.eai.portal.support.vaadin.validation.ValidationException

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
        instance.config != null
    }

    def 'should load a config from classpath by absolute path'() {
        when:
        load '/de/unioninvestment/crud2go/testing/tests/testingSimpleConfig.xml'

        then:
        instance != null
    }

    def 'should load combined file relative to testclass if existing'() {
        when:
        load 'testingCombinedConfig.xml'
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        instance.mainScript.type == 'combined'
    }

    def 'should load combined file relative to combined.path if existing'() {
        when:
        load '/de/unioninvestment/crud2go/testing/tests/testingCombinedConfig.xml'
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        instance.mainScript.type == 'combined'
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

    def 'should allow modification of current user'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            currentUserName 'carsten'
            currentUserRoles(['admin'])
        }
        ScriptCurrentUser currentUser = instance.mainScript.currentUser

        then:
        currentUser.name == 'carsten'
        currentUser.roles == ['all', 'admin', 'authenticated'] as Set
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

    def 'should set a testing flag inside the portlet'() {
        when:
        load 'testingSimpleConfig.xml'

        then:
        portlet.inTest == true
    }

    // TODO API erstellen und testen
    def 'should allow optional additional validation'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
            validate()
        }

        then:
        true // thrown ValidationException()
    }

}