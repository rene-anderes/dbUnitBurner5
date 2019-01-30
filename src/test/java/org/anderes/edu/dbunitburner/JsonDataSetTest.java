package org.anderes.edu.dbunitburner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.anderes.edu.dbunitburner.JsonDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;

public class JsonDataSetTest {

    @Test
    public void shouldBeRightTables() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/dbUnit/forDbUnitRuleTest.json");
        String[] tables = dataSet.getTableNames();
        
        assertThat(tables, is(notNullValue()));
        assertThat(tables.length, is(2));
        assertThat(tables[0], is("RECIPE"));
        assertThat(tables[1], is("INGREDIENT"));
    }
    
    @Test
    public void shouldBeRightTablesComplete() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/sample/prepare.json");
        String[] tables = dataSet.getTableNames();
        
        assertThat(tables, is(notNullValue()));
        assertThat(tables.length, is(3));
        assertThat(tables[0], is("RECIPE"));
        assertThat(tables[1], is("TAGS"));
        assertThat(tables[2], is("INGREDIENT"));
    }
    
    @Test
    public void shouldBeRightITable() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/dbUnit/forDbUnitRuleTest.json");
        ITable table = dataSet.getTable("RECIPE");
        
        assertThat(table.getValue(0, "ID"), is(instanceOf(Number.class)));
        assertThat((Integer)table.getValue(0, "ID"), is(100));
        assertThat(table.getValue(0, "TITLE"), is(instanceOf(String.class)));
        assertThat((String)table.getValue(0, "TITLE"), is("Arabische Spaghetti"));
        assertThat(table.getValue(0, "PREAMBLE"), is(instanceOf(String.class)));
        assertThat((String)table.getValue(0, "PREAMBLE"), is("Da bei diesem Rezept das Scharfe (Curry) mit dem Süssen (Sultaninen) gemischt wird, habe ich diese Rezept \"Arabische Spaghetti\" benannt."));
        assertThat(table.getValue(0, "ADDINGDATE"), is(instanceOf(String.class)));
        assertThat((String)table.getValue(0, "ADDINGDATE"), is("22.01.2014 23:03:20"));
    }
}
