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
public class StocServiceLevel1ValidatorIntTest {

    private Stoc stoc;
    private StocValidator stocValidator;
    private Repository<Integer, Stoc> stocRepo;
    private StocService stocService;

    @BeforeEach
    void setUp() {
        // Step 2 scenariul 2: integram V; E si R raman mock
        stoc = mock(Stoc.class);
        stocValidator = new StocValidator();
        stocRepo = mock(Repository.class);

        stocService = new StocService(stocRepo, stocValidator);
    }

    @Test
    @Order(1)
    void testAddValid_withRealValidator() {
        when(stoc.getId()).thenReturn(1);
        when(stoc.getIngredient()).thenReturn("Apa");
        when(stoc.getCantitate()).thenReturn(5.0);
        when(stoc.getStocMinim()).thenReturn(1.0);
        when(stocRepo.save(stoc)).thenReturn(stoc);

        try {
            stocService.add(stoc);
        } catch (Exception e) {
            fail("Invalid add operation " + e);
        }

        verify(stocRepo, times(1)).save(stoc);
        verify(stoc, times(1)).getId();
        verify(stoc, times(1)).getIngredient();
        verify(stoc, times(2)).getCantitate();
        verify(stoc, times(2)).getStocMinim();
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealValidator() {
        when(stoc.getId()).thenReturn(-1);
        when(stoc.getIngredient()).thenReturn("Apa");
        when(stoc.getCantitate()).thenReturn(5.0);
        when(stoc.getStocMinim()).thenReturn(10.0);

        Assertions.assertThrows(ValidationException.class, () -> stocService.add(stoc));

        verify(stocRepo, never()).save(any());
        verify(stoc, times(1)).getId();
        verify(stoc, times(2)).getCantitate();
        verify(stoc, times(2)).getStocMinim();
    }
}
