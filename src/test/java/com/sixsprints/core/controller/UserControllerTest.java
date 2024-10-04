package com.sixsprints.core.controller;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsprints.core.BaseControllerTest;
import com.sixsprints.core.mock.dto.UserDto;

public class UserControllerTest extends BaseControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  public void shouldCreateUser() throws Exception {
    String email = "kgujral@gmail.com";
    String name = "Karan";

    String userJson = userJson(email, name);
    mvc.perform(
      MockMvcRequestBuilders.post("/api/v1/user")
        .content(userJson)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.status().isCreated())
      .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(Boolean.TRUE)))
      .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", CoreMatchers.is(name)))
      .andExpect(MockMvcResultMatchers.jsonPath("$.data.email", CoreMatchers.is(email)));
  }

  private String userJson(String email, String name) throws JsonProcessingException {
    return mapper.writeValueAsString(userDto(email, name));
  }

  private UserDto userDto(String email, String name) {
    return UserDto.builder().email(email).name(name).build();
  }

}
