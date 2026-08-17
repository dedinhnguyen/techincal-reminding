package com.devcompanion.repository;

import com.devcompanion.domain.document.AdvancedTemplateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvancedTemplateRepository extends MongoRepository<AdvancedTemplateDocument, String> {
    Optional<AdvancedTemplateDocument> findByTopic(String topic);
    List<AdvancedTemplateDocument> findByTechnology(String technology);

    @Query("{ 'tags': { $regex: ?0, $options: 'i' } }")
    List<AdvancedTemplateDocument> findByTagLike(String tag);
}
