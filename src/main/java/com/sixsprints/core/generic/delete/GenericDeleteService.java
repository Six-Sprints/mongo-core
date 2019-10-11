package com.sixsprints.core.generic.delete;

import java.util.List;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface GenericDeleteService<T extends AbstractMongoEntity> {

  void delete(String id);

  void delete(T entity);

  void delete(List<T> entities);

  void softDelete(String id);

  void softDelete(T entity);

  void softDelete(List<String> ids);

}
