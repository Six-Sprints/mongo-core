package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;

import com.sixsprints.core.mock.domain.inheritance.Animal;

@JaversSpringDataAuditable
public interface AnimalRepository extends GenericAnimalRepository<Animal> {

}
