package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.repository.UserRepository;
import com.sixsprints.core.mock.service.UserService;
import com.sixsprints.core.service.AbstractCrudService;

@Service
public class UserServiceImpl extends AbstractCrudService<User> implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  protected UserRepository repository() {
    return userRepository;
  }

  @Override
  protected MetaData<User> metaData() {
    return MetaData.<User>builder().classType(User.class).build();
  }

  @Override
  protected User findDuplicate(User entity) {
    return userRepository.findByEmail(entity.getEmail());
  }

  @Override
  protected void enhanceEntity(User entity) {
    if (entity.getFlag() == null) {
      entity.setFlag(true);
    }
  }

}
