package de.unioninvestment.crud2go.testing

import de.unioninvestment.crud2go.spi.security.CryptorFactory
import de.unioninvestment.crud2go.testing.db.TestConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.database.ConnectionPoolFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.form.ResetFormAction
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Preference
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.UserFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.portal.Portal
import de.unioninvestment.eai.portal.portlet.crud.domain.support.DefaultQueryOptionListRepository
import de.unioninvestment.eai.portal.portlet.crud.domain.support.PreferencesRepository
import de.unioninvestment.eai.portal.portlet.crud.domain.support.QueryOptionListRepository
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptModelFactory
import de.unioninvestment.eai.portal.support.scripting.ConfigurationScriptsCompiler
import de.unioninvestment.eai.portal.support.scripting.ScriptCompiler
import de.unioninvestment.eai.portal.support.vaadin.database.DatabaseDialect
import de.unioninvestment.eai.portal.support.vaadin.validation.FieldValidatorFactory
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.Ehcache
import net.sf.ehcache.config.CacheConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Scope
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.io.ClassPathResource

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static org.mockito.Mockito.mock

/**
 * Created by cmj on 17.07.14.
 */
@Profile("testing")
@Configuration
@CompileStatic
class CrudTestSpringApplicationContext {

    int defaultSelectWidth = 300;
    boolean separateEditMode = true

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
        new ModelFactory();

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
}
