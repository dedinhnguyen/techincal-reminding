package com.devcompanion.service;

import com.devcompanion.domain.document.AdvancedTemplateDocument;
import com.devcompanion.repository.AdvancedTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoTemplateService {

    private final AdvancedTemplateRepository advancedTemplateRepository;

    @Value("${app.mongodb.enabled:false}")
    private boolean mongoEnabled;

    @Cacheable(value = "mongo_templates", key = "'all'")
    public List<AdvancedTemplateDocument> getAllTemplates() {
        if (mongoEnabled) {
            try {
                return advancedTemplateRepository.findAll();
            } catch (Exception e) {
                log.warn("Failed fetching from MongoDB, returning fallback seed data: {}", e.getMessage());
                return getFallbackTemplates();
            }
        }
        return getFallbackTemplates();
    }

    public AdvancedTemplateDocument getTemplateByTopic(String topic) {
        if (mongoEnabled) {
            try {
                return advancedTemplateRepository.findByTopic(topic)
                        .orElseGet(() -> getFallbackTemplates().stream()
                                .filter(t -> t.getTopic().equalsIgnoreCase(topic))
                                .findFirst()
                                .orElse(null));
            } catch (Exception e) {
                log.warn("Failed fetching from MongoDB: {}", e.getMessage());
            }
        }
        return getFallbackTemplates().stream()
                .filter(t -> t.getTopic().equalsIgnoreCase(topic))
                .findFirst()
                .orElse(null);
    }

    public List<AdvancedTemplateDocument> getFallbackTemplates() {
        List<AdvancedTemplateDocument> list = new ArrayList<>();

        list.add(AdvancedTemplateDocument.builder()
                .id("mongo-agg-1")
                .topic("mongo-aggregation-lookup-and-group")
                .technology("SPRING_BOOT_MONGODB")
                .scenario("Join Orders with Customers and compute Total Spent per Customer with conditional status")
                .springCode(Map.of(
                        "mongoTemplate", """
                                MatchOperation matchCompleted = Aggregation.match(Criteria.where("status").is("COMPLETED"));
                                LookupOperation lookupCustomer = Aggregation.lookup("customers", "customerId", "_id", "customerDetails");
                                UnwindOperation unwindCustomer = Aggregation.unwind("customerDetails");
                                GroupOperation groupByCustomer = Aggregation.group("customerDetails._id")
                                        .first("customerDetails.fullName").as("customerName")
                                        .sum("totalAmount").as("totalSpent")
                                        .count().as("orderCount");
                                SortOperation sortBySpent = Aggregation.sort(Sort.Direction.DESC, "totalSpent");

                                Aggregation aggregation = Aggregation.newAggregation(
                                        matchCompleted, lookupCustomer, unwindCustomer, groupByCustomer, sortBySpent
                                );
                                AggregationResults<CustomerOrderSummaryDto> results = mongoTemplate.aggregate(
                                        aggregation, "orders", CustomerOrderSummaryDto.class
                                );
                                return results.getMappedResults();
                                """,
                        "reactiveMongoTemplate", """
                                return reactiveMongoTemplate.aggregate(aggregation, "orders", CustomerOrderSummaryDto.class);
                                """
                ))
                .rawQuery(Map.of(
                        "pipeline", List.of(
                                Map.of("$match", Map.of("status", "COMPLETED")),
                                Map.of("$lookup", Map.of("from", "customers", "localField", "customerId", "foreignField", "_id", "as", "customerDetails")),
                                Map.of("$unwind", "$customerDetails"),
                                Map.of("$group", Map.of(
                                        "_id", "$customerDetails._id",
                                        "customerName", Map.of("$first", "$customerDetails.fullName"),
                                        "totalSpent", Map.of("$sum", "$totalAmount"),
                                        "orderCount", Map.of("$sum", 1)
                                )),
                                Map.of("$sort", Map.of("totalSpent", -1))
                        )
                ))
                .explanation("Multi-stage Aggregation pipeline combining $match filter, $lookup foreign collection join, $unwind array deconstruction, and $group accumulator in Spring Data MongoDB.")
                .tags(List.of("MongoDB", "Aggregation", "MongoTemplate", "Lookup", "Spring Boot"))
                .complexity("ADVANCED")
                .build());

        list.add(AdvancedTemplateDocument.builder()
                .id("mongo-agg-2")
                .topic("mongo-dynamic-facet-search")
                .technology("SPRING_BOOT_MONGODB")
                .scenario("Faceted Search with Bucket categorization and Price Range analytics")
                .springCode(Map.of(
                        "mongoTemplate", """
                                FacetOperation facetOperation = Aggregation.facet(
                                        Aggregation.match(Criteria.where("inStock").is(true)),
                                        Aggregation.sortByCount("category")
                                ).as("categorizedCounts")
                                .and(
                                        Aggregation.bucket("price")
                                                .withBoundaries(0, 50, 100, 500, 1000)
                                                .withDefaultBucket("other")
                                                .andOutputCount().as("count")
                                ).as("priceRanges");

                                Aggregation agg = Aggregation.newAggregation(facetOperation);
                                return mongoTemplate.aggregate(agg, "products", ProductFacetDto.class).getUniqueMappedResult();
                                """
                ))
                .rawQuery(Map.of(
                        "$facet", Map.of(
                                "categorizedCounts", List.of(Map.of("$sortByCount", "$category")),
                                "priceRanges", List.of(Map.of("$bucket", Map.of("groupBy", "$price", "boundaries", List.of(0, 50, 100, 500, 1000))))
                        )
                ))
                .explanation("Faceted Search ($facet) allows computing multiple parallel aggregation pipelines within a single database round-trip.")
                .tags(List.of("MongoDB", "Facet", "Bucket", "Analytics", "Spring Data"))
                .complexity("ADVANCED")
                .build());

        return list;
    }
}
