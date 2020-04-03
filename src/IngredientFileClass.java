import java.io.*;

public class IngredientFileClass {
    private IngredientManager ingredientManager = new IngredientManager();

    // ================================================== UPDATE FILE ==================================================

    public void updateFile() throws IOException {
        // updates IngredientsFile.txt file, reading from the lists of lists of ingredients (list of recipes before being made recipes)
        // removes corrupted ingredients

        File ingredientTempFile = new File(Files.filesFolder + "\\ingredientTempFile.txt");
        BufferedWriter ingredientTempWriter = new BufferedWriter(new FileWriter(ingredientTempFile));

        for (int i = 0; i < Main.ingredientListClass.getIngredientList().size(); i++) {
            for (Ingredient ing : Main.ingredientListClass.getIngredientList().get(i)) {
                ingredientManager.writeMacrosInFile(ing, ingredientTempWriter);
            }
            if (i < Main.ingredientListClass.getIngredientList().size() - 1) {
                ingredientTempWriter.write(Files.RECIPE_SEPARATOR + "\n");
            }
        }
        ingredientTempWriter.flush();
        ingredientTempWriter.close();

        System.gc();
        Files.deleteFile(Files.ingredientsFile);
        Files.renameFile(removeCorruptedSeparators(ingredientTempFile), Files.ingredientsFile);
    }

    public void reupdateFile() throws IOException {
        // reupdates IngredientsFile.txt file, reading from the list of recipes

        BufferedWriter ingredientWriter = new BufferedWriter(new FileWriter(Files.ingredientsFile));

        for (int recipeIndex = 0; recipeIndex < Main.recipeListClass.getRecipeList().size(); recipeIndex++) {
            for (Ingredient ing : Main.recipeListClass.getRecipeList().get(recipeIndex).getIngredientList()) {
                ingredientManager.writeMacrosInFile(ing, ingredientWriter);
            }
            if (recipeIndex < Main.recipeListClass.getRecipeList().size() - 1) {
                ingredientWriter.write(Files.RECIPE_SEPARATOR + "\n");
            }
        }

        ingredientWriter.flush();
        ingredientWriter.close();
    }


    // =============================================== REMOVE CORRUPTED ================================================

    private File removeCorruptedSeparators(File ingredientTempFile) throws IOException {
        // removes corrupted recipe separators - consecutive and last separator

        File newIngredientTempFile = new File(Files.filesFolder + "\\newIngredientTempFile.txt");

        String lastLine = removeMultipleSeparators(ingredientTempFile, newIngredientTempFile);

        if (lastLine != null) {
            if (lastLine.equals(Files.RECIPE_SEPARATOR)) {
                // if last line is "=", is being removed
                removeLastSeparator(ingredientTempFile, newIngredientTempFile);
            }

        }

        System.gc();
        Files.deleteFile(ingredientTempFile);

        return newIngredientTempFile;
    }

    private String removeMultipleSeparators(File ingredientTempFile, File newIngredientTempFile) throws IOException {
        // removes consecutive separators and returns the last line written in the file

        BufferedReader ingredientTempReader = new BufferedReader(new FileReader(ingredientTempFile));
        BufferedWriter newIngredientTempWriter = new BufferedWriter(new FileWriter(newIngredientTempFile));

        String line = null;
        String lastLine = null;
        boolean firstRead = true;

        while ((line = ingredientTempReader.readLine()) != null) {
            if (firstRead) {
                if (!line.equals(Files.RECIPE_SEPARATOR)) {
                    //to avoid first "="
                    newIngredientTempWriter.write(line + "\n");
                    Main.recipeListClass.setTotalRecipes(Main.recipeListClass.getTotalRecipes()+1);
                }
            } else {
                if (line.equals(Files.RECIPE_SEPARATOR)) {
                    if (lastLine.equals(Files.RECIPE_SEPARATOR)) {
                        // to avoid multiple "="
                        continue;
                    } else {
                        Main.recipeListClass.setTotalRecipes(Main.recipeListClass.getTotalRecipes()+1);
                    }
                }
                newIngredientTempWriter.write(line + "\n");

            }
            firstRead = false;
            lastLine = line;
        }

        newIngredientTempWriter.flush();
        newIngredientTempWriter.close();
        ingredientTempReader.close();

        return lastLine;
    }

    private void removeLastSeparator(File ingredientTempFile, File newIngredientTempFile) throws IOException {
        // if last line from the file is a separator, the method removes it and renames the new file as the original file

        BufferedReader newIngredientTempReader = new BufferedReader(new FileReader(ingredientTempFile));
        File noLastSeparatorFile = new File(Files.filesFolder + "\\noLastSeparatorFile.txt");
        BufferedWriter noLastSeparatorWriter = new BufferedWriter(new FileWriter(noLastSeparatorFile));

        String currentLine = null;
        int sepOccurences = 0;

        while ((currentLine = newIngredientTempReader.readLine()) != null) {
            if (currentLine.equals(Files.RECIPE_SEPARATOR)) {
                if (++sepOccurences == Main.recipeListClass.getTotalRecipes()+1) {
                    // last = gets removed
                    break;
                }
            }
            noLastSeparatorWriter.write(currentLine + "\n");
        }

        noLastSeparatorWriter.flush();
        noLastSeparatorWriter.close();
        newIngredientTempReader.close();

        System.gc();
        Files.deleteFile(newIngredientTempFile);
        Files.renameFile(noLastSeparatorFile, newIngredientTempFile);
    }
}
