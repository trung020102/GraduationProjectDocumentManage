package com.doc.mamagement.document;

import com.doc.mamagement.entity.document.PendingDocumentFile;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PendingDocumentFileRepository extends ReactiveNeo4jRepository<PendingDocumentFile, String> {
    Mono<Void> deleteAllByUuidFileNameIn(List<String> uuidFileNames);
    Mono<Void> deleteAllByCreatedAtLessThan(LocalDateTime time);
    Flux<PendingDocumentFile> getAllByCreatedAtLessThan(LocalDateTime time);
}
