package com.devcompanion.domain.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Document(collection = "advanced_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvancedTemplateDocument implements Serializable {

    @Id
    private String id;

    private String topic;

    private String technology;

    private String scenario;

    private Map<String, String> springCode;

    private Map<String, Object> rawQuery;

    private String explanation;

    private List<String> tags;

    private String complexity;
}
