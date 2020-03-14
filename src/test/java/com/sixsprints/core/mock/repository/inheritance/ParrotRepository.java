package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.mock.domain.inheritance.Parrot;

@Repository
@JaversSpringDataAuditable
public interface ParrotRepository extends GenericAnimalRepository<Parrot> {

}
