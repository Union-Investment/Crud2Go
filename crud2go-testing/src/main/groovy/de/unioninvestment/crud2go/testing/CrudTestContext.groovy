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
@Singleton(lazy=true)
class CrudTestContext {

    static final long LIFERAY_COMMUNITY_ID = 17808L

    private ConfigurableApplicationContext applicationContext

    private ScriptCompiler scriptCompiler = new ScriptCompiler()

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

    CrudTestConfigBuilder configBuilder() {
        applicationContext.getBean(CrudTestConfigBuilder)
    }
}
