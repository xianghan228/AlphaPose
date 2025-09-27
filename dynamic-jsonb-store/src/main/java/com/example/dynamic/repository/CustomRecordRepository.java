package com.example.dynamic.repository;

import com.example.dynamic.model.CustomRecord;
import com.example.dynamic.model.CustomSchema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomRecordRepository extends JpaRepository<CustomRecord, UUID> {
    List<CustomRecord> findBySchema(CustomSchema schema);
}

