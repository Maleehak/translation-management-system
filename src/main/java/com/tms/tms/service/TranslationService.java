package com.tms.tms.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.tms.dto.TranslationDto;
import com.tms.tms.dto.TranslationExportDto;
import com.tms.tms.mapper.TranslationMapper;
import com.tms.tms.model.Language;
import com.tms.tms.model.Translation;
import com.tms.tms.repository.LanguageRepository;
import com.tms.tms.repository.TranslationRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final LanguageRepository languageRepository;
    private final TranslationMapper translationMapper;

    public TranslationService(TranslationRepository translationRepository, LanguageRepository languageRepository, TranslationMapper translationMapper) {
        this.translationRepository = translationRepository;
        this.languageRepository = languageRepository;
        this.translationMapper = translationMapper;
    }

    public TranslationDto createTranslation(TranslationDto dto) {
        Language lang = languageRepository.findById(dto.getLangCode())
                .orElseThrow(() -> new RuntimeException("Language not found"));

        Translation entity = translationMapper.toEntity(dto, lang);
        entity.setLanguage(lang);

        Translation saved = translationRepository.save(entity);
        return translationMapper.toDto(saved);
    }

    public TranslationDto updateTranslation(Long id, TranslationDto dto) {
        return translationRepository.findById(id).map(existing -> {
            existing.setText(dto.getText());
            existing.setTags(dto.getTags());
            Translation updated = translationRepository.save(existing);
            return translationMapper.toDto(updated);
        }).orElseThrow(() -> new RuntimeException("Translation not found"));
    }

    public void deleteTranslation(Long id) {
        translationRepository.deleteById(id);
    }

    public List<TranslationDto> getByLang(String langCode) {
        return translationRepository.findByLanguageCode(langCode).stream()
                .map(translationMapper::toDto)
                .toList();
    }

    public List<TranslationDto> getByKey(String key) {
        return translationRepository.findByTranslationKey(key).stream()
                .map(translationMapper::toDto)
                .toList();
    }

    public List<TranslationDto> searchByTag(List<String> tags) {
        return translationRepository.findByTags(tags).stream()
                .map(translationMapper::toDto)
                .toList();
    }

    public List<TranslationDto> searchByText(String query) {
        return translationRepository.searchByText(query).stream()
                .map(translationMapper::toDto)
                .toList();
    }

    public void exportTranslationsViaJsonFactory(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        List<TranslationExportDto> rows = translationRepository.exportTranslationsAsJson()
                .stream()
                .map(row -> new TranslationExportDto((String) row[0], row[1].toString()))
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(response.getWriter());

        generator.setCodec(mapper);
        generator.writeStartObject();

        for (TranslationExportDto row : rows) {
            JsonNode node = new ObjectMapper().readTree(row.getTranslationsJson());
            generator.writeFieldName(row.getLangCode());
            generator.writeTree(node);
        }

        generator.writeEndObject();
        generator.flush();
    }

}

