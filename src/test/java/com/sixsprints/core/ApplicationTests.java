package com.sixsprints.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.mock.init.ApplicationInit;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTests {

  @Autowired
  private MongoTemplate mongo;

  @Autowired
  private ApplicationInit appInit;

  @Test
  public void contextLoads() {
  }

  @BeforeEach
  public void before() {
    mongo.getDb().drop();
    appInit.init();
  }

  @AfterEach
  public void after() {
    mongo.getDb().drop();
  }

  protected void entityAssert(AbstractMongoEntity entity, int i) {
    assertThat(entity).isNotEqualTo(null);
    assertThat(entity.getId()).isNotEqualTo(null);
    assertThat(entity.getSlug()).isEqualTo("U" + StringUtils.leftPad("" + i, 8, "0"));
    assertThat(entity.getSequence()).isEqualTo(i);
    assertThat(entity.getDateCreated()).isNotEqualTo(null);
    assertThat(entity.getDateModified()).isNotEqualTo(null);
  }

}
