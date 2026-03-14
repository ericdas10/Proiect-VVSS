package drinkshop.domain.entityValidator;

import drinkshop.domain.OrderItem;
import drinkshop.exceptions.ValidationException;
import drinkshop.service.validator.Validator;

public class OrderItemValidator implements Validator<OrderItem> {

    @Override
    public void validate(OrderItem item) {

        String errors = "";

        if (item.getProduct().getId() <= 0)
            errors += "Product ID invalid!\n";

        if (item.getQuantity() <= 0)
            errors += "Cantitate invalida!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
