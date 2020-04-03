import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Ingredient {

    private String name;
    private BigDecimal proteinPer100;
    private BigDecimal carbPer100;
    private BigDecimal fatPer100;
    private BigDecimal caloriesPer100;

    private int cantity;
    private BigDecimal totalProtein;
    private BigDecimal totalCarb;
    private BigDecimal totalFat;
    private BigDecimal totalCalories;

    public BigDecimal getProteinPer100() {
        return proteinPer100;
    }

    public BigDecimal getCarbPer100() {
        return carbPer100;
    }

    public BigDecimal getFatPer100() {
        return fatPer100;
    }

    public BigDecimal getCaloriesPer100() {
        return caloriesPer100;
    }

    public BigDecimal getTotalProtein() {
        return totalProtein;
    }

    public BigDecimal getTotalCarb() {
        return totalCarb;
    }

    public BigDecimal getTotalFat() {
        return totalFat;
    }

    public BigDecimal getTotalCalories() {
        return totalCalories;
    }

    public String getName() {
        return name;
    }

    public int getCantity() {
        return cantity;
    }

    public void setCantity(int cantity) {
        this.cantity = cantity;
    }

    private IngredientManager ingredientManager = new IngredientManager();

    public Ingredient(String name, BigDecimal proteinPer100, BigDecimal carbPer100, BigDecimal fatPer100) {
        // the constructor gets called whenever a new ingredient is being added to the list of recipes
        // it requires a name and value for protein, carbohydrate and fat for 100g of ingredient

        this.name = name;
        this.proteinPer100 = proteinPer100;
        this.carbPer100 = carbPer100;
        this.fatPer100 = fatPer100;

        caloriesPer100 = (proteinPer100.add(carbPer100).multiply(new BigDecimal("4")).add(fatPer100.multiply(new BigDecimal("9"))).setScale(0, RoundingMode.HALF_EVEN));
    }

    public void setTotalMacros() {
        // macros per 100g multiplied by the cantity chosen

        totalProtein = proteinPer100.multiply(new BigDecimal(String.valueOf((double) cantity / 100)));
        totalCarb = carbPer100.multiply(new BigDecimal(String.valueOf((double) cantity / 100)));
        totalFat = fatPer100.multiply(new BigDecimal(String.valueOf((double) cantity / 100)));
        totalCalories = caloriesPer100.multiply(new BigDecimal(String.valueOf((double) cantity / 100)));

//        totalCalories = ((double)cantity / 100) * caloriesPer100;
//        totalProtein = ((double)cantity / 100) * proteinPer100;
//        totalCarb = ((double)cantity / 100) * carbPer100;
//        totalFat = ((double)cantity / 100) * fatPer100;
    }

    public void editIngredient(List<Ingredient> ingredientList) {
        // edits the name and its macros

        int ingredientIndex = getIngredientIndex(ingredientList);

        String ingredientName = ingredientManager.addIngredientName(ingredientIndex, ingredientList);
        BigDecimal[] macro = new BigDecimal[3];
        ingredientManager.addIngredientMacros(ingredientIndex, macro, ingredientList);

        name = ingredientName;
        proteinPer100 = macro[0];
        carbPer100 = macro[1];
        fatPer100 = macro[2];

        caloriesPer100 = (proteinPer100.add(carbPer100).multiply(new BigDecimal("4")).add(fatPer100.multiply(new BigDecimal("9"))).setScale(0, RoundingMode.HALF_EVEN));
    }

    private int getIngredientIndex(List<Ingredient> ingredientList) {
        // iterates through the ingredient list until the name of the ingredient matches the one from the ingredient list
        // that ingredient index gets returned

        for (int ingIndex = 0; ingIndex < ingredientList.size(); ingIndex++) {
            if (name.equals(ingredientList.get(ingIndex).getName())) {
                return ingIndex;
            }
        }
        return 0;
    }
}
