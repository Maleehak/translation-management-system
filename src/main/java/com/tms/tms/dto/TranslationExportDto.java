package com.tms.tms.dto;

public class TranslationExportDto {
    private String langCode;
    private String translationsJson;

    public TranslationExportDto (String langCode, String translationsJson) {
        this.langCode = langCode;
        this.translationsJson = translationsJson;
    }

    public String getLangCode() {
        return langCode;
    }

    public String getTranslationsJson() {
        return translationsJson;
    }
}