package com.tms.tms.repository;

import com.tms.tms.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    @Query("SELECT t FROM Translation t JOIN FETCH t.language WHERE t.language.code = :langCode")
    List<Translation> findByLanguageCode(String langCode);

    List<Translation> findByTranslationKey(String key);

    @Query("SELECT t FROM Translation t JOIN t.tags tag WHERE tag IN :tags")
    List<Translation> findByTags(@Param("tags") List<String> tags);

    @Query("SELECT t FROM Translation t WHERE LOWER(t.text) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Translation> searchByText(@Param("query") String query);

    @Query("SELECT t FROM Translation t JOIN FETCH t.language")
    List<Translation> findAllWithLanguage();

    @Query(value = """
        SELECT lang_code, json_object_agg(translation_key, text) AS translations
        FROM translations
        GROUP BY lang_code
        ORDER BY lang_code
        """, nativeQuery = true)
    List<Object[]> exportTranslationsAsJson();
}
