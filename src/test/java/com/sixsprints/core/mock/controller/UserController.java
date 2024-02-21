package com.sixsprints.core.mock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.mapper.UserDtoMapper;
import com.sixsprints.core.mock.service.UserService;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController extends AbstractCrudController<User, UserDto, UserDto, UserDto> {

  public UserController(UserService crudService, UserDtoMapper mapper) {
    super(crudService, mapper, mapper, mapper);
  }

}
