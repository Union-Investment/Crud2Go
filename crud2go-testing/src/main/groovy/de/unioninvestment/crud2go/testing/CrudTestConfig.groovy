package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptPortlet

/**
 * Created by cmj on 31.07.14.
 */
interface CrudTestConfig {
    /**
     * @return the XML content of the configuration
     */
    String getXml()

    /**
     * @return the portlet scripting API
     */
    ScriptPortlet getPortlet()

    /**
     * @return the portlet main script
     */
    Script getMainScript()

    /**
     * @return Statistics about loading times
     */
    Statistics getStatistics()

    /**
     * run the main script that has been skipped before by the dontRunMainScript() statement
     */
    void runMainScript()
}