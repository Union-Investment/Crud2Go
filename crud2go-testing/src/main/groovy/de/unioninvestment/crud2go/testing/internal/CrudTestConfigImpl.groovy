package de.unioninvestment.crud2go.testing.internal

import de.unioninvestment.crud2go.testing.CrudTestConfig
import de.unioninvestment.crud2go.testing.Statistics
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.validation.ModelValidator
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfigImpl implements CrudTestConfig {

    private String xml
    private PortletConfig config
    private ModelBuilder modelBuilder
    private Portlet modelPortlet
    private ScriptPortlet scriptPortlet
    private Script mainScript
    private Statistics statistics

    CrudTestConfigImpl(String xml, PortletConfig config, ModelBuilder modelBuilder, Portlet portlet, ScriptPortlet scriptPortlet, Script mainScript, Statistics statistics) {
        this.modelBuilder = modelBuilder
        this.xml = xml
        this.mainScript = mainScript
        this.modelPortlet = portlet
        this.scriptPortlet = scriptPortlet
        this.config = config
        this.statistics = statistics
    }

    String getXml() {
        xml
    }

    PortletConfig getConfig() {
        config
    }

    ScriptPortlet getPortlet() {
        scriptPortlet
    }

    Script getMainScript() {
        mainScript
    }

    Statistics getStatistics() {
        statistics
    }

    void runMainScript() {
        mainScript.run()
        modelPortlet.handleLoad()
    }

    void validate() {
        new ModelValidator().validateModel(modelBuilder, modelPortlet, config)
    }
}
