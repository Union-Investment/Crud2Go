package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import groovy.lang.GroovyShell;
import de.unioninvestment.eai.portal.portlet.crud.config.GroovyScript;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTAttributeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTChangeConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTContainerConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTDeleteConfig;
import de.unioninvestment.eai.portal.portlet.crud.config.ReSTQueryConfig;

public class RestTestConfig {

	public static ReSTContainerConfig readonlyConfig() {
		ReSTAttributeConfig idAttribute = new ReSTAttributeConfig();
		idAttribute.setName("id");
		idAttribute.setPrimaryKey(true);

		ReSTAttributeConfig anAttribute = new ReSTAttributeConfig();
		anAttribute.setName("a");

		ReSTQueryConfig queryConfig = new ReSTQueryConfig();
		queryConfig.getAttribute().add(idAttribute);
		queryConfig.getAttribute().add(anAttribute);
		queryConfig.setUrl(createGroovyScript("http://test.de/path",
				"{-> \"http://test.de/path\"}"));

		ReSTContainerConfig containerConfig = new ReSTContainerConfig();
		containerConfig.setQuery(queryConfig);
		return containerConfig;
	}

	public static ReSTContainerConfig readwriteConfig() {
		ReSTContainerConfig config = readonlyConfig();

		ReSTChangeConfig insert = new ReSTChangeConfig();
		insert.setUrl(createGroovyScript("http://test.de/insertpath",
				"{ -> \"http://test.de/insertpath\" }"));
		insert.setValue(createGroovyScript("['a': row.values.a ]",
				"{ row -> ['a': row.values.a ] }"));
		config.setInsert(insert);

		ReSTChangeConfig update = new ReSTChangeConfig();
		update.setUrl(createGroovyScript("http://test.de/updatepath",
				"{ -> \"http://test.de/updatepath\" }"));
		update.setValue(createGroovyScript("['a': row.values.a ]",
				"{ row -> ['a': row.values.a ] }"));
		config.setUpdate(update);

		ReSTDeleteConfig delete = new ReSTDeleteConfig();
		delete.setUrl(createGroovyScript("http://test.de/deletepath",
				"{ -> \"http://test.de/deletepath\" }"));
		config.setDelete(delete);

		return config;
	}

	public static GroovyScript createGroovyScript(String source,
			String scriptSource) {
		GroovyScript script = new GroovyScript(source);
		script.setClazz(new GroovyShell().parse(scriptSource).getClass());
		return script;
	}

	public static GroovyScript createGStringScript(String source,
			String... argNames) {
		GroovyScript script = new GroovyScript(source);
		script.setClazz(new GroovyShell().parse(
				"{ -> \"\"\"" + source + "\"\"\"}").getClass());
		return script;
	}

}
