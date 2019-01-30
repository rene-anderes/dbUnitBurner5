package org.anderes.edu.dbunitburner5.sample;

import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.anderes.edu.dbunitburner5.DbUnitExtension;
import org.anderes.edu.dbunitburner5.ShouldMatchDataSet;
import org.anderes.edu.dbunitburner5.UsingDataSet;
import org.anderes.edu.dbunitburner5.sample.data.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Disabled("Für diesen Test muss eine MySQL-Server-Instanz laufen und "
                + "auf dieser eine Datenbank mittels dem Script 'sql/mysql/mysql-createUserAndSchema.sql' ein DB-schema erstellt sein.")
@ExtendWith(DbUnitExtension.class)
public class MySQLTest {
    
    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mySqlPU", getProperties());
    private EntityManager manager;
    @SuppressWarnings("unused")
    private Connection connection = getConnection();

    @BeforeEach
    public void setup() {
        manager = entityManagerFactory.createEntityManager();
    }
    
    @AfterEach
    public void tearDown() {
        manager.close();
    }

    @Test
    @UsingDataSet(value = { "/sample/prepareForMySql.json" })
    @ShouldMatchDataSet(
            value = { "/sample/prepare.json" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void shouldBeFindAll() {
        
        // when
        final Iterable<Recipe> recipes = manager.createQuery("select r from Recipe r", Recipe.class).getResultList();
        
        // then
        assertThat(recipes, is(notNullValue()));
        assertThat(recipes.iterator().hasNext(), is(true));
        int counter = 0;
        for (Recipe recipe : recipes) {
            assertThat(recipe.getTitle(), is(notNullValue()));
            counter++;
        }
        assertThat(counter, is(2));
    }
    
    private static Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>(1);
        properties.put(ECLIPSELINK_PERSISTENCE_XML, "META-INF/mysql-persistence.xml");
        return properties;
    }
    
    private static Connection getConnection() {
        EntityManager manager = entityManagerFactory.createEntityManager();
        manager.getTransaction().begin();
        Connection connection = manager.unwrap(Connection.class);
        manager.getTransaction().commit();
        return connection;
    }
}
