package com.sixsprints.core.mock.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.auth.BasicAuth;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.mapper.UserDtoMapper;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController extends AbstractCrudController<User, UserDto, UserDto, UserDto> {

  private final UserService crudService;

  private final UserDtoMapper mapper;

  public UserController(UserService crudService, UserDtoMapper mapper) {
    super(crudService, mapper, mapper, mapper);
    this.crudService = crudService;
    this.mapper = mapper;
  }

  @PostMapping
  @BasicAuth(permission = BasicPermissionEnum.CREATE, required = false)
  public ResponseEntity<RestResponse<UserDto>> add(@RequestBody @Validated UserDto dto)
    throws BaseException {
    return RestUtil.successResponse(mapper.toDto(crudService.create(mapper.toDomain(dto))), HttpStatus.CREATED);
  }

}
