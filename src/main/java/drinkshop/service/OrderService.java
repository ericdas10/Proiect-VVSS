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
        double total = 0.0;

        for (OrderItem item : o.getItems()) {
            Product p = productRepo.findOne(item.getProduct().getId());

            // PROTECȚIE: Dacă produsul nu există în repo, nu lăsăm programul să crape cu NPE
            if (p == null) {
                throw new IllegalArgumentException("Produsul cu ID-ul " +
                        item.getProduct().getId() + " nu a fost găsit!");
            }

            double basePrice = p.getPret();
            double currentItemTotal = basePrice * item.getQuantity();

            // Discount de volum > 10 bucăți (10%)
            if (item.getQuantity() > 10) {
                currentItemTotal *= 0.9;
            }

            // Taxă preparare Smoothie
            if (p.getCategorie() == CategorieBautura.SMOOTHIE) {
                currentItemTotal += 2.0;
            }

            total += currentItemTotal;
        }

        // Praguri de discount total
        if (total > 100.0) {
            total -= 10.0;
        } else if (total > 50.0) {
            total -= 5.0;
        }

        return total;
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