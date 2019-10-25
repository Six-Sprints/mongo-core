package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.generic.GenericRepository;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;
import com.sixsprints.core.mock.repository.UserRepository;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.mock.util.UserFieldData;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.transformer.UserMapper;

@Service
public class UserServiceImpl extends AbstractCrudService<User> implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  protected GenericRepository<User> repository() {
    return userRepository;
  }

  @Override
  protected MetaData<User> metaData(User entity) {
    return MetaData.<User>builder().collection("user").prefix("U")
      .classType(User.class).dtoClassType(UserDto.class)
      .fields(UserFieldData.fields())
      .build();
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

  @Override
  protected void copyNonNullValues(User source, User target) {
    UserMapper.INSTANCE.copyNonNull(source, target);
  }

}
