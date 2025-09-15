package com.tms.tms.loader;

import com.tms.tms.model.Language;
import com.tms.tms.model.Translation;
import com.tms.tms.repository.LanguageRepository;
import com.tms.tms.repository.TranslationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TranslationDataLoader implements CommandLineRunner {

    private final TranslationRepository translationRepository;
    private final LanguageRepository languageRepository;

    public TranslationDataLoader(TranslationRepository translationRepository,
                                 LanguageRepository languageRepository) {
        this.translationRepository = translationRepository;
        this.languageRepository = languageRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (languageRepository.count() == 0) {
            List<Language> langs = List.of(
                    new Language("en", "English"),
                    new Language("fr", "French"),
                    new Language("es", "Spanish")
            );
            languageRepository.saveAll(langs);
            System.out.println("Languages table populated");
        }


        // Check if DB is already populated
        if (translationRepository.count() > 0) {
            System.out.println("Database already populated. Skipping...");
            return;
        }

        List<Translation> batch = new ArrayList<>();
        Random random = new Random();
        List<Language> languages = languageRepository.findAll();

        if (languages.isEmpty()) {
            System.out.println("No languages found! Add languages first.");
            return;
        }

        int totalRecords = 100_000;
        int batchSize = 1000;

        for (int i = 1; i <= totalRecords; i++) {
            Translation t = new Translation();
            t.setTranslationKey("key_" + i);
            t.setText("Sample translation text " + i);
            t.setLanguage(languages.get(random.nextInt(languages.size())));
            t.setTags(List.of("web", "mobile", "desktop").subList(0, random.nextInt(3) + 1));

            batch.add(t);

            if (i % batchSize == 0) {
                translationRepository.saveAll(batch);
                batch.clear();
                System.out.println(i + " records inserted...");
            }
        }

        if (!batch.isEmpty()) {
            translationRepository.saveAll(batch);
        }

        System.out.println("Finished inserting 100k+ translation records!");
    }
}

