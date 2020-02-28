package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.mock.domain.inheritance.Animal;

@Repository
@JaversSpringDataAuditable
public interface AnimalRepository extends GenericAnimalRepository<Animal> {

}
