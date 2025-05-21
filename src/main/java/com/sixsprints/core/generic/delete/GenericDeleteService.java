package com.sixsprints.core.generic.delete;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.sixsprints.core.domain.AbstractMongoEntity;

public interface GenericDeleteService<T extends AbstractMongoEntity> {

  void delete(String id);

  void delete(T entity);

  void delete(List<String> ids);

  void softDelete(String id);

  void softDelete(T entity);

  void softDelete(List<String> ids);

  void deleteBySlug(String slug);

  void deleteBySlug(List<String> slug);

  void deleteByCriteria(Criteria criteria);

}
