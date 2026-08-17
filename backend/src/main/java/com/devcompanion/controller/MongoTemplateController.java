package com.devcompanion.controller;

import com.devcompanion.domain.document.AdvancedTemplateDocument;
import com.devcompanion.service.MongoTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo-templates")
@RequiredArgsConstructor
@Tag(name = "MongoDB Templates", description = "Endpoints for dynamic MongoDB aggregation pipelines and MongoTemplate patterns")
public class MongoTemplateController {

    private final MongoTemplateService mongoTemplateService;

    @GetMapping
    @Operation(summary = "Get all MongoDB advanced aggregation templates")
    public ResponseEntity<List<AdvancedTemplateDocument>> getAllTemplates() {
        return ResponseEntity.ok(mongoTemplateService.getAllTemplates());
    }

    @GetMapping("/{topic}")
    @Operation(summary = "Get MongoDB template by topic slug")
    public ResponseEntity<AdvancedTemplateDocument> getTemplateByTopic(@PathVariable String topic) {
        AdvancedTemplateDocument template = mongoTemplateService.getTemplateByTopic(topic);
        if (template == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(template);
    }
}
