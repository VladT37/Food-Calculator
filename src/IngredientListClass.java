import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IngredientListClass{

    private List<List<Ingredient>> ingredientList = new ArrayList<>(); // only used for the first update of the list and file
                                                                       // use recipeList from RecipeListClass afterwards
    private List<Ingredient> uniqueIngredientsList = new ArrayList<>();
    private List<String> uniqueIngredientsNameList = new ArrayList<>();
    private IngredientManager ingredientManager = new IngredientManager();

    public List<Ingredient> getUniqueIngredientsList() {
        return uniqueIngredientsList;
    }
    public List<String> getUniqueIngredientsNameList() {
        return uniqueIngredientsNameList;
    }

    public List<List<Ingredient>> getIngredientList() {
        return ingredientList;
    }


    // ================================================= UPDATE LIST ===================================================

    public void clearLists() {
        // clears all the lists that contain ingredients
        // used when the user erases the recipes / all data

        ingredientList.clear();
        uniqueIngredientsList.clear();
        uniqueIngredientsNameList.clear();
    }

    public void updateList() throws IOException {
        // iterates through a txt file and updates the list that contain the ingredients

        ingredientList.clear();
        ingredientList.add(new ArrayList<>());
        addIngredientsToUpdatedList();
    }

    private void addIngredientsToUpdatedList() throws IOException {
        // reads every line from txt file, and if the line is not corrupt, the ingredient gets added into the list

        BufferedReader ingredientReader = new BufferedReader(new FileReader(Files.ingredientsFile));
        int recipeIndex = 0;
        String line;
        String lastLine = null;
        boolean firstRead = true;
        boolean newListToAdd = false;

        uniqueIngredientsNameList.clear();
        uniqueIngredientsList.clear();
        Main.recipeListClass.getEmptyRecipesIndexList().clear();

        while ((line = ingredientReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                if (newListToAdd) {
                    // if newListToAdd trigger is true, a new list of ingredient is added to the lists that contains all those lists
                    ingredientList.add(new ArrayList<>());
                    newListToAdd = false;
                }
            }

            if (!firstRead) {
                // if it's not the first line from the file
                if (line.equals(Files.RECIPE_SEPARATOR)) {
                    // if line is "="
                    if (!(lastLine.equals(Files.RECIPE_SEPARATOR))) {
                        // if last line is NOT "="
                        if (ingredientList.get(recipeIndex).isEmpty()) {
                            // if the recipe does not contain any good ingredients
                            // a new list is gonna be added, after the current one gets removed
                            newListToAdd = true;
                            Main.recipeListClass.getEmptyRecipesIndexList().add(recipeIndex);
                            recipeIndex++;
                            continue;
                        } else {
                            lastLine = line;
                            //emptyRecipe = true;
                        }

                        recipeIndex++;
                        newListToAdd = true;

                        continue;
                    } else {
                        continue;
                    }
                }
            }

            firstRead = false;
            lastLine = line;

            String[] words = line.toLowerCase().split("\\" + Files.MACRO_SEPARATOR); // 0 - name | 1 - P | 2 - C | 3 - F
            words[0] = words[0].trim();
            boolean isCorrupt = checkIngredient(words, line, recipeIndex);

            if (!isCorrupt) {
                // checks if the word is NOT corrupt
                addIngredientIntoList(line, recipeIndex, words);
                //emptyRecipe = addIngredientIntoList(line, recipeIndex, words);
            }

        }
        ingredientReader.close();

        for (int index = 0; index < ingredientList.size(); index++) {
            if (ingredientList.get(index).isEmpty()) {
                ingredientList.remove(index);
                index--;
            }
        }
    }

    private void addIngredientIntoList(String line, int recipeIndex, String[] words) {
        // tries to add a new ingredient into the list
        // throws an exception if it fails, and that ingredient is skipped

        DecimalFormat decimalFormat = new DecimalFormat();
        boolean isUnique = !uniqueIngredientsNameList.contains(words[0]);
        try {
            ingredientList.get(recipeIndex).add(new Ingredient(words[0], new BigDecimal(decimalFormat.format(Double.valueOf(words[1]))),
                    new BigDecimal(decimalFormat.format(Double.valueOf(words[2]))), new BigDecimal(decimalFormat.format(Double.valueOf(words[3]))) ));
            if (isUnique) {
                uniqueIngredientsList.add(new Ingredient(words[0], new BigDecimal(decimalFormat.format(Double.valueOf(words[1]))),
                        new BigDecimal(decimalFormat.format(Double.valueOf(words[2]))), new BigDecimal(decimalFormat.format(Double.valueOf(words[3])))));
                uniqueIngredientsNameList.add(words[0]);
            }
        } catch (Exception e) {
            System.out.println("[WARNING] CORRUPTED INGREDIENT FOUND: " + line);
        }
    }

    private boolean checkIngredient(String[] words, String line, int recipeIndex) {
        // returns true if corrupt, false otherwise

        if (!words[0].matches(Files.onlyLettersPattern.toString())) {
            // checks if the name does NOT contain only a-z characters and whitespaces
            System.out.println("[WARNING] [NON a-z] CORRUPTED INGREDIENT FOUND: " + line);
            return true;
        }

        words[0] = NameEditor.replaceMultipleWhitespaces(words[0]);
        words[0] = NameEditor.capitalizeFirstLetter(words[0]);

        if (ingredientManager.checkIngredientDuplicate(words[0], ingredientList.get(recipeIndex))) {
            // checks if it's a duplicate
            System.out.println("[WARNING] DUPLICATED INGREDIENT FOUND: " + line);
            return true;
        }

        return ingredientManager.checkIngredientMacros(words, line);
    }

}
