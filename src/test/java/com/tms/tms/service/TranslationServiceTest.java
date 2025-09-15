package com.tms.tms.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.tms.dto.TranslationDto;
import com.tms.tms.mapper.TranslationMapper;
import com.tms.tms.model.Language;
import com.tms.tms.model.Translation;
import com.tms.tms.repository.LanguageRepository;
import com.tms.tms.repository.TranslationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepo;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private TranslationMapper translationMapper;

    @InjectMocks
    private TranslationService translationService;

    private final ObjectMapper mapper = new ObjectMapper();

    private Language en;
    private Translation t1;
    private TranslationDto dto1;

    public TranslationServiceTest() {
        MockitoAnnotations.openMocks(this);

        en = new Language();
        en.setCode("en");
        en.setName("English");

        t1 = new Translation();
        t1.setId(1L);
        t1.setTranslationKey("welcome");
        t1.setText("Welcome");
        t1.setLanguage(en);
        t1.setTags(List.of("web"));

        dto1 = new TranslationDto();
        dto1.setTranslationKey("welcome");
        dto1.setText("Welcome");
        dto1.setLangCode("en");
        dto1.setTags(List.of("web"));
    }

    @Test
    void exportTranslationsViaJsonFactory_shouldReturnValidJson() throws IOException {
        // Arrange: mock repository result
        List<Object[]> rows = List.of(
                new Object[]{"en", "{\"hello\":\"Hello\"}"},
                new Object[]{"fr", "{\"hello\":\"Bonjour\"}"}
        );
        when(translationRepo.exportTranslationsAsJson()).thenReturn(rows);

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        translationService.exportTranslationsViaJsonFactory(response);

        // Assert
        String jsonOutput = response.getContentAsString();
        assertThat(jsonOutput).isNotBlank();

        // Parse back into JSON for correctness
        var jsonNode = mapper.readTree(jsonOutput);

        assertThat(jsonNode.has("en")).isTrue();
        assertThat(jsonNode.has("fr")).isTrue();
        assertThat(jsonNode.get("en").get("hello").asText()).isEqualTo("Hello");
        assertThat(jsonNode.get("fr").get("hello").asText()).isEqualTo("Bonjour");
    }

    @Test
    void testCreateTranslation_success() {
        TranslationDto dto = new TranslationDto();
        dto.setTranslationKey("welcome");
        dto.setLangCode("en");
        dto.setText("Welcome");

        Translation entity = new Translation();
        entity.setTranslationKey("welcome");
        entity.setText("Welcome");

        Translation saved = new Translation();
        saved.setId(1L);
        saved.setTranslationKey("welcome");
        saved.setText("Welcome");

        en = new Language();
        en.setCode("en");
        en.setName("English");

        when(languageRepository.findById("en")).thenReturn(Optional.of(en));
        when(translationMapper.toEntity(dto, en )).thenReturn(entity);
        when(translationRepo.save(entity)).thenReturn(saved);
        when(translationMapper.toDto(saved)).thenReturn(dto);

        TranslationDto result = translationService.createTranslation(dto);

        assertNotNull(result);
        assertEquals("welcome", result.getTranslationKey());
        verify(translationRepo, times(1)).save(entity);
    }

    @Test
    void testCreateTranslation_languageNotFound() {
        TranslationDto dto = new TranslationDto();
        dto.setLangCode("fr");

        when(languageRepository.findById("fr")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            translationService.createTranslation(dto);
        });

        assertEquals("Language not found", exception.getMessage());
    }

    @Test
    void testUpdateTranslation_success() {
        Translation existing = new Translation();
        existing.setId(1L);
        existing.setText("Hello");
        existing.setTags(List.of("web"));

        TranslationDto dto = new TranslationDto();
        dto.setText("Bonjour");
        dto.setTags(List.of("mobile"));

        Translation updatedEntity = new Translation();
        updatedEntity.setId(1L);
        updatedEntity.setText("Bonjour");
        updatedEntity.setTags(List.of("mobile"));

        when(translationRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(translationRepo.save(existing)).thenReturn(updatedEntity);
        when(translationMapper.toDto(updatedEntity)).thenReturn(dto);

        TranslationDto result = translationService.updateTranslation(1L, dto);

        assertNotNull(result);
        assertEquals("Bonjour", result.getText());
        assertEquals(List.of("mobile"), result.getTags());
        verify(translationRepo, times(1)).save(existing);
    }

    @Test
    void testUpdateTranslation_notFound() {
        TranslationDto dto = new TranslationDto();
        dto.setText("Bonjour");

        when(translationRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            translationService.updateTranslation(1L, dto);
        });

        assertEquals("Translation not found", exception.getMessage());
    }

    @Test
    void testDeleteTranslation() {
        doNothing().when(translationRepo).deleteById(1L);
        translationService.deleteTranslation(1L);
        verify(translationRepo, times(1)).deleteById(1L);
    }

    @Test
    void testGetByLang() {
        when(translationRepo.findByLanguageCode("en")).thenReturn(List.of(t1));
        when(translationMapper.toDto(t1)).thenReturn(dto1);

        List<TranslationDto> result = translationService.getByLang("en");

        assertEquals(1, result.size());
        assertEquals("welcome", result.get(0).getTranslationKey());
    }

    @Test
    void testGetByKey() {
        when(translationRepo.findByTranslationKey("welcome")).thenReturn(List.of(t1));
        when(translationMapper.toDto(t1)).thenReturn(dto1);

        List<TranslationDto> result = translationService.getByKey("welcome");

        assertEquals(1, result.size());
        assertEquals("welcome", result.get(0).getTranslationKey());
    }

    @Test
    void testSearchByTag() {
        List<String> tags = List.of("web");
        when(translationRepo.findByTags(tags)).thenReturn(List.of(t1));
        when(translationMapper.toDto(t1)).thenReturn(dto1);

        List<TranslationDto> result = translationService.searchByTag(tags);

        assertEquals(1, result.size());
        assertEquals("web", result.get(0).getTags().get(0));
    }

    @Test
    void testSearchByText() {
        String query = "Welcome";
        when(translationRepo.searchByText(query)).thenReturn(List.of(t1));
        when(translationMapper.toDto(t1)).thenReturn(dto1);

        List<TranslationDto> result = translationService.searchByText(query);

        assertEquals(1, result.size());
        assertEquals("Welcome", result.get(0).getText());
    }

    @Test
    void testDeleteTranslation_nonExistingId() {
        // deleteById does nothing if id doesn't exist, no exception thrown
        doNothing().when(translationRepo).deleteById(99L);

        assertDoesNotThrow(() -> translationService.deleteTranslation(99L));
        verify(translationRepo, times(1)).deleteById(99L);
    }

    @Test
    void testGetByLang_emptyResult() {
        when(translationRepo.findByLanguageCode("fr")).thenReturn(Collections.emptyList());

        List<TranslationDto> result = translationService.getByLang("fr");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetByKey_emptyResult() {
        when(translationRepo.findByTranslationKey("nonexistent")).thenReturn(Collections.emptyList());

        List<TranslationDto> result = translationService.getByKey("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchByTag_emptyResult() {
        List<String> tags = List.of("nonexistent");
        when(translationRepo.findByTags(tags)).thenReturn(Collections.emptyList());

        List<TranslationDto> result = translationService.searchByTag(tags);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchByText_emptyResult() {
        String query = "unknown text";
        when(translationRepo.searchByText(query)).thenReturn(Collections.emptyList());

        List<TranslationDto> result = translationService.searchByText(query);

        assertTrue(result.isEmpty());
    }
}

