package com.sixsprints.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.dto.ImportResponseWrapper;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.transformer.UserMapper;

public class UserServiceTest extends ApplicationTests {

  @Autowired
  private UserService service;

  private UserMapper userMapper = UserMapper.INSTANCE;

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
    List<BulkUpdateInfo<User>> users = service.updateAll(list);
    int i = 1;
    for (BulkUpdateInfo<User> user : users) {
      if (!user.getUpdateAction().equals(UpdateAction.IGNORE)) {
        userAssert(user.getData(), i++);
      }
    }
    List<BulkUpdateInfo<User>> user = service.updateAll(ImmutableList.<User>of(userWithNullAddress(--i)));
    assertThat(user.get(0).getUpdateAction()).isEqualTo(UpdateAction.IGNORE);
    userAssert(user.get(0).getData(), i);
  }

  @Test
  public void shouldExportToCsv() throws IOException, BaseException {

    List<User> list = Lists.newArrayList();
    for (int i = 1; i < 10; i++) {
      list.add(user(i));
    }
    service.updateAll(list);

    String fileName = fileName();
    PrintWriter writer = new PrintWriter(new File(fileName));
    service.exportData(userMapper, null, writer, Locale.ENGLISH);
  }

  @Test
  public void shouldImportFromCsv() throws IOException, BaseException {

    String fileName = "/test.csv";

    InputStream stream = this.getClass().getResourceAsStream(fileName);
    ImportResponseWrapper<UserDto> dataFromStream = service.importData(stream, Locale.ENGLISH);
    System.out.println(dataFromStream);
  }

  private String fileName() {
    String currentUsersHomeDir = System.getProperty("user.home");
    String otherFolder = currentUsersHomeDir + File.separator + "Desktop" + File.separator + "test.csv";
    return otherFolder;
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
