package com.sixsprints.core.mock.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.generic.GenericRepository;
import com.sixsprints.core.mock.domain.User;

@Repository
@JaversSpringDataAuditable
public interface UserRepository extends GenericRepository<User> {

  User findByEmail(String email);

}
