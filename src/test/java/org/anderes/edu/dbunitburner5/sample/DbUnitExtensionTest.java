package org.anderes.edu.dbunitburner5.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.anderes.edu.dbunitburner5.DbUnitExtension;
import org.anderes.edu.dbunitburner5.ShouldMatchDataSet;
import org.anderes.edu.dbunitburner5.UsingDataSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Disabled("Testklasse nur für Dokumentationszwecke")
@ExtendWith(DbUnitExtension.class)
class DbUnitExtensionTest {

    @SuppressWarnings("unused")
    private Connection connection = getConnection();
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.json" })
    @ShouldMatchDataSet(
            value = { "/sample/prepare.json" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    void databaseTest() {
        
    }
    
    private Connection getConnection() {
        Connection connection = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "APP");
        connectionProps.put("password", "");
        try {
            connection = DriverManager.getConnection("jdbc:derby:memory:testDB;create=true", connectionProps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
