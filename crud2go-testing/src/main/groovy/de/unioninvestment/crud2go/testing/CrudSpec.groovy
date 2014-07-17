package de.unioninvestment.crud2go.testing

import groovy.lang.Closure
import groovy.lang.Script

import java.io.InputStream
import java.util.Collection
import java.util.Map
import java.util.concurrent.ExecutorService

import javax.xml.bind.JAXBException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestContextManager

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import javax.xml.bind.JAXBException

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestContextManager
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller
import de.unioninvestment.eai.portal.portlet.crud.config.resource.Config
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.exception.TechnicalCrudPortletException
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.context.BackgroundThreadContextProvider
import de.unioninvestment.eai.portal.support.vaadin.context.Context
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.config.converter.PortletConfigurationUnmarshaller
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory

import spock.lang.Specification
import org.junit.Rule

@TestExecutionListeners( [DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class ])
public class CrudSpec extends Specification {
	protected @Autowired ApplicationContext applicationContext

	private static final String TEST_PORTLET_ID = "PortletId"
	private static final long TEST_COMMUNITY_ID = 17808L


	protected EventBus eventBus
	protected ConnectionPoolFactory connectionPoolFactory
	protected ResetFormAction resetFormAction
	protected FieldValidatorFactory fieldValidatorFactory
	protected int defaultSelectWidth = 300

	private Map<String, Long> resourceIds = new HashMap<String, Long>()
	private ExecutorService prefetchExecutor = Executors.newSingleThreadExecutor()



	protected Portlet createModel(PortletConfig configuration) {
		ModelBuilder modelBuilder = createModelBuilder(configuration)
		return modelBuilder.build()
	}

	protected ModelBuilder createModelBuilder(PortletConfig configuration) {
		ModelFactory factory = new ModelFactory(connectionPoolFactory,
				prefetchExecutor, resetFormAction, fieldValidatorFactory,
				defaultSelectWidth)

		return factory.getBuilder(eventBus, new Config((PortletConfig)configuration,
		(Map<String, Long>)resourceIds, (String)null, (Date)null))
	}

	protected PortletConfig createConfiguration(String configRessource)
	throws JAXBException {

		try {
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(configRessource)
			return createConfiguration(stream)
		}catch (Exception e) {
			throw new TechnicalCrudPortletException("Error loading configuration $configRessource", e)
		}
	}

	protected PortletConfig createConfiguration(InputStream stream) throws JAXBException {
		return unmarshaller.unmarshal(stream)
	}

	protected void setPrefetchExecutor(ExecutorService prefetchExecutor) {
		this.prefetchExecutor = prefetchExecutor
	}

	private TestContextManager testContextManager

	final static String PORTLET_ID = "portletId"
	final static long COMMUNITY_ID = 18004L

	@Autowired
	private TestConnectionPoolFactory testConnectionPoolFactory

	protected EventBus eventBusMock = Mock()

	protected Closure closureMock = Mock()

	protected UserFactory userFactoryMock  = Mock()

	@Rule
	public LiferayContext liferayContext = new LiferayContext(PORTLET_ID, COMMUNITY_ID)

	static Map cache = [:]

	protected ScriptBuilder scriptBuilder
	protected ScriptCompiler scriptCompiler
	protected ConfigurationScriptsCompiler scriptsCompiler
	protected ModelBuilder modelBuilder
	protected ScriptModelFactory factory
	protected ScriptModelBuilder scriptModelBuilder
	protected Map<Object, Object> mapping

	protected PortletConfig portletConfig
	protected Portlet portlet
	protected Script mainScript
	protected ScriptPortlet scriptPortlet

	public void injectDependencies() throws Exception {
		this.testContextManager.prepareTestInstance(this)
	}

	public void configurePortletUtils() {
		Context.setProvider(new BackgroundThreadContextProvider(applicationContext, Locale.GERMANY))
	}

	public void initializeDependencies() {
		eventBus = new EventBus()
		connectionPoolFactory = Mock(ConnectionPoolFactory)
		resetFormAction = Mock(ResetFormAction)
		fieldValidatorFactory = Mock(FieldValidatorFactory)
		defaultSelectWidth = 300

		resourceIds.put(TEST_PORTLET_ID + "_" + TEST_COMMUNITY_ID + "_admin",
				1l)
		resourceIds.put(
				TEST_PORTLET_ID + "_" + TEST_COMMUNITY_ID + "_benutzer", 1l)
	}

	protected void load(String configFile) throws JAXBException {
		portletConfig = cache[configFile]
		if (!portletConfig) {
			portletConfig = createConfiguration(configFile)
			scriptsCompiler.compileAllScripts(portletConfig)
			cache[configFile] = portletConfig
		}

		modelBuilder = createModelBuilder(portletConfig)
		interaction {
			portlet = modelBuilder.build()
			userFactoryMock.getCurrentUser(portlet) >> new CurrentUser(['admin', 'user', 'guest']as HashSet)
		}
		
		mapping = modelBuilder.getModelToConfigMapping()
		scriptModelBuilder = new ScriptModelBuilder(factory, eventBusMock,
				testConnectionPoolFactory, userFactoryMock, scriptBuilder,
				portlet, mapping)
		scriptPortlet = scriptModelBuilder.build()
		mainScript = scriptBuilder.mainScript
	}


	public final void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext
	}

	def setup() throws JAXBException {
		this.testContextManager = new TestContextManager(getClass(), null)
		injectDependencies()
		configurePortletUtils()
		initializeDependencies()
		internalSetup()
	}
	
	def cleanup() {
		Context.setProvider(null)
	}
	
	final void internalSetup() throws Exception {
		connectionPoolFactory = testConnectionPoolFactory
		
		scriptBuilder = new ScriptBuilder()
		scriptCompiler = new ScriptCompiler()
		scriptsCompiler = new ConfigurationScriptsCompiler(scriptCompiler)
		
		factory = new ScriptModelFactory(testConnectionPoolFactory,
						userFactoryMock, null)
		
	}

}
