package drinkshop.domain;

public class Stoc {

    private int id;
    private String ingredient;
    private double cantitate;
    private double stocMinim;

    public Stoc(int id, String ingredient, int cantitate, int stocMinim) {
        this.id = id;
        this.ingredient = ingredient;
        this.cantitate = cantitate;
        this.stocMinim = stocMinim;
    }

    // --- getters ---
    public int getId() {
        return id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public double getCantitate() {
        return cantitate;
    }

    public double getStocMinim() {
        return stocMinim;
    }

    // --- setters ---

    public void setCantitate(double cantitate) {
        this.cantitate = cantitate;
    }



    // --- helper methods (safe to keep in entity) ---

    @Override
    public String toString() {
        return ingredient + " (" + cantitate + " / minim: " + stocMinim + ")";
    }
}