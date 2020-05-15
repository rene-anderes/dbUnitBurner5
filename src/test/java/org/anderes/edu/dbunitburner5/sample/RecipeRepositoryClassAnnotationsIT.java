package org.anderes.edu.dbunitburner5.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.anderes.edu.dbunitburner5.CleanupUsingScript;
import org.anderes.edu.dbunitburner5.DbUnitExtension;
import org.anderes.edu.dbunitburner5.ShouldMatchDataSet;
import org.anderes.edu.dbunitburner5.UsingDataSet;
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
@CleanupUsingScript(value = { "/sql/DeleteTableContentScript.sql" })
@UsingDataSet(value = { "/sample/prepare.xls" })
public class RecipeRepositoryClassAnnotationsIT {

    @Inject
    private RecipeRepository repository;
    @Inject @SuppressWarnings("unused")
    private DataSource datasource; /* Required for extension */
       
    @Test
    @ShouldMatchDataSet(
            value = { "/sample/prepare.xls" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void shouldBeFindAll() {
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
    public void shouldBeOneRecipe() {
        final Optional<Recipe> recipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(recipe.isPresent()).isTrue();
        assertThat(recipe.get().getTitle()).isEqualTo("Arabische Spaghetti");
    }
    
    @Test
    @ShouldMatchDataSet(
            value = { "/sample/prepare.xls" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void getRecipesByTitle() {
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");

        assertNotNull(recipes);
        assertThat(recipes.size()).isEqualTo(1);
        final Recipe recipe = recipes.iterator().next();
        assertThat(recipe.getTitle()).isEqualTo("Arabische Spaghetti");
    }
    
    @Test
    public void shouldBeSaveNewRecipe() {
        // given
        final Recipe newRecipe = RecipeBuilder.buildRecipe();
        
        // when
        final Recipe savedRecipe = repository.save(newRecipe);
        
        // then
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getUuid()).isNotNull();
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe.isPresent()).isTrue();
        assertNotSame(newRecipe, findRecipe.get());
        assertThat(newRecipe).isEqualTo(findRecipe.get());
    }
    
    @Test
    @ShouldMatchDataSet(value = { "/sample/expected-afterUpdate.xls" },
            excludeColumns = { "INGREDIENT.ID" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ANNOTATION" }
    )
    public void shouldBeUpdateRecipe() {
        final Optional<Recipe> updateRecipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(updateRecipe.isPresent()).isTrue();
        updateRecipe.get().setPreamble("Neuer Preamble vom Test");
        updateRecipe.get().addIngredient(new Ingredient("1", "Tomate", "vollreif"));
        final Recipe savedRecipe = repository.save(updateRecipe.get());
        
        assertThat(savedRecipe).isNotNull();
        assertThat(savedRecipe.getPreamble()).isEqualTo("Neuer Preamble vom Test");
        assertThat(savedRecipe.getIngredients().size()).isEqualTo(4);
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe.isPresent()).isTrue();
        assertThat(findRecipe.get().getPreamble()).isEqualTo("Neuer Preamble vom Test");
        assertNotSame(updateRecipe.get(), findRecipe.get());
        assertThat(updateRecipe.get()).isEqualTo(findRecipe.get());
    }
    
    @Test
    @ShouldMatchDataSet(value = { "/sample/expected-afterDeleteOne.xls" },
            excludeColumns = { "RECIPE.ADDINGDATE" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void shouldBeDelete() {
        final Optional<Recipe> toDelete = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(toDelete.isPresent()).as("Das Rezept mit der ID 'c0e5582e-252f-4e94-8a49-e12b4b047afb' existiert nicht in der Datenbank").isTrue();
        repository.delete(toDelete.get());
        
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");
        assertNotNull(recipes);
        assertThat(recipes.size()).isEqualTo(0);
    }
    
    @Test
    public void shouldBeFindAllTag() {
        final List<String> tags = repository.findAllTag();
        assertThat(tags).isNotNull();
        assertThat(tags.size()).isEqualTo(4);
    }
}
