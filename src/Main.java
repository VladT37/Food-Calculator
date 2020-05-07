import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static boolean hasQuit = false;
    public static final Scanner scanner = new Scanner(System.in);
    public static RecipeListClass recipeListClass = new RecipeListClass();
    public static RecipeFileClass recipeFileClass = new RecipeFileClass();
    public static IngredientListClass ingredientListClass = new IngredientListClass();
    public static IngredientFileClass ingredientFileClass = new IngredientFileClass();
    public static SavedIngredientsFileClass savedIngredientsFileClass = new SavedIngredientsFileClass();

    private IngredientManager ingredientManager = new IngredientManager();
    private RecipeManager recipeManager = new RecipeManager();

    private void printMenu() {
        // the main menu of the application

        System.out.println("\n      MENU");
        System.out.println("\nTYPE:");
        System.out.println("1 - To view the recipes");
        System.out.println("2 - To add a new recipe");
        System.out.println("3 - To delete a recipe");
        System.out.println("4 - To delete a saved ingredient");
        System.out.println("5 - To delete ALL saved recipes");
        System.out.println("6 - To delete ALL saved DATA");
        System.out.println("9 - To exit the application\n");
    }
    private void chooseKey() throws IOException {
        // the main menu of the application

        Main main = new Main();
        main.printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":                  // view the recipes
                if (!recipeListClass.getRecipeList().isEmpty()) {
                    recipeListClass.viewRecipes();
                } else {
                    System.out.println("[ERROR] There are no recipes saved");
                    break;
                }

                int recipeIndex = recipeListClass.mixRecipe();
                if (recipeIndex == -1) {
                    break;
                }

                boolean anotherServing = recipeListClass.getRecipeList().get(recipeIndex).checkAnotherServing();
                while (anotherServing) {
                    anotherServing = recipeListClass.getRecipeList().get(recipeIndex).checkAnotherServing();
                }
                break;
            case "2":                 // add a new recipe
                recipeListClass.addRecipe();
                break;
            case "3":                 // deletes a certain recipe
                recipeManager.deleteRecipe(recipeListClass.getRecipeList());
                break;
            case "4":                 // deletes a certain ingredient from the saved Ingredients List and also from the recipes
                ingredientManager.deleteIngredient(savedIngredientsFileClass.getSavedIngredientsList());
                break;
            case "5":                 // deletes all the recipes, after saving a backup file for IngredientsFile.txt and RecipesFile.txt
                recipeListClass.deleteAllRecipes();
                break;
            case "6":                 // deletes all data, after saving a backup file for IngredientsFile.txt, RecipesFile.txt and SavedIngredientsFile.txt
                recipeListClass.deleteAllData();
                break;
            case "9":                 // quits the application
                hasQuit = true;
                System.out.println("APPLICATION IS CLOSING...");
                return;
            default:
                System.out.println("[ERROR] WRONG INPUT");
                break;
        }
    }


    public static void main(String[] args) throws IOException {
        Main main = new Main();
        Files.createFiles();

        // updates all the lists and files
        Main.ingredientListClass.clearLists();
        Main.ingredientListClass.updateList();
        Main.ingredientFileClass.updateFile();

        Main.recipeListClass.clearLists();
        Main.recipeFileClass.updateFile();
        Main.recipeListClass.updateList();

        Main.savedIngredientsFileClass.updateFile();

        System.out.println("\n\n            WELCOME TO FOOD CALCULATOR");
        System.out.println("USE THIS CALCULATOR TO TRACK ANY RECIPE'S AVERAGE MACRONUTRIENTS");
        System.out.println("TIP: You can also  the amount for a serving only\n");
        while(!hasQuit) {
            // the application will run until the user decides to quit
            // on quit, chooseKey will return, making the hasQuit variable true
            main.chooseKey();
        }


    }
}
