package com.lmlasmo.gioscito.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import com.mongodb.reactivestreams.client.MongoClient;

@Testcontainers
public class MongoDBTestContainerConf {
	
	@Bean(destroyMethod = "stop")
	public MongoDBContainer mongoDBContainer() {
		MongoDBContainer container = new MongoDBContainer("mongo:4.4");
		container.start();
		return container;
	}
	
	@Bean
	@Primary
	public MongoClient reactiveMongoClient(MongoDBContainer container) {
        return com.mongodb.reactivestreams.client.MongoClients.create(container.getReplicaSetUrl());
    }

    @Bean
    @Primary
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient client) {
        return new ReactiveMongoTemplate(client, "testdb");
    }
	
}
