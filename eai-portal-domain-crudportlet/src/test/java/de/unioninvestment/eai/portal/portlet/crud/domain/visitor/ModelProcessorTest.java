package de.unioninvestment.eai.portal.portlet.crud.domain.visitor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

/**
 * Created by cmj on 01.08.14.
 */
public class ModelProcessorTest {

    @Mock
    private Portlet portletMock;

    @Mock
    private ModelVisitor visitorMock;

    @Mock
    private ModelProcessor processor;
    @Mock
    private Page pageMock;
    @Mock
    private Tabs tabsMock;
    @Mock
    private Tab tabMock;
    @Mock
    private Panel panelMock;
    @Mock
    private Component componentMock;
    @Mock
    private Role role1Mock, role2Mock;
    @Mock
    private Dialog dialog1Mock;

    private InOrder inOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        processor = new ModelProcessor(visitorMock);
        inOrder = inOrder(visitorMock);
    }

    @Test
    public void shouldVisitAPortlet() {
        processor.traverse(portletMock);
        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

    @Test
    public void shouldVisitAPortletWithRoles() {
        when(portletMock.getRoles()).thenReturn(Sets.<Role>newLinkedHashSet(asList(role1Mock, role2Mock)));
        processor.traverse(portletMock);
        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visit(role1Mock);
        inOrder.verify(visitorMock).visitAfter(role1Mock);
        inOrder.verify(visitorMock).visit(role2Mock);
        inOrder.verify(visitorMock).visitAfter(role2Mock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

    @Test
    public void shouldVisitAPortletPage() {
        when(portletMock.getPage()).thenReturn(pageMock);

        processor.traverse(portletMock);

        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visit(pageMock);
        inOrder.verify(visitorMock).visitAfter(pageMock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

    @Test
    public void shouldVisitDialogsBeforePageOrTabs() {
        when(portletMock.getDialogsById()).thenReturn(ImmutableMap.of("1", dialog1Mock));
        when(portletMock.getPage()).thenReturn(pageMock);

        processor.traverse(portletMock);

        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visit(dialog1Mock);
        inOrder.verify(visitorMock).visitAfter(dialog1Mock);
        inOrder.verify(visitorMock).visit(pageMock);
        inOrder.verify(visitorMock).visitAfter(pageMock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

    @Test
    public void shouldVisitAPortletPageWithComponents() {
        when(portletMock.getPage()).thenReturn(pageMock);
        when(pageMock.getElements()).thenReturn(asList(panelMock, componentMock));

        processor.traverse(portletMock);

        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visit(pageMock);
        inOrder.verify(visitorMock).visit(panelMock);
        inOrder.verify(visitorMock).visitAfter(panelMock);
        inOrder.verify(visitorMock).visit(componentMock);
        inOrder.verify(visitorMock).visitAfter(componentMock);
        inOrder.verify(visitorMock).visitAfter(pageMock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

    @Test
    public void shouldVisitPortletTabs() {
        when(portletMock.getTabs()).thenReturn(tabsMock);
        when(tabsMock.getElements()).thenReturn(asList(tabMock));
        when(tabMock.getElements()).thenReturn(asList(componentMock));

        processor.traverse(portletMock);

        inOrder.verify(visitorMock).visit(portletMock);
        inOrder.verify(visitorMock).visit(tabsMock);
        inOrder.verify(visitorMock).visit(tabMock);
        inOrder.verify(visitorMock).visit(componentMock);
        inOrder.verify(visitorMock).visitAfter(componentMock);
        inOrder.verify(visitorMock).visitAfter(tabMock);
        inOrder.verify(visitorMock).visitAfter(tabsMock);
        inOrder.verify(visitorMock).visitAfter(portletMock);
    }

}
