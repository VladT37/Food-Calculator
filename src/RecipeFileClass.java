import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeFileClass {
    private List<String> uniqueRecipeList = new ArrayList<>();

    public List<String> getUniqueRecipeList() {
        return uniqueRecipeList;
    }


    // ================================================= UPDATE FILE ===================================================

    public void updateFile() throws IOException {
        // updates the txt file, reading from RecipesFile.txt, removing corrupted recipes

        File newRecipeTempFile = new File(Files.filesFolder + "\\newRecipeTempFile.txt");
        BufferedWriter recipeTempWriter = new BufferedWriter(new FileWriter(newRecipeTempFile));
        BufferedReader recipeReader = new BufferedReader(new FileReader(Files.recipesFile));

        uniqueRecipeList.clear();
        String line;
        int recipeCount = 0;
        while ((line = recipeReader.readLine()) != null) {
            if (Main.recipeListClass.getEmptyRecipesIndexList().contains(recipeCount)) {
                // skips the recipes that are contained in the list that contains the indexes of the empty recipes
                recipeCount++;
                continue;
            }
            String recipeName = getEditedRecipeName(line.toLowerCase());
            boolean isCorrupt = checkRecipe(recipeName, line);

            if (isCorrupt) {
                recipeTempWriter.write("Corrupted\n");
            } else {
                recipeTempWriter.write(recipeName + "\n");
                uniqueRecipeList.add(recipeName);
            }

            recipeCount++;
        }

        while (recipeCount++ < Main.ingredientListClass.getIngredientList().size()) {
            // if the RecipesFile.txt contains less recipes than it should, more lines with << Corrupted >> will be printed
            recipeTempWriter.write("Corrupted\n" );
        }

        recipeTempWriter.flush();
        recipeTempWriter.close();
        recipeReader.close();

        System.gc();
        Files.deleteFile(Files.recipesFile);
        Files.renameFile(newRecipeTempFile, Files.recipesFile);

    }

    public void reupdateFile() throws IOException {
        // updates the txt file, reading from the list of recipes

        BufferedWriter recipeWriter = new BufferedWriter(new FileWriter(Files.recipesFile));

        for (Recipe recipe : Main.recipeListClass.getRecipeList()) {
            recipeWriter.write(recipe.getName() + "\n");
        }

        recipeWriter.flush();
        recipeWriter.close();

    }

    private String getEditedRecipeName(String name) {
        // returns the name of the recipe, after trimming and formatting it

        name = NameEditor.replaceMultipleWhitespaces(name);
        name = NameEditor.capitalizeWords(name);

        return name;
    }


    private boolean checkRecipe(String recipeName, String line) {
        // returns true if corrupt, false otherwise

        if (!recipeName.matches(Files.onlyLettersAndNumPattern.toString())) {
            // checks if the name does NOT contain only a-z characters, numbers and whitespaces
            System.out.println("[WARNING] [NON a-z or 0-9] CORRUPTED RECIPE FOUND: " + line);
            return true;
        }

        if (uniqueRecipeList.contains(recipeName)) {
            // checks if it's a duplicate
            System.out.println("[WARNING] DUPLICATED RECIPE FOUND: " + line);
            return true;
        }

        return false;
    }
}
