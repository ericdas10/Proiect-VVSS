package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;

import java.util.List;

public class OrderService {

    private final Repository<Integer, Order> orderRepo;
    private final Repository<Integer, Product> productRepo;

    public OrderService(Repository<Integer, Order> orderRepo, Repository<Integer, Product> productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;

    }

    public void addOrder(Order o) {
        orderRepo.save(o);
    }

    public void updateOrder(Order o) {
        orderRepo.update(o);
    }

    public void deleteOrder(int id) {
        orderRepo.delete(id);
    }

    public List<Order> getAllOrders() {
//        return StreamSupport.stream(orderRepo.findAll().spliterator(), false)
//                .collect(Collectors.toList());
        return orderRepo.findAll();
    }

    public Order findById(int id) {
        return orderRepo.findOne(id);
    }

    public double computeTotal(Order o) {
        double total = 0.0; // Node 1

        for (OrderItem item : o.getItems()) { // Node 2 (Loop)
            Product p = productRepo.findOne(item.getProduct().getId());
            double basePrice = p.getPret();
            double currentItemTotal = basePrice * item.getQuantity();

            // Ramificație 1: Discount de volum per produs
            if (item.getQuantity() > 10) { // Node 3
                currentItemTotal *= 0.9; // Node 4
            }

            // Ramificație 2: Taxă specială pentru anumite categorii
            if (p.getCategorie() == CategorieBautura.SMOOTHIE) { // Node 5
                currentItemTotal += 2.0; // Node 6 (Taxă preparare proaspătă)
            }

            total += currentItemTotal; // Node 7
        }

        // Ramificație 3: Praguri de discount pentru comanda totală
        if (total > 100.0) { // Node 8
            total -= 10.0; // Node 9
        } else if (total > 50.0) { // Node 10
            total -= 5.0; // Node 11
        }

        return total; // Node 12
    }

    public void addItem(Order o, OrderItem item) {
        o.getItems().add(item);
        orderRepo.update(o);
    }

    public void removeItem(Order o, OrderItem item) {
        o.getItems().remove(item);
        orderRepo.update(o);
    }
}