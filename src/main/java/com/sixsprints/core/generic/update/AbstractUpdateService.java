package com.sixsprints.core.generic.update;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.create.AbstractCreateService;
import com.sixsprints.core.utils.BeanWrapperUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    List<String> list = new ArrayList<>();
    list.add(propChanged);
    BeanWrapperUtil.copyProperties(domain, entity, list);

    return update(entity);
  }

  @Override
  public List<BulkUpdateInfo<T>> updateAll(List<T> list) {
    List<BulkUpdateInfo<T>> updateInfo = new ArrayList<>();
    if (CollectionUtils.isEmpty(list)) {
      return updateInfo;
    }
    for (T domain : list) {
      updateInfo.add(saveOneWhileBulkImport(domain));
    }
    return updateInfo;
  }

  @Override
  public T saveOrUpdate(T domain) throws EntityInvalidException {
    BulkUpdateInfo<T> updateInfo = saveOneWhileBulkImport(domain);
    if (UpdateAction.INVALID.equals(updateInfo.getUpdateAction())) {
      throw invalidException(domain);
    }
    return updateInfo.getData();
  }

  protected BulkUpdateInfo<T> saveOneWhileBulkImport(T domain) {
    if (isInvalid(domain)) {
      return bulkImportInfo(null, UpdateAction.INVALID);
    }
    return saveOrOverwrite(domain);
  }

  protected BulkUpdateInfo<T> saveOrOverwrite(T domain) {
    T fromDb = findDuplicate(domain);
    if (fromDb != null) {
      if (!fromDb.getActive()) {
        delete(fromDb);
      } else {
        Boolean active = domain.getActive();
        domain.copyEntityFrom(fromDb);
        domain.setActive(active);

        T copy = clone(fromDb);
        if (metaData().isIgnoreNullWhileBulkUpdate()) {
          copyNonNullValues(domain, fromDb);
        } else {
          fromDb = domain;
        }

        if (checkEquals(fromDb, copy)) {
          return bulkImportInfo(fromDb, UpdateAction.IGNORE);
        }

        preUpdate(fromDb);
        fromDb = save(fromDb);
        postUpdate(fromDb);
        return bulkImportInfo(fromDb, UpdateAction.UPDATE);
      }
    }
    preCreate(domain);
    domain = save(domain);
    postCreate(domain);
    return bulkImportInfo(domain, UpdateAction.CREATE);
  }

  @SuppressWarnings("unchecked")
  protected T clone(T domain) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(domain);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (T) ois.readObject();
    } catch (Exception e) {
      log.warn("Clone failed. Either override the clone method or mark {} as Serializable.",
        e.getMessage());
      return null;
    }
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

  private BulkUpdateInfo<T> bulkImportInfo(T fromDB, UpdateAction action) {
    return BulkUpdateInfo.<T>builder().updateAction(action).data(fromDB).build();
  }

}
