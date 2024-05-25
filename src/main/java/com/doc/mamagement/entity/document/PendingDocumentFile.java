package com.doc.mamagement.entity.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;

@Getter
@Setter
@Node("PendingDocumentFile")
/* This node contain name of new documents files that have been uploaded from creation form but not officially stored */
public class PendingDocumentFile {
    @Id
    @Property("uuid_file_name")
    private String uuidFileName;
    @Property("created_at")
    private LocalDateTime createdAt;

    public PendingDocumentFile(String uuidFileName) {
        this.uuidFileName = uuidFileName;
        this.createdAt = LocalDateTime.now();
    }
}
