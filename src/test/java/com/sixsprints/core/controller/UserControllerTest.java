package com.sixsprints.core.controller;

import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsprints.core.BaseControllerTest;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.ColumnFilterTypeEnum;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.dto.filter.SearchColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.utils.AuthUtil;

public class UserControllerTest extends BaseControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private UserService userService;

  @Test
  public void shouldCreateUser() throws Exception {
    String email = "kgujral@gmail.com";
    String name = "Karan";

    String userJson = userJson(email, name);
    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user").content(userJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", CoreMatchers.is(name)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(email)));
  }

  @Test
  public void shouldSearchUserByExactEmail() throws Exception {
    // First create a user
    String email = "test@example.com";
    String name = "Test User";
    createUser(email, name);

    User user = userService.findOneByCriteria(Criteria.where("email").is(email))
        .orElseThrow(() -> EntityNotFoundException.childBuilder()
            .error("User not found with email: " + email).build());
    String token = AuthUtil.createToken(user.getId());

    // Create exact match filter for email
    ExactMatchColumnFilter emailFilter = ExactMatchColumnFilter.builder().filter(email).build();
    emailFilter.setFilterType(ColumnFilterTypeEnum.EXACT);

    Map<String, ColumnFilter> filterModel = new HashMap<>();
    filterModel.put("email", emailFilter);

    FilterRequestDto filterRequest =
        FilterRequestDto.builder().page(0).size(10).filterModel(filterModel).build();

    String searchJson = mapper.writeValueAsString(filterRequest);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/search").content(searchJson)
        .contentType(MediaType.APPLICATION_JSON).header("X-AUTH-TOKEN", token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.data.content[0].email", CoreMatchers.is(email)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].name", CoreMatchers.is(name)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements", CoreMatchers.is(1)));
  }

  @Test
  public void shouldSearchUserByTextSearch() throws Exception {
    // First create a user
    String email = "john.doe@example.com";
    String name = "John Doe";
    createUser(email, name);

    User user = userService.findOneByCriteria(Criteria.where("email").is(email))
        .orElseThrow(() -> EntityNotFoundException.childBuilder()
            .error("User not found with email: " + email).build());

    String token = AuthUtil.createToken(user.getId());

    // Create text search filter for name
    SearchColumnFilter nameFilter = SearchColumnFilter.builder().filter("John").build();
    nameFilter.setFilterType(ColumnFilterTypeEnum.TEXT);

    Map<String, ColumnFilter> filterModel = new HashMap<>();
    filterModel.put("name", nameFilter);

    FilterRequestDto filterRequest =
        FilterRequestDto.builder().page(0).size(10).filterModel(filterModel).build();

    String searchJson = mapper.writeValueAsString(filterRequest);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/search").content(searchJson)
        .contentType(MediaType.APPLICATION_JSON).header("X-AUTH-TOKEN", token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].name", CoreMatchers.is(name)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements", CoreMatchers.is(1)));
  }

  @Test
  public void shouldSearchUserWithPagination() throws Exception {
    // Create multiple users
    createUser("user1@example.com", "User One");
    createUser("user2@example.com", "User Two");
    createUser("user3@example.com", "User Three");

    User user = userService.findOneByCriteria(Criteria.where("email").is("user1@example.com"))
        .orElseThrow(() -> EntityNotFoundException.childBuilder()
            .error("User not found with email: user1@example.com").build());
    String token = AuthUtil.createToken(user.getId());

    // Create text search filter
    SearchColumnFilter nameFilter = SearchColumnFilter.builder().filter("User").build();
    nameFilter.setFilterType(ColumnFilterTypeEnum.TEXT);

    Map<String, ColumnFilter> filterModel = new HashMap<>();
    filterModel.put("name", nameFilter);

    FilterRequestDto filterRequest = FilterRequestDto.builder().page(0).size(2) // Only 2 items per page
        .filterModel(filterModel).build();

    String searchJson = mapper.writeValueAsString(filterRequest);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/search").content(searchJson)
        .contentType(MediaType.APPLICATION_JSON).header("X-AUTH-TOKEN", token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.length()", CoreMatchers.is(2)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements", CoreMatchers.is(3)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPages", CoreMatchers.is(2)));
  }

  @Test
  public void shouldSearchUserWithSorting() throws Exception {
    // Create multiple users
    createUser("charlie@ex.com", "Charlie");
    createUser("alice@ex.com", "Alice");
    createUser("bob@ex.com", "Bob");

    User user = userService.findOneByCriteria(Criteria.where("email").is("alice@ex.com"))
        .orElseThrow(() -> EntityNotFoundException.childBuilder()
            .error("User not found with email: alice@ex.com").build());
    String token = AuthUtil.createToken(user.getId());

    // Create text search filter
    ExactMatchColumnFilter nameFilter = ExactMatchColumnFilter.builder().filter("Bob").build();
    nameFilter.setFilterType(ColumnFilterTypeEnum.EXACT);

    Map<String, ColumnFilter> filterModel = new HashMap<>();
    filterModel.put("name", nameFilter);

    // Create sort model for name ascending
    SortModel sortModel = SortModel.builder().colId("name").sort(Direction.ASC).build();

    FilterRequestDto filterRequest = FilterRequestDto.builder().page(0).size(10)
        .filterModel(filterModel).sortModel(java.util.Arrays.asList(sortModel)).build();

    String searchJson = mapper.writeValueAsString(filterRequest);
    System.out.println(searchJson);

    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/search").content(searchJson)
        .contentType(MediaType.APPLICATION_JSON).header("X-AUTH-TOKEN", token))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].name", CoreMatchers.is("Bob")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalElements", CoreMatchers.is(1)));
  }

  private String userJson(String email, String name) throws JsonProcessingException {
    return mapper.writeValueAsString(userDto(email, name));
  }

  private UserDto userDto(String email, String name) {
    return UserDto.builder().email(email).name(name).build();
  }

  private void createUser(String email, String name) throws Exception {
    String userJson = userJson(email, name);
    mvc.perform(MockMvcRequestBuilders.post("/api/v1/user").content(userJson)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

}
