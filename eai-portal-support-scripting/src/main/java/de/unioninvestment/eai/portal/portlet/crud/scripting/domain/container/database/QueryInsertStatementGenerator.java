package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import com.google.common.base.Strings;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumn;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import groovy.lang.GString;
import org.codehaus.groovy.runtime.GStringImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by cmj on 13.07.14.
 */
public class QueryInsertStatementGenerator implements QueryStatementGenerator {

    private final Table table;

    private String tablename;
    private List<TableColumn> insertColumns;

    public QueryInsertStatementGenerator(Table table) {
        this.table = table;
    }

    private void updateConfig() {
        if (tablename == null) {
            DatabaseQueryContainer container = (DatabaseQueryContainer) table.getContainer();
            tablename = container.getTablename();
            checkArgument(tablename != null, "Cannot generate INSERT statements - Table name needed");

            insertColumns = table.getColumns().getInsertColumns();
            checkArgument(insertColumns.size() >= 1, "Cannot generate INSERT statements - at least one column must be editable");
        }
    }

    public GString generateStatement(ScriptRow row) {
        updateConfig();

        Map<String,Object> columnValues = row.getValues();
        Object[] values = new Object[insertColumns.size()];
        String[] strings = new String[values.length + 1];
        int stringIdx = 0;
        int valueIdx = 0;

        String head = "INSERT INTO " + QueryBuilder.quote(tablename) + " (";

        for (TableColumn column : insertColumns) {
            head += QueryBuilder.quote(column.getName()) + ",";
        }
        head = head.substring(0, head.length()-1) + ") VALUES (";

        for (TableColumn column : insertColumns) {
            if (!Strings.isNullOrEmpty(column.getSequence())) {
                // Oracle specific
                head += QueryBuilder.quote(column.getSequence()) + ".NEXTVAL,";
            } else {
                strings[stringIdx++] = head;
                values[valueIdx++] = columnValues.get(column.getName());
                head = ",";
            }
        }
        strings[stringIdx++] = ")";

        return new GStringImpl(
                Arrays.copyOf(values, valueIdx),
                Arrays.copyOf(strings, stringIdx));
    }

}
