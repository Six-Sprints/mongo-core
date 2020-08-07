package com.sixsprints.core.transformer;

import org.mapstruct.Mapper;

import com.sixsprints.core.domain.ImportLogDetails;
import com.sixsprints.core.dto.ImportLogDetailsDto;

@Mapper(componentModel = "spring")
public abstract class ImportLogDetailsMapper extends GenericTransformer<ImportLogDetails, ImportLogDetailsDto> {

  @Override
  public abstract ImportLogDetailsDto toDto(ImportLogDetails importLogDetails);

  @Override
  public abstract ImportLogDetails toDomain(ImportLogDetailsDto dto);

}
