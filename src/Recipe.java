import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private BigDecimal proteinPer100;
    private BigDecimal carbPer100;
    private BigDecimal fatPer100;
    private BigDecimal caloriesPer100;

    private int serving;
    private BigDecimal proteinPerServing;
    private BigDecimal carbPerServing;
    private BigDecimal fatPerServing;
    private BigDecimal caloriesPerServing;

    private int totalCantity;
    private BigDecimal totalProtein;
    private BigDecimal totalCarb;
    private BigDecimal totalFat;
    private BigDecimal totalCalories;

    private List<Ingredient> ingredientList = new ArrayList<>();
    private IngredientManager ingredientManager = new IngredientManager();
    private RecipeManager recipeManager = new RecipeManager();
    private final int MAX_CANTITY_VALUE = 2000000000;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public Recipe(String name, List<Ingredient> ingredientList) {
        // constructor gets called whenever a new recipe is being created

        this.ingredientList = ingredientList;
        this.name = name;
    }

    // ================================================= EDIT RECIPE ===================================================

    public boolean edit() throws IOException {
        // called whenever the user wants to edit the recipe
        // false -- the recipe has been deleted
        // true -- the recipe is good to be mixed

        System.out.println("Do you want to edit the recipe? (Y/N)");
        if (recipeManager.getConfirmation()) {
            return getEditChoice();
        } else {
            return true;
        }
    }

    private boolean getEditChoice() throws IOException {
        // edit options
        // false -- the recipe has been deleted
        // true -- the recipe is good to be mixed

        while (true) {
            System.out.println("\nTYPE: ");
            System.out.println("1 - to edit the name of the recipe");
            System.out.println("2 - to edit the ingredients of the recipe");
            System.out.println("3 - to add an ingredient to the recipe");
            System.out.println("4 - to remove an ingredient from the recipe");
            System.out.println("5 - to delete the recipe");
            System.out.println("9 - to go back");

            String choice = Main.scanner.nextLine().trim();
            label:
            while (true) {
                switch (choice) {
                    case "1":                 // edit the name of the recipe
                        name = recipeManager.setRecipeName(getRecipeIndex());
                        break label;
                    case "2":                 // edit the ingredients of the recipe
                        recipeManager.printRecipeInfo(name, ingredientList);
                        System.out.print("Type the index of the ingredient that you'd like to edit [leave BLANK to skip]: ");
                        int ingIndexToEdit = ingredientManager.chooseIngredient(true, ingredientList);
                        if (ingIndexToEdit == -1) {
                            // skip
                            break label;
                        }

                        String ingNameBeforeEdit = ingredientList.get(ingIndexToEdit).getName();
                        // used to recognize that ingredient in different recipes

                        ingredientList.get(ingIndexToEdit).editIngredient(ingredientList);
                        editIngredientInOtherRecipes(ingIndexToEdit, ingNameBeforeEdit);
                        editIngredientInUniqueList(ingIndexToEdit, ingNameBeforeEdit);
                        Main.savedIngredientsFileClass.editSavedIngredient(ingNameBeforeEdit, ingredientList.get(ingIndexToEdit));
                        break label;
                    case "3":                 // add an ingredient to the recipe
                        addIngredient();
                        break label;
                    case "4":                 // remove an ingredient to the recipe
                        ingredientManager.removeIngredient(false, ingredientList);
                        break label;
                    case "5":                 // delete the recipe
                        System.out.println("Are you sure you want to delete the recipe? (Y/N)");
                        if (recipeManager.getConfirmation()) {
                            recipeManager.deleteRecipe(name, Main.recipeListClass.getRecipeList());
                            return false;
                        }
                        break label;
                    case "9":                 // go back
                        return true;
                    default:
                        System.out.println("[ERROR] WRONG INPUT");
                        choice = Main.scanner.nextLine();
                }
            }

            Files.reupdateFiles();
        }
    }

    private int getRecipeIndex() {
        // iterates through the list of recipes until the name of this recipe matches the one from the list
        // that ingredient index gets returned

        for (int recipeIndex = 0; recipeIndex < Main.recipeListClass.getRecipeList().size(); recipeIndex++) {
            if (name.equals(Main.recipeListClass.getRecipeList().get(recipeIndex).getName())) {
                return recipeIndex;
            }
        }
        return 0;
    }

    private void addIngredient() {
        // adds either a new ingredient or a saved ingredient to the recipe

        System.out.println("TYPE:");
        System.out.println("1 - to add a new ingredient");
        System.out.println("2 - to add a saved ingredient");
        System.out.println("9 - to go back");

        label:
        while (true) {
            String choice = Main.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    ingredientManager.addNewIngredient(ingredientList);
                    break label;
                case "2":
                    if (!Main.savedIngredientsFileClass.getSavedIngredientsList().isEmpty()) {
                        ingredientManager.addSavedIngredient(true, ingredientList);
                    }
                    break label;
                case "9":
                    return;
            }
        }
    }

    private void editIngredientInOtherRecipes(int ingIndexToEdit, String ingNameToEdit) {
        // used to edit all the ingredients with the one that has been already edited

        for (int recipeIndex = 0; recipeIndex < Main.recipeListClass.getRecipeList().size(); recipeIndex++) {
            for (int ingIndex = 0; ingIndex < Main.recipeListClass.getRecipeList().get(recipeIndex).ingredientList.size(); ingIndex++) {
                if (Main.recipeListClass.getRecipeList().get(recipeIndex).ingredientList.get(ingIndex).getName().equals(ingNameToEdit)) {
                    // the edited ingredient is being used in another recipe as well
                    Main.recipeListClass.getRecipeList().get(recipeIndex).ingredientList.set(ingIndex, ingredientList.get(ingIndexToEdit));
                }
            }
        }
    }

    private void editIngredientInUniqueList(int ingIndexToEdit, String ingNameToEdit) {
        // used to edit the ingredient in unique ingredients list with the one that has been already edited

        for (int ingIndex = 0; ingIndex < Main.ingredientListClass.getUniqueIngredientsList().size(); ingIndex++) {
            if (Main.ingredientListClass.getUniqueIngredientsList().get(ingIndex).getName().equals(ingNameToEdit)) {
                // the edited ingredient has been found in the unique ingredients list
                Main.ingredientListClass.getUniqueIngredientsNameList().set(ingIndex, ingNameToEdit);
                Main.ingredientListClass.getUniqueIngredientsList().set(ingIndex, ingredientList.get(ingIndexToEdit));
            }
        }

    }


    // ==================================================== SERVING ====================================================

    private int chooseCantity(boolean serving) {
        // waits for the user's input of cantity
        // used for cantity of ingredients and also for serving

        while (true) {
            String cantity = Main.scanner.nextLine().trim();
            try {
                int cantityInt = Integer.valueOf(cantity);
                if (cantityInt > 0) {
                    if (serving) {
                        if (cantityInt <= totalCantity) {
                            return cantityInt;
                        } else {
                            System.out.println("[ERROR] The serving can't be more than the total cantity of: " + totalCantity + "g");
                        }
                    } else {
                        return cantityInt;
                    }
                } else {
                    System.out.println("[ERROR] The cantity has to be more than 0");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("[ERROR] WRONG INPUT");
            }
        }
    }

    private int lastRecipeIndex;

    public void takeAServing(int choice) {
        // calculates macros per serving

        lastRecipeIndex = choice;
        insertServing();
        calculateTotalMacros();
        calculateMacrosPer100();
        calculateMacrosPerServing();
        printInfo();
    }

    public boolean checkAnotherServing() {
        // when the user has successfully viewed his first serving, he can try another one, go back to main menu, or quit

        System.out.println("TYPE 1 - to try another serving");
        System.out.println("TYPE 2 - to go back to Main Menu");
        System.out.println("TYPE 9 - to close the application");
        while (true) {
            String choice = Main.scanner.nextLine().trim();
            switch (choice) {
                case "1":                 // try another serving
                    takeAServing(lastRecipeIndex);
                    return true;
                case "2":                 // go back to Main Menu
                    return false;
                case "3":                 // close the application
                    System.out.println("APPLICATION IS CLOSING...");
                    Main.hasQuit = true;
                    return false;
                default:
                    System.out.println("[ERROR] WRONG INPUT");
                    break;
            }
        }
    }

    public void mixFood() {
        // all recipe's ingredients get their cantity set

        resetCantity();
        for (Ingredient ing : ingredientList) {
            System.out.print("Set the cantity(in grams) of " + ing.getName() + ": ");
            while (true) {
                int cantity = chooseCantity(false);
                if (totalCantity + cantity >= MAX_CANTITY_VALUE) {
                    System.out.println("[ERROR] The recipe has reached its max capacity of " + MAX_CANTITY_VALUE +"g");
                    System.out.print("Please choose another cantity(in grams) for " + ing.getName() + ": ");
                } else {
                    ing.setCantity(cantity);
                    ing.setTotalMacros();
                    totalCantity += ing.getCantity();
                    break;
                }
            }
        }
    }

    public void insertServing() {
        // choose the cantity for the serving

        System.out.print("Insert the serving(in grams): ");
        serving = chooseCantity(true);
    }


    // ==================================================== MACROS =====================================================

    public void calculateTotalMacros() {
        // the recipe is all done and all of its ingredients macros get mixed together

        resetMacros();
        for (Ingredient i : ingredientList) {
            totalProtein = totalProtein.add(i.getTotalProtein());
            totalCarb = totalCarb.add(i.getTotalCarb());
            totalFat = totalFat.add(i.getTotalFat());

//            totalProtein += i.getTotalProtein();
//            totalCarb += i.getTotalCarb();
//            totalFat += i.getTotalFat();
        }
        totalCalories = totalProtein.add(totalCarb).multiply(new BigDecimal("4")).add(totalFat.multiply(new BigDecimal("9"))).setScale(0, RoundingMode.HALF_EVEN);
        //totalCalories = (totalProtein + totalCarb) * 4 + totalFat * 9;
    }

    public void calculateMacrosPer100() {
        // recipe's total macros get shrink into macros per 100g based on the totalCantity

        proteinPer100 = totalProtein.divide(new BigDecimal(String.valueOf( (double)totalCantity / 100)), 1, RoundingMode.HALF_EVEN);
        carbPer100 = totalCarb.divide(new BigDecimal(String.valueOf( (double)totalCantity / 100)), 1, RoundingMode.HALF_EVEN);
        fatPer100 = totalFat.divide(new BigDecimal(String.valueOf( (double)totalCantity / 100)), 1, RoundingMode.HALF_EVEN);
        caloriesPer100 = proteinPer100.add(carbPer100).multiply(new BigDecimal("4")).add(fatPer100.multiply(new BigDecimal("9"))).setScale(0, RoundingMode.HALF_EVEN);

//        proteinPer100 = totalProtein / ((double)totalCantity / 100);
//        carbPer100 = totalCarb / ((double)totalCantity / 100);
//        fatPer100 = totalFat / ((double)totalCantity / 100);
//        caloriesPer100 = totalCalories / ((double)totalCantity / 100);
    }

    public void calculateMacrosPerServing() {
        // recipe's macros per 100g get multiplied based on the serving

        proteinPerServing = proteinPer100.multiply(new BigDecimal(String.valueOf( (double)serving / 100))).setScale(1, RoundingMode.HALF_EVEN);
        carbPerServing = carbPer100.multiply(new BigDecimal(String.valueOf( (double)serving / 100))).setScale(1, RoundingMode.HALF_EVEN);
        fatPerServing = fatPer100.multiply(new BigDecimal(String.valueOf( (double)serving / 100))).setScale(1, RoundingMode.HALF_EVEN);
        caloriesPerServing = proteinPerServing.add(carbPerServing).multiply(new BigDecimal("4")).add(fatPerServing.multiply(new BigDecimal("9"))).setScale(0, RoundingMode.HALF_EVEN);

//        proteinPerServing = ((double) serving / 100) * proteinPer100;
//        carbPerServing = ((double) serving / 100) * carbPer100;
//        fatPerServing = ((double) serving / 100) * fatPer100;
//        caloriesPerServing = (proteinPerServing + carbPerServing) * 4 + fatPerServing * 9;
    }

    private void resetCantity() {
        // resets the value of totalCanity to 0
        // used before mixing a serving of the recipe

        totalCantity = 0;
    }

    private void resetMacros() {
        // resets the value of macros to 0
        // used before mixing a serving of the recipe

        totalProtein = new BigDecimal("0");
        totalCarb = new BigDecimal("0");
        totalFat = new BigDecimal("0");
        totalCalories = new BigDecimal("0");
    }

    public void printInfo() {
        // recipe is all finished and ready to get printed

        printFinalRecipe();
        printMacrosPer100();
        printMacrosPerServing();
    }

    private void printFinalRecipe() {
        // prints the name of the recipe and the cantity chosen for each ingredients

        System.out.println("\n==========================================================================================");
        System.out.println("        " + name.toUpperCase());
        for (int ingIndex = 0; ingIndex < ingredientList.size(); ingIndex++) {
            System.out.println((ingIndex+1) + ". " + ingredientList.get(ingIndex).getCantity() + "g of " +
                    ingredientList.get(ingIndex).getName());
        }
        System.out.println("TOTAL: " + totalCantity + "g");
    }

    private void printMacrosPer100() {
        // prints macros per 100g of the mixed recipe

        System.out.println("==========================================================================================");
        System.out.println("        MACROS per 100g of mixed " + name.toUpperCase());

        //protein
        if (proteinPer100.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.print("Proteins: " + proteinPer100.intValueExact() +  " || ");
        } else {
            System.out.print("Proteins: " + proteinPer100 + " || ");
        }

        //carbohydrate
        if (carbPer100.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.print("Carbohydrates: " + carbPer100.intValueExact() + " || ");
        } else {
            System.out.print("Carbohydrates: " + carbPer100 + " || ");
        }

        //fat
        if (fatPer100.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.println("Fats: " + fatPer100.intValueExact());
        } else {
            System.out.println("Fats: " + fatPer100);
        }

        //calories
        System.out.println("Calories: " + caloriesPer100);
    }

    private void printMacrosPerServing() {
        // prints macros per serving of mixed recipe

        System.out.println("==========================================================================================");
        System.out.println("        MACROS per serving of " + serving + "g of mixed " + name.toUpperCase());

        //protein
        if (proteinPerServing.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.print("Proteins: " + proteinPerServing.intValueExact() + " || ");
        } else {
            System.out.print("Proteins: " + proteinPerServing + " || ");
        }

        //carbohydrate
        if (carbPerServing.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.print("Carbohydrates: " + carbPerServing.intValueExact() + " || ");
        } else {
            System.out.print("Carbohydrates: " + carbPerServing + " || ");
        }

        //fat
        if (fatPerServing.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0")) ) {
            System.out.println("Fats: " + fatPerServing.intValueExact());
        } else {
            System.out.println("Fats: " + fatPerServing);
        }

        //calories
        System.out.println("Calories: " + caloriesPerServing);
        System.out.println("==========================================================================================\n");
    }

}
