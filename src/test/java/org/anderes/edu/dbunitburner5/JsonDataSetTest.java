package org.anderes.edu.dbunitburner5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.junit.jupiter.api.Test;

public class JsonDataSetTest {

    @Test
    public void shouldBeRightTables() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/dbUnit/forDbUnitExtensionTest.json");
        String[] tables = dataSet.getTableNames();
        
        assertThat(tables).isNotNull();
        assertThat(tables.length).isEqualTo(2);
        assertThat(tables[0]).isEqualTo("RECIPE");
        assertThat(tables[1]).isEqualTo("INGREDIENT");
    }
    
    @Test
    public void shouldBeRightTablesComplete() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/sample/prepare.json");
        String[] tables = dataSet.getTableNames();
        
        assertThat(tables).isNotNull();
        assertThat(tables.length).isEqualTo(3);
        assertThat(tables[0]).isEqualTo("RECIPE");
        assertThat(tables[1]).isEqualTo("TAGS");
        assertThat(tables[2]).isEqualTo("INGREDIENT");
    }
    
    @Test
    public void shouldBeRightITable() throws DataSetException, IOException {
        
        JsonDataSet dataSet = new JsonDataSet("/dbUnit/forDbUnitExtensionTest.json");
        ITable table = dataSet.getTable("RECIPE");
        
        assertThat(table.getValue(0, "ID")).isInstanceOf(Number.class);
        assertThat((Integer)table.getValue(0, "ID")).isEqualTo(100);
        assertThat(table.getValue(0, "TITLE")).isInstanceOf(String.class);
        assertThat((String)table.getValue(0, "TITLE")).isEqualTo("Arabische Spaghetti");
        assertThat(table.getValue(0, "PREAMBLE")).isInstanceOf(String.class);
        assertThat((String)table.getValue(0, "PREAMBLE")).isEqualTo("Da bei diesem Rezept das Scharfe (Curry) mit dem Süssen (Sultaninen) gemischt wird, habe ich diese Rezept \"Arabische Spaghetti\" benannt.");
        assertThat(table.getValue(0, "ADDINGDATE")).isInstanceOf(String.class);
        assertThat((String)table.getValue(0, "ADDINGDATE")).isEqualTo("22.01.2014 23:03:20");
    }
}
