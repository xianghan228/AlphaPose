package com.example.dynamic.service;

import com.example.dynamic.model.CustomSchema;
import com.example.dynamic.repository.CustomSchemaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchemaService {

    private final CustomSchemaRepository schemaRepository;

    @Transactional
    public CustomSchema create(String name, Integer version, JsonNode jsonSchema) {
        Optional<CustomSchema> existing = schemaRepository.findByNameAndVersion(name, version);
        if (existing.isPresent()) {
            return existing.get();
        }
        CustomSchema schema = new CustomSchema();
        schema.setName(name);
        schema.setVersion(version);
        schema.setJsonSchema(jsonSchema);
        return schemaRepository.save(schema);
    }

    public Optional<CustomSchema> getByNameVersion(String name, Integer version) {
        return schemaRepository.findByNameAndVersion(name, version);
    }
}

