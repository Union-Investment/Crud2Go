package de.unioninvestment.crud2go.testing

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by cmj on 17.07.14.
 */
@Singleton(lazy=true)
class CrudTestContext {

    private ClassPathXmlApplicationContext applicationContext

    CrudTestContext() {
        applicationContext = new ClassPathXmlApplicationContext("/eai-portal-config-test-applicationcontext.xml")
        applicationContext.getEnvironment().setActiveProfiles("testing")
        applicationContext.start()
    }

    void close() {
        applicationContext.stop()
    }

    CrudTestConfigBuilder configBuilder() {
        return new CrudTestConfigBuilder()
    }
}
