package de.unioninvestment.crud2go.testing.db

import de.unioninvestment.crud2go.testing.internal.db.DatabaseSchemas
import spock.lang.Specification

class DatabaseSchemasSpec extends Specification {

	DatabaseSchemas schemas = new DatabaseSchemas()
	
	def shouldResolveDefaultUrl() {
		expect: schemas.schemaData('DEFAULT_TEST').url == 'jdbc:oracle:thin:@localhost:1521:XE'
	}	
}
