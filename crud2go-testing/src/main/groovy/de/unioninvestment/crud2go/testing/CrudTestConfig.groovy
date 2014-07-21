package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.config.PortletConfig
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet

/**
 * Created by cmj on 17.07.14.
 */
class CrudTestConfig {

    private PortletConfig config
    private ScriptPortlet portlet
    private Script mainScript

    CrudTestConfig(PortletConfig config, ScriptPortlet portlet, Script mainScript) {
        this.mainScript = mainScript
        this.portlet = portlet
        this.config = config
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
}
