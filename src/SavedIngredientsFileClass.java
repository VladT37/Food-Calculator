import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SavedIngredientsFileClass {

    private List<Ingredient> savedIngredientsList = new ArrayList<>();
    private List<String> uniqueSavedIngredientsList = new ArrayList<>();

    public List<String> getUniqueSavedIngredientsList() {
        return uniqueSavedIngredientsList;
    }

    public List<Ingredient> getSavedIngredientsList() {
        return savedIngredientsList;
    }

    private IngredientManager ingredientManager = new IngredientManager();

    public void clearLists() {
        // clears the list of saved ingredients and unique saved ingredients

        uniqueSavedIngredientsList.clear();
        savedIngredientsList.clear();
    }

    // ================================================= UPDATE FILE ===================================================

    public void updateFile() throws IOException {
        // updates SavedIngredientsFile.txt, reading from SavedIngredientsFile.txt and also from the list of unique ingredients
        // removes corrupted ingredients

        File tempIngredientsFile = new File(Files.filesFolder + "\\tempIngredientsFile.txt");
        BufferedReader savedIngredientsReader = new BufferedReader(new FileReader(Files.savedIngredientsFile));
        BufferedWriter tempIngredientsWriter = new BufferedWriter(new FileWriter(tempIngredientsFile));

        clearLists();

        String line;
        while ((line = savedIngredientsReader.readLine()) != null) {
            String[] words = line.trim().split("\\" + Files.MACRO_SEPARATOR); // 0 - name | 1 - P | 2 - C | 3 - F
            words[0] = words[0].trim();
            boolean isUnique = uniqueSavedIngredientsList.contains(words[0]);
            if (!isUnique) {
                boolean isCorrupt = checkIngredient(words, line);
                if (!isCorrupt) {
                    tempIngredientsWriter.write(line + "\n");
                    addIngredientIntoList(words);
                }

            }
        }

        for (Ingredient ing : Main.ingredientListClass.getUniqueIngredientsList()) {
            if (!uniqueSavedIngredientsList.contains(ing.getName())) {
                String[] words = new String[4];
                words[0] = ing.getName();
                words[1] = ing.getProteinPer100().toString();
                words[2] = ing.getCarbPer100().toString();
                words[3] = ing.getFatPer100().toString();

                ingredientManager.writeMacrosInFile(ing, tempIngredientsWriter);
                addIngredientIntoList(words);
            }
        }

        savedIngredientsReader.close();
        tempIngredientsWriter.flush();
        tempIngredientsWriter.close();

        System.gc();
        Files.deleteFile(Files.savedIngredientsFile);
        Files.renameFile(tempIngredientsFile, Files.savedIngredientsFile);
    }

    public void reupdateFiles() throws IOException {
        // reupdates the SavedIngredientFile.txt, reading from the list of saved ingredients

        BufferedWriter ingredientWriter = new BufferedWriter(new FileWriter(Files.savedIngredientsFile));

        for (Ingredient ing : savedIngredientsList) {
            ingredientManager.writeMacrosInFile(ing, ingredientWriter);
        }

        ingredientWriter.flush();
        ingredientWriter.close();

    }

    private void addIngredientIntoList(String[] words) {
        // tries to add a new ingredient into the saved ingredients list
        // throws an exception if it fails, and that ingredient is skipped

        DecimalFormat decimalFormat = new DecimalFormat();
        try {
            savedIngredientsList.add(new Ingredient(words[0], new BigDecimal(decimalFormat.format(Double.valueOf(words[1]))),
                    new BigDecimal(decimalFormat.format(Double.valueOf(words[2]))), new BigDecimal(decimalFormat.format(Double.valueOf(words[3])))));
            uniqueSavedIngredientsList.add(words[0]);
        } catch (Exception e) {
            System.out.println("[WARNING] CORRUPTED SAVED INGREDIENT FOUND: " + words[0]);
        }
    }

    private boolean checkIngredient(String[] words, String line) {
        // returns true if corrupt, false otherwise

        if (!words[0].matches(Files.onlyLettersPattern.toString())) {
            // checks if the name does NOT contain only a-z characters and whitespaces
            System.out.println("[WARNING] [NON a-z] CORRUPTED INGREDIENT FOUND: " + line);
            return true;
        }

        words[0] = NameEditor.replaceMultipleWhitespaces(words[0]);
        words[0] = NameEditor.capitalizeFirstLetter(words[0]);

        return ingredientManager.checkIngredientMacros(words, line);
    }

    public void editSavedIngredient(String ingNameBeforeEdit, Ingredient newIngredient) throws IOException {
        // updates the SavedIngredientFile.txt, reading from SavedIngredientsFile.txt
        // looks for the ingredient that has been edited before, and writes the edited version of it instead

        File tempIngredientsFile = new File(Files.filesFolder + "\\tempIngredientsFile.txt");
        BufferedWriter tempIngredientsWriter = new BufferedWriter(new FileWriter(tempIngredientsFile));
        BufferedReader savedIngredientsReader = new BufferedReader(new FileReader(Files.savedIngredientsFile));

        String line;
        while ((line = savedIngredientsReader.readLine()) != null) {
            String[] words = line.trim().split("\\" + Files.MACRO_SEPARATOR); // 0 - name | 1 - P | 2 - C | 3 - F
            words[0] = words[0].trim();
            if (words[0].equals(ingNameBeforeEdit)) {
                ingredientManager.writeMacrosInFile(newIngredient, tempIngredientsWriter);
            } else {
                tempIngredientsWriter.write(line + "\n");
            }
        }

        savedIngredientsReader.close();
        tempIngredientsWriter.flush();
        tempIngredientsWriter.close();

        System.gc();
        Files.deleteFile(Files.savedIngredientsFile);
        Files.renameFile(tempIngredientsFile, Files.savedIngredientsFile);
    }
}
