package com.doc.mamagement.category;

import com.doc.mamagement.entity.category.Category;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface CategoryRepository extends ReactiveNeo4jRepository<Category, Long> {
    Flux<Category> findAllByIsParentTrue();
    Flux<Category> getAllByIdIn(List<Long> ids);
}
