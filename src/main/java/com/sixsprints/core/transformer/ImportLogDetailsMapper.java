package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.sixsprints.core.domain.ImportLogDetails;
import com.sixsprints.core.dto.ImportLogDetailsDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ImportLogDetailsMapper extends GenericMapper<ImportLogDetails, ImportLogDetailsDto> {

  public static ImportLogDetailsMapper INSTANCE = Mappers.getMapper(ImportLogDetailsMapper.class);

  @Override
  public abstract ImportLogDetailsDto toDto(ImportLogDetails category);

  @Override
  public abstract ImportLogDetails toDomain(ImportLogDetailsDto dto);

}
