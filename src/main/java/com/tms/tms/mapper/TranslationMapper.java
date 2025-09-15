package com.tms.tms.mapper;

import com.tms.tms.dto.TranslationDto;
import com.tms.tms.model.Language;
import com.tms.tms.model.Translation;
import org.springframework.stereotype.Component;

@Component
public class TranslationMapper {

    public TranslationDto toDto(Translation entity) {
        if (entity == null) {
            return null;
        }

        TranslationDto dto = new TranslationDto();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setTags(entity.getTags());

        if (entity.getLanguage() != null) {
            dto.setLangCode(entity.getLanguage().getCode());
        }

        return dto;
    }

    public Translation toEntity(TranslationDto dto, Language language) {
        if (dto == null) {
            return null;
        }

        Translation entity = new Translation();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setTags(dto.getTags());
        entity.setLanguage(language);

        return entity;
    }
}

