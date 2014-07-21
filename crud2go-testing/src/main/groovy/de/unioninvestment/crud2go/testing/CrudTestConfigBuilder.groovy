package de.unioninvestment.crud2go.testing

import de.unioninvestment.crud2go.testing.db.TestConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelPreferences
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfigBuilder {
    private static final PortletConfigurationUnmarshaller unmarshaller = new PortletConfigurationUnmarshaller()

    private static final String TEST_PORTLET_ID = "PortletId";

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

    Map configCache

    private Resource configResource

    private ScriptBuilder scriptBuilder = new ScriptBuilder()

    private EventBus eventBus;

    private Map<String, Long> resourceIds = new HashMap<String, Long>();

    private ModelPreferences preferences = new ModelPreferences()

    CrudTestConfigBuilder() {
        eventBus = new EventBus();
        resourceIds.put(TEST_PORTLET_ID + "_" + CrudTestContext.LIFERAY_COMMUNITY_ID + "_admin",
                1l);
        resourceIds.put(
                TEST_PORTLET_ID + "_" + CrudTestContext.LIFERAY_COMMUNITY_ID + "_benutzer", 1l);
    }

    CrudTestConfigBuilder fromClasspath(Class<?> currentSpec, String configFilename) {
        configResource = new ClassPathResource(configFilename, currentSpec)
        return this
    }

    protected Portlet createModel(PortletConfig configuration) {
        ModelBuilder modelBuilder = createModelBuilder(configuration);
        return modelBuilder.build();
    }

    protected ModelBuilder createModelBuilder(PortletConfig configuration) {
        return modelFactory.getBuilder(eventBus, new Config((PortletConfig)configuration,
                (Map<String, Long>)resourceIds, (String)null, (Date)null), preferences);
    }


    synchronized CrudTestConfig build() {
        PortletConfig portletConfig = prepareConfig()

        when(userFactoryMock.getCurrentUser(any(Portlet))).thenReturn(new CurrentUser(['admin', 'user', 'guest']as HashSet))

        ModelBuilder modelBuilder = createModelBuilder(portletConfig)
        Portlet portlet = modelBuilder.build()

        Map mapping = modelBuilder.getModelToConfigMapping()
        ScriptModelBuilder scriptModelBuilder = new ScriptModelBuilder(scriptModelFactory, eventBus,
                testConnectionPoolFactory, userFactoryMock, scriptCompiler, scriptBuilder,
                portlet, mapping)

        return new CrudTestConfig(portletConfig, scriptModelBuilder.build(), scriptBuilder.mainScript)
    }

    private PortletConfig prepareConfig() {
        assert configResource: 'No configuration source given'

        def existingConfig = configCache[configResource.URL]
        if (existingConfig) {
            return existingConfig
        } else {
            assert configResource.exists(): 'Configuration source does not exist'
            PortletConfig portletConfig = unmarshaller.unmarshal(configResource.inputStream)
            scriptsCompiler.compileAllScripts(portletConfig)
            configCache[configResource.URL] = portletConfig
            return portletConfig
        }
    }
}
