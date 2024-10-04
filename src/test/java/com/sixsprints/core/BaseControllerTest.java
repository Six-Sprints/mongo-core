package com.sixsprints.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseControllerTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Test
  public void contextLoads() {
    assertThat(Boolean.TRUE).isTrue();
  }

  @AfterEach
  public void tearDown() {
    mongoTemplate.getDb().drop();
  }

}
