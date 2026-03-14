package drinkshop.domain.entityValidator;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;

public class OrderValidator implements Validator<Order> {

    private final OrderItemValidator itemValidator = new OrderItemValidator();

    @Override
    public void validate(Order order) {

        String errors = "";

        if (order.getId() <= 0)
            errors += "ID comanda invalid!\n";

        if (order.getItems() == null || order.getItems().isEmpty())
            errors += "Comanda fara produse!\n";

        for (OrderItem item : order.getItems()) {
            try {
                itemValidator.validate(item);
            } catch (ValidationException e) {
                errors += e.getMessage();
            }
        }

        if (order.getTotalPrice() < 0)
            errors += "Total invalid!\n";

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
