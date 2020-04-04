package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.mock.domain.inheritance.Parrot;
import com.sixsprints.core.mock.repository.inheritance.GenericAnimalRepository;
import com.sixsprints.core.mock.repository.inheritance.ParrotRepository;
import com.sixsprints.core.mock.service.ParrotService;

@Service("parrot")
public class ParrotServiceImpl extends AnimalAbstractService<Parrot> implements ParrotService {

  @Autowired
  private ParrotRepository repository;

  @Override
  protected GenericAnimalRepository<Parrot> repository() {
    return repository;
  }

  @Override
  protected MetaData<Parrot> metaData() {
    return MetaData.<Parrot>builder().classType(Parrot.class).build();
  }

}
