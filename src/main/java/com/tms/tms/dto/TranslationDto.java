package com.tms.tms.dto;

import java.util.List;

public class TranslationDto {
    private Long id;
    private String translationKey;
    private String langCode;
    private String text;
    private List<String> tags;


    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public String getLangCode() {
        return langCode;
    }

    public String getText() {
        return text;
    }

    public List<String> getTags() {
        return tags;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

