package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import com.google.common.collect.ImmutableMap;
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
public class QueryUpdateStatementGeneratorTest {
    private QueryUpdateStatementGenerator generator;

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
        when(columnsMock.getUpdateColumnNames()).thenReturn(asList("COL1", "COL2"));
        when(columnsMock.getPrimaryKeyNames()).thenReturn(asList("ID1","ID2"));
        when(rowMock.getValues()).thenReturn(ImmutableMap.<String,Object>of("COL1", "1", "COL2", 2));
        when(rowMock.getId()).thenReturn(rowIdMock);
        when(rowIdMock.get("ID1")).thenReturn("3");
        when(rowIdMock.get("ID2")).thenReturn("4");

        generator = new QueryUpdateStatementGenerator(tableMock);

    }

    @Test
    public void shouldReturnAnUpdateStatement() {
        GString update = generator.generateStatement(rowMock);

        assertThat(update.getStrings().length, is(4));
        assertThat(update.getStrings()[0], is("UPDATE \"MYTABLE\" SET \"COL1\"="));
        assertThat(update.getStrings()[1], is(", \"COL2\"="));
        assertThat(update.getStrings()[2], is(" WHERE \"ID1\"="));
        assertThat(update.getStrings()[3], is(" AND \"ID2\"="));

        assertThat(update.getValues().length, is(4));
        assertEquals("1", update.getValues()[0]);
        assertEquals(2, update.getValues()[1]);
        assertEquals("3", update.getValues()[2]);
        assertEquals("4", update.getValues()[3]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingTable() {
        when(containerMock.getTablename()).thenReturn(null);
        generator = new QueryUpdateStatementGenerator(tableMock);
        generator.generateStatement(rowMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNothingUpdateable() {
        when(columnsMock.getUpdateColumnNames()).thenReturn(Collections.<String>emptyList());
        generator = new QueryUpdateStatementGenerator(tableMock);
        generator.generateStatement(rowMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingPrimaryKey() {
        when(columnsMock.getPrimaryKeyNames()).thenReturn(Collections.<String>emptyList());
        generator = new QueryUpdateStatementGenerator(tableMock);
        generator.generateStatement(rowMock);
    }
}
