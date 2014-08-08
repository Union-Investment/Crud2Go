package de.unioninvestment.crud2go.testing
import de.unioninvestment.crud2go.testing.internal.CrudTestConfigImpl
import de.unioninvestment.crud2go.testing.internal.db.TestConnectionPoolFactory
import de.unioninvestment.crud2go.testing.internal.gui.GuiMocksBuilder
import de.unioninvestment.crud2go.testing.internal.prefs.TestPreferencesRepository
import de.unioninvestment.crud2go.testing.internal.user.CurrentTestUser
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.AnonymousUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.NamedUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal
import de.unioninvestment.eai.portal.portlet.crud.domain.validation.ModelValidator
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor
import groovy.transform.TypeCheckingMode
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

import java.security.Principal

import static java.util.Collections.unmodifiableMap
import static org.mockito.Matchers.*
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.when
/**
 * Created by cmj on 17.07.14.
 */
@CompileStatic
class CrudTestConfigBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrudTestConfigBuilder)

    private static final PortletConfigurationUnmarshaller unmarshaller = new PortletConfigurationUnmarshaller()

    static CrudTestConfigBuilder currentBuilder

    @TupleConstructor
    private static class CacheEntry {
        long compileTime
        String xml
        PortletConfig config
    }

	@Autowired
	private Portal portalMock
	
    @Autowired
    ScriptModelFactory scriptModelFactory

    @Autowired
    private TestConnectionPoolFactory testConnectionPoolFactory

    @Autowired
    protected ConfigurationScriptsCompiler scriptsCompiler

    @Autowired
    ScriptCompiler scriptCompiler

    @Autowired
    private ModelFactory modelFactory

    @Autowired
    private UserFactory userFactoryMock

    @Autowired
    private TestPreferencesRepository preferencesRepository

    @PackageScope LiferayContext liferayContext
    @PackageScope File combinedPath
    @PackageScope Class<?> testClass
    @PackageScope Map<URL, CacheEntry> configCache

    private boolean preferCombinedFiles = false
	private String currentUserName = null
    private Set currentUserRoles = []
    private Set portalRoles = []
	Map<Long,String> roleId2Name = [:]

    List<Closure<String>> xmlModifiers = [] as LinkedList

    private boolean recompile = false
    private boolean validationEnabled = false

    private Resource configResource
    private String configXml
    private PortletConfig portletConfig

    private ScriptBuilder scriptBuilder = new ScriptBuilder()

    private EventBus eventBus;

    private Map<String, Long> resourceIds = new HashMap<String, Long>();

    private ModelPreferences modelPreferences = new ModelPreferences()

    private Map<String,String> portletPreferences = [:]
    private boolean runMainScript = true

    private Statistics statistics = new Statistics()

    CrudTestConfigBuilder() {
        eventBus = new EventBus();
    }

    /**
     * Prefer combined files over the file in the classpath if using load(String). The default behaviour is to use files
     * from the classpath and merge external scripts on the fly.
     *
     * @param preferCombinedFiles true, if a combined file should be loaded for testing
     * @return the builder instance
     */
    CrudTestConfigBuilder preferCombinedFiles(boolean preferCombinedFiles = true) {
        this.preferCombinedFiles = preferCombinedFiles
        return this
    }

    /**
     * Load the configuration from the following places (match wins).
     *
     * @param name
     *          <ul>
     *            <li>If preferred and existing: A file relative to the combined.path + the package folder of the test class</li>
     *            <li>For paths statring with slash: A path relative to the combined.path of the test class</li>
     *            <li>A classpath resource besides the test class</li>
     *            <li>For paths statring with slash: An absolute classpath entry</li>
     *          </ul>
     * @return the builder instance
     */
    CrudTestConfigBuilder from(String name) {
        File configFile = null
        if (preferCombinedFiles && combinedPath?.exists()) {
            if (name.startsWith('/') || name.startsWith('\\')) {
                configFile = new File(combinedPath, name.substring(1))
            } else {
                String relativePath = testClass.package.name.replace('.', '/')
                configFile = new File(new File(combinedPath, relativePath), name)
            }
        }
        if (configFile?.exists()) {
            fromFile(configFile)
        } else {
            fromClasspath name
        }
    }

    /**
     * Load the configuration from classpath.
     *
     * @param classpathReference the path relative to the testing class or if starting with a slash, relative to the classloader root
     * @return the builder instance
     */
    CrudTestConfigBuilder fromClasspath(String classpathReference) {
        configResource = new ClassPathResource(classpathReference, testClass)
        return this
    }

    /**
     * Load the configuration from a file.
     *
     * @param configFile the xml configuration file
     * @return the builder instance
     */
    CrudTestConfigBuilder fromFile(File configFile) {
        configResource = new FileSystemResource(configFile)
        return this
    }

    /**
     * Load the configuration from a file.
     *
     * @param configFile a path relative to the execution folder (the maven project root)
     * @return the builder instance
     */
    CrudTestConfigBuilder fromFile(String configFilename) {
        configResource = new FileSystemResource(configFilename)
        return this
    }

    CrudTestConfigBuilder modifyXml(Closure<String> modifier) {
        xmlModifiers << modifier
        return this
    }
    /**
     * Changes the currentUserName in the script context.
     *
     * @param name the new user name
     * @return the builder instance
     */
    CrudTestConfigBuilder currentUserName(String name) {
		this.currentUserName = name
		return this
	}

    /**
     * Changes the roles of the current user. If no user name is given, it is set to 'unnamed'.
     *
     * @param name the new user name
     * @return the builder instance
     */
	CrudTestConfigBuilder currentUserRoles(String... roles) {
		this.currentUserRoles = roles as HashSet
        if (!currentUserName) {
            currentUserName = 'unnamed'
        }
		return this
	}

    CrudTestConfigBuilder currentUserPortalRoles(String... portalRoles) {
        this.portalRoles = portalRoles as HashSet
        if (!currentUserName) {
            currentUserName = 'unnamed'
        }
        return this
    }
    /**
     * Changes a script preference.
     *
     * @param key the preference key
     * @param value the preference value
     * @return the builder instance
     */
    CrudTestConfigBuilder preference(String key, String value) {
        this.portletPreferences[key] = value
        return this
    }

    CrudTestConfigBuilder dontRunMainScript() {
        this.runMainScript = false
        return this
    }

    protected ModelBuilder createModelBuilder(PortletConfig configuration) {
        return modelFactory.getBuilder(eventBus, new Config((PortletConfig)configuration,
                (Map<String, Long>)resourceIds, (String)null, (Date)null), modelPreferences);
    }


    synchronized CrudTestConfig build() {
        try {
            currentBuilder = this

            reset(portalMock, userFactoryMock);

            long startTime = System.currentTimeMillis()

            prepareConfig()

            long postBuildStartTime = System.currentTimeMillis()
            prepareUserAndRoles()

            ModelBuilder modelBuilder = createModelBuilder(portletConfig)
            Portlet portlet = modelBuilder.build()

            preparePortletPreferences(portletConfig, portlet)

            ScriptPortlet scriptPortlet = prepareScriptModel(modelBuilder, portlet)

            long endTime = System.currentTimeMillis()
            statistics.postCompileTime = endTime - postBuildStartTime
            statistics.buildTime = endTime - startTime

            prepareGuiMocks(modelBuilder, portlet)

            if (validationEnabled) {
                new ModelValidator().validateModel(modelBuilder, portlet, portletConfig)
            }

            return new CrudTestConfigImpl(configXml, portletConfig, scriptPortlet, scriptBuilder.mainScript, statistics)

        } finally {
            currentBuilder = null
        }
    }

    private void prepareGuiMocks(ModelBuilder modelBuilder, Portlet portlet) {
        new GuiMocksBuilder(modelBuilder, portlet).build()
    }

    private ScriptPortlet prepareScriptModel(ModelBuilder modelBuilder, Portlet portlet) {
        Map mapping = modelBuilder.getModelToConfigMapping()
        ScriptModelBuilder scriptModelBuilder = new ScriptModelBuilder(scriptModelFactory, eventBus,
                testConnectionPoolFactory, userFactoryMock, scriptCompiler, scriptBuilder,
                portlet, mapping)
        scriptModelBuilder.runMainScript = runMainScript
        ScriptPortlet scriptPortlet = scriptModelBuilder.build()
        scriptPortlet
    }

    private void prepareUserAndRoles() {
        long roleId = 1
        portletConfig.roles?.role?.each { role ->
            roleId2Name[roleId] = role.name
            resourceIds.put("${CrudTestContext.TEST_PORTLET_ID}_${CrudTestContext.LIFERAY_COMMUNITY_ID}_${role.name}".toString(), roleId++)
        }
        mockCurrentUser()
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private void preparePortletPreferences(PortletConfig portletConfig, Portlet portlet) {
        def validAuthKeys = getAuthenticationPreferenceKeys(portletConfig)
        def validPreferenceKeys = portletConfig.preferences?.preference?.collect { it.@key }
        portletPreferences.each { key, value ->
            if (validPreferenceKeys.contains(key)) {
                preferencesRepository.setPreference(portlet, key, value)
            } else if (!validAuthKeys.contains(key)) {
                throw new IllegalArgumentException("Unknown preference '$key'. Allowed are $validPreferenceKeys")
            }
        }
    }

    Set getAuthenticationPreferenceKeys(PortletConfig portletConfig) {
        Set keys = []
        portletConfig.authentication?.realm*.credentials?.each { cred ->
            keys << cred.username?.preferenceKey
            keys << cred.password?.preferenceKey
        }
        return keys
    }

    private mockCurrentUser() {
        when(liferayContext.portletRequestMock.getUserPrincipal()).thenReturn({currentUserName} as Principal)
        when(portalMock.getRoles(currentUserName)).thenReturn(portalRoles);
		when(portalMock.hasPermission(eq(currentUserName), eq("MEMBER"), eq(PortletRole.RESOURCE_KEY), anyString())).thenAnswer(
                { InvocationOnMock inv ->
                    long id = Long.parseLong(inv.arguments[3] as String)
                    currentUserRoles.contains(roleId2Name[id])
                } as Answer);
		when(userFactoryMock.getCurrentUser(any(Portlet))).thenAnswer({ InvocationOnMock inv ->
            Portlet portlet = inv.arguments[0] as Portlet
			def user = currentUserName ? new NamedUser(currentUserName, portlet.roles as HashSet<Role>)
							: new AnonymousUser()
			return new CurrentTestUser(user)
		} as Answer<CurrentUser>)
	}

    CrudTestConfigBuilder recompile() {
        recompile = true
        return this
    }

    CrudTestConfigBuilder validate() {
        validationEnabled = true
        return this
    }

    void prepareConfig() {
        assert configResource: 'No configuration source given'
        CacheEntry existingEntry = configCache[configResource.URL]
        if (existingEntry && !recompile && !xmlModifiers) {
            configXml = existingEntry.xml
            portletConfig = existingEntry.config
            statistics.compileTime = existingEntry.compileTime
        } else {
            assert configResource.exists(): "Configuration source $configResource.URL does not exist"

            configXml = configResource.inputStream.getText('utf-8')
            xmlModifiers.each { modifier ->
                configXml = modifier.call(configXml)
            }

            long compileStartTime = System.currentTimeMillis()
            portletConfig = unmarshaller.unmarshal(configXml)
            mergeConfigScripts()
            scriptsCompiler.compileAllScripts(portletConfig)
            statistics.compileTime = System.currentTimeMillis() - compileStartTime

            if (!xmlModifiers) {
                configCache[configResource.URL] = new CacheEntry(statistics.compileTime, configXml, portletConfig)
            }
        }
    }

    def mergeConfigScripts() {
        portletConfig.script
                .findAll { it.src != null &&
                    (it.value == null || it.value.source == null || it.value.source.isEmpty()) }
                .each { script ->
            def scriptResource = configResource.createRelative(script.src)
            LOGGER.debug("Merging $scriptResource.URL into $configResource.URL")
            assert scriptResource.exists()
            script.value = new GroovyScript(scriptResource.getInputStream().getText('UTF-8'))
        }
    }

    void setLiferayContext(LiferayContext ctx) {
        this.liferayContext = ctx
    }

    Map<String,String> getPortletPreferences() {
        return unmodifiableMap(portletPreferences)
    }
}
