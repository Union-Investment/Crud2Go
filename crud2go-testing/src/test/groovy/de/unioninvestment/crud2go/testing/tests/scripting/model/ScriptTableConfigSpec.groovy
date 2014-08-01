package de.unioninvestment.crud2go.testing.tests.scripting.model

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptContainer
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptTable
import de.unioninvestment.eai.portal.support.vaadin.groovy.VaadinBuilder

/**
 * Created by cmj on 23.07.14.
 */
class ScriptTableConfigSpec extends CrudConfigSpec {

    ScriptTable table
    ScriptContainer container

    def setup() {
        load '../../testingSimpleConfig.xml'
        table = portlet.elements.table1
        container = table.container
    }

    def 'should allow adding new generated columns'() {
        when:
        table.addGeneratedColumn('generated') { ScriptRow row, VaadinBuilder builder ->
            builder.label(value:"Test $row.values.TESTDATA}")
        }

        then:
        table.hasGeneratedColumn('generated')
    }

    def 'should allow removal of generated columns'() {
        given:
        table.addGeneratedColumn('generated') { ScriptRow row, VaadinBuilder builder ->
            builder.label(value:"Test $row.values.TESTDATA}")
        }

        when:
        table.removeGeneratedColumn('generated')

        then:
        table.hasGeneratedColumn('generated') == false
    }

    def 'should allow setting visible columns'() {
        when:
        table.visibleColumns = ['A', 'B', 'C']

        then:
        table.visibleColumns == ['A', 'B', 'C']
    }

}
