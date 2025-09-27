package com.example.dynamic.web;

import com.example.dynamic.model.CustomSchema;
import com.example.dynamic.service.SchemaService;
import com.example.dynamic.web.dto.SchemaDtos.CreateSchemaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
public class SchemaController {

    private final SchemaService schemaService;

    @PostMapping
    public ResponseEntity<CustomSchema> create(@Valid @RequestBody CreateSchemaRequest req) {
        CustomSchema saved = schemaService.create(req.getName(), req.getVersion(), req.getJsonSchema());
        return ResponseEntity.created(URI.create("/api/schemas/" + saved.getId())).body(saved);
    }
}

