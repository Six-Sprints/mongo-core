package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.dto.UserDto;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class UserMapper extends GenericTransformer<User, UserDto> {

  public static UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  public abstract void copyNonNull(User src, @MappingTarget User target);

  public abstract void copyAddress(Address src, @MappingTarget Address target);

  public abstract User clone(User src);

  public abstract Address clone(Address vendor);

}
