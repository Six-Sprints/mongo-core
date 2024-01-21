package com.sixsprints.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoOperations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.ImportLogDetailsDto;
import com.sixsprints.core.dto.KeyLabelDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.SearchColumnFilter;
import com.sixsprints.core.enums.ImportOperation;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.mock.domain.Role;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.enums.Gender;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.transformer.UserExcelMapper;
import com.sixsprints.core.utils.AppConstants;

public class UserServiceTest extends ApplicationTests {

  @Autowired
  private UserService userService;

  @Autowired
  private MongoOperations mongo;

  @Autowired
  private UserExcelMapper userExcelMapper;

  @Test
  public void testSave() {
    User user = user(1);
    user = userService.save(user);
    userAssert(user, 1);
  }

  @Test
  public void testFindAllWithConverters() {
    testSave();
    List<User> findAll = userService.findAll();
    userAssert(findAll.get(0), 1);
  }

  @Test
  public void testSaveTwiceAndSlugMustNotChange() {
    User user = user(1);
    user = userService.save(user);
    user = userService.save(user);
    userAssert(user, 1);
  }

  @Test
  public void testSaveAll() {
    List<User> list = Lists.newArrayList();
    for (int i = 1; i < 10; i++) {
      list.add(user(i));
      list.add(user(i));
    }
    List<BulkUpdateInfo<User>> users = userService.bulkUpsert(list);
    int i = 1;
    for (BulkUpdateInfo<User> user : users) {
      if (!user.getUpdateAction().equals(UpdateAction.IGNORE)) {
        userAssert(user.getData(), i++);
      }
    }
    List<BulkUpdateInfo<User>> user = userService.bulkUpsert(ImmutableList.<User>of(userWithNullAddress(--i)));
    assertThat(user.get(0).getUpdateAction()).isEqualTo(UpdateAction.IGNORE);
    userAssert(user.get(0).getData(), i);
  }

  @Test
  public void shouldExportData() throws IOException, BaseException {

    List<User> list = Lists.newArrayList();
    for (int i = 1; i < 10; i++) {
      list.add(user(i));
    }
    userService.bulkUpsert(list);

    String fileName = fileName();
    OutputStream writer = new FileOutputStream(new File(fileName));
    userService.exportData(userExcelMapper, null, writer);
  }

  @Test
  public void shouldImportData() throws Exception {

    mongo.save(Role.builder().name("ADMIN").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").slug("R2").build(), "role");

    String fileName = "/test.xlsx";

    InputStream stream = this.getClass().getResourceAsStream(fileName);
    Map<ImportOperation, ImportLogDetailsDto> importData = userService.importData(stream, userExcelMapper);
    System.out.println(importData);

  }

  @Test
  public void shouldFetchRoleValues() {

    mongo.save(Role.builder().name("ADMIN").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").slug("R2").build(), "role");

    for (int i = 1; i <= 10; i++) {
      userService.save(user(i));
    }
    List<KeyLabelDto> list = userService.distinctColumnValues("roleSlug", null);
    assertThat(list.size()).isEqualTo(3);
    assertThat(list.get(1).getLabel()).isEqualTo("ADMIN");
    assertThat(list.get(1).getKey()).isEqualTo("R1");
    assertThat(list.get(2).getLabel()).isEqualTo("USER");
    assertThat(list.get(2).getKey()).isEqualTo("R2");
  }

  @Test
  public void shouldFindDistinctColumnValues() {
    mongo.save(Role.builder().name("ADMIN").group("READ_WRITE").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").group("READ_ONLY").slug("R2").build(), "role");

    for (int i = 1; i <= 10; i++) {
      userService.save(user(i));
    }
    List<KeyLabelDto> values = userService.distinctColumnValues("roleGroup", null);
    assertThat(values.size()).isEqualTo(3);
    assertThat(values.get(2).getKey()).isEqualTo("R1");
  }

  @Test
  public void shouldFilterOnDate() {
    mongo.save(Role.builder().name("ADMIN").group("READ_WRITE").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").group("READ_ONLY").slug("R2").build(), "role");
    for (int i = 1; i <= 10; i++) {
      userService.save(user(i));
    }

    Map<String, ColumnFilter> filterModel = ImmutableMap.<String, ColumnFilter>builder()
      .put("dateCreated", DateColumnFilter.builder()
        .type(AppConstants.EQUALS)
        .dateFrom(new Date()).build())
      .build();

    FilterRequestDto filters = FilterRequestDto.builder()
      .page(0)
      .size(10)
      .filterModel(filterModel)
      .build();
    Page<User> users = userService.filter(filters);
    List<User> list = users.getContent();
    assertThat(list.size()).isEqualTo(10);

  }

  @Test
  public void shouldFilterOnJoinColumns() {

    mongo.save(Role.builder().name("ADMIN").group("READ_WRITE").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").group("READ_ONLY").slug("R2").build(), "role");

    for (int i = 1; i <= 10; i++) {
      userService.save(user(i));
    }

    Map<String, ColumnFilter> filterModel = ImmutableMap.<String, ColumnFilter>builder()
      .put("_", SearchColumnFilter.builder().filter("READ_WRITE").build())
      .build();

    FilterRequestDto filters = FilterRequestDto.builder()
      .page(0)
      .size(10)
      .filterModel(filterModel)
      .build();
    Page<User> users = userService.filter(filters);
    List<User> list = users.getContent();
    assertThat(list.size()).isEqualTo(1);
    assertThat(list.get(0).getRoleSlug()).isEqualTo("R1");
  }

  private String fileName() {
    String currentUsersHomeDir = System.getProperty("user.home");
    String otherFolder = currentUsersHomeDir + File.separator + "Desktop" + File.separator + "test.xlsx";
    return otherFolder;
  }

  private User user(int i) {
    Address address = Address.builder().city("city" + i).state("state" + i).country("country" + i).build();
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i)
      .gender(Gender.values()[i % Gender.values().length]).flag(true).customId(Long.valueOf(i))
      .roleSlug(i == 1 ? "R1" : "R2")
      .address(address)
      .build();
  }

  private User userWithNullAddress(int i) {
    Address address = Address.builder().city("city" + i).build();
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i).flag(true).customId(Long.valueOf(i))
      .address(address).build();
  }

  protected void userAssert(User user, int i) {
    entityAssert(user, i);
    assertThat(user.getEmail()).isEqualTo("email" + i + "@gmail.com");
    assertThat(user.getName()).isEqualTo("Name" + i);
    assertThat(user.getFlag()).isTrue();
    assertThat(user.getCustomId()).isEqualTo(Long.valueOf(i));
  }

}
