package com.doc.mamagement;

import com.doc.mamagement.utility.file_handling.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"vn.rananu.spring", "com.doc"})
@EnableNeo4jRepositories
@EnableNeo4jAuditing
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties(StorageProperties.class)
public class DocManagement2Application {

    public static void main(String[] args) {
        SpringApplication.run(DocManagement2Application.class, args);
    }
    @Bean
    Configuration cypherDslConfiguration() {
        return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }
}
