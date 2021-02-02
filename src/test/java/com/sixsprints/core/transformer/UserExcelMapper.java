package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.sixsprints.core.mock.domain.User;
import com.sixsprints.core.mock.dto.UserExcelDto;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserExcelMapper extends GenericMapper<User, UserExcelDto> {

  @Override
  public abstract UserExcelDto toDto(User entity);

  @Override
  public abstract User toDomain(UserExcelDto dto);

}
