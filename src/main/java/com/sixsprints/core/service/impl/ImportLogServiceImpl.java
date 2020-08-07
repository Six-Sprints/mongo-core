package com.sixsprints.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.domain.ImportLogDetails;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.ImportLogRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.service.ImportLogService;

@Service
public class ImportLogServiceImpl extends AbstractCrudService<ImportLogDetails> implements ImportLogService {

  @Autowired
  private ImportLogRepository importLogRepository;

  @Override
  protected ImportLogRepository repository() {
    return importLogRepository;
  }

  @Override
  protected MetaData<ImportLogDetails> metaData() {
    return MetaData.<ImportLogDetails>builder().classType(ImportLogDetails.class).dtoClassType(ImportLogDetails.class)
      .build();
  }

  @Override
  protected ImportLogDetails findDuplicate(ImportLogDetails entity) {
    return importLogRepository.findBySlug(entity.getSlug());
  }

}
