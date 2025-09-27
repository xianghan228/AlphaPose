package com.example.dynamic.web;

import com.example.dynamic.model.CustomRecord;
import com.example.dynamic.model.CustomSchema;
import com.example.dynamic.service.RecordService;
import com.example.dynamic.service.SchemaService;
import com.example.dynamic.service.SearchService;
import com.example.dynamic.web.dto.RecordDtos.CreateRecordRequest;
import com.example.dynamic.web.dto.RecordDtos.SearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final SchemaService schemaService;
    private final RecordService recordService;
    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<CustomRecord> create(@Valid @RequestBody CreateRecordRequest req) {
        CustomSchema schema = schemaService.getByNameVersion(req.getSchemaName(), req.getSchemaVersion())
                .orElseThrow(() -> new IllegalArgumentException("Schema not found"));
        CustomRecord saved = recordService.create(schema, req.getData());
        return ResponseEntity.created(URI.create("/api/records/" + saved.getId())).body(saved);
    }

    @PostMapping("/search")
    public ResponseEntity<List<CustomRecord>> search(@Valid @RequestBody SearchRequest req) {
        CustomSchema schema = schemaService.getByNameVersion(req.getSchemaName(), req.getSchemaVersion())
                .orElseThrow(() -> new IllegalArgumentException("Schema not found"));
        List<CustomRecord> results = searchService.search(schema, req.getQuery(),
                req.getLimit() == null ? 20 : req.getLimit(),
                req.getOffset() == null ? 0 : req.getOffset());
        return ResponseEntity.ok(results);
    }
}

