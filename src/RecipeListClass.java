import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeListClass {
    private List<Recipe> recipeList = new ArrayList<>();
    private List<Integer> emptyRecipesIndexList = new ArrayList<>();
    private IngredientManager ingredientManager = new IngredientManager();
    private RecipeManager recipeManager = new RecipeManager();


    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    public List<Integer> getEmptyRecipesIndexList() {
        return emptyRecipesIndexList;
    }

    private int totalRecipes = 0;

    public int getTotalRecipes() {
        return totalRecipes;
    }

    public void setTotalRecipes(int totalRecipes) {
        this.totalRecipes = totalRecipes;
    }

    public void clearLists() {
        // clears the list of recipes
        // used when the user erases the recipes / all data

        recipeList.clear();
        Main.recipeFileClass.getUniqueRecipeList().clear();
    }

    public void viewRecipes() {
        // prints all the recipes

        System.out.println("\n        RECIPES LIST");
        int index = 1;
        for (Recipe r : recipeList) {
            System.out.println(index++ + ". " + r.getName());
        }
        System.out.print("\nType the index of the recipe that you'd like to view [Leave BLANK to go back]: ");
    }


    // ============================================== UPDATE RECIPE LIST ==============================================

    public void updateList() throws IOException {
        // reads every line from txt file, and if the line is not corrupt, the recipe gets added into the list

        BufferedReader recipeReader = new BufferedReader(new FileReader(Files.recipesFile));
        File recipeTempFile = new File("recipeTempFile.txt");
        BufferedWriter recipeTempWriter = new BufferedWriter(new FileWriter(recipeTempFile));

        int recipeCount = -1;
        String line;

        while ((line = recipeReader.readLine()) != null) {
            if (++recipeCount > totalRecipes) {
                break;
            }

            addRecipeIntoList(line, recipeCount, recipeTempWriter);
        }
        recipeTempWriter.flush();
        recipeTempWriter.close();
        recipeReader.close();

        Files.deleteFile(Files.recipesFile);
        Files.renameFile(recipeTempFile, Files.recipesFile);
    }

    private void addRecipeIntoList(String line, int recipeCount, BufferedWriter recipeTempWriter) {
        // tries to add a new recipe into the list
        // throws an exception if it fails, and that recipe is skipped

        try {
            recipeList.add(new Recipe(line, Main.ingredientListClass.getIngredientList().get(recipeCount)));
            recipeTempWriter.write(line + "\n");
        } catch (Exception e) {
            System.out.println("[WARNING] CORRUPTED RECIPE FOUND: " + line);
        }

    }

    // ================================================== DELETE DATA ==================================================

    public void deleteAllRecipes() throws IOException {
        // asks for confirmation
        // deletes all saved recipes

        if (recipeList.isEmpty()) {
            System.out.println("[ERROR] There are no saved recipes yet");
            return;
        }

        System.out.println("This action will delete " + Files.recipesFile.getName() + " and " + Files.ingredientsFile.getName());
        System.out.println("A backup file for both of the files will be made");
        System.out.println("Are you sure you want to delete all saved recipes? (Y/N)");
        if (recipeManager.getConfirmation()) {
            Files.createBackup("recipes");
            clearLists();
            Main.ingredientListClass.clearLists();
            Main.ingredientFileClass.updateFile();
            Main.recipeFileClass.reupdateFile();
            System.out.println("All the recipes have been successfully erased");
        }
    }

    public void deleteAllData() throws IOException {
        // asks for confirmation
        // deletes all saved data

        if (Main.savedIngredientsFileClass.getSavedIngredientsList().isEmpty()) {
            System.out.println("[ERROR] There is no saved data yet");
            return;
        }

        System.out.println("This action will delete " + Files.recipesFile.getName() + " , " + Files.ingredientsFile.getName() + " and " + Files.savedIngredientsFile.getName());
        System.out.println("A backup file for all of the files will be made");
        System.out.println("Are you sure you want to delete all saved data? (Y/N)");
        if (recipeManager.getConfirmation()) {
            Files.createBackup("data");
            clearLists();
            Main.ingredientListClass.clearLists();
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(Files.savedIngredientsFile));
            bWriter.close();

            Main.ingredientFileClass.updateFile();
            Main.recipeFileClass.reupdateFile();
            Main.savedIngredientsFileClass.updateFile();
            System.out.println("All saved data has been successfully erased");
        }
    }

    // ================================================== NEW RECIPE ==================================================

    public void addRecipe() throws IOException {
        // adds a new recipe into the list

        String recipeName = recipeManager.setRecipeName(-1);
        List<Ingredient> newRecipeIngredients = new ArrayList<>();
        setNewRecipeIngredients(recipeName, newRecipeIngredients);
        if (newRecipeIngredients.isEmpty()) {
            System.out.println("[ERROR] The recipe: " + recipeName + " has been deleted as it didn't contain any ingredients");
            return;
        }
        recipeList.add(new Recipe(recipeName, newRecipeIngredients));
        System.out.println(recipeName + " has been successfully added to the list of recipes");

        Files.reupdateFiles();
    }

    private void setNewRecipeIngredients(String recipeName, List<Ingredient> ingList) {
        // adds new/saved ingredients to ingList which will later be used to create a new Recipe

        label:
        while (true) {
            System.out.println("\nTYPE: ");
            System.out.println("1 - to view the current recipe");
            System.out.println("2 - to add a saved ingredient to the recipe");
            System.out.println("3 - to add a new ingredient to the recipe");
            System.out.println("4 - to remove an ingredient from the recipe");
            System.out.println("9 - to mix the recipe");
            String choice = Main.scanner.nextLine().trim();
            switch (choice) {
                case "1":                  // view the current recipe
                    recipeManager.printRecipeInfo(recipeName, ingList);
                    break;
                case "2":                  // add a saved ingredient to the recipe
                    ingredientManager.addSavedIngredient(true, ingList);
                    break;
                case "3":                  // add a new ingredient to the recipe
                    ingredientManager.addNewIngredient(ingList);
                    break;
                case "4":                  // remove an ingredient from the recipe
                    ingredientManager.removeIngredient(true, ingList);
                    break;
                case "9":                  // mix the recipe
                    break label;
                default:
                    System.out.println("[ERROR] WRONG INPUT");
                    break;
            }
        }
    }

    public int mixRecipe() throws IOException {
        // waits for user's input and repeats until the user has introduced a correct key

        int recipeIndex = recipeManager.chooseRecipe(true, recipeList);

        if (recipeIndex == -1) {
            // back to main menu
            return -1;
        }

        if (recipeList.get(recipeIndex).getName().trim().equals("Corrupted")) {
            renameCorruptedRecipe(recipeIndex);
            Main.recipeFileClass.reupdateFile();
        }

        recipeManager.printRecipeInfo(recipeList.get(recipeIndex).getName(), recipeList.get(recipeIndex).getIngredientList());
        if (!recipeList.get(recipeIndex).edit()) {
            // recipe has been deleted
            return -1;
        }

        if (recipeList.get(recipeIndex).getIngredientList().isEmpty()) {
            // recipe is empty
            removeEmptyRecipes(recipeIndex);
            return -1;
        }

        recipeManager.printRecipeInfo(recipeList.get(recipeIndex).getName(), recipeList.get(recipeIndex).getIngredientList());
        recipeList.get(recipeIndex).mixFood();
        recipeList.get(recipeIndex).takeAServing(recipeIndex);
        return recipeIndex;
    }

    private void renameCorruptedRecipe(int choiceInt) {
        // used to rename a recipe that has a corrupted name

        System.out.println("That recipe's name is corrupted");
        recipeList.get(choiceInt).setName(recipeManager.setRecipeName(-1));
    }

    public void removeEmptyRecipes() {
        // iterates through the list of recipes and removes any empty recipe

        for (int recipeIndex = 0; recipeIndex < recipeList.size(); recipeIndex++) {
            if (recipeList.get(recipeIndex).getIngredientList().isEmpty()) {
                recipeList.remove(recipeIndex);
                recipeIndex--;
            }
        }
    }

    private void removeEmptyRecipes(int choiceInt) throws IOException {
        // removes a single empty recipe based on the choiceInt variable used as index

        System.out.println("[WARNING] The recipe: " + recipeList.get(choiceInt).getName() + " has been deleted as it didn't contain any ingredients");
        recipeList.remove(choiceInt);
        System.gc();
        Main.ingredientFileClass.updateFile();
        // ^ this time the normal update. the same one that was used at launch
        // because it needs to deal with multiple separators as a recipe got removed

        Main.recipeFileClass.reupdateFile();
    }

}
