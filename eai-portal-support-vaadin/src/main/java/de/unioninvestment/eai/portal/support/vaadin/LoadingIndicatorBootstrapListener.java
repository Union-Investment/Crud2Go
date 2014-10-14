package de.unioninvestment.eai.portal.support.vaadin;

import com.google.common.base.Strings;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by cmj on 10.10.14.
 */
public class LoadingIndicatorBootstrapListener implements BootstrapListener {

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
    }

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        String message = getMessage(response);
        if (!Strings.isNullOrEmpty(message)) {
            List<Node> nodes = response.getFragmentNodes();
            for(Node node : nodes) {
                if (isMainDiv(node)) {
                    addLoadingIndicator((Element) node, message);
                }
            }
        }
    }

    public String getMessage(BootstrapFragmentResponse response) {
        ResourceBundle messages = ResourceBundle.getBundle("de.unioninvestment.eai.portal.portlet.crud.messages", response.getRequest().getLocale());
        return messages.getString("portlet.crud.loadingindicator.text");
    }

    public boolean isMainDiv(Node node) {
        return node instanceof Element && node.nodeName().equals("div") && node.hasAttr("class") && node.attr("class").contains("v-app");
    }

    public void addLoadingIndicator(Element mainDiv, String message) {
        mainDiv.appendElement("div").addClass("loadingindicator").text(message);
    }

}
