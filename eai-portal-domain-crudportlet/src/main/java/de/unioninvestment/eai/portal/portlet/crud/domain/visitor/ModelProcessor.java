package de.unioninvestment.eai.portal.portlet.crud.domain.visitor;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;

import java.util.Collection;

/**
 * Processor that traverses Panels and Components of the Crud2Go Domain Model.
 *
 * Created by cmj on 01.08.14.
 */
public class ModelProcessor {
    private final ModelVisitor visitor;

    /**
     * Konstruktor.
     *
     * @param visitor
     *            ConfigurationVisitor-Implementierung
     */
    public ModelProcessor(ModelVisitor visitor) {
        this.visitor = visitor;
    }

    /**
     * Traverses the Model-Structure.
     *
     * @param portlet
     *            Portlet-Model-Klasse
     */
    public void traverse(Portlet portlet) {
        visitor.visit(portlet);

        visitLeafs(portlet.getRoles());

        traverseDialogs(portlet.getDialogsById().values());

        if (portlet.getPage() != null) {
            traversePanel(portlet.getPage());
        } else if (portlet.getTabs() != null) {
            traverseTabs(portlet.getTabs());
        }

        visitor.visitAfter(portlet);
    }

    void traverseDialogs(Collection<Dialog> dialogs) {
        for (Panel dialog : dialogs) {
            traversePanel(dialog);
        }
    }

    void traverseTabs(Tabs tabs) {
        visitor.visit(tabs);
        for (Panel tab : tabs.getElements()) {
            traversePanel(tab);
        }
        visitor.visitAfter(tabs);
    }

    void traversePanel(Panel panel) {
        visitor.visit(panel);
        for (Component component : panel.getElements()) {
            if (component instanceof Panel) {
                traversePanel((Panel)component);
            } else {
                visitLeaf(component);
            }
        }
        visitor.visitAfter(panel);
    }

    private <T> void visitLeafs(Collection<T> leafs) {
        for (T leaf : leafs) {
            visitLeaf(leaf);
        }
    }

    private void visitLeaf(Object leafConfig) {
        if (leafConfig != null) {
            visitor.visit(leafConfig);
            visitor.visitAfter(leafConfig);
        }
    }
}
