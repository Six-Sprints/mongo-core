package com.sixsprints.core.mock.init;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import com.sixsprints.core.domain.CustomSequence;
import com.sixsprints.core.domain.ImportLogDetails;
import com.sixsprints.core.mock.domain.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationInit {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MongoMappingContext mongoMappingContext;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    createIndexes();
  }

  private void createIndexes() {
    List<Class<?>> classes = List.of(CustomSequence.class, ImportLogDetails.class, User.class);

    classes.forEach(clzz -> {
      IndexOperations indexOps = mongoTemplate.indexOps(clzz);
      IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
      resolver.resolveIndexFor(clzz).forEach(indexOps::ensureIndex);
    });

    log.info("Mongo Indexes created.");
  }

}
