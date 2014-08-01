package de.unioninvestment.crud2go.testing.tests.scripting.model
import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptContainer
/**
 * Created by cmj on 23.07.14.
 */
class ScriptContainerConfigSpec extends CrudConfigSpec {

    ScriptContainer container

    def setup() {
        load '../../testingSimpleConfig.xml'
        container = portlet.elements.table1.container
    }

    def 'should allow traversing rows'() {
        when:
        def cnt = 0
        container.eachRow {
            cnt++
        }

        then:
        cnt == 4000
    }

    def 'should allow adding rows'() {
        when:
        container.withTransaction {
            def newRow = container.addRow()
            newRow.values.ID = 4001L
            newRow.values.CVARCHAR5_NN = 'Hallo'
            newRow.values.CNUMBER5_2_NN = 5
        }
        def storedRow = backend.data[4001L]

        then:
        backend.data.size() == 4001
        storedRow[0] == 4001L
        storedRow[4] == 'Hallo'
        storedRow[5] == 5
    }

}
