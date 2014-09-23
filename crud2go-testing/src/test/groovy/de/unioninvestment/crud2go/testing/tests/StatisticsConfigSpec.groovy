package de.unioninvestment.crud2go.testing.tests

import de.unioninvestment.crud2go.testing.spock.CrudConfigSpec

/**
 * Created by cmj on 23.07.14.
 */
class StatisticsConfigSpec extends CrudConfigSpec {

    def 'should collect statistics for build times'() {
        when:
        load {
            from 'testingSimpleConfig.xml'
            preference 'sleep', '100'
        }

        then:
        instance.statistics.compileTime > 0
        instance.statistics.postCompileTime > 0
        instance.statistics.buildTime >= instance.statistics.postCompileTime
    }

    def 'should cache compileTime '() {
        when:
        load 'testingSimpleConfig.xml'
        def firstCompileTime = instance.statistics.compileTime
        load 'testingSimpleConfig.xml'

        then:
        instance.statistics.compileTime == firstCompileTime
    }

    def 'should allow disabling caching for calculation of average compile times'() {
        when:
        load {
            fromClasspath 'testingSimpleConfig.xml'
        }
        def firstConfig = instance.config
        load {
            fromClasspath 'testingSimpleConfig.xml'
            recompile()
        }

        then:
        !instance.config.is(firstConfig)
    }
}
