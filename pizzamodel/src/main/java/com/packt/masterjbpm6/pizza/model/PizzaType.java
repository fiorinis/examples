package com.packt.masterjbpm6.pizza.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.packt.masterjbpm6.pizza.model.Ingredient.IngredientType;

public class PizzaType implements Serializable {

	private static final long serialVersionUID = -2273576357648156562L;

	static Map<Types, PizzaType> types = new HashMap<Types, PizzaType>();
	private List<Ingredient> ingredients = new LinkedList<Ingredient>();
	private List<Ingredient> moreToppings = new LinkedList<Ingredient>();
	private Types type;
	private double baseprice;

	static {

		addPizzaType(Types.FOURSEASONS, new IngredientType[] {
				IngredientType.MOZZARELLA, IngredientType.TOMATO,
				IngredientType.ARTICHOKES, IngredientType.HAM,
				IngredientType.MUSHROOMS }, 6.5);

		addPizzaType(Types.SALAME, new IngredientType[] {
				IngredientType.MOZZARELLA, IngredientType.TOMATO,
				IngredientType.SALAME }, 5.5);

		addPizzaType(Types.NAPOLI, new IngredientType[] {
				IngredientType.MOZZARELLA, IngredientType.TOMATO,
				IngredientType.ANCHOVIES }, 5.5);

		addPizzaType(Types.MARGHERITA, new IngredientType[] {
				IngredientType.MOZZARELLA, IngredientType.TOMATO }, 5.0);

	}

	private static void addPizzaType(Types type,
			IngredientType[] ingredientTypes, double baseprice) {
		types.put(
				type,
				new PizzaType(type, Arrays.asList(Ingredient
						.getIngredients(ingredientTypes)), baseprice));
	}

	public static PizzaType getType(Types type) {
		return types.get(type);
	}

	private PizzaType(Types type, List<Ingredient> ingredients, double baseprice) {
		this.type = type;
		setIngredients(ingredients);
	}

	public double getBaseprice() {
		return baseprice;
	}

	public void setBaseprice(double baseprice) {
		this.baseprice = baseprice;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
		this.type = type;
	}

	public List<Ingredient> getMoreToppings() {
		return moreToppings;
	}

	public void setMoreToppings(List<Ingredient> moreToppings) {
		this.moreToppings = moreToppings;
	}

	public double getPrice() {
		double price = getBaseprice();
		for (Ingredient topping : moreToppings) {
			price += 0.5;
		}
		return price;
	}

	public List<Material> getAllIngredients() {
		List<Material> materials = new ArrayList<Material>();
		materials.addAll(getIngredients());
		materials.addAll(getMoreToppings());
		return materials;
	}
}
