package com.sixsprints.core.mock.service.impl;

import java.util.List;

import com.sixsprints.core.mock.domain.inheritance.Animal;
import com.sixsprints.core.mock.repository.inheritance.GenericAnimalRepository;
import com.sixsprints.core.mock.service.GenericAnimalService;
import com.sixsprints.core.service.AbstractCrudService;

public abstract class AnimalAbstractService<T extends Animal> extends AbstractCrudService<T>
  implements GenericAnimalService<T> {

  @Override
  protected abstract GenericAnimalRepository<T> repository();

  @Override
  protected T findDuplicate(T entity) {
    return repository().findBySlug(entity.getSlug());
  }

  @Override
  public List<T> findByCanFly(Boolean canFly) {
    return repository().findByCanFly(canFly);
  }

}
