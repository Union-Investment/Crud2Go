package de.unioninvestment.crud2go.testing.internal.gui

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ContainerRow
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Download
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table

/**
 * Created by cmj on 01.08.14.
 */
class TablePresenterStub implements Table.Presenter {

    Map<String, com.vaadin.ui.Table.ColumnGenerator> generatedColumns = [:]
    List<String> visibleColumns = []

    @Override
    void addGeneratedColumn(String columnName, String columnTitle, com.vaadin.ui.Table.ColumnGenerator columnGenerator) {
        generatedColumns.put(columnName, columnGenerator)
        visibleColumns << columnName
    }

    @Override
    void removeGeneratedColumn(String columnName) {
        generatedColumns.remove(columnName)
        visibleColumns.remove(columnName)
    }

    @Override
    void renderOnce(Table.DynamicColumnChanges changes) {
        changes.apply()
    }

    @Override
    boolean hasGeneratedColumn(String columnName) {
        generatedColumns.containsKey(columnName)
    }

    @Override
    void clearAllGeneratedColumns() {
        generatedColumns.clear()
    }

    @Override
    List<String> getVisibleColumns() {
        visibleColumns
    }

    @Override
    void setVisibleColumns(List<String> visibleColumns) {
        this.visibleColumns = visibleColumns
    }

    @Override
    void setTableActionVisibility(String id, boolean visible) {
        // do nothing
    }

    @Override
    ContainerRow createNewRow(Map<String, Object> values) {
        null
    }

    @Override
    void download(Download download) {
        // do nothing
    }
}
