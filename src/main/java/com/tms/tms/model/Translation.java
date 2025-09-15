package com.tms.tms.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "translations", uniqueConstraints = { @UniqueConstraint(columnNames = {"translation_key", "lang_code"})})
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "translation_key", nullable = false, length = 255)
    private String translationKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lang_code", nullable = false)
    private Language language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    private List<String> tags = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Language getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
