package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.SlugFormatter;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.dto.UserExcelDto;
import com.sixsprints.core.mock.repository.UserRepository;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.mock.util.UserFieldData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.transformer.UserMapper;

@Service
public class UserServiceImpl extends AbstractCrudService<User> implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserMapper userMapper;

  @Override
  protected GenericRepository<User> repository() {
    return userRepository;
  }

  @Override
  protected MetaData<User> metaData() {
    return MetaData.<User>builder()
      .classType(User.class)
      .crudDtoClassType(UserDto.class)
      .exportDataClassType(UserExcelDto.class)
      .importDataClassType(UserExcelDto.class)
      .fields(UserFieldData.fields())
      .build();
  }

  @Override
  protected SlugFormatter slugFromatter(User entity) {
    return SlugFormatter.builder().collection("user").prefix("U").build();
  }

  @Override
  protected User findDuplicate(User entity) {
    return userRepository.findByEmail(entity.getEmail());
  }

  @Override
  protected void preSave(User entity) {
    if (entity.getFlag() == null) {
      entity.setFlag(true);
    }
  }

  // Required to properly ignore non-null values of the embedded objects.
  @Override
  protected void copyNonNullValues(User source, User target) {
    userMapper.copyNonNull(source, target);
  }

}
