package org.anderes.edu.dbunitburner.sample;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.anderes.edu.dbunitburner.sample.data.Image;
import org.anderes.edu.dbunitburner.sample.data.Ingredient;
import org.anderes.edu.dbunitburner.sample.data.Recipe;


public class RecipeBuilder {

	public static Recipe buildRecipe() {
		final Recipe recipe = new Recipe(UUID.randomUUID().toString());
		recipe.setTitle("Meine neues Rezept");
		recipe.setPreamble("Einige Informationen im Voraus.");
		recipe.setNoOfPerson("2");
		recipe.setPreparation("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque convallis lacus at sagittis scelerisque. Donec at mauris ac mi pharetra.");
		recipe.setAddingDate(december(24, 2000));
		recipe.setLastUpdate(december(31, 2000));
		recipe.setRating(4);
		recipe.addTag("dessert").addTag("süss");
		addIngredients(recipe);
		recipe.setImage(createImage());
		return recipe;
	}
	
	private static Image createImage() {
        return new Image("http://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Ferrari_Werke.JPG/800px-Ferrari_Werke.JPG", "Home of Ferrari");
    }

    public static void addIngredients(final Recipe recipe) {
		recipe.addIngredient(new Ingredient("1", "Tomate", "Vollreif"));
		recipe.addIngredient(new Ingredient("2", "Peperoni", null));
		recipe.addIngredient(new Ingredient("etwas", "Pfeffer", "schwarz"));
	}
	
	private static Date december(int day, int year) {
	    final Calendar cal = Calendar.getInstance();
	    cal.set(year, Calendar.DECEMBER, day);
	    return cal.getTime();
	}
}
