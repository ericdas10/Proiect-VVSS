package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    private static final double DELTA = 1e-9;

    @Mock
    private Repository<Integer, Product> productRepo;

    @Mock
    private Repository<Integer, Order> orderRepo;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Inițializare manuală pentru a evita problemele cu @InjectMocks pe Java 25
        orderService = new OrderService(orderRepo, productRepo);
    }

    @Test
    @DisplayName("TC01 - Comandă fără produse: Total zero")
    void computeTotal_EmptyOrder() {
        Order order = new Order(1);
        assertEquals(0.0, orderService.computeTotal(order), DELTA);
    }

    @Test
    @DisplayName("TC02 - No Discounts: Regular sub 50 lei")
    void computeTotal_Simple() {
        Product tea = new Product(101, "Tea", 10.0, CategorieBautura.TEA, TipBautura.BASIC);
        when(productRepo.findOne(anyInt())).thenReturn(tea);

        Order o = new Order(1);
        o.getItems().add(new OrderItem(tea, 2)); // 2 * 10 = 20.0

        assertEquals(20.0, orderService.computeTotal(o), DELTA);
    }

    @Test
    @DisplayName("TC03 - Smoothie & Mid Discount: Total 52 -> 47")
    void computeTotal_SmoothieMid() {
        Product smoothie = new Product(102, "Mango Smoothie", 50.0, CategorieBautura.SMOOTHIE, TipBautura.BASIC);
        when(productRepo.findOne(anyInt())).thenReturn(smoothie);

        Order o = new Order(1);
        o.getItems().add(new OrderItem(smoothie, 1));

        // Calcul: (50*1) + 2.0 taxă = 52.0. Peste 50 lei => 52 - 5 = 47.0
        assertEquals(47.0, orderService.computeTotal(o), DELTA);
    }

    @Test
    @DisplayName("TC04 - Volume & High Discount: Total 108 -> 98")
    void computeTotal_VolumeHigh() {
        Product juice = new Product(103, "Apple Juice", 10.0, CategorieBautura.JUICE, TipBautura.BASIC);
        when(productRepo.findOne(anyInt())).thenReturn(juice);

        Order o = new Order(1);
        o.getItems().add(new OrderItem(juice, 12));

        // Calcul: 12 * 10 = 120 -> Discount volum 10% = 108.0. Peste 100 lei => 108 - 10 = 98.0
        assertEquals(98.0, orderService.computeTotal(o), DELTA);
    }

    @Test
    @DisplayName("TC05 - Mixed Items: Iterații multiple")
    void computeTotal_MixedItems() {
        Product p1 = new Product(104, "Tea", 20.0, CategorieBautura.TEA, TipBautura.BASIC);
        Product p2 = new Product(105, "Smoothie", 30.0, CategorieBautura.SMOOTHIE, TipBautura.BASIC);

        // Mapăm ID-urile specifice pentru a testa acuratețea repo-ului
        when(productRepo.findOne(104)).thenReturn(p1);
        when(productRepo.findOne(105)).thenReturn(p2);

        Order o = new Order(1);
        o.getItems().add(new OrderItem(p1, 2)); // 40.0 lei
        o.getItems().add(new OrderItem(p2, 1)); // 30.0 + 2.0 taxă = 32.0 lei
        // Brut: 72.0. Peste 50 lei => 72 - 5 = 67.0

        assertEquals(67.0, orderService.computeTotal(o), DELTA);
    }

    @Test
    @DisplayName("TC06 - Exception Path: Produs inexistent în DB")
    void computeTotal_NullProduct_ThrowsException() {
        Product p = new Product(999, "Ghost", 10.0, CategorieBautura.TEA, TipBautura.BASIC);
        when(productRepo.findOne(anyInt())).thenReturn(null);

        Order o = new Order(1);
        o.getItems().add(new OrderItem(p, 1));

        // Verificăm dacă aruncă IllegalArgumentException conform noii logici din Service
        assertThrows(IllegalArgumentException.class, () -> orderService.computeTotal(o));
    }
}