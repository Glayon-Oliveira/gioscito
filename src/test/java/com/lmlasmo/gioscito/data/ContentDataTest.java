package com.lmlasmo.gioscito.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.lmlasmo.gioscito.content.ContentProperties;
import com.lmlasmo.gioscito.content.validation.schema.ContentValidationException;
import com.lmlasmo.gioscito.data.dao.FindControl;
import com.lmlasmo.gioscito.service.CollectionService;

import reactor.core.publisher.Mono;

@SpringBootTest
@ComponentScans({
	@ComponentScan("com.lmlasmo.gioscito.content"),
	@ComponentScan("com.lmlasmo.gioscito.data"),
	@ComponentScan("com.lmlasmo.gioscito.service.data")
	})
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoReactiveAutoConfiguration.class})
@Import(MongoDBTestContainerConf.class)
@ActiveProfiles("test")
public class ContentDataTest {
	
	@Autowired
    private Set<CollectionService> collectionServices;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @BeforeEach
    void upSetup() {
        mongoTemplate.getMongoDatabase()
            .flatMap(mdb -> Mono.from(mdb.drop()))
            .block();
        
        ContentProperties.successCreater().forEach((cn, cc) -> {
            CollectionService collectionService = collectionServices.stream()
                    .filter(cs -> cs.getCollectionName().equals(cn))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException());

            Map<String, Object> contentCreated = collectionService.runCreate(cc).block();

            assertNotNull(contentCreated.get("_id"));

            contentCreated.forEach((ccdk, ccdv) -> {
                if (ccdk.equals("_id")) return;
                assertEquals(ccdv, cc.get(ccdk));
            });
        });
    }

    @AfterEach
    void downSetup() {
        mongoTemplate.getMongoDatabase()
            .flatMap(mdb -> Mono.from(mdb.drop()))
            .block();
    }
    
    @Test
    public void runFailCreater() {
    	ContentProperties.failCreater().forEach((cn, cc) -> {
            CollectionService collectionService = collectionServices.stream()
                    .filter(cs -> cs.getCollectionName().equals(cn))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException());
            
            assertThrows(ContentValidationException.class, () -> collectionService.runCreate(cc).block());
        });
    }

    @Test
    public void runRead() {
        collectionServices.stream()
            .flatMap(cs -> {
                return cs.runFind(FindControl.builder().build())
                        .flatMap(cf -> cs.runFindById(cf.get("_id").toString()))
                        .collectList()
                        .block()
                        .stream();
            })
            .forEach(found -> {
                assertNotNull(found.get("_id"));
            });
    }

    @Test
    public void runUpdate() {
        ContentProperties.successUpdate().forEach((cn, cc) -> {
            CollectionService collectionService = collectionServices.stream()
                    .filter(cs -> cs.getCollectionName().equals(cn))
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException());

            collectionService.runFind(FindControl.builder().build())
                .flatMap(cln -> {
                    return collectionService.runUpdateById(cln.get("_id").toString(), cc)
                        .flatMap(upc -> {
                            upc.forEach((ccdk, ccdv) -> {
                                if (ccdk.equals("_id")) return;
                                assertEquals(normalize(ccdv), cc.get(ccdk));
                            });
                            return Mono.empty();
                        });
                })
                .collectList()
                .block();
        });
    }

    @Test
    public void runDelete() {
        collectionServices.stream()
            .flatMap(cs -> {
                return cs.runFind(FindControl.builder().build())
                        .flatMap(cf -> cs.runFindById(cf.get("_id").toString()))
                        .flatMap(cf -> cs.runDeleteById(cf.get("_id").toString()))
                        .collectList()
                        .block()
                        .stream();
            })
            .forEach(dd -> assertTrue(dd));
    }

    private Object normalize(Object value) {
        if (value instanceof Document doc) {
            return doc.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> normalize(e.getValue())));
        }

        if (value instanceof Collection<?> list) {
            return list.stream().map(this::normalize).toList();
        }
        return value;
    }

}
