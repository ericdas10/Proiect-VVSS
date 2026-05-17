package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.util.ArrayList;
import java.util.List;

public class ReceiptGenerator {
    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            List<Product> list = new ArrayList<>();
            for (Product p1 : products) {
                if (i.getProduct().getId() == p1.getId()) {
                    list.add(p1);
                }
            }
            Product p = list.get(0);
            sb.append(p.getNume()+": ").append(p.getPret()).append(" x ").append(i.getQuantity()).append(" = ").append(i.getTotal()).append(" RON\n");
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }
}