package org.anderes.edu.dbunitburner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.anderes.edu.dbunitburner.SqlHelper;
import org.junit.jupiter.api.Test;

public class SqlHelperTest {
    
    @Test
    public void shouldBeExtractCommands() throws IOException {
        Path sqlFile = Paths.get("sql", "DeleteTableContentScript.sql");
        Collection<String> commands = SqlHelper.extractSqlCommands(sqlFile);
        
        assertThat(commands, is(notNullValue()));
        assertThat(commands.size(), is(3));
        final Iterator<String> iterator = commands.iterator();
        assertThat(iterator.next(), is("delete from TAGS"));
        assertThat(iterator.next(), is("delete from INGREDIENT"));
        assertThat(iterator.next(), is("delete from RECIPE"));
    }
    
    @Test
    public void shouldBeExtractCommandsUnix() throws IOException {
        Path sqlFile = Paths.get("sql", "DeleteTableContentScriptUnix.sql");
        Collection<String> commands = SqlHelper.extractSqlCommands(sqlFile);
        
        assertThat(commands, is(notNullValue()));
        assertThat(commands.size(), is(3));
        final Iterator<String> iterator = commands.iterator();
        assertThat(iterator.next(), is("delete from TAGS"));
        assertThat(iterator.next(), is("delete from INGREDIENT"));
        assertThat(iterator.next(), is("delete from RECIPE"));
    }
    
    @Test
    public void shouldBeWrongPath() throws IOException {
        Path sqlFile = Paths.get("notValid", "sql", "DeleteTableContentScript.sql");
        assertThrows(IOException.class, () -> { SqlHelper.extractSqlCommands(sqlFile); });
    }

    @Test
    public void shouldBeExecuteBatchCommands() throws SQLException {
        // given
        final List<String> commands = new ArrayList<String>(3);
        commands.add("delete from INGREDIENT");
        commands.add("delete from TAGS");
        commands.add("delete from RECIPE");
        Connection connection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        int[] batchResult = { 2, 4, 1 };
        when(mockStatement.executeBatch()).thenReturn(batchResult);
        when(connection.createStatement()).thenReturn(mockStatement);
        
        // when
        int[] values = SqlHelper.execute(connection, commands);
        
        // then
        assertThat(values.length, is(3));
        verify(mockStatement).addBatch("delete from INGREDIENT");
        verify(mockStatement).addBatch("delete from TAGS");
        verify(mockStatement).addBatch("delete from RECIPE");
        verify(mockStatement).executeBatch();
    }
}
