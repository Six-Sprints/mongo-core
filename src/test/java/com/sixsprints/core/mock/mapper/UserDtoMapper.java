package com.sixsprints.core.mock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.sixsprints.core.mapper.GenericCrudMapper;
import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserDtoMapper extends GenericCrudMapper<User, UserDto> {

  @Override
  public abstract User toDomain(UserDto dto);

  @Override
  public abstract UserDto toDto(User domain);

}
