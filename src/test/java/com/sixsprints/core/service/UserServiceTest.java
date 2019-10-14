package com.sixsprints.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.service.UserService;

public class UserServiceTest extends ApplicationTests {

  @Autowired
  private UserService service;

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
      list.add(user(i));
    }
    List<BulkUpdateInfo<User>> users = service.bulkImport(list);
    int i = 1;
    for (BulkUpdateInfo<User> user : users) {
      if (!user.getUpdateAction().equals(UpdateAction.IGNORE)) {
        userAssert(user.getData(), i++);
      }
    }
    List<BulkUpdateInfo<User>> user = service.bulkImport(ImmutableList.<User>of(userWithNullAddress(--i)));
    assertThat(user.get(0).getUpdateAction()).isEqualTo(UpdateAction.IGNORE);
    userAssert(user.get(0).getData(), i);
  }

  private User user(int i) {
    Address address = Address.builder().city("city" + i).state("state" + i).country("country" + i).build();
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i).flag(true)
      .address(address).build();
  }

  private User userWithNullAddress(int i) {
    Address address = Address.builder().city("city" + i).build();
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i).flag(true)
      .address(address).build();
  }

  protected void userAssert(User user, int i) {
    entityAssert(user, i);
    assertThat(user.getEmail()).isEqualTo("email" + i + "@gmail.com");
    assertThat(user.getName()).isEqualTo("Name" + i);
    assertThat(user.getFlag()).isTrue();
  }

}
