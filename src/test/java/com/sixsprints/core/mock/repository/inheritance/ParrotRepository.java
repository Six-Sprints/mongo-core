package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;

import com.sixsprints.core.mock.domain.inheritance.Parrot;

@JaversSpringDataAuditable
public interface ParrotRepository extends GenericAnimalRepository<Parrot> {

}
