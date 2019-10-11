package com.sixsprints.core.mock.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.GenericRepository;
import com.sixsprints.core.generic.create.AbstractCreateService;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.repository.UserRepository;
import com.sixsprints.core.mock.service.UserCreateService;

@Service
public class UserCreateServiceImpl extends AbstractCreateService<User> implements UserCreateService {

  @Autowired
  private UserRepository userRepository;

  @Override
  protected GenericRepository<User> repository() {
    return userRepository;
  }

  @Override
  protected MetaData<User> metaData(User entity) {
    return MetaData.<User>builder().collection("user").prefix("U").build();
  }

  @Override
  protected User findDuplicate(User entity) {
    return null;
  }

  @Override
  public Page<User> findAll(Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<User> findAll(int page, int size) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<User> findAllActive(Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<User> findAllActive(int page, int size) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> findAllActive() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User findOne(String id) throws EntityNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User findBySlug(String slug) throws EntityNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<User> findAllLike(User example, Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<User> findAllLike(User example) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User findOneLike(User example) {
    // TODO Auto-generated method stub
    return null;
  }

}
