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
public class QueryInsertStatementGenerator implements QueryStatementGenerator {

    private final Table table;

    private String tablename;
    private List<String> insertColumns;

    public QueryInsertStatementGenerator(Table table) {
        this.table = table;
    }

    private void updateConfig() {
        if (tablename == null) {
            DatabaseQueryContainer container = (DatabaseQueryContainer) table.getContainer();
            tablename = container.getTablename();
            checkArgument(tablename != null, "Cannot generate INSERT statements - Table name needed");

            insertColumns = table.getColumns().getInsertColumnNames();
            checkArgument(insertColumns.size() >= 1, "Cannot generate INSERT statements - at least one column must be editable");
        }
    }

    public GString generateStatement(ScriptRow row) {
        updateConfig();

        Map<String,Object> columnValues = row.getValues();
        Object[] values = new Object[insertColumns.size()];
        ArrayList<String> strings = new ArrayList(values.length + 1);

        String head = "INSERT INTO " + QueryBuilder.quote(tablename) + " (";

        for (String colName : insertColumns) {
            head += QueryBuilder.quote(colName) + ",";
        }
        head = head.substring(0, head.length()-1) + ") VALUES (";

        int idx = 0;
        for (String colName : insertColumns) {
            values[idx++] = columnValues.get(colName);

            strings.add(head);
            head = ",";
        }
        strings.add(")");

        return new GStringImpl(values, strings.toArray(new String[strings.size()]));
    }

}
