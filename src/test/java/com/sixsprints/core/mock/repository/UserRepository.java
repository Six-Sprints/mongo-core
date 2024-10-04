package com.sixsprints.core.mock.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;

import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.repository.GenericRepository;

@JaversSpringDataAuditable
public interface UserRepository extends GenericRepository<User> {

  User findByEmail(String email);

}
