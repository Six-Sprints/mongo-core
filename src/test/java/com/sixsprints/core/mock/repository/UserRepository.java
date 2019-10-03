package com.sixsprints.core.mock.repository;

import org.springframework.stereotype.Repository;

import com.sixsprints.core.generic.GenericRepository;
import com.sixsprints.core.mock.domain.User;

@Repository
public interface UserRepository extends GenericRepository<User> {

}
