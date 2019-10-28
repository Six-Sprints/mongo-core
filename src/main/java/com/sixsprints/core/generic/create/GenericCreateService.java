package com.sixsprints.core.generic.create;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.ImportResponseWrapper;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;

public interface GenericCreateService<T extends AbstractMongoEntity> {

  T save(T entity);

  List<T> saveAll(List<T> entities);

  List<T> saveAllWithHooks(List<T> entities);

  T create(T entity) throws EntityAlreadyExistsException, EntityInvalidException;

  <E> ImportResponseWrapper<E> importData(InputStream inputStream, Locale locale)
    throws IOException, BaseException;

}
