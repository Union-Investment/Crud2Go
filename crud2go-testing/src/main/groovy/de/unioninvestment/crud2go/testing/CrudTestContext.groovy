package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.context.BackgroundThreadContextProvider
import de.unioninvestment.eai.portal.support.vaadin.context.Context
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import groovy.transform.PackageScope

/**
 * Created by cmj on 17.07.14.
 */
@Singleton(lazy=true,strict =false)
class CrudTestContext {

    static final String TEST_PORTLET_ID = "PortletId"
    static final long LIFERAY_COMMUNITY_ID = 17808L

    private ConfigurableApplicationContext applicationContext

    private ScriptCompiler scriptCompiler = new ScriptCompiler()

    private Map configCache = [:]

    @Autowired
    private ConfigurationScriptsCompiler scriptsCompiler

    @Autowired
    private ScriptModelFactory factory

    @Autowired
    ConnectionPoolFactory testConnectionPoolFactory

    CrudTestContext() {
        applicationContext = new AnnotationConfigApplicationContext()
        applicationContext.getEnvironment().setActiveProfiles("testing")
        applicationContext.register(CrudTestSpringApplicationContext)
        applicationContext.refresh()
        applicationContext.autowireCapableBeanFactory.autowireBean(this)

        Context.setProvider(new BackgroundThreadContextProvider(applicationContext, Locale.GERMANY, LIFERAY_COMMUNITY_ID))
    }

    void close() {
        applicationContext.stop()
    }

    CrudTestConfigBuilder configBuilder(Class<?> testClass) {
        def builder = applicationContext.getBean(CrudTestConfigBuilder)
        builder.configCache = configCache
        builder.testClass = testClass
        return builder
    }

    CrudTestConfig load(Class<?> testClass, Closure params) {
        def builder = CrudTestContext.instance.configBuilder(testClass)
        builder.with params
        return builder.build()
    }

}
