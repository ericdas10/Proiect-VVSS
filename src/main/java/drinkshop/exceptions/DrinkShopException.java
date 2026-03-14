package drinkshop.exceptions;

public class DrinkShopException extends RuntimeException {

    public DrinkShopException(String message) {
        super(message);
    }

    public DrinkShopException(String message, Throwable cause) {
        super(message, cause);
    }
}