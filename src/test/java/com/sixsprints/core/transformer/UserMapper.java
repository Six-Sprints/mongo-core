package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.dto.UserDto;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public abstract class UserMapper extends GenericTransformer<User, UserDto> {

  @Override
  public abstract UserDto toDto(User entity);

  @Override
  public abstract User toDomain(UserDto dto);

  public abstract void copyNonNull(User src, @MappingTarget User target);

  public abstract void copyAddress(Address src, @MappingTarget Address target);

}
