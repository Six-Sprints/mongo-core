package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  void copyNonNull(User src, @MappingTarget User target);

  void copyAddress(Address src, @MappingTarget Address target);

  User clone(User src);

  Address clone(Address vendor);

}
