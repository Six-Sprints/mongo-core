package com.sixsprints.core.generic.update;

import java.util.List;
import java.util.Map;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.create.AbstractCreateService;

public abstract class AbstractUpdateService<T extends AbstractMongoEntity> extends AbstractCreateService<T>
  implements GenericUpdateService<T> {

  @Override
  public T update(String id, T domain) throws EntityNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T patchUpdate(String id, T domain, String propChanged)
    throws EntityNotFoundException, EntityAlreadyExistsException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean patchById(String id, Map<String, Object> values) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean patchBySlug(String slug, Map<String, Object> values) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean patch(T oldData, T newData) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> bulkImport(List<T> list) {
    // TODO Auto-generated method stub
    return null;
  }

}
