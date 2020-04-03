import java.io.IOException;
import java.util.List;

public class RecipeManager {

    public String setRecipeName(int recipeIndex) {
        // the user inputs a name for the recipe and then it gets verified for duplicates

        // if recipeIndex >= 0 then it will be used, aka. this method is called to edit the recipe
        // else if recipeIndex == -1 then it won't be used, aka. this method is called to add a new recipe
        // TL:DR >> if recipeIndex >= 0 >> input is skippable by leaving it BLANK

        while (true) {
            if (recipeIndex >= 0) {
                System.out.print("Insert a new name for " + Main.recipeListClass.getRecipeList().get(recipeIndex).getName() +
                        " [Leave BLANK to skip] [ONLY a-z and whitespaces]: ");
            } else {
                System.out.print("Insert a name for the recipe [ONLY a-z, 0-9 and whitespaces]: ");
            }

            String testName = Main.scanner.nextLine().trim().toLowerCase();
            testName = NameEditor.capitalizeWords(NameEditor.replaceMultipleWhitespaces(testName));

            if (testName.matches(Files.onlyLettersAndNumPattern.toString())) {
                if (!testName.equals("corrupted")) {
                    if (!checkDuplicateRecipe(recipeIndex, testName)) {
                        System.out.println("Recipe's name has been successfully set and edited to: " + testName + "\n");
                        return testName;
                    } else {
                        System.out.println("[ERROR] There's already a recipe with the name: " + testName);
                    }
                } else {
                    System.out.println("[ERROR] Recipe's name cannot be << Corrupted >> ");
                }
            } else {
                if (recipeIndex >= 0) {
                    if (testName.isEmpty()) {
                        // skip
                        return Main.recipeListClass.getRecipeList().get(recipeIndex).getName();
                    }
                }
                System.out.println("[ERROR] [WRONG FORMAT] WRONG INPUT");
            }
        }
    }

    private boolean checkDuplicateRecipe(int recipeIndex, String testName) {
        // checks the list of recipes if it contains the name chosen for the recipe

        // if recipeIndex >= 0 then it will be used, aka. this method is called to edit the recipe
        // else if recipeIndex == -1 then it won't be used, aka. this method is called to add a new recipe
        // TL:DR >> if recipeIndex >= 0 >> input is skippable by leaving it BLANK

        for (Recipe recipe : Main.recipeListClass.getRecipeList()) {
            if (testName.equals(recipe.getName())) {
                if (recipeIndex >= 0) {
                    if (!Main.recipeListClass.getRecipeList().get(recipeIndex).getName().equals(recipe.getName())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public void printRecipeInfo(String recipeName, List<Ingredient> ingredientList) {
        // prints the name of the recipe and its ingredients and also their macros

        System.out.println("\n==========================================================================================");
        System.out.println("          " + recipeName);
        for (int i = 0; i < ingredientList.size(); i++) {
            System.out.print((i+1) + ". " + ingredientList.get(i).getName() + " || ");
            System.out.println(ingredientList.get(i).getProteinPer100() + " P - " + ingredientList.get(i).getCarbPer100()
                    + " C - " + ingredientList.get(i).getFatPer100() + " F - " + ingredientList.get(i).getCaloriesPer100()
                    + " Kcal");
        }
        System.out.println("==========================================================================================\n");
    }

    public int chooseRecipe(boolean skippable, List<Recipe> recipeList) {
        // waits for user's input and repeats until the user has introduced a correct key
        // if skippable is true, the input can be skipped by inserting a blank line

        while (true) {
            String choice = Main.scanner.nextLine().trim();
            if (skippable) {
                if (choice.isEmpty()) {
                    return -1;
                }
            }

            try {
                int choiceInt = Integer.valueOf(choice) -1;
                if (choiceInt < recipeList.size() && choiceInt >= 0) {
                    return choiceInt;
                } else {
                    System.out.println("[ERROR] WRONG INPUT");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] [NON-NUMERIC] WRONG INPUT");
            }
        }
    }

    public boolean getConfirmation() {
        // waits for the input of 'y' or 'n'

        String answer = Main.scanner.nextLine().toLowerCase().trim();
        while(!answer.equals("y") && !answer.equals("n")) {
            answer = Main.scanner.nextLine();
        }

        return answer.equals("y");
    }

    // ================================================= DELETE RECIPE =================================================

    public void deleteRecipe(List<Recipe> recipeList) throws IOException {
        // waits for the user to input the index of the recipe that will be deleted
        // if recipeIndex == -1 >> this action is skipped

        if (!recipeList.isEmpty()) {
            System.out.println("Type the index of the recipe that you'd like to delete [Leave BLANK to skip]: ");
            for (int i = 0; i < recipeList.size(); i++) {
                System.out.println((i + 1) + ". " + recipeList.get(i).getName());
            }
            int recipeIndex = chooseRecipe(true, recipeList);
            if (recipeIndex == -1) {
                // skip
                return;
            }
            String recipeName = recipeList.get(recipeIndex).getName();
            recipeList.remove(recipeIndex);
            System.out.println(recipeName + " has been successfully deleted from the list of recipes");

            Files.reupdateFiles();
        } else {
            System.out.println("[ERROR] There are no recipes in the list");
        }
    }

    public void deleteRecipe(String recipeName, List<Recipe> recipeList) throws IOException {
        // same as the method that has been overloaded but this time, the user has already chosen the recipe that will be deleted

        for (int recipeIndex = 0; recipeIndex < recipeList.size(); recipeIndex++) {
            if (recipeList.get(recipeIndex).getName().equals(recipeName)) {
                recipeList.remove(recipeIndex);
                System.out.println(recipeName + " has been successfully deleted from the list of recipes");
                break;
            }
        }
        Files.reupdateFiles();
    }
}
