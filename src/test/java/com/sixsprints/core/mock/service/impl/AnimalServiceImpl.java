package com.sixsprints.core.mock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.mock.domain.inheritance.Animal;
import com.sixsprints.core.mock.repository.inheritance.AnimalRepository;
import com.sixsprints.core.mock.repository.inheritance.GenericAnimalRepository;
import com.sixsprints.core.mock.service.GenericAnimalService;

@Service("animal")
public class AnimalServiceImpl extends AnimalAbstractService<Animal> implements GenericAnimalService<Animal> {

  @Autowired
  private AnimalRepository animalRepository;

  @Override
  protected GenericAnimalRepository<Animal> repository() {
    return animalRepository;
  }

  @Override
  protected MetaData<Animal> metaData() {
    return MetaData.<Animal>builder().classType(Animal.class).build();
  }

}
