package com.example.dynamic.service;

import com.example.dynamic.model.CustomRecord;
import com.example.dynamic.model.CustomSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Dynamic search API leveraging jsonb operators and GIN index.
     * Supports operators: eq, contains (substring), range (number/date), exists, text (tsvector simple).
     */
    public List<CustomRecord> search(CustomSchema schema, JsonNode queryNode, int limit, int offset) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("select r.* from custom_record r where r.schema_id = ? ");
        params.add(schema.getId());

        if (queryNode != null && queryNode.isObject()) {
            // expected shape: { filters: [{path:"$.field", op:"eq|contains|range|exists", value:...}], text: "..." }
            JsonNode filters = queryNode.get("filters");
            if (filters != null && filters.isArray()) {
                for (JsonNode f : filters) {
                    String path = f.path("path").asText();
                    String op = f.path("op").asText("eq");
                    JsonNode valueNode = f.get("value");

                    switch (op) {
                        case "eq" -> {
                            sql.append(" and r.data #>> ? = ? ");
                            params.add(toPathArray(path));
                            params.add(valueNode.isValueNode() ? valueNode.asText() : valueNode.toString());
                        }
                        case "contains" -> {
                            sql.append(" and r.data #>> ? ilike ? ");
                            params.add(toPathArray(path));
                            params.add("%" + (valueNode.isValueNode() ? valueNode.asText() : valueNode.toString()) + "%");
                        }
                        case "exists" -> {
                            sql.append(" and r.data #> ? is not null ");
                            params.add(toPathArray(path));
                        }
                        case "range" -> {
                            JsonNode min = valueNode != null ? valueNode.get("min") : null;
                            JsonNode max = valueNode != null ? valueNode.get("max") : null;
                            if (min != null && !min.isNull()) {
                                sql.append(" and (r.data #>> ?)::numeric >= ?::numeric ");
                                params.add(toPathArray(path));
                                params.add(min.asText());
                            }
                            if (max != null && !max.isNull()) {
                                sql.append(" and (r.data #>> ?)::numeric <= ?::numeric ");
                                params.add(toPathArray(path));
                                params.add(max.asText());
                            }
                        }
                        default -> {
                            // ignore unknown op
                        }
                    }
                }
            }

            JsonNode textNode = queryNode.get("text");
            if (textNode != null && textNode.isTextual() && !textNode.asText().isBlank()) {
                sql.append(" and r.data_tsv @@ plainto_tsquery('simple', ?) ");
                params.add(textNode.asText());
            }
        }

        sql.append(" order by r.created_at desc limit ? offset ? ");
        params.add(limit);
        params.add(offset);

        var query = entityManager.createNativeQuery(sql.toString(), CustomRecord.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        return query.getResultList();
    }

    private String[] toPathArray(String dotPath) {
        String p = dotPath.replace("$.", "");
        return p.split("\\.");
    }

    private String lastKey(String dotPath) {
        String[] arr = toPathArray(dotPath);
        return arr[arr.length - 1];
    }
}

