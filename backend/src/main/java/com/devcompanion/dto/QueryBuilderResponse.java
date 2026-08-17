package com.devcompanion.dto;

import java.io.Serializable;

public record QueryBuilderResponse(
        String derivedQueryMethod,
        String jpqlQuery,
        String nativeSqlQuery,
        String criteriaApiSnippet,
        String specificationSnippet,
        String mongoTemplateSnippet,
        String explanation,
        String performanceTip
) implements Serializable {}
