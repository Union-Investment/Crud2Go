package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
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
public class QueryUpdateStatementGenerator implements QueryStatementGenerator {

    private final Table table;

    private String tablename;
    private List<TableColumn> updateColumns;
    private List<String> idColumns;

    public QueryUpdateStatementGenerator(Table table) {
        this.table = table;
    }

    private void updateConfig() {
        if (tablename == null) {
            DatabaseQueryContainer container = (DatabaseQueryContainer) table.getContainer();
            tablename = container.getTablename();
            updateColumns = table.getColumns().getUpdateColumns();
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
        String[] strings = new String[values.length];
        int valueIdx = 0;
        int stringIdx = 0;

        String head = "UPDATE " + QueryBuilder.quote(tablename) + " SET ";

        for (TableColumn column : updateColumns) {
            values[valueIdx++] = columnValues.get(column.getName());
            strings[stringIdx++] = head + QueryBuilder.quote(column.getName()) + "=";
            head = ", ";
        }

        head = " WHERE ";

        Map<String,Object> idValues = row.getId();
        for (String idName : idColumns) {
            values[valueIdx++] = idValues.get(idName);

            strings[stringIdx++] = head + QueryBuilder.quote(idName) + "=";
            head = " AND ";
        }

        return new GStringImpl(values, strings);
    }
}
