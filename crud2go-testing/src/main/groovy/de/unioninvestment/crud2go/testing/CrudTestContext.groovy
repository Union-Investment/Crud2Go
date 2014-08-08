package de.unioninvestment.crud2go.testing
import de.unioninvestment.crud2go.testing.internal.CrudTestSpringApplicationContext
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.context.BackgroundThreadContextProvider
import de.unioninvestment.eai.portal.support.vaadin.context.Context
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
/**
 * Created by cmj on 17.07.14.
 */
@Singleton(lazy=true,strict=false)
@CompileStatic
class CrudTestContext {

    static final String TEST_PORTLET_ID = "PortletId"
    static final long LIFERAY_COMMUNITY_ID = 17808L

    private AnnotationConfigApplicationContext applicationContext

    private ScriptCompiler scriptCompiler = new ScriptCompiler()

    File combinedPath
    boolean preferCombinedFiles

    private Map configCache = [:]

    @Autowired
    private ConfigurationScriptsCompiler scriptsCompiler

    @Autowired
    private ScriptModelFactory factory

    @Autowired
    ConnectionPoolFactory testConnectionPoolFactory

    CrudTestContext() {
        def config = new Properties()
        def props = CrudTestContext.classLoader.getResourceAsStream('crud2go-testing.properties')
        if (props) {
            config.load(props)
            props.close()
        }
        combinedPath = new File(config.getProperty('combined.path','target/combined'))
        preferCombinedFiles = config.getProperty('preferCombinedFiles', 'false') == 'true'

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

    CrudTestConfigBuilder configBuilder(Class<?> testClass, LiferayContext liferayContext) {
        def builder = applicationContext.getBean(CrudTestConfigBuilder)
        builder.combinedPath = combinedPath
        builder.configCache = configCache
        builder.testClass = testClass
        builder.liferayContext = liferayContext
        builder.preferCombinedFiles(preferCombinedFiles)
        return builder
    }

    /**
     * Loads the given configuration from filesystem or classpath using default settings:
     *
     * @param testClass the current class for relative classpath resolution
     * @param name the relative or absolute filename of the configuration
     * @return an initialized configuration
     */
    CrudTestConfig load(Class<?> testClass, LiferayContext liferayContext, String name) {
        load(testClass, liferayContext) { from name }
    }

    CrudTestConfig load(Class<?> testClass, LiferayContext liferayContext, @DelegatesTo(CrudTestConfigBuilder) Closure params) {
        def builder = configBuilder(testClass, liferayContext)
        builder.with params
        return builder.build()
    }

}
