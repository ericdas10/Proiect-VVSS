package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProductServiceTest {

    private FakeProductRepository productRepo;
    private ProductService productService;

    private Product product(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        return new Product(id, name, price, categorie, tip);
    }

    private static Stream<Product> validProducts() {
        return Stream.of(
                new Product(1, "Espresso", 8.5, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC),
                new Product(2, "Green Tea", 7.0, CategorieBautura.TEA, TipBautura.PLANT_BASED),
                new Product(3, "Mango Smoothie", 15.25, CategorieBautura.SMOOTHIE, TipBautura.WATER_BASED)
        );
    }

    private static Stream<Double> boundaryPrices() {
        return Stream.of(0.0, 0.01);
    }

    @Nested
    @DisplayName("ECP")
    class EcpTests {

        @Test
        void addProduct_validProduct_delegatesToRepository() {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            Product p = product(10, "Limonada", 10.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

            // Act
            productService.addProduct(p);

            // Assert
            assertSame(p, productRepo.savedProduct);
        }

        @Test
        void findById_existingId_returnsProduct() {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            Product expected = product(11, "Iced Latte", 14.5, CategorieBautura.ICED_COFFEE, TipBautura.DAIRY);
            productRepo.products.add(expected);

            // Act
            Product actual = productService.findById(11);

            // Assert
            assertSame(expected, actual);
        }

        @ParameterizedTest
        @MethodSource("drinkshop.service.ProductServiceTest#validProducts")
        void filterByCategorie_validCategory_returnsOnlyMatchingProducts(Product p) {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            CategorieBautura otherCategory = p.getCategorie() == CategorieBautura.TEA ? CategorieBautura.JUICE : CategorieBautura.TEA;
            Product other = product(99, "Other", 5.0, otherCategory, TipBautura.BASIC);
            productRepo.products.add(p);
            productRepo.products.add(other);

            // Act
            List<Product> filtered = productService.filterByCategorie(p.getCategorie());

            // Assert
            assertEquals(List.of(p), filtered);
        }

        @ParameterizedTest
        @EnumSource(value = TipBautura.class, names = {"BASIC", "DAIRY", "LACTOSE_FREE", "WATER_BASED", "PLANT_BASED", "POWDER"})
        void filterByTip_validTip_returnsOnlyMatchingProducts(TipBautura tip) {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            Product matching = product(20, "Match", 12.0, CategorieBautura.JUICE, tip);
            Product other = product(21, "Other", 13.0, CategorieBautura.SMOOTHIE, TipBautura.ALL);
            productRepo.products.add(matching);
            productRepo.products.add(other);

            // Act
            List<Product> filtered = productService.filterByTip(tip);

            // Assert
            assertEquals(List.of(matching), filtered);
        }
    }

    @Nested
    @DisplayName("BVA")
    class BvaTests {

        @ParameterizedTest
        @ValueSource(ints = {1, 1_000_000})
        void findById_boundaryIds_returnProduct(int id) {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            Product expected = product(id, "Boundary", 9.99, CategorieBautura.JUICE, TipBautura.BASIC);
            productRepo.products.add(expected);

            // Act
            Product actual = productService.findById(id);

            // Assert
            assertSame(expected, actual);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void deleteProduct_invalidBoundaryIds_delegateToRepository(int id) {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);

            // Act
            productService.deleteProduct(id);

            // Assert
            assertEquals(id, productRepo.lastDeleteId);
        }

        @ParameterizedTest
        @MethodSource("drinkshop.service.ProductServiceTest#boundaryPrices")
        void updateProduct_boundaryPrices_delegateToRepository(double price) {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);

            // Act
            productService.updateProduct(50, "BoundaryProduct", price, CategorieBautura.TEA, TipBautura.WATER_BASED);

            // Assert
            assertEquals(price, productRepo.lastUpdatedProduct.getPret());
        }

        @Test
        void filterByCategorie_all_returnsAllProducts() {
            // Arrange
            productRepo = new FakeProductRepository();
            productService = new ProductService(productRepo);
            List<Product> all = List.of(
                    product(1, "A", 4.0, CategorieBautura.JUICE, TipBautura.BASIC),
                    product(2, "B", 5.0, CategorieBautura.TEA, TipBautura.PLANT_BASED)
            );
            productRepo.products.addAll(all);

            // Act
            List<Product> filtered = productService.filterByCategorie(CategorieBautura.ALL);

            // Assert
            assertEquals(all, filtered);
        }
    }

    private static class FakeProductRepository implements Repository<Integer, Product> {
        private final List<Product> products = new ArrayList<>();
        private Product savedProduct;
        private Product lastUpdatedProduct;
        private Integer lastDeleteId;

        @Override
        public Product findOne(Integer id) {
            for (Product product : products) {
                if (product.getId() == id) {
                    return product;
                }
            }
            return null;
        }

        @Override
        public List<Product> findAll() {
            return new ArrayList<>(products);
        }

        @Override
        public Product save(Product entity) {
            savedProduct = entity;
            products.add(entity);
            return entity;
        }

        @Override
        public Product delete(Integer id) {
            lastDeleteId = id;
            Product removed = findOne(id);
            if (removed != null) {
                products.remove(removed);
            }
            return removed;
        }

        @Override
        public Product update(Product entity) {
            lastUpdatedProduct = entity;
            Product existing = findOne(entity.getId());
            if (existing != null) {
                products.remove(existing);
            }
            products.add(entity);
            return entity;
        }
    }
}
