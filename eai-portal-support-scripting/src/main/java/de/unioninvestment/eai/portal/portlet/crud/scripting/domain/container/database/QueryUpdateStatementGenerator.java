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
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by cmj on 13.07.14.
 */
public class QueryUpdateStatementGenerator implements QueryStatementGenerator {

    private final Table table;

    private String tablename;
    private List<String> updateColumns;
    private List<String> idColumns;

    public QueryUpdateStatementGenerator(Table table) {
        this.table = table;
    }

    private void updateConfig() {
        if (tablename == null) {
            DatabaseQueryContainer container = (DatabaseQueryContainer) table.getContainer();
            tablename = container.getTablename();
            updateColumns = table.getColumns().getUpdateColumnNames();
            idColumns = table.getColumns().getPrimaryKeyNames();

            checkArgument(tablename != null, "Cannot generate UPDATE statements - Table name needed");
            checkArgument(updateColumns.size() >= 1, "Cannot generate UPDATE statements - at least one column must be editable");
            checkArgument(idColumns.size() >= 1, "Cannot generate UPDATE statements - at least one primary key column must exist");
        }
    }

    public GString generateStatement(ScriptRow row) {
        updateConfig();

        Map<String,Object> columnValues = row.getValues();
        Object[] values = new Object[updateColumns.size() + idColumns.size()];
        ArrayList<String> strings = new ArrayList(values.length + 1);

        String head = "UPDATE " + QueryBuilder.quote(tablename) + " SET ";

        int idx = 0;
        for (String colName : updateColumns) {
            values[idx++] = columnValues.get(colName);

            strings.add(head + QueryBuilder.quote(colName) + "=");
            head = ", ";
        }

        head = " WHERE ";

        Map<String,Object> idValues = row.getId();
        for (String idName : idColumns) {
            values[idx++] = idValues.get(idName);

            strings.add(head + QueryBuilder.quote(idName) + "=");
            head = " AND ";
        }

        return new GStringImpl(values, strings.toArray(new String[strings.size()]));
    }
}
