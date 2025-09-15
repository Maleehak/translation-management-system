package com.tms.tms.controllers;

import com.tms.tms.dto.TranslationDto;
import com.tms.tms.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/translations")
@Tag(name = "Translations", description = "CRUD operations for translations")
public class TranslationController {

    private final TranslationService service;

    public TranslationController(TranslationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new translation")
    public ResponseEntity<TranslationDto> create(@RequestBody TranslationDto t) {
        return ResponseEntity.ok(service.createTranslation(t));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing translation")
    public ResponseEntity<TranslationDto> update(@PathVariable Long id, @RequestBody TranslationDto t) {
        return ResponseEntity.ok(service.updateTranslation(id, t));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a translation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lang/{langCode}")
    @Operation(summary = "Find translations by language code")
    public ResponseEntity<List<TranslationDto>> getByLang(@PathVariable String langCode) {
        return ResponseEntity.ok(service.getByLang(langCode));
    }

    @GetMapping("/key/{key}")
    @Operation(summary = "Find translations by key")
    public ResponseEntity<List<TranslationDto>> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.getByKey(key));
    }

    @GetMapping("/search/tags")
    @Operation(summary = "Find translations by tag")
    public ResponseEntity<List<TranslationDto>> searchByTags(@RequestParam List<String> tags) {
        return ResponseEntity.ok(service.searchByTag(tags));
    }

    @GetMapping("/search/text")
    @Operation(summary = "Find translations by text")
    public ResponseEntity<List<TranslationDto>> searchByText(@RequestParam String query) {
        return ResponseEntity.ok(service.searchByText(query));
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        service.exportTranslationsViaJsonFactory(response);
    }
}

