package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StocService {

    private final Repository<Integer, Stoc> stocRepo;

    private final Validator<Stoc> stocValidator;

    public StocService(Repository<Integer, Stoc> stocRepo, Validator<Stoc> stocValidator) {
        this.stocRepo = stocRepo;
        this.stocValidator = stocValidator;
    }

    public List<Stoc> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stoc s) {
        stocValidator.validate(s);
        stocRepo.save(s);
    }

    public void update(Stoc s) {
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Reteta reteta) {
        List<IngredientReteta> ingredienteNecesare = reteta.getIngrediente();

        for (IngredientReteta e : ingredienteNecesare) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            double disponibil = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .mapToDouble(Stoc::getCantitate)
                    .sum();

            if (disponibil < necesar) {
                return false;
            }
        }
        return true;
    }

    public void consuma(Reteta reteta) {
        if (!areSuficient(reteta)) {
            throw new IllegalStateException("Stoc insuficient pentru rețeta.");
        }

        for (IngredientReteta e : reteta.getIngrediente()) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            List<Stoc> ingredienteStoc = new ArrayList<>();
            for (Stoc stoc : stocRepo.findAll()) {
                if (stoc.getIngredient().equalsIgnoreCase(ingredient)) {
                    ingredienteStoc.add(stoc);
                }
            }

            double ramas = necesar;

            for (Stoc s : ingredienteStoc) {
                if (ramas <= 0) break;

                double deScazut = Math.min(s.getCantitate(), ramas);
                s.setCantitate((int)(s.getCantitate() - deScazut));
                ramas -= deScazut;

                stocRepo.update(s);
            }
        }
    }
}