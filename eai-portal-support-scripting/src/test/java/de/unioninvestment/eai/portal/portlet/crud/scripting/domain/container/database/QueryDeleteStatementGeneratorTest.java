package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRowId;
import groovy.lang.GString;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by cmj on 13.07.14.
 */
public class QueryDeleteStatementGeneratorTest {
    private QueryDeleteStatementGenerator generator;

    @Mock
    private ScriptRow rowMock;

    @Mock
    private Table tableMock;

    @Mock
    private TableColumns columnsMock;
    @Mock
    private DatabaseQueryContainer containerMock;
    @Mock
    private ScriptRowId rowIdMock;

    @Before
    public void setup() {
        initMocks(this);

        when(tableMock.getColumns()).thenReturn(columnsMock);
        when(tableMock.getContainer()).thenReturn(containerMock);
        when(containerMock.getTablename()).thenReturn("MYTABLE");
        when(columnsMock.getPrimaryKeyNames()).thenReturn(asList("ID1", "ID2"));
        when(rowMock.getId()).thenReturn(rowIdMock);
        when(rowIdMock.get("ID1")).thenReturn("3");
        when(rowIdMock.get("ID2")).thenReturn("4");

        generator = new QueryDeleteStatementGenerator(tableMock);

    }

    @Test
    public void shouldReturnAnUpdateStatement() {
        GString update = generator.generateStatement(rowMock);

        assertThat(update.getStrings().length, is(2));
        assertThat(update.getStrings()[0], is("DELETE FROM \"MYTABLE\" WHERE \"ID1\"="));
        assertThat(update.getStrings()[1], is(" AND \"ID2\"="));

        assertThat(update.getValues().length, is(2));
        assertEquals("3", update.getValues()[0]);
        assertEquals("4", update.getValues()[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingTable() {
        when(containerMock.getTablename()).thenReturn(null);
        generator = new QueryDeleteStatementGenerator(tableMock);
        generator.generateStatement(rowMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingPrimaryKey() {
        when(columnsMock.getPrimaryKeyNames()).thenReturn(Collections.<String>emptyList());
        generator = new QueryDeleteStatementGenerator(tableMock);
        generator.generateStatement(rowMock);
    }
}
