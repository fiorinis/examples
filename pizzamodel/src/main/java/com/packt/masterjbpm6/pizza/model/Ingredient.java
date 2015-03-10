package com.packt.masterjbpm6.pizza.model;

public class Ingredient extends Material {

	private static final long serialVersionUID = 2781285770516421119L;

	private IngredientType type;

	public static enum IngredientType {
		TOMATO, MOZZARELLA, SALAME, PEPPERONI, ANCHOVIES, ONIONS, ARTICHOKES, HAM, MUSHROOMS, OLIVES

	}

	public static Ingredient getIngredient(IngredientType it) {
		return new Ingredient(it);
	}

	public static Ingredient[] getIngredients(IngredientType[] its) {
		Ingredient[] ingredients = new Ingredient[its.length];
		int idx = 0;
		for (IngredientType it : its) {
			ingredients[idx++] = getIngredient(it);
		}
		return ingredients;
	}

	public Ingredient(IngredientType type) {
		this.type = type;
	}

	@Override
	public String getId() {
		return type.toString();
	}
}
