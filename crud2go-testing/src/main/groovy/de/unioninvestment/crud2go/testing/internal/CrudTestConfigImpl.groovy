package de.unioninvestment.crud2go.testing.internal

import de.unioninvestment.crud2go.testing.CrudTestConfig
import de.unioninvestment.crud2go.testing.Statistics
import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfigImpl implements CrudTestConfig {

    private String xml
    private PortletConfig config
    private ScriptPortlet portlet
    private Script mainScript
    private Statistics statistics

    CrudTestConfigImpl(String xml, PortletConfig config, ScriptPortlet portlet, Script mainScript, Statistics statistics) {
        this.xml = xml
        this.mainScript = mainScript
        this.portlet = portlet
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
        portlet
    }

    Script getMainScript() {
        mainScript
    }

    Statistics getStatistics() {
        statistics
    }
}
