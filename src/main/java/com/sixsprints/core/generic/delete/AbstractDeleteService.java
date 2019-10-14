package com.sixsprints.core.generic.delete;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.generic.read.AbstractReadService;

public abstract class AbstractDeleteService<T extends AbstractMongoEntity> extends AbstractReadService<T>
  implements GenericDeleteService<T> {

  private static final String ACTIVE = "active";

  private static final String ID = "id";

  @Override
  public void delete(String id) {
    repository().deleteById(id);
  }

  @Override
  public void delete(T entity) {
    repository().delete(entity);
  }

  @Override
  public void delete(List<String> ids) {
    Criteria criteria = new Criteria(ID).in(ids);
    Query query = new Query(criteria);
    mongo.remove(query, metaData().getClassType());
  }

  @Override
  public void softDelete(T entity) {
    softDelete(entity.getId());
  }

  @Override
  public void softDelete(String id) {
    Criteria criteria = new Criteria(ID).is(id);
    softDeleteQuery(criteria);
  }

  @Override
  public void softDelete(List<String> ids) {
    Criteria criteria = new Criteria(ID).in(ids);
    softDeleteQuery(criteria);
  }

  private void softDeleteQuery(Criteria criteria) {
    Query query = new Query(criteria);
    Update update = new Update().set(ACTIVE, Boolean.FALSE);
    mongo.updateMulti(query, update, metaData().getClassType());
  }

}
