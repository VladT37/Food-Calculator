import java.io.*;
import java.nio.file.FileSystemException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Files {
    public static File backupFolder = new File ("Backups");
    public static File filesFolder = new File ("Files");
    public static File recipesFile = new File(filesFolder + "\\RecipesFile.txt");
    public static File ingredientsFile = new File(filesFolder + "\\IngredientsFile.txt");
    public static File savedIngredientsFile = new File(filesFolder + "\\SavedIngredientsFile.txt");

    public static final String RECIPE_SEPARATOR = "=";
    public static final String MACRO_SEPARATOR = "|";

    public static final Pattern onlyLettersPattern = Pattern.compile("[a-zA-Z ]+");
    public static final Pattern onlyLettersAndNumPattern = Pattern.compile("[a-zA-Z0-9 ]+");


    // ================================================== CREATE FILES =================================================

    public static void createFiles() throws IOException {
        // creates Files folder which will include all the text files
        // creates RecipesFile.txt, IngredientsFile.txt and SavedIngredientsFile.txt if they don't exist already

        if (filesFolder.mkdir()) {
            System.out.println(filesFolder.getName() + " directory has been created");
        }

        if (recipesFile.createNewFile()) {
            System.out.println(recipesFile.getName() + " has been created");
        }

        if (ingredientsFile.createNewFile()) {
            System.out.println(ingredientsFile.getName() + " has been created");
        }

        if (savedIngredientsFile.createNewFile()) {
            System.out.println(savedIngredientsFile.getName() + " has been created");
        }

    }

    public static void createBackup(String backup) throws IOException {
        // creates the Backups folder if not created already
        // creates Backup_3-APR-2020 (as example; the date passed as name will be the date from user's pc) if not created already
        // saves the files as x_backup.txt before erasing any data from the original files

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();

        if (backupFolder.mkdir()) {
            System.out.println(backupFolder.getName() + " directory has been created");
        }

        File currentDateBackup = new File (backupFolder + "\\Backup_" + dateFormat.format(date));

        if (currentDateBackup.mkdir()) {
            System.out.println(currentDateBackup.getName() + " directory has been created");
        }

    // ================================================ DELETE RECIPES =================================================

        if (backup.equals("recipes")) {
            if (!Main.recipeListClass.getRecipeList().isEmpty()) {
                // if the list of recipes is not empty, it will create a backup file for RecipesFile.txt

                File recipesFile_Backup = new File(currentDateBackup + "\\RecipesFile_Backup.txt");
                if (recipesFile_Backup.createNewFile()) {
                    System.out.println(recipesFile_Backup.getName() + " has been created");
                } else {
                    deleteFile(recipesFile_Backup);
                    if (recipesFile_Backup.createNewFile()) {
                        System.out.println(recipesFile_Backup.getName() + " has been created");
                    }
                }

                copyFile(recipesFile, recipesFile_Backup);
            } else {
                System.out.println("[WARNING] The Recipe Backup File was not created as the list of recipes was empty");
            }

            if (!Main.recipeListClass.getRecipeList().isEmpty()) {
                // if the list of recipes is not empty, it will create a backup file for IngredientsFile.txt

                File ingredientsFile_Backup = new File(currentDateBackup + "\\IngredientsFile_Backup.txt");
                if (ingredientsFile_Backup.createNewFile()) {
                    System.out.println(ingredientsFile_Backup.getName() + " has been created");
                } else {
                    deleteFile(ingredientsFile_Backup);
                    if (ingredientsFile_Backup.createNewFile()) {
                        System.out.println(ingredientsFile_Backup.getName() + " has been created");
                    }
                }

                copyFile(ingredientsFile, ingredientsFile_Backup);
            } else {
                System.out.println("[WARNING] The Ingredient Backup File was not created as the list of recipes was empty");
            }


    // ================================================== DELETE DATA ==================================================

        } else if (backup.equals("data")) {
            if (!Main.recipeListClass.getRecipeList().isEmpty()) {
                // if the list of recipes is not empty, it will create a backup file for RecipesFile.txt

                File recipesFile_Backup = new File(currentDateBackup + "\\RecipesFile_Backup.txt");
                if (recipesFile_Backup.createNewFile()) {
                    System.out.println(recipesFile_Backup.getName() + " has been created");
                } else {
                    deleteFile(recipesFile_Backup);
                    if (recipesFile_Backup.createNewFile()) {
                        System.out.println(recipesFile_Backup.getName() + " has been created");
                    }
                }

                copyFile(recipesFile, recipesFile_Backup);
            } else {
                System.out.println("[WARNING] The Recipe Backup File was not created as the list of recipes was empty");
            }

            if (!Main.recipeListClass.getRecipeList().isEmpty()) {
                // if the list of recipes is not empty, it will create a backup file for IngredientsFile.txt

                File ingredientsFile_Backup = new File(currentDateBackup + "\\IngredientsFile_Backup.txt");
                if (ingredientsFile_Backup.createNewFile()) {
                    System.out.println(ingredientsFile_Backup.getName() + " has been created");
                } else {
                    deleteFile(ingredientsFile_Backup);
                    if (ingredientsFile_Backup.createNewFile()) {
                        System.out.println(ingredientsFile_Backup.getName() + " has been created");
                    }
                }
                copyFile(ingredientsFile, ingredientsFile_Backup);
            } else {
                System.out.println("[WARNING] The Ingredient Backup File was not created as the list of recipes was empty");
            }

            if (!Main.savedIngredientsFileClass.getSavedIngredientsList().isEmpty()) {
                // if the list of saved ingredients is not empty, it will create a backup file for SavedIngredientsFile.txt

                File savedIngredientsFile_Backup = new File(currentDateBackup + "\\SavedIngredientsFile_Backup.txt");
                if (savedIngredientsFile_Backup.createNewFile()) {
                    System.out.println(savedIngredientsFile_Backup.getName() + " has been created");
                } else {
                    deleteFile(savedIngredientsFile_Backup);
                    if (savedIngredientsFile_Backup.createNewFile()) {
                        System.out.println(savedIngredientsFile_Backup.getName() + " has been created");
                    }
                }

                copyFile(savedIngredientsFile, savedIngredientsFile_Backup);
            } else {
                System.out.println("[WARNING] The Saved Ingredient Backup File was not created as the list of saved ingredients was empty");
            }

        }
    }

    // =================================================== EDIT FILES ==================================================

    public static void copyFile(File copyFile, File pasteFile) throws IOException {
        // copies line-by-line from copyFile to pasteFile

        BufferedReader copyReader = new BufferedReader(new FileReader(copyFile));
        BufferedWriter pasteWriter = new BufferedWriter(new FileWriter(pasteFile));

        String line;
        while ((line = copyReader.readLine()) != null) {
            pasteWriter.write(line + "\n");
        }

        pasteWriter.flush();
        pasteWriter.close();
        copyReader.close();
    }

    public static void deleteFile(File fileDeleted) throws IOException {
        // Deletes the original file

        try {
            System.out.print("[LOG] Original file: " + fileDeleted.getName() + " ");
            java.nio.file.Files.delete(fileDeleted.toPath());
            System.out.println("successfully deleted");
        } catch (FileSystemException e) {
            System.out.println("could not be deleted");
        }
    }

    public static void renameFile(File fileRenamed, File nameOfFile) {
        // Renames the new file to the filename the original file had.

        System.out.print("[LOG] Original file: " + fileRenamed.getName() + " ");
        if (fileRenamed.renameTo(nameOfFile)) {
            System.out.println("successfully renamed to: " + nameOfFile.getName());
        } else {
            System.out.println("could not be renamed to: " + nameOfFile.getName());
        }
    }

    // ================================================= UPDATE FILES ==================================================

    public static void reupdateFiles() throws IOException {
        // used to update RecipeFile, IngredientsFile and SavedIngredientsFile after making a change to either an ingredient or recipe

        System.gc();
        Main.ingredientFileClass.reupdateFile();
        Main.recipeFileClass.reupdateFile();
        Main.savedIngredientsFileClass.updateFile();
    }

}
