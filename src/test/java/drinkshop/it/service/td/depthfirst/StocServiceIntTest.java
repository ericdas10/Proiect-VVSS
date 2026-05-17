package drinkshop.it.service.td.depthfirst;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileStocRepository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StocServiceIntTest {

    private StocValidator stocValidator;
    private Repository<Integer, Stoc> stocRepo;
    private StocService stocService;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Step 4 optional scenariul 2: integram R; S + V + E + R reale
        tempFile = Files.createTempFile("stocuri-test", ".txt");
        Files.write(tempFile, java.util.Arrays.asList(
                "1;Apa;5.0;1.0",
                "2;Cafea;10.0;2.0"
        ));

        stocValidator = new StocValidator();
        stocRepo = new FileStocRepository(tempFile.toString());
        stocService = new StocService(stocRepo, stocValidator);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @Order(1)
    void testAddValid_withRealRepo() {
        Stoc stoc = new Stoc(3, "Lapte", 7.0, 1.0);

        try {
            stocService.add(stoc);
        } catch (Exception e) {
            fail("Invalid add operation " + e);
        }

        assert 3 == stocRepo.findAll().size();
        assert 3 == stocService.getAll().size();
    }

    @Test
    @Order(2)
    void testAddInvalid_withRealRepo() {
        Stoc stoc = new Stoc(-1, "Zahar", 5.0, 10.0);

        Assertions.assertThrows(ValidationException.class, () -> stocService.add(stoc));

        assert 2 == stocRepo.findAll().size();
        assert 2 == stocService.getAll().size();
    }
}
