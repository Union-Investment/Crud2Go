package de.unioninvestment.crud2go.testing.internal.db

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource

class DatabaseSchemas {
	
	private Map<String,DataSource> dataSources = [:]
	
	private Properties props
	
	DatabaseSchemas() {
		props = new Properties()
        loadPropertiesIfExists("database-default.properties")
        loadPropertiesIfExists("database.properties")
	}

    private loadPropertiesIfExists(String resourceName) {
        def stream = DatabaseSchemas.class.classLoader.getResourceAsStream(resourceName);
        if (stream) {
            props.load(stream)
            stream.close()
        }
    }

    def propertyMissing(String name) {
		def dataSource = dataSources[name]
		if (!dataSource) {
			dataSource = createDataSource(name)
			dataSources[name] = dataSource
		}
		return dataSource
	}
	
	DataSource createDataSource(name) {
		def data = schemaData(name)
		assert data?.url : "DataSource $name not configured properly"
		new DriverManagerDataSource(url:data.url, driverClassName:data.driver, username:data.username, password:data.password)
	}
	
	def schemaData(name) {
		[url: props.getProperty("${name}.url", props.getProperty('url')),
		 driver: props.getProperty("${name}.driver", props.getProperty('driver')),
		 username: props.getProperty("${name}.username"),
		 password: props.getProperty("${name}.password", props.getProperty('password'))
		]
	}
	
	def getSchemaNames() {
		def schemaNames = [] as LinkedHashSet
		props.each { key, value ->
            def name = key.toString()
			if (name.contains('.')) {
				schemaNames.add (name.split('\\.')[0])
			}
		}
		return schemaNames
	}
}
