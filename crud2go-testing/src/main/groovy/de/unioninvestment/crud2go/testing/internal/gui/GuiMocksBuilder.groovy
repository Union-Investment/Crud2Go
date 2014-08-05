package de.unioninvestment.crud2go.testing.internal.gui

import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelBuilder
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table
import de.unioninvestment.eai.portal.portlet.crud.domain.visitor.ModelProcessor
import de.unioninvestment.eai.portal.portlet.crud.domain.visitor.ModelVisitor
import groovy.transform.CompileStatic

/**
 * Created by cmj on 01.08.14.
 */
@CompileStatic
class GuiMocksBuilder implements ModelVisitor {

    ModelBuilder modelBuilder
    Portlet portlet

    GuiMocksBuilder(ModelBuilder modelBuilder, Portlet portlet) {
        this.modelBuilder = modelBuilder
        this.portlet = portlet
    }

    void build() {
        new ModelProcessor(this).traverse(portlet)
    }


    @Override
    void visit(Object element) {
        if (element instanceof Table) {
            stubGuiForTable((Table)element)
        }
    }

    void stubGuiForTable(Table table) {
        table.setPresenter(new TablePresenterStub())
    }

    @Override
    void visitAfter(Object element) {
        // nothing to do
    }

}
