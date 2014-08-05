package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import groovy.lang.GString;
import org.codehaus.groovy.runtime.GStringImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by cmj on 13.07.14.
 */
public class QueryDeleteStatementGenerator implements QueryStatementGenerator {

    private final Table table;

    private String tablename;
    private List<String> idColumns;

    public QueryDeleteStatementGenerator(Table table) {
        this.table = table;
    }
    private void updateConfig() {
        if (tablename == null) {
            DatabaseQueryContainer container = (DatabaseQueryContainer) table.getContainer();
            tablename = container.getTablename();
            idColumns = table.getColumns().getPrimaryKeyNames();

            checkArgument(tablename != null, "Cannot generate DELETE statements - Table name needed");
            checkArgument(idColumns.size() >= 1, "Cannot generate DELETE statements - at least one primary key column must exist");
        }
    }

    public GString generateStatement(ScriptRow row) {
        updateConfig();

        Object[] values = new Object[idColumns.size()];
        ArrayList<String> strings = new ArrayList<String>(values.length + 1);

        String head = "DELETE FROM " + QueryBuilder.quote(tablename) + " WHERE ";

        int idx = 0;

        Map<String,Object> idValues = row.getId();
        for (String idName : idColumns) {
            values[idx++] = idValues.get(idName);

            strings.add(head + QueryBuilder.quote(idName) + "=");
            head = " AND ";
        }

        return new GStringImpl(values, strings.toArray(new String[strings.size()]));
    }
}
