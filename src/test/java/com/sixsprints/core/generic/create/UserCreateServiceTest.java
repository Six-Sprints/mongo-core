package com.sixsprints.core.generic.create;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.service.UserCreateService;

public class UserCreateServiceTest extends ApplicationTests {

  @Autowired
  private UserCreateService service;

  @Test
  public void testSave() {
    User user = user(1);
    user = service.save(user);
    userAssert(user, 1);
  }

  @Test
  public void testSaveTwiceAndSlugMustNotChange() {
    User user = user(1);
    user = service.save(user);
    user = service.save(user);
    userAssert(user, 1);
  }

  @Test
  public void testSaveAll() {
    List<User> list = Lists.newArrayList();
    for (int i = 1; i < 10; i++) {
      list.add(user(i));
    }
    List<User> users = service.saveAll(list);
    int i = 1;
    for (User user : users) {
      userAssert(user, i++);
    }
    User user = service.save(user(i));
    userAssert(user, i);
  }

  private User user(int i) {
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i).build();
  }

  protected void userAssert(User user, int i) {
    entityAssert(user, i);
    assertThat(user.getEmail()).isEqualTo("email" + i + "@gmail.com");
    assertThat(user.getName()).isEqualTo("Name" + i);
  }

}
