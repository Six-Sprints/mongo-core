package com.sixsprints.core.controller;

import com.sixsprints.core.domain.ImportLogDetails;
import com.sixsprints.core.dto.ImportLogDetailsDto;
import com.sixsprints.core.service.ImportLogDetailsService;
import com.sixsprints.core.transformer.ImportLogDetailsMapper;

public abstract class AbstractImportLogDetailsController
  extends AbstractReadController<ImportLogDetails, ImportLogDetailsDto> {

  protected ImportLogDetailsMapper mapper;

  public AbstractImportLogDetailsController(ImportLogDetailsService service) {
    super(service, ImportLogDetailsMapper.INSTANCE);
    this.mapper = ImportLogDetailsMapper.INSTANCE;
  }

}
