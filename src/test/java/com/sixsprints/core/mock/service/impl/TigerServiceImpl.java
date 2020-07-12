package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.mock.domain.inheritance.Tiger;
import com.sixsprints.core.mock.repository.inheritance.GenericAnimalRepository;
import com.sixsprints.core.mock.repository.inheritance.TigerRepository;
import com.sixsprints.core.mock.service.TigerService;
import com.sixsprints.core.mock.util.AnimalFieldData;

@Service("tiger")
public class TigerServiceImpl extends AnimalAbstractService<Tiger> implements TigerService {

  @Autowired
  private TigerRepository repository;

  @Override
  protected GenericAnimalRepository<Tiger> repository() {
    return repository;
  }

  @Override
  protected MetaData<Tiger> metaData() {
    return MetaData.<Tiger>builder().classType(Tiger.class).fields(AnimalFieldData.fields()).build();
  }

}
