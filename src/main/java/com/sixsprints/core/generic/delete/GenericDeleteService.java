package com.sixsprints.core.generic.delete;

import java.util.List;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityNotFoundException;

public interface GenericDeleteService<T extends AbstractMongoEntity> {

  void delete(String id) throws EntityNotFoundException;

  void delete(T entity);

  void delete(List<T> entities);

  void softDelete(String id) throws EntityNotFoundException;

  void softDelete(T entity);

  void softDelete(List<String> ids);

}
