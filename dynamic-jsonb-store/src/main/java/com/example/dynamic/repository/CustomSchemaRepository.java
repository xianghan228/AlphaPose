package com.example.dynamic.repository;

import com.example.dynamic.model.CustomSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomSchemaRepository extends JpaRepository<CustomSchema, UUID> {
    Optional<CustomSchema> findByNameAndVersion(String name, Integer version);
}

