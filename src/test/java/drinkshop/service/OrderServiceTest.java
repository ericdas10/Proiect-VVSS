package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final double DELTA = 1e-9;

    @Mock
    private Repository<Integer, Order> orderRepo;

    @Mock
    private Repository<Integer, Product> productRepo;

    @InjectMocks
    private OrderService orderService;

    private Product product(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        return new Product(id, name, price, categorie, tip);
    }

    private Order orderWithItems(OrderItem... items) {
        Order order = new Order(1);
        for (OrderItem item : items) {
            order.getItems().add(item);
        }
        return order;
    }

    private void stubProduct(Product product) {
        when(productRepo.findOne(product.getId())).thenReturn(product);
    }

    @Test
    @DisplayName("Empty order returns 0 and does not query product repository")
    void computeTotal_emptyOrder_returnsZero() {
        Order order = new Order(1);

        double total = orderService.computeTotal(order);

        assertEquals(0.0, total, DELTA);
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("Single non-smoothie item below threshold has no discounts")
    void computeTotal_singleRegularItem_noDiscounts() {
        Product tea = product(1, "Tea", 8.0, CategorieBautura.TEA, TipBautura.PLANT_BASED);
        stubProduct(tea);
        Order order = orderWithItems(new OrderItem(tea, 5));

        double total = orderService.computeTotal(order);

        assertEquals(40.0, total, DELTA);
        verify(productRepo).findOne(1);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    @DisplayName("Single smoothie item applies smoothie fee and total discount over 50")
    void computeTotal_singleSmoothieItem_appliesSmoothieFeeAndMidDiscount() {
        Product smoothie = product(2, "Mango Smoothie", 12.0, CategorieBautura.SMOOTHIE, TipBautura.WATER_BASED);
        stubProduct(smoothie);
        Order order = orderWithItems(new OrderItem(smoothie, 5));

        double total = orderService.computeTotal(order);

        assertEquals(62.0, total, DELTA);
        verify(productRepo).findOne(2);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    @DisplayName("Single large item applies volume discount and total discount over 100")
    void computeTotal_singleLargeItem_appliesVolumeAndLargeDiscount() {
        Product juice = product(3, "Orange Juice", 10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        stubProduct(juice);
        Order order = orderWithItems(new OrderItem(juice, 12));

        double total = orderService.computeTotal(order);

        assertEquals(98.0, total, DELTA);
        verify(productRepo).findOne(3);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    @DisplayName("Multiple items exercise repeated loop iterations and mixed branches")
    void computeTotal_multipleItems_exercisesRepeatedLoopAndMixedBranches() {
        Product regular = product(4, "Iced Tea", 20.0, CategorieBautura.TEA, TipBautura.BASIC);
        Product smoothie = product(5, "Berry Smoothie", 8.0, CategorieBautura.SMOOTHIE, TipBautura.WATER_BASED);
        stubProduct(regular);
        stubProduct(smoothie);
        Order order = orderWithItems(
                new OrderItem(regular, 3),
                new OrderItem(smoothie, 11)
        );

        double total = orderService.computeTotal(order);

        assertEquals(131.2, total, DELTA);
        verify(productRepo).findOne(4);
        verify(productRepo).findOne(5);
        verifyNoMoreInteractions(productRepo);
    }

    @Test
    @DisplayName("Null order triggers NullPointerException")
    void computeTotal_nullOrder_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> orderService.computeTotal(null));
        verifyNoInteractions(productRepo);
    }

    @Test
    @DisplayName("Repository returning null product triggers NullPointerException")
    void computeTotal_missingProduct_throwsNullPointerException() {
        Product stubbedProduct = product(6, "Ghost Product", 9.0, CategorieBautura.JUICE, TipBautura.BASIC);
        when(productRepo.findOne(6)).thenReturn(null);
        Order order = orderWithItems(new OrderItem(stubbedProduct, 2));

        assertThrows(NullPointerException.class, () -> orderService.computeTotal(order));
        verify(productRepo).findOne(6);
        verifyNoMoreInteractions(productRepo);
    }
}
