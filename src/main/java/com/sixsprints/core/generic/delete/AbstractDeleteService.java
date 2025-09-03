package com.sixsprints.core.generic.delete;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.generic.read.AbstractReadService;

public abstract class AbstractDeleteService<T extends AbstractMongoEntity>
    extends AbstractReadService<T> implements GenericDeleteService<T> {

  @Override
  public long deleteOneById(String id) {
    assertValid(id != null, "id", id);
    return deleteOneByCriteria(Criteria.where(AbstractMongoEntity.Fields.id).is(id));
  }

  @Override
  public long deleteOneBySlug(String slug) {
    assertValid(slug != null, "slug", slug);
    return deleteOneByCriteria(Criteria.where(AbstractMongoEntity.Fields.slug).is(slug));
  }

  @Override
  public long deleteOneByCriteria(Criteria criteria) {
    assertValid(criteria != null, "criteria", criteria);
    Query query = new Query(criteria);
    T entity = mongo.findAndRemove(query, metaData().getClassType());
    return Optional.ofNullable(entity).map(e -> 1).orElse(0);
  }

  @Override
  public long bulkDeleteById(List<String> ids) {
    assertValid(ids != null, "ids", ids);
    Criteria criteria = Criteria.where(AbstractMongoEntity.Fields.id).in(ids);
    return bulkDeleteByCriteria(criteria);
  }

  @Override
  public long bulkDeleteBySlug(List<String> slugs) {
    assertValid(slugs != null, "slugs", slugs);
    Criteria criteria = Criteria.where(AbstractMongoEntity.Fields.slug).in(slugs);
    return bulkDeleteByCriteria(criteria);
  }

  @Override
  public long bulkDeleteByCriteria(Criteria criteria) {
    assertValid(criteria != null, "criteria", criteria);
    Query query = new Query(criteria);
    return mongo.remove(query, metaData().getClassType()).getDeletedCount();
  }

}
