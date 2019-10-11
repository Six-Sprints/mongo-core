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

  public void delete(String id) {
    repository().deleteById(id);
  }

  public void delete(T entity) {
    repository().delete(entity);
  }

  public void delete(List<T> entities) {
    repository().deleteAll(entities);
  }

  public void softDelete(String id) {
    Criteria criteria = new Criteria(ID).is(id);
    softDeleteQuery(criteria);
  }

  public void softDelete(T entity) {
    softDelete(entity.getId());
  }

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
