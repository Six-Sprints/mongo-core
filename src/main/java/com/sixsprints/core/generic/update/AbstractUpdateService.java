package com.sixsprints.core.generic.update;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import com.mongodb.client.result.UpdateResult;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.create.AbstractCreateService;
import com.sixsprints.core.utils.ApplicationContext;
import com.sixsprints.core.utils.BeanWrapperUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUpdateService<T extends AbstractMongoEntity>
    extends AbstractCreateService<T> implements GenericUpdateService<T> {

  @Override
  public T updateOneById(String id, T entity)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneById(id).orElseThrow(() -> notFoundException(id));
    enhanceEntity(entity);
    return update(entity, entityFromDb);
  }

  @Override
  public T updateOneBySlug(String slug, T entity)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneBySlug(slug).orElseThrow(() -> notFoundException(slug));
    enhanceEntity(entity);
    return update(entity, entityFromDb);
  }

  @Override
  public T updateOneByCriteria(Criteria criteria, T entity)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneByCriteria(criteria).orElseThrow(() -> notFoundExceptionCriteria());
    enhanceEntity(entity);
    return update(entity, entityFromDb);
  }

  @Override
  public T patchUpdateOneById(String id, T entity, String propChanged)
      throws EntityNotFoundException, EntityInvalidException {
    assertValid(propChanged != null, "propChanged", propChanged);
    return patchUpdateOneById(id, entity, List.of(propChanged));
  }

  @Override
  public T patchUpdateOneById(String id, T entity, List<String> propsChanged)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneById(id).orElseThrow(() -> notFoundException(id));
    patchUpdateOne(Criteria.where(AbstractMongoEntity.Fields.id).is(id), entity, entityFromDb,
        propsChanged);
    return entity;
  }

  @Override
  public T patchUpdateOneBySlug(String slug, T entity, String propChanged)
      throws EntityNotFoundException, EntityInvalidException {
    assertValid(propChanged != null, "propChanged", propChanged);
    return patchUpdateOneBySlug(slug, entity, List.of(propChanged));
  }

  @Override
  public T patchUpdateOneBySlug(String slug, T entity, List<String> propsChanged)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneBySlug(slug).orElseThrow(() -> notFoundException(slug));
    patchUpdateOne(Criteria.where(AbstractMongoEntity.Fields.slug).is(slug), entity, entityFromDb,
        propsChanged);
    return entity;
  }

  @Override
  public T patchUpdateOneByCriteria(Criteria criteria, T entity, String propChanged)
      throws EntityNotFoundException, EntityInvalidException {
    assertValid(propChanged != null, "propChanged", propChanged);
    return patchUpdateOneByCriteria(criteria, entity, List.of(propChanged));
  }

  @Override
  public T patchUpdateOneByCriteria(Criteria criteria, T entity, List<String> propsChanged)
      throws EntityNotFoundException, EntityInvalidException {
    T entityFromDb = findOneByCriteria(criteria).orElseThrow(() -> notFoundExceptionCriteria());
    patchUpdateOne(criteria, entity, entityFromDb, propsChanged);
    return entity;
  }

  @Override
  public long bulkPatchUpdateByCriteria(Criteria criteria, T entity, String propChanged) {
    return bulkPatchUpdateByCriteria(criteria, entity, List.of(propChanged));
  }

  @Override
  public long bulkPatchUpdateByCriteria(Criteria criteria, T entity, List<String> propsChanged) {
    assertValid(criteria != null, "criteria", criteria);
    assertValid(entity != null, metaData().getClassType().getSimpleName(), entity);
    assertValid(propsChanged != null, "propsChanged", propsChanged);
    Update update = preparePatchUpdate(entity, propsChanged);
    return mongo.updateMulti(Query.query(criteria), update, metaData().getClassType())
        .getMatchedCount();
  }

  private UpdateResult patchUpdateOne(Criteria criteria, T entity, T entityFromDb,
      List<String> propsChanged) throws EntityInvalidException {
    assertValid(criteria != null, "criteria", criteria);
    assertValid(entity != null, metaData().getClassType().getSimpleName(), entity);
    assertValid(propsChanged != null, "propsChanged", propsChanged);
    Update update = preparePatchUpdate(entity, propsChanged);
    BeanWrapperUtil.copyProperties(entity, entityFromDb, propsChanged);
    preUpdateCheck(entity);
    return mongo.updateFirst(Query.query(criteria), update, metaData().getClassType());
  }

  private Update preparePatchUpdate(T entity, List<String> propsChanged) {
    List<String> propsChangedWithAudit = new ArrayList<>(propsChanged);
    entity.setDateModified(System.currentTimeMillis());
    entity.setLastModifiedBy(userAuditField(ApplicationContext.getCurrentUser()));
    propsChangedWithAudit.add(AbstractMongoEntity.Fields.dateModified);
    propsChangedWithAudit.add(AbstractMongoEntity.Fields.lastModifiedBy);

    Update update = new Update();
    for (String prop : propsChangedWithAudit) {
      update.set(prop, BeanWrapperUtil.getValue(entity, prop));
    }
    return update;
  }

  protected String userAuditField(AbstractMongoEntity currentUser) {
    return currentUser.getSlug();
  }

  @Override
  public T upsertOne(T entity) throws EntityInvalidException {
    assertValid(entity != null, metaData().getClassType().getSimpleName(), entity);
    enhanceEntity(entity);
    T entityFromDb = findDuplicate(entity);
    if (entityFromDb != null) {
      return update(entity, entityFromDb);
    }
    preInsert(entity);
    List<String> errors = checkValidity(entity);
    if (!CollectionUtils.isEmpty(errors)) {
      throw validationException(errors);
    }
    generateSlugIfRequired(entity);
    entity = repository().insert(entity);
    postInsert(entity);
    return entity;
  }

  @Override
  @SuppressWarnings("null")
  public List<T> bulkUpsert(List<T> list) throws EntityInvalidException {
    assertValid(list != null, metaData().getClassType().getSimpleName(), list);
    List<T> entities = new ArrayList<>();
    for (T entity : list) {
      entities.add(upsertOne(entity));
    }
    return entities;
  }

  private void preUpdateCheck(T entity) throws EntityInvalidException {
    List<String> errors = checkValidity(entity);
    if (!CollectionUtils.isEmpty(errors)) {
      throw validationException(errors);
    }
    List<String> updateErrors = checkValidityPreUpdate(entity);
    if (!CollectionUtils.isEmpty(updateErrors)) {
      throw validationException(updateErrors);
    }
  }

  protected List<String> checkValidityPreUpdate(T entity) {
    return new ArrayList<>();
  }

  @SuppressWarnings("null")
  private T update(T entity, T entityFromDb) throws EntityInvalidException {
    assertValid(entity != null, metaData().getClassType().getSimpleName(), entity);
    preUpdate(entityFromDb, entity);
    preUpdateCheck(entity);
    entity.copyEntityFrom(entityFromDb);
    repository().save(entity);
    postUpdate(entity);
    return entity;
  }

}
