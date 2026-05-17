package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceLevel2StocIntTest {

    private StocValidator stocValidator;
    private Repository<Integer, Stoc> stocRepo;
    private StocService stocService;

    @BeforeEach
    void setUp() {
        // Step 3 scenariul 2: integram E; R ramane mock
        stocValidator = new StocValidator();
        stocRepo = mock(Repository.class);
        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealStoc() {
        Stoc stoc = new Stoc(1, "Apa", 5.0, 1.0);
        when(stocRepo.save(stoc)).thenReturn(stoc);

        try {
            stocService.add(stoc);
        } catch (Exception e) {
            fail("Invalid add operation " + e);
        }

        verify(stocRepo, times(1)).save(stoc);
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealStoc() {
        Stoc stoc = new Stoc(-1, "Apa", 5.0, 10.0);
        when(stocRepo.save(stoc)).thenReturn(stoc); // nu se va ajunge la save

        Assertions.assertThrows(ValidationException.class, () -> stocService.add(stoc));

        verify(stocRepo, never()).save(any());
    }
}
