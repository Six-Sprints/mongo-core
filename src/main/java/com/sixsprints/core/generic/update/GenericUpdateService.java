package com.sixsprints.core.generic.update;

import java.util.List;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;

public interface GenericUpdateService<T extends AbstractMongoEntity> {

  T update(String id, T domain) throws EntityNotFoundException, EntityAlreadyExistsException;

  T patchUpdate(String id, T domain, String propChanged) throws EntityNotFoundException, EntityAlreadyExistsException;

  List<BulkUpdateInfo<T>> updateAll(List<T> list);

  T saveOrUpdate(T domain) throws EntityInvalidException;

}
