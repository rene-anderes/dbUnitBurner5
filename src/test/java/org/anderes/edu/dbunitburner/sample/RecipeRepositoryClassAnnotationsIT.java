package org.anderes.edu.dbunitburner.sample;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.anderes.edu.dbunitburner.CleanupUsingScript;
import org.anderes.edu.dbunitburner.DbUnitExtension;
import org.anderes.edu.dbunitburner.ShouldMatchDataSet;
import org.anderes.edu.dbunitburner.UsingDataSet;
import org.anderes.edu.dbunitburner.sample.data.Ingredient;
import org.anderes.edu.dbunitburner.sample.data.Recipe;
import org.anderes.edu.dbunitburner.sample.data.RecipeRepository;
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
        assertThat(recipes, is(notNullValue()));
        assertThat(recipes.iterator().hasNext(), is(true));
        int counter = 0;
        for (Recipe recipe : recipes) {
            assertThat(recipe.getTitle(), is(notNullValue()));
            counter++;
        }
        assertThat(counter, is(2));
    }
    
    @Test
    public void shouldBeOneRecipe() {
        final Optional<Recipe> recipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(recipe.isPresent(), is(true));
        assertThat(recipe.get().getTitle(), is("Arabische Spaghetti"));
    }
    
    @Test
    @ShouldMatchDataSet(
            value = { "/sample/prepare.xls" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void getRecipesByTitle() {
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");

        assertNotNull(recipes);
        assertThat(recipes.size(), is(1));
        final Recipe recipe = recipes.iterator().next();
        assertThat(recipe.getTitle(), is("Arabische Spaghetti"));
    }
    
    @Test
    public void shouldBeSaveNewRecipe() {
        // given
        final Recipe newRecipe = RecipeBuilder.buildRecipe();
        
        // when
        final Recipe savedRecipe = repository.save(newRecipe);
        
        // then
        assertThat(savedRecipe, is(not(nullValue())));
        assertThat(savedRecipe.getUuid(), is(not(nullValue())));
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe.isPresent(), is(true));
        assertNotSame(newRecipe, findRecipe.get());
        assertThat(newRecipe, is(findRecipe.get()));
    }
    
    @Test
    @ShouldMatchDataSet(value = { "/sample/expected-afterUpdate.xls" },
            excludeColumns = { "INGREDIENT.ID" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ANNOTATION" }
    )
    public void shouldBeUpdateRecipe() {
        final Optional<Recipe> updateRecipe = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat(updateRecipe.isPresent(), is(true));
        updateRecipe.get().setPreamble("Neuer Preamble vom Test");
        updateRecipe.get().addIngredient(new Ingredient("1", "Tomate", "vollreif"));
        final Recipe savedRecipe = repository.save(updateRecipe.get());
        
        assertThat(savedRecipe, is(not(nullValue())));
        assertThat(savedRecipe.getPreamble(), is("Neuer Preamble vom Test"));
        assertThat(savedRecipe.getIngredients().size(), is(4));
        
        final Optional<Recipe> findRecipe = repository.findById(savedRecipe.getUuid());
        assertThat(findRecipe.isPresent(), is(true));
        assertThat(findRecipe.get().getPreamble(), is("Neuer Preamble vom Test"));
        assertNotSame(updateRecipe.get(), findRecipe.get());
        assertThat(updateRecipe.get(), is(findRecipe.get()));
    }
    
    @Test
    @ShouldMatchDataSet(value = { "/sample/expected-afterDeleteOne.xls" },
            excludeColumns = { "RECIPE.ADDINGDATE" },
            orderBy = { "RECIPE.UUID", "INGREDIENT.ID" })
    public void shouldBeDelete() {
        final Optional<Recipe> toDelete = repository.findById("c0e5582e-252f-4e94-8a49-e12b4b047afb");
        assertThat("Das Rezept mit der ID 'c0e5582e-252f-4e94-8a49-e12b4b047afb' existiert nicht in der Datenbank", toDelete.isPresent(), is(true));
        repository.delete(toDelete.get());
        
        final Collection<Recipe> recipes = repository.findByTitleLike("%Spaghetti%");
        assertNotNull(recipes);
        assertThat(recipes.size(), is(0));
    }
    
    @Test
    public void shouldBeFindAllTag() {
        final List<String> tags = repository.findAllTag();
        assertThat(tags, is(notNullValue()));
        assertThat(tags.size(), is(4));
    }
}
