package de.unioninvestment.crud2go.testing.internal
import de.unioninvestment.crud2go.spi.security.CryptorFactory
import de.unioninvestment.crud2go.testing.CrudTestConfigBuilder
import de.unioninvestment.crud2go.testing.internal.db.TestConnectionPoolFactory
import de.unioninvestment.crud2go.testing.internal.prefs.TestPreferencesRepository
import de.unioninvestment.eai.portal.portlet.crud.domain.container.EditorSupport
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal
import de.unioninvestment.eai.portal.portlet.crud.domain.support.DefaultQueryOptionListRepository
import de.unioninvestment.eai.portal.portlet.crud.domain.support.QueryOptionListRepository
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptBuilder
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect
import de.unioninvestment.eai.portal.support.vaadin.mvp.EventBus
import de.unioninvestment.eai.portal.support.vaadin.table.DisplaySupport
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.Ehcache
import net.sf.ehcache.config.CacheConfiguration
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.*
import org.springframework.context.annotation.aspectj.EnableSpringConfigured
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static java.util.Arrays.asList
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.withSettings
/**
 * Created by cmj on 17.07.14.
 */
@Profile("testing")
@Configuration
@CompileStatic
@EnableSpringConfigured
class CrudTestSpringApplicationContext  {

    int defaultSelectWidth = 300;
    boolean separateEditMode = true

    @Autowired
    private ConnectionPoolFactory connectionPoolFactory;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private Portal portal;

    @Value('${portlet.crud.databaseBackend.dialect}')
    private DatabaseDialect databaseDialect;

    @Bean
    public static PropertyPlaceholderConfigurer properties(){
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ClassPathResource[] resources = [ new ClassPathResource("/eai-portal-administration.properties") ];
        ppc.setLocations( resources );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

    @Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("de.unioninvestment.eai.portal.portlet.crud.messages",
			"de.unioninvestment.eai.portal.support.vaadin.validation.messages");
        return messageSource;
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    CrudTestConfigBuilder configBuilder() {
        new CrudTestConfigBuilder()
    }

    @Bean
    QueryOptionListRepository queryOptionListRepository() {
        new DefaultQueryOptionListRepository(testConnectionPoolFactory(), optionListCache())
    }

    @Bean @Qualifier("optionListCache")
    Ehcache optionListCache() {
        System.setProperty("net.sf.ehcache.disabled", "true")
        new Cache(new CacheConfiguration("optionListCache", 0))
    }

    @Bean
    ModelFactory modelFactory() {
        new CrudTestModelFactory();

    }

    @Bean
    CryptorFactory cryptorFactoryMock() {
        mock(CryptorFactory)
    }

    @Bean
    FieldValidatorFactory fieldValidatorFactory() {
        new FieldValidatorFactory()
    }

    @Bean
    ResetFormAction resetFormAction() {
        new ResetFormAction()
    }

    @Bean
    ExecutorService prefetchExecutor() {
         Executors.newSingleThreadExecutor();
    }

    @Bean
    UserFactory userFactoryMock() {
        mock(UserFactory);
    }

    @Bean
    ConnectionPoolFactory testConnectionPoolFactory() {
        new TestConnectionPoolFactory()
    }

    @Bean
    ScriptModelFactory scriptModelFactory() {
        new ScriptModelFactory(testConnectionPoolFactory(),
                userFactoryMock(), portalMock(), scriptCompiler(), DatabaseDialect.ORACLE, true)
    }

    @Bean
    ConfigurationScriptsCompiler scriptsCompiler() {
        new ConfigurationScriptsCompiler(scriptCompiler())
    }

    @Bean
    ScriptCompiler scriptCompiler() {
        new ScriptCompiler()
    }

    @Bean
    Portal portalMock() {
        mock(Portal)
    }

    @Bean
    TestPreferencesRepository testPreferencesRepository() {
        new TestPreferencesRepository()
    }


    /**
     * Erzeugt eine Instanz der Klasse EventBus.
     *
     * @return EventBus
     */
    @Bean
    @org.springframework.context.annotation.Lazy
    public EventBus eventBus() {
        return new EventBus();
    }

    /**
     * Erzeugt eine Instanz der Klasse ScriptBuilder.
     *
     * @return ScriptBuilder
     */
    @Bean()
    @org.springframework.context.annotation.Lazy
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
    public ScriptBuilder scriptBuilder() {
        return new ScriptBuilder();
    }

    @Bean
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.afterPropertiesSet();
        return scheduler;
    }

    /**
     * @return eine Liste datentypspezifischer Hilfsklassen in Reihenfolge der
     *         Relevanz
     */
    @Bean
    public List<DisplaySupport> displaySupport() {
        asList(Mockito.mock(DisplaySupport))
    }

    /**
     * @return eine Liste datentypspezifischer Hilfsklassen in Reihenfolge der
     *         Relevanz
     */
    @Bean
    public List<EditorSupport> editorSupport() {
        asList(Mockito.mock(EditorSupport, withSettings().extraInterfaces(DisplaySupport.class)))
    }

}
