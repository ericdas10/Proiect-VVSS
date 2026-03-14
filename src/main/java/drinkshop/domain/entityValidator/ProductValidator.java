package drinkshop.domain.entityValidator;

import drinkshop.domain.Product;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {

        String errors = "";

        if (product.getId() <= 0)
            errors += "ID invalid!\n";

        if (product.getNume() == null || product.getNume().isBlank())
            errors += "Numele nu poate fi gol!\n";

        if (product.getPret() <= 0)
            errors += "Pret invalid!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
