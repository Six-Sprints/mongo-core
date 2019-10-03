package com.sixsprints.core.generic.delete;

import java.util.List;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.read.ReadAbstractService;

public abstract class DeleteAbstractService<T extends AbstractMongoEntity> extends ReadAbstractService<T>
  implements GenericDeleteService<T> {

  public void delete(String id) throws EntityNotFoundException {

  }

  public void delete(T entity) {

  }

  public void delete(List<T> entities) {

  }

  public void softDelete(String id) throws EntityNotFoundException {

  }

  public void softDelete(T entity) {

  }

  public void softDelete(List<String> ids) {

  }

}
