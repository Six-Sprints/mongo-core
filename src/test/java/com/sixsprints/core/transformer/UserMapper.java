package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.sixsprints.core.mapper.GenericCrudMapper;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.dto.UserDto;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper extends GenericCrudMapper<User, UserDto> {

  @Override
  public abstract UserDto toDto(User entity);

  @Override
  public abstract User toDomain(UserDto dto);

  public abstract void copyNonNull(User src, @MappingTarget User target);

  public abstract void copyAddress(Address src, @MappingTarget Address target);

}
