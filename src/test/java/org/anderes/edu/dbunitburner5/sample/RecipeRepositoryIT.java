package org.anderes.edu.dbunitburner5.sample;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.anderes.edu.dbunitburner5.CleanupUsingScript;
import org.anderes.edu.dbunitburner5.DbUnitExtension;
import org.anderes.edu.dbunitburner5.ShouldMatchDataSet;
import org.anderes.edu.dbunitburner5.UsingDataSet;
import org.anderes.edu.dbunitburner5.UsingDataSetScript;
import org.anderes.edu.dbunitburner5.sample.data.Ingredient;
import org.anderes.edu.dbunitburner5.sample.data.Recipe;
import org.anderes.edu.dbunitburner5.sample.data.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(DbUnitExtension.class)
@ContextConfiguration(locations = { "classpath:/sample/application-context.xml" })
class RecipeRepositoryIT {

    @Inject
    private RecipeRepository repository;
    @Inject @SuppressWarnings("unused")
    private DataSource datasource; /* Required for extension */
        
    @Test
    @UsingDataSet(value = { "/sample/prepare.json" })
    @ShouldMatchDataSet(
            value = { "/sample/prepare.json" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    void shouldBeFindAll() {
        Iterable<Recipe> recipes = repository.findAll();
        assertThat(recipes).isNotNull();
        assertThat(recipes.iterator().hasNext()).isTrue();
        int counter = 0;
        for (Recipe recipe : recipes) {
            assertThat(recipe.getTitle()).isNotNull();
            counter++;
        }
        assertThat(counter).isEqualTo(2);
    }
    
    @Test
    @UsingDataSetScript(value = { "/sql/LoadTestdata.sql" })
    void shouldBeFindAllTestdata() {
        Iterable<Recipe> recipes = repository.findAll();
        assertThat(recipes).isNotNull();
        assertThat(recipes.iterator().hasNext()).isTrue();
        int counter = 0;
        for (Recipe recipe : recipes) {
            assertThat(recipe.getTitle()).isNotNull();
            counter++;
        }
        assertThat(counter).isEqualTo(5);
    }
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.xls" })
    void shouldBeOneRecipe() {
        final Optional<Recipe> recipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(recipe).isPresent();
        assertThat(recipe.get().getTitle()).isEqualTo("Arabische Spaghetti");
    }
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.xls" })
    @ShouldMatchDataSet(
            value = { "/sample/prepare.xls" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    void getRecipesByTitle() {
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");

        assertNotNull(recipes);
        assertThat(recipes).hasSize(1);
        final Recipe recipe = recipes.iterator().next();
        assertThat(recipe.getTitle()).isEqualTo("Arabische Spaghetti");
    }
    
    @Test
    @CleanupUsingScript(value = { "/sql/DeleteTableContentScript.sql" })
    @UsingDataSet(value = { "/sample/prepare.xls" })
    void shouldBeSaveNewRecipe() {
        // given
        final Recipe newRecipe = RecipeBuilder.buildRecipe();
        
        // when
        final Recipe savedRecipe = repository.save(newRecipe);
        
        // then
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getUuid()).isNotNull();
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe).isPresent();
        assertNotSame(newRecipe, findRecipe.get());
        assertThat(newRecipe).isEqualTo(findRecipe.get());
    }
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.xls" })
    @ShouldMatchDataSet(value = { "/sample/expected-afterUpdate.xls" },
            excludeColumns = { "INGREDIENT.ID" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ANNOTATION" }
    )
    void shouldBeUpdateRecipe() {
        final Optional<Recipe> updateRecipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(updateRecipe).isPresent();
        updateRecipe.get().setPreamble("Neuer Preamble vom Test");
        updateRecipe.get().addIngredient(new Ingredient("1", "Tomate", "vollreif"));
        final Recipe savedRecipe = repository.save(updateRecipe.get());
        
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getPreamble()).isEqualTo("Neuer Preamble vom Test");
        assertThat(savedRecipe.getIngredients()).hasSize(4);
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe).isPresent();
        final Recipe recipe = findRecipe.get();
        assertThat(recipe.getPreamble()).isEqualTo("Neuer Preamble vom Test");
        assertNotSame(updateRecipe.get(), recipe);
        assertThat(updateRecipe).contains(recipe);
    }
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.xls" })
    @ShouldMatchDataSet(value = { "/sample/expected-afterDeleteOne.xls" },
            excludeColumns = { "RECIPE.ADDINGDATE" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    void shouldBeDelete() {
        final Optional<Recipe> toDelete = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(toDelete).as("Das Rezept mit der ID 'c0e5582e-252f-4e94-8a49-e12b4b047afb' existiert nicht in der Datenbank").isPresent();
        repository.delete(toDelete.get());
        
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");
        assertNotNull(recipes);
        assertThat(recipes).isEmpty();
    }
    
    @Test
    @UsingDataSet(value = { "/sample/prepare.xls" })
    void shouldBeFindAllTag() {
        final List<String> tags = repository.findAllTag();
        assertThat(tags).isNotNull().hasSize(4);
    }
}
