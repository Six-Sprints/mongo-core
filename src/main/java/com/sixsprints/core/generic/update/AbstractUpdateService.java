package com.sixsprints.core.generic.update;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.create.AbstractCreateService;
import com.sixsprints.core.utils.BeanWrapperUtil;

public abstract class AbstractUpdateService<T extends AbstractMongoEntity> extends AbstractCreateService<T>
  implements GenericUpdateService<T> {

  @Override
  public T update(String id, T domain) throws EntityNotFoundException, EntityAlreadyExistsException {
    T entity = findOne(id);
    domain.copyEntityFrom(entity);
    return update(domain);
  }

  @Override
  public T patchUpdate(String id, T domain, String propChanged)
    throws EntityNotFoundException, EntityAlreadyExistsException {

    T entity = findOne(id);
    BeanWrapperUtil.copyProperties(domain, entity, ImmutableList.<String>of(propChanged));

    return update(entity);
  }

  @Override
  public List<BulkUpdateInfo<T>> bulkImport(List<T> list) {
    List<BulkUpdateInfo<T>> updateInfo = Lists.newArrayList();
    if (CollectionUtils.isEmpty(list)) {
      return updateInfo;
    }
    for (T domain : list) {
      updateInfo.add(saveOneWhileBulkImport(domain));
    }
    return updateInfo;
  }

  protected BulkUpdateInfo<T> saveOneWhileBulkImport(T domain) {
    if (isInvalid(domain)) {
      return BulkUpdateInfo.<T>builder().updateAction(UpdateAction.INVALID).build();
    }
    return saveOrOverwrite(domain);
  }

  private BulkUpdateInfo<T> saveOrOverwrite(T domain) {
    T fromDB = findDuplicate(domain);
    if (fromDB != null) {
      if (!fromDB.getActive()) {
        delete(fromDB);
      } else {
        Boolean active = domain.getActive();
        domain.copyEntityFrom(fromDB);
        domain.setActive(active);

        T copy = clone(fromDB);
        if (metaData().isIgnoreNullWhileBulkUpdate()) {
          copyNonNullValues(domain, fromDB);
        } else {
          fromDB = domain;
        }

        if (checkEquals(fromDB, copy)) {
          return BulkUpdateInfo.<T>builder().updateAction(UpdateAction.IGNORE).data(fromDB).build();
        }
        preUpdate(fromDB);
        fromDB = save(fromDB);
        postUpdate(fromDB);
        return BulkUpdateInfo.<T>builder().updateAction(UpdateAction.UPDATE).data(fromDB).build();
      }
    }
    preCreate(domain);
    domain = save(domain);
    postCreate(domain);
    return BulkUpdateInfo.<T>builder().updateAction(UpdateAction.CREATE).data(domain).build();
  }

  protected T clone(T domain) {
    return null;
  }

  protected boolean checkEquals(T obj1, T obj2) {
    if (obj1 == null && obj2 == null) {
      return true;
    }
    return obj1 != null && obj2 != null && obj1.equals(obj2);
  }

  protected void copyNonNullValues(T source, T target) {
    BeanWrapperUtil.copyNonNullProperties(source, target);
  }

  private T update(T domain) throws EntityAlreadyExistsException {
    T fromDB = findDuplicate(domain);
    if (fromDB != null && !domain.getId().equals(fromDB.getId())) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB);
      }
      delete(fromDB);
    }
    preUpdate(domain);
    save(domain);
    postUpdate(domain);
    return domain;
  }

}
