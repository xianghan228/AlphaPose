package com.example.dynamic.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchemaDtos {
    @Data
    public static class CreateSchemaRequest {
        @NotBlank
        private String name;
        @NotNull
        @Min(1)
        private Integer version;
        @NotNull
        private JsonNode jsonSchema;
    }
}

