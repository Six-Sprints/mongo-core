package com.sixsprints.core.generic.update;

import java.util.List;
import java.util.Map;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityNotFoundException;

public interface GenericUpdateService<T extends AbstractMongoEntity> {

  T update(String id, T domain) throws EntityNotFoundException;

  T patchUpdate(String id, T domain, String propChanged) throws EntityNotFoundException, EntityAlreadyExistsException;

  Boolean patchById(String id, Map<String, Object> values);

  Boolean patchBySlug(String slug, Map<String, Object> values);

  Boolean patch(T oldData, T newData);

  List<T> bulkImport(List<T> list);

}
