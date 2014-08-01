package de.unioninvestment.crud2go.testing

import de.unioninvestment.crud2go.testing.internal.db.TestConnectionPoolFactory
import de.unioninvestment.crud2go.testing.internal.CrudTestConfigImpl
import de.unioninvestment.crud2go.testing.internal.gui.GuiMocksBuilder
import de.unioninvestment.crud2go.testing.internal.user.CurrentTestUser
import de.unioninvestment.crud2go.testing.internal.prefs.TestPreferencesRepository
import de.unioninvestment.eai.portal.portlet.crud.domain.validation.ModelValidator;
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelPreferences
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.PortletRole;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.AnonymousUser;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.NamedUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.reset
import static org.mockito.Mockito.when

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfigBuilder {
    private static final PortletConfigurationUnmarshaller unmarshaller = new PortletConfigurationUnmarshaller()

    private class CacheEntry {
        long compileTime
        PortletConfig config
        String xml
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

    File combinedPath
    Class<?> testClass
    Map configCache
	
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

    private Statistics statistics = new Statistics()

    CrudTestConfigBuilder() {
        eventBus = new EventBus();
    }

    /**
     * Load the configuration from the following places (match wins).
     *
     * @param name
     *          <ul>
     *            <li>A file relative to the combined.path + the package folder of the test class</li>
     *            <li>For paths statring with slash: A path relative to the combined.path of the test class</li>
     *            <li>A classpath resource besides the test class</li>
     *            <li>For paths statring with slash: An absolute classpath entry</li>
     *          </ul>
     * @return the builder instance
     */
    CrudTestConfigBuilder from(String name) {
        File configFile = null
        if (combinedPath?.exists()) {
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

    protected Portlet createModel(PortletConfig configuration) {
        ModelBuilder modelBuilder = createModelBuilder(configuration);
        return modelBuilder.build();
    }

    protected ModelBuilder createModelBuilder(PortletConfig configuration) {
        return modelFactory.getBuilder(eventBus, new Config((PortletConfig)configuration,
                (Map<String, Long>)resourceIds, (String)null, (Date)null), modelPreferences);
    }


    synchronized CrudTestConfig build() {
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
    }

    private void prepareGuiMocks(ModelBuilder modelBuilder, Portlet portlet) {
        new GuiMocksBuilder(modelBuilder, portlet).build()
    }

    private ScriptPortlet prepareScriptModel(ModelBuilder modelBuilder, Portlet portlet) {
        Map mapping = modelBuilder.getModelToConfigMapping()
        ScriptModelBuilder scriptModelBuilder = new ScriptModelBuilder(scriptModelFactory, eventBus,
                testConnectionPoolFactory, userFactoryMock, scriptCompiler, scriptBuilder,
                portlet, mapping)
        ScriptPortlet scriptPortlet = scriptModelBuilder.build()
        scriptPortlet
    }

    private void prepareUserAndRoles() {
        long roleId = 1
        portletConfig.roles?.role.each { role ->
            roleId2Name[roleId] = role.name
            resourceIds.put("${CrudTestContext.TEST_PORTLET_ID}_${CrudTestContext.LIFERAY_COMMUNITY_ID}_${role.name}".toString(), roleId++)
        }
        mockCurrentUser()
    }

    private void preparePortletPreferences(PortletConfig portletConfig, Portlet portlet) {
        def validPreferenceKeys = portletConfig.preferences?.preference?.collect { it.@key }
        portletPreferences.each { key, value ->
            if (validPreferenceKeys.contains(key)) {
                preferencesRepository.setPreference(portlet, key, value)
            } else {
                throw new IllegalArgumentException("Unknown preference '$key'. Allowed are $validPreferenceKeys")
            }
        }
    }

    private mockCurrentUser() {
		when(portalMock.getRoles(currentUserName)).thenReturn(portalRoles);
		when(portalMock.hasPermission(eq(currentUserName), eq("MEMBER"), eq(PortletRole.RESOURCE_KEY), anyString())).thenAnswer(
                { InvocationOnMock inv ->
                    long id = Long.parseLong(inv.arguments[3])
                    currentUserRoles.contains(roleId2Name[id])
                } as Answer);
		when(userFactoryMock.getCurrentUser(any(Portlet))).thenAnswer({ InvocationOnMock inv ->
			def user = currentUserName ? new NamedUser(currentUserName, inv.arguments[0].roles)
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
            portletConfig = unmarshaller.unmarshal(new ByteArrayInputStream(configXml.getBytes('utf-8')))
            scriptsCompiler.compileAllScripts(portletConfig)
            statistics.compileTime = System.currentTimeMillis() - compileStartTime

            if (!xmlModifiers) {
                configCache[configResource.URL] = new CacheEntry(compileTime: statistics.compileTime, config: portletConfig, xml: configXml)
            }
        }
    }
}
