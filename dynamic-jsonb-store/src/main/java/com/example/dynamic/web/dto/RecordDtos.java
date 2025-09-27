package com.example.dynamic.web.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordDtos {
    @Data
    public static class CreateRecordRequest {
        @NotBlank
        private String schemaName;
        @NotNull
        private Integer schemaVersion;
        @NotNull
        private JsonNode data;
    }

    @Data
    public static class SearchRequest {
        @NotBlank
        private String schemaName;
        @NotNull
        private Integer schemaVersion;
        private JsonNode query; // {filters: [...], text: "..."}
        private Integer limit = 20;
        private Integer offset = 0;
    }
}

