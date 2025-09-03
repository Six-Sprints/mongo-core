package com.sixsprints.core.mock.repository.inheritance;

import java.util.List;

import com.sixsprints.core.mock.domain.inheritance.Animal;
import com.sixsprints.core.repository.GenericCrudRepository;

public interface GenericAnimalRepository<T extends Animal> extends GenericCrudRepository<T> {

  List<T> findByCanFly(Boolean canFly);

}
