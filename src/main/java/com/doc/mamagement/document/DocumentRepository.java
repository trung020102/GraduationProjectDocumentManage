package com.doc.mamagement.document;

import com.doc.mamagement.entity.document.Document;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface DocumentRepository extends ReactiveNeo4jRepository<Document, String> {
    Mono<Document> findByCode(String code);
    @Query("MATCH (d:Document) RETURN d.code")
    Flux<String> getAllCodes();
    Flux<Document> getAllByCodeIn(List<String> codes);
    @Query("MATCH (d:Document)-[r]-() WHERE d.code = $code delete r")
    Mono<Void> deleteAllRelationship(String code);
    Flux<Document> findAllByOrderByCreatedAtDesc();
}
