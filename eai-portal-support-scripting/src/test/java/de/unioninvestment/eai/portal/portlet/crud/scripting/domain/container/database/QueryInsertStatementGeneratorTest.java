package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.database;

import com.google.common.collect.ImmutableMap;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.DatabaseQueryContainer;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Table;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.TableColumns;
import de.unioninvestment.eai.portal.portlet.crud.scripting.model.ScriptRow;
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
public class QueryInsertStatementGeneratorTest {
    private QueryInsertStatementGenerator generator;

    @Mock
    private ScriptRow rowMock;

    @Mock
    private Table tableMock;

    @Mock
    private TableColumns columnsMock;
    @Mock
    private DatabaseQueryContainer containerMock;

    @Before
    public void setup() {
        initMocks(this);

        when(tableMock.getColumns()).thenReturn(columnsMock);
        when(tableMock.getContainer()).thenReturn(containerMock);
        when(containerMock.getTablename()).thenReturn("MYTABLE");
        when(columnsMock.getInsertColumnNames()).thenReturn(asList("COL1", "COL2"));
        when(columnsMock.getPrimaryKeyNames()).thenReturn(asList("ID1","ID2"));
        when(rowMock.getValues()).thenReturn(ImmutableMap.<String,Object>of("COL1", "1", "COL2", 2));

        generator = new QueryInsertStatementGenerator(tableMock);

    }

    @Test
    public void shouldReturnAnInsertStatement() {
        GString insert = generator.generateStatement(rowMock);

        assertThat(insert.getStrings().length, is(3));
        assertThat(insert.getStrings()[0], is("INSERT INTO \"MYTABLE\" (\"COL1\",\"COL2\") VALUES ("));
        assertThat(insert.getStrings()[1], is(","));
        assertThat(insert.getStrings()[2], is(")"));

        assertThat(insert.getValues().length, is(2));
        assertEquals("1", insert.getValues()[0]);
        assertEquals(2, insert.getValues()[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingTable() {
        when(containerMock.getTablename()).thenReturn(null);
        generator = new QueryInsertStatementGenerator(tableMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNothingInsertable() {
        when(columnsMock.getInsertColumnNames()).thenReturn(Collections.<String>emptyList());
        generator = new QueryInsertStatementGenerator(tableMock);
    }

}
