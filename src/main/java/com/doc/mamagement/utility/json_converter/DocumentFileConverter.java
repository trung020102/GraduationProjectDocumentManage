package com.doc.mamagement.utility.json_converter;

import com.doc.mamagement.document.dto.DocumentFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;

public class DocumentFileConverter implements Neo4jPersistentPropertyConverter<DocumentFile>{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Value write(DocumentFile source) {
        if (source == null)
            return null;

        String jsonDocumentFileString;
        try {
            jsonDocumentFileString = objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Values.value(jsonDocumentFileString);
    }

    @Override
    public DocumentFile read(Value source) {
        if (source == null)
            return null;

        try {
            return objectMapper.readValue(source.asString(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
