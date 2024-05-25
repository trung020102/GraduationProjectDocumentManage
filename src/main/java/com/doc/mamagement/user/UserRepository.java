package com.doc.mamagement.user;


import com.doc.mamagement.entity.RelationshipConstant;
import com.doc.mamagement.entity.user.User;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserRepository extends ReactiveNeo4jRepository<User, Long> {
    Mono<User> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmailAndIdNot(String email, Long id);

    @Query("MATCH (user:User)-[hasRole:" + RelationshipConstant.HAS_ROLE + "]->(role:Role) " +
            "WITH user, hasRole, role " +
            "ORDER BY role.code, user.created_at DESC " +
            "RETURN user, collect(hasRole), collect(role) ")
        /* This cypher returns all users with role, sorted by role code and creation date */
    Flux<User> findAllWithRole();
    Flux<User> getAllByIdIn(List<Long> ids);
}
