import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class IngredientManager {

    private RecipeManager recipeManager = new RecipeManager();

    public int chooseIngredient(boolean skippable, List<Ingredient> ingList) {
        // used when the user has to input an index, to choose the ingredient
        // used to edit/remove/delete an ingredient
        // if skippable is true, this method can be skipped by inputing a blank line.
        // ^ this will end up returning -1, which will later be used by skipping the method that would have been next

        while (true) {
            String choice = Main.scanner.nextLine().trim();
            if (skippable) {
                if (choice.isEmpty()) {
                    return -1;
                }
            }

            try {
                int choiceInt = Integer.valueOf(choice) -1;
                if (choiceInt < ingList.size() && choiceInt >= 0) {
                    return choiceInt;
                } else {
                    System.out.println("[ERROR] WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] [NON-NUMERIC] WRONG INPUT");
            }
        }
    }

    public void writeMacrosInFile(Ingredient ingredient, BufferedWriter ingredientTempWriter) throws IOException {
        // used to write the macros from the ingredient passed as parameter, into the BufferedWriter passed as parameter
        // used to write the ingredients into IngredientsFile.txt and SavedIngredientsFile.txt

        // name
        ingredientTempWriter.write(ingredient.getName() + " " + Files.MACRO_SEPARATOR + " ");

        // protein
        if (ingredient.getProteinPer100().remainder(new BigDecimal("1")).equals(new BigDecimal("0"))) {
            // no decimals
            ingredientTempWriter.write(ingredient.getProteinPer100().setScale(0, RoundingMode.CEILING) + " " + Files.MACRO_SEPARATOR + " ");
        } else {
            ingredientTempWriter.write(ingredient.getProteinPer100().setScale(1, RoundingMode.HALF_EVEN) + " " + Files.MACRO_SEPARATOR + " ");
        }

        // carbohydrate
        if (ingredient.getCarbPer100().remainder(new BigDecimal("1")).equals(new BigDecimal("0"))) {
            // no decimals
            ingredientTempWriter.write(ingredient.getCarbPer100().setScale(0, RoundingMode.CEILING) + " " + Files.MACRO_SEPARATOR + " ");
        } else {
            ingredientTempWriter.write(ingredient.getProteinPer100().setScale(1, RoundingMode.HALF_EVEN) + " " + Files.MACRO_SEPARATOR + " ");
        }

        // fat
        if (ingredient.getFatPer100().remainder(new BigDecimal("1")).equals(new BigDecimal("0"))) {
            // no decimals
            ingredientTempWriter.write( ingredient.getFatPer100().setScale(0, RoundingMode.CEILING) + "\n");
        } else {
            ingredientTempWriter.write(ingredient.getProteinPer100().setScale(1, RoundingMode.HALF_EVEN) + "\n");
        }
    }

    public boolean checkIngredientMacros(String[] words, String line) {
        // returns true if macros are corrupted, false otherwise

        try {
            BigDecimal[] macro = new BigDecimal[3];
            macro[0] = new BigDecimal(words[1].trim()).setScale(1, RoundingMode.HALF_EVEN);
            macro[1] = new BigDecimal(words[2].trim()).setScale(1, RoundingMode.HALF_EVEN);
            macro[2] = new BigDecimal(words[3].trim()).setScale(1, RoundingMode.HALF_EVEN);

            words[1] = String.valueOf(macro[0]);
            words[2] = String.valueOf(macro[1]);
            words[3] = String.valueOf(macro[2]);

            if (!checkMacroValue(words[1], "Protein")) {
                if (!checkMacroValue(words[2], "Carbohydrate")) {
                    if (!checkMacroValue(words[3], "Fat")) {
                        return checkMacroInputs(macro);
                    }
                }
            }
            return true;

        } catch (Exception e) {
            System.out.println("[ERROR] [WRONG FORMAT] CORRUPTED MACROS FOUND IN INGREDIENT: " + line);
            return true;
        }
    }

    public boolean checkIngredientDuplicate(String name, List<Ingredient> ingredientList) {
        // checks if the ingredientList passed as parameter, contains an ingredient with the name of the string passed as parameter

        for (Ingredient i : ingredientList) {
            if (i.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


    // ================================================ NEW INGREDIENT ================================================

    public void addNewIngredient(List<Ingredient> ingList) {
        // adds a new ingredient to the passed list

        String ingredientName = addIngredientName( -1, ingList);
        // ingredientIndex won't be used if edit is false

        BigDecimal[] macro = new BigDecimal[3];
        addIngredientMacros(-1, macro, ingList);
        // ingredientIndex won't be used if edit is false

        ingList.add(new Ingredient(ingredientName, macro[0], macro[1], macro[2]));
        if (!Main.ingredientListClass.getUniqueIngredientsNameList().contains(ingredientName)) {
            // if the unique list of ingredients doesn't contain this new ingredient , then that ingredient will be added
            Main.ingredientListClass.getUniqueIngredientsNameList().add(ingredientName);
            Main.ingredientListClass.getUniqueIngredientsList().add(new Ingredient(ingredientName, macro[0], macro[1], macro[2]));
        }

    }

    public String addIngredientName(int ingredientIndex, List<Ingredient> ingList) {
        // the user inputs a name for the ingredient and then it gets verified for duplicates

        // if ingredientIndex >= 0 then it will be used, aka. this method is called to edit the ingredient
        // else if ingredientIndex == -1 then it won't be used, aka. this method is called to add an ingredient to the list
        // TL:DR >> if ingredientIndex >= 0 >> input is skippable by leaving it BLANK

        while (true) {
            if (ingredientIndex >= 0) {
                System.out.print("Insert a new name for " + ingList.get(ingredientIndex).getName() +
                        " [Leave BLANK to skip] [ONLY a-z and whitespaces]: ");
            } else {
                System.out.print("Insert a name for the ingredient [ONLY a-z and whitespaces]: ");
            }

            String testName = Main.scanner.nextLine().trim().toLowerCase();
            testName = NameEditor.capitalizeFirstLetter(NameEditor.replaceMultipleWhitespaces(testName));

            if (testName.matches(Files.onlyLettersPattern.toString())) {
                if (!checkDuplicateIngInRecipe(ingredientIndex, testName, ingList)) {
                    if (!checkDuplicateInSavedIng(ingredientIndex, testName, ingList)) {
                        System.out.println("Ingredient's name has been successfully set and edited to: " + testName +"\n");
                        return testName;
                    } else {
                        System.out.println("[ERROR] There's already an ingredient saved with the name: " + testName);
                    }
                } else {
                    System.out.println("[ERROR] There's already an ingredient in this recipe with the name: " + testName);
                }
            } else {
                if (ingredientIndex >= 0){
                    if (testName.isEmpty()) {
                        // skip
                        return ingList.get(ingredientIndex).getName();
                    }
                }
                System.out.println("[ERROR] [WRONG FORMAT] WRONG INPUT");
            }
        }
    }

    private boolean checkDuplicateIngInRecipe(int ingredientIndex, String testName, List<Ingredient> ingList) {
        // checks the ingredient list that has been passed if it contains the name chosen for the ingredient

        // if ingredientIndex >= 0 then it will be used, aka. this method is called to edit the ingredient
        // else if ingredientIndex == -1 then it won't be used, aka. this method is called to add an ingredient to the list

        for (Ingredient ing : ingList) {
            if (testName.equals(ing.getName())) {
                if (ingredientIndex >= 0) {
                    if (!testName.equals(ingList.get(ingredientIndex).getName())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDuplicateInSavedIng(int ingredientIndex, String testName, List<Ingredient> ingredientList) {
        // checks the saved ingredient list if it contains the name chosen for the ingredient

        // if ingredientIndex >= 0 then it will be used, aka. this method is called to edit the ingredient
        // else if ingredientIndex == -1 then it won't be used, aka. this method is called to add an ingredient to the list

        for (Ingredient ing : Main.savedIngredientsFileClass.getSavedIngredientsList()) {
            if (testName.equals(ing.getName())) {
                if (ingredientIndex >= 0) {
                    if (!testName.equals(ingredientList.get(ingredientIndex).getName())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public void addIngredientMacros(int ingredientIndex, BigDecimal[] macro, List<Ingredient> ingList) {
        // adds values to the members of the BigDecimal array
        // 0 - Protein
        // 1 - Carbohydrate
        // 2 - Fat

        // if ingredientIndex >= 0 then it will be used, aka. this method is called to edit the ingredient
        // else if ingredientIndex == -1 then it won't be used, aka. this method is called to add an ingredient to the list

        if (ingredientIndex >= 0) {
            do {
            System.out.print("Type a new value for Protein [Leave BLANK to skip]: ");
            macro[0] = chooseMacroValue(ingredientIndex, "Protein", ingList);
            System.out.print("Type a new value for Carbohydrate [Leave BLANK to skip]: ");
            macro[1] = chooseMacroValue(ingredientIndex, "Carbohydrate", ingList);
            System.out.print("Type a new value for Fat [Leave BLANK to skip]: ");
            macro[2] = chooseMacroValue(ingredientIndex,"Fat", ingList);
            } while (checkMacroInputs(macro));

            System.out.println("Ingredient's macros have been successfully edited\n");

        } else {
            do {
                System.out.print("Insert a value for Protein: ");
                macro[0] = chooseMacroValue(ingredientIndex,"Protein", ingList);
                System.out.print("Insert a value for Carbohydrate: ");
                macro[1] = chooseMacroValue(ingredientIndex,"Carbohydrate", ingList);
                System.out.print("Insert a value for Fat: ");
                macro[2] = chooseMacroValue(ingredientIndex,"Fat", ingList);
            } while (checkMacroInputs(macro));

            System.out.println("Ingredient's macros have been successfully set\n");
        }
    }

    private BigDecimal chooseMacroValue(int ingredientIndex, String macro, List<Ingredient> ingList) {
        // if ingredientIndex >= 0 then it will be used, aka. this method is called to edit the ingredient
        // else if ingredientIndex == -1 then it won't be used, aka. this method is called to add an ingredient to the list

        while (true) {
            String value = Main.scanner.nextLine().trim();

            if (ingredientIndex >= 0) {
                if (value.isEmpty()) {
                    // skip
                    switch (macro) {
                        case "Protein":
                            return ingList.get(ingredientIndex).getProteinPer100();
                        case "Carbohydrate":
                            return ingList.get(ingredientIndex).getCarbPer100();
                        case "Fat":
                            return ingList.get(ingredientIndex).getFatPer100();
                    }
                }
            }

            boolean isCorrupted = checkMacroValue(value, macro);
            if (isCorrupted) {
                System.out.println("Insert a value for " + macro + ": ");
            } else {
                BigDecimal valueBD = new BigDecimal(value).setScale(1, RoundingMode.HALF_EVEN);
                if (valueBD.remainder(new BigDecimal("1")).equals(new BigDecimal("0.0"))) {
                    return valueBD.setScale(0, RoundingMode.HALF_EVEN);
                } else {
                    return valueBD.setScale(1, RoundingMode.HALF_EVEN);
                }
            }
        }
    }



    private boolean checkMacroValue(String value, String macro) {
        // returns true if macros are corrupted, false otherwise

        try {
            double testedValue = Double.valueOf(value);

            if (testedValue < 0) {
                System.out.println("[ERROR] [NEGATIVE] WRONG INPUT");
                return true;
            }

            if (testedValue > 100) {
                System.out.println("[ERROR] MORE THAN 100g of " + macro.toUpperCase() + " in 100g of ingredient");
                return true;
            }

        } catch (Exception e) {
            System.out.println("[ERROR] [WRONG FORMAT] WRONG INPUT");
            return true;
        }
        return false;
    }

    private boolean checkMacroInputs(BigDecimal[] macro) {
        // returns true if macros are corrupted, false otherwise
        // 0 - Protein
        // 1 - Carbohydrate
        // 2 - Fat

        if (macro[0].add(macro[1]).add(macro[2]).compareTo(new BigDecimal("100")) > 0) {
            System.out.println("[ERROR] MORE THAN 100g of MACROS in 100g of ingredient");
            return true;
        }

        if (macro[0].add(macro[1]).add(macro[2]).compareTo(new BigDecimal("0")) <= 0) {
            System.out.println("[ERROR] [EMPTY] WRONG MACROS");
            return true;
        }

        return false;
    }


    // =============================================== SAVED INGREDIENT ================================================

    public void addSavedIngredient(boolean skippable, List<Ingredient> ingList) {
        // adds a saved ingredient to the passed list
        // if skippable is true, this method can be skipped by inputing a blank line.

        if (Main.savedIngredientsFileClass.getSavedIngredientsList().isEmpty()) {
            System.out.println("[ERROR] There are no ingredients saved");
            return;
        }
        System.out.println("Type the index of the ingredient that you'd like to add to the recipe [Leave BLANK to skip]: " );
        for (int ingIndex = 0; ingIndex < Main.savedIngredientsFileClass.getSavedIngredientsList().size(); ingIndex++) {
            System.out.println((ingIndex+1) + ". " + Main.savedIngredientsFileClass.getSavedIngredientsList().get(ingIndex).getName() + " || " +
                    Main.savedIngredientsFileClass.getSavedIngredientsList().get(ingIndex).getProteinPer100() + " P - " +
                    Main.savedIngredientsFileClass.getSavedIngredientsList().get(ingIndex).getCarbPer100() + " C - " +
                    Main.savedIngredientsFileClass.getSavedIngredientsList().get(ingIndex).getFatPer100() + " F - " +
                    Main.savedIngredientsFileClass.getSavedIngredientsList().get(ingIndex).getCaloriesPer100() + " Kcal");
        }

        while (true) {
            String choice = Main.scanner.nextLine().trim();
            if (skippable) {
                if (choice.isEmpty()) {
                    break;
                }
            }

            try {
                int choiceInt = Integer.valueOf(choice) -1;
                if (choiceInt < Main.savedIngredientsFileClass.getSavedIngredientsList().size() && choiceInt >= 0) {
                    if (!checkIngredientList(choiceInt, ingList)) {
                        ingList.add(Main.savedIngredientsFileClass.getSavedIngredientsList().get(choiceInt));
                        String savedIngName = Main.savedIngredientsFileClass.getSavedIngredientsList().get(choiceInt).getName();

                        if (!Main.ingredientListClass.getUniqueIngredientsNameList().contains(savedIngName)) {
                            // if the unique list of ingredients doesn't contain this new ingredient , then that ingredient will be added
                            Main.ingredientListClass.getUniqueIngredientsNameList().add(savedIngName);
                            Main.ingredientListClass.getUniqueIngredientsList().add(Main.savedIngredientsFileClass.getSavedIngredientsList().get(choiceInt));
                        }
                        System.out.println(Main.savedIngredientsFileClass.getSavedIngredientsList().get(choiceInt).getName() +
                                " has been successfully added to the recipe");
                        break;
                    } else {
                        System.out.println("[ERROR] " + Main.savedIngredientsFileClass.getSavedIngredientsList().get(choiceInt).getName() +
                                " is already in the recipe");
                    }
                } else {
                    System.out.println("[ERROR] WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] [NON-NUMERIC] WRONG INPUT");
            }
        }
    }

    private boolean checkIngredientList(int choice, List<Ingredient> ingList) {
        // iterates through the passed list of ingredients and checks if the saved ingredient that was chosen, is already in the list
        
        for (Ingredient ing : ingList) {
            if (ing.getName().equals(Main.savedIngredientsFileClass.getSavedIngredientsList().get(choice).getName())) {
                return true;
            }
        }

        return false;
    }

    // =============================================== REMOVE INGREDIENT ===============================================

    public void removeIngredient(boolean newRecipe, List<Ingredient> ingredientList) {
        // removes an ingredient from the recipe
        // if newRecipe is true, this will perform an extra check on the occurrences of an ingredient, in the passed list of ingredients

        if (!ingredientList.isEmpty()) {
            System.out.println("Type the index of the ingredient that you'd like to remove [Leave BLANK to skip]: ");
            for (int i = 0; i < ingredientList.size(); i++) {
                System.out.println((i + 1) + ". " + ingredientList.get(i).getName() + " || " + ingredientList.get(i).getProteinPer100() + " P - " +
                        ingredientList.get(i).getCarbPer100() + " C - " + ingredientList.get(i).getFatPer100() + " F - " +
                        ingredientList.get(i).getCaloriesPer100() + " Kcal");
            }
            int ingIndex = chooseIngredient(true, ingredientList);
            if (ingIndex == -1) {
                // skip
                return;
            }

            String ingName = ingredientList.get(ingIndex).getName();
            int ingNameOccurrences = 0;
            for (int recipeIndex = 0; recipeIndex < Main.recipeListClass.getRecipeList().size(); recipeIndex++) {
                for (int ingredientIndex = 0; ingredientIndex < Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList().size(); ingredientIndex++) {
                    if (Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList().get(ingredientIndex).getName().equals(ingName)) {
                        ingNameOccurrences++;
                    }
                }
            }

            if (newRecipe) {
                for (Ingredient ingredient : ingredientList) {
                    if (ingredient.getName().equals(ingName)) {
                        ingNameOccurrences++;
                    }
                }
            }

            if (ingNameOccurrences == 1) {
                // if the chosen ingredient is only used once , then that ingredient will be removed from the unique ingredients list
                Main.ingredientListClass.getUniqueIngredientsNameList().remove(ingName);
                Main.ingredientListClass.getUniqueIngredientsList().remove(ingredientList.get(ingIndex));
            }

            ingredientList.remove(ingIndex);
            System.out.println(ingName + " has been successfully removed from the recipe");
        } else {
            System.out.println("[ERROR] The recipe doesn't contain any ingredients");
        }
    }

    // =============================================== DELETE INGREDIENT ===============================================

    public void deleteIngredient(List <Ingredient> ingredientList) throws IOException {
        // deletes an ingredient from SavedIngredientsFile and IngredientsFile

        if (!ingredientList.isEmpty()) {
            System.out.println("Type the index of the ingredient that you'd like to delete [Leave BLANK to skip]: ");
            for (int i = 0; i < ingredientList.size(); i++) {
                System.out.println((i + 1) + ". " + ingredientList.get(i).getName() + " || " + ingredientList.get(i).getProteinPer100() + " - " +
                        ingredientList.get(i).getCarbPer100() + " - " + ingredientList.get(i).getFatPer100() + " - " +
                        ingredientList.get(i).getCaloriesPer100());
            }
            int ingIndex = chooseIngredient(true, ingredientList);
            if (ingIndex == -1) {
                // skip
                return;
            }

            System.out.println("Are you sure you want to delete the ingredient? (Y/N)");
            if (!recipeManager.getConfirmation()) {
                return;
            }

            String ingName = ingredientList.get(ingIndex).getName();
            ingredientList.remove(ingIndex);
            removeIngredientFromRecipes(ingName);
            removeIngredientFromUniqueList(ingName);
            Main.recipeListClass.removeEmptyRecipes();
            System.out.println(ingName + " has been successfully deleted from the list");

            Main.ingredientFileClass.reupdateFile();
            Main.recipeFileClass.reupdateFile();
            Main.savedIngredientsFileClass.reupdateFiles();

        } else {
            System.out.println("[ERROR] There are no ingredients saved");
        }
    }

    private void removeIngredientFromUniqueList(String ingName) {
        // this method removes the deleted ingredient from the unique ingredients list

        Main.ingredientListClass.getUniqueIngredientsNameList().remove(ingName);
        for (int ingredientIndex = 0; ingredientIndex < Main.ingredientListClass.getUniqueIngredientsList().size(); ingredientIndex++) {
            if (Main.ingredientListClass.getUniqueIngredientsList().get(ingredientIndex).getName().equals(ingName)) {
                Main.ingredientListClass.getUniqueIngredientsList().remove(ingredientIndex);
                break;
            }
        }
    }

    private void removeIngredientFromRecipes(String ingName) {
        // this method removes the deleted ingredient from the all the recipes

        for (int recipeIndex = 0; recipeIndex < Main.recipeListClass.getRecipeList().size(); recipeIndex++) {
            for (int ingredientIndex = 0; ingredientIndex < Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList().size(); ingredientIndex++) {
                if (ingName.equals(Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList().get(ingredientIndex).getName())) {
                    Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList().remove(ingredientIndex);
                    ingredientIndex--;
                }
            }
        }
    }
}
