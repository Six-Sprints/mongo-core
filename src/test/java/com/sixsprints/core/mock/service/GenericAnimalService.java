package com.sixsprints.core.mock.service;

import java.util.List;

import com.sixsprints.core.mock.domain.inheritance.Animal;
import com.sixsprints.core.service.GenericCrudService;

public interface GenericAnimalService<T extends Animal> extends GenericCrudService<T> {

  List<T> findByCanFly(Boolean canFly);

}
