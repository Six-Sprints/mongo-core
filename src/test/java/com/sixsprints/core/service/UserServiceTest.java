package com.sixsprints.core.service;

import static org.assertj.core.api.Assertions.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoOperations;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.mock.domain.Role;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.enums.Gender;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.utils.AppConstants;

public class UserServiceTest extends ApplicationTests {

  @Autowired
  private UserService userService;

  @Autowired
  private MongoOperations mongo;

  @Test
  public void testSave() throws EntityAlreadyExistsException, EntityInvalidException {
    User user = user(1);
    user = userService.insertOne(user);
    userAssert(user, 1);
  }

  @Test
  public void testFindAllWithConverters()
      throws EntityAlreadyExistsException, EntityInvalidException {
    testSave();
    List<User> findAll = userService.findAllList();
    userAssert(findAll.get(0), 1);
  }

  @Test
  public void testSaveTwiceAndSlugMustNotChange()
      throws EntityAlreadyExistsException, EntityInvalidException {
    User user = user(1);
    user = userService.insertOne(user);
    user = userService.upsertOne(user);
    userAssert(user, 1);
  }

  @Test
  public void testSaveAll() throws EntityAlreadyExistsException, EntityInvalidException {
    List<User> list = Lists.newArrayList();
    for (int i = 1; i < 10; i++) {
      list.add(user(i));
    }
    List<User> users = userService.bulkInsert(list);
    int i = 1;
    for (User user : users) {
      userAssert(user, i++);
    }
  }

  @Test
  public void shouldFilterOnDate() throws EntityAlreadyExistsException, EntityInvalidException {
    mongo.save(Role.builder().name("ADMIN").group("READ_WRITE").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").group("READ_ONLY").slug("R2").build(), "role");
    for (int i = 1; i <= 10; i++) {
      userService.insertOne(user(i));
    }

    Map<String, ColumnFilter> filterModel = ImmutableMap.<String, ColumnFilter>builder()
        .put("dateCreated",
            DateColumnFilter.builder().type(AppConstants.EQUALS).dateFrom(new Date()).build())
        .build();

    FilterRequestDto filters =
        FilterRequestDto.builder().page(0).size(10).filterModel(filterModel).build();
    Page<User> users = userService.filterByFilterRequestDto(filters);
    List<User> list = users.getContent();
    assertThat(list.size()).isEqualTo(10);

  }

  @Test
  public void shouldFilterOnArrayColumns()
      throws EntityAlreadyExistsException, EntityInvalidException {

    mongo.save(Role.builder().name("ADMIN").group("READ_WRITE").slug("R1").build(), "role");
    mongo.save(Role.builder().name("USER").group("READ_ONLY").slug("R2").build(), "role");

    for (int i = 1; i <= 10; i++) {
      userService.insertOne(user(i));
    }

    Map<String, ColumnFilter> filterModel = ImmutableMap.<String, ColumnFilter>builder()
        .put("tags", SetColumnFilter.builder().values(List.<String>of("", "2")).build()).build();

    FilterRequestDto filters =
        FilterRequestDto.builder().page(0).size(10).filterModel(filterModel).build();
    Page<User> users = userService.filterByFilterRequestDto(filters);
    List<User> list = users.getContent();
    assertThat(list.size()).isEqualTo(6);
    userAssert(list.get(0), list.get(0).getCustomId().intValue());
  }

  private User user(int i) {
    Address address =
        Address.builder().city("city" + i).state("state" + i).country("country" + i).build();
    return User.builder().email("email" + i + "@gmail.com").name("Name" + i)
        .gender(Gender.values()[i % Gender.values().length]).flag(true).customId(Long.valueOf(i))
        .roleSlug(i == 1 ? "R1" : "R2").address(address)
        .tags(i % 2 == 0 ? List.of("" + i) : new ArrayList<>()).build();
  }

  protected void userAssert(User user, int i) {
    entityAssert(user, i);
    assertThat(user.getEmail()).isEqualTo("email" + i + "@gmail.com");
    assertThat(user.getName()).isEqualTo("Name" + i);
    assertThat(user.getFlag()).isTrue();
    assertThat(user.getCustomId()).isEqualTo(Long.valueOf(i));
  }

}
