package com.example.dynamic.service;

import com.example.dynamic.model.CustomRecord;
import com.example.dynamic.model.CustomSchema;
import com.example.dynamic.repository.CustomRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final CustomRecordRepository recordRepository;
    private final ValidationService validationService;

    @Transactional
    public CustomRecord create(CustomSchema schema, JsonNode data) {
        Set<ValidationMessage> violations = validationService.validate(schema.getJsonSchema(), data);
        if (!violations.isEmpty()) {
            throw new DataIntegrityViolationException("JSON does not match schema: " + violations);
        }
        CustomRecord record = new CustomRecord();
        record.setSchema(schema);
        record.setData(data);
        return recordRepository.save(record);
    }

    public List<CustomRecord> listBySchema(CustomSchema schema) {
        return recordRepository.findBySchema(schema);
    }
}

