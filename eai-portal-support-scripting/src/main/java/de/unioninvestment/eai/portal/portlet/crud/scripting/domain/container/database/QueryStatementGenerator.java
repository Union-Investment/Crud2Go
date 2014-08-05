package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import groovy.lang.GString;

/**
 * Created by cmj on 13.07.14.
 */
public interface QueryStatementGenerator {

    public GString generateStatement(ScriptRow row);
}
