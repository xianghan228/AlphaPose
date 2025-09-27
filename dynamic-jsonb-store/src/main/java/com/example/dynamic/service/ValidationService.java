package com.example.dynamic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {

    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

    public Set<ValidationMessage> validate(JsonNode schemaNode, JsonNode data) {
        JsonSchema schema = schemaFactory.getSchema(schemaNode);
        return schema.validate(data);
    }
}

