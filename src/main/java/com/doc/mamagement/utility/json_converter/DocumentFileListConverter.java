package com.doc.mamagement.utility.json_converter;

import com.doc.mamagement.document.dto.DocumentFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import java.util.List;

public class DocumentFileListConverter implements Neo4jPersistentPropertyConverter<List<DocumentFile>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Value write(List<DocumentFile> source) {
        if (source == null) {
            return Values.NULL;
        }

        String jsonDocumentFileString;
        try {
            jsonDocumentFileString = objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Values.value(jsonDocumentFileString);
    }

    @Override
    public List<DocumentFile> read(Value source) {
        if (source == null || source.isNull()) {
            return null;
        }

        try {
            return objectMapper.readValue(source.asString(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
