package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;

import com.sixsprints.core.mock.domain.inheritance.Tiger;

@JaversSpringDataAuditable
public interface TigerRepository extends GenericAnimalRepository<Tiger> {

}
