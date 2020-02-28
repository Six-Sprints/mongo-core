package com.sixsprints.core.mock.repository.inheritance;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.mock.domain.inheritance.Tiger;

@Repository
@JaversSpringDataAuditable
public interface TigerRepository extends GenericAnimalRepository<Tiger> {

}
