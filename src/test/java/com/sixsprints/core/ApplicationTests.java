package com.sixsprints.core;

import static org.assertj.core.api.Assertions.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import com.sixsprints.core.domain.AbstractMongoEntity;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTests {

  @Autowired
  private MongoTemplate mongo;

  @Value("${slug.padding.character:0}")
  private String slugPaddingCharacter;

  @Value("${slug.padding.length:8}")
  private int slugPaddingLength;

  @Test
  public void contextLoads() {}

  @BeforeEach
  public void before() {
    mongo.getDb().drop();
  }

  @AfterEach
  public void after() {
    mongo.getDb().drop();
  }

  protected void entityAssert(AbstractMongoEntity entity, int i) {
    assertThat(entity).isNotEqualTo(null);
    assertThat(entity.getId()).isNotEqualTo(null);
    assertThat(entity.getSlug())
        .isEqualTo("USR" + StringUtils.leftPad("" + i, slugPaddingLength, slugPaddingCharacter));
    assertThat(entity.getSequence()).isEqualTo(i);
    assertThat(entity.getDateCreated()).isNotEqualTo(null);
    assertThat(entity.getDateModified()).isNotEqualTo(null);
  }

}
