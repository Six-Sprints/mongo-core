
package com.sixsprints.core.generic;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;

@Deprecated
public interface GenericService<T extends AbstractMongoEntity> {

  Page<T> findAll(Pageable page);

  Page<T> findAll(int page, int size);

  List<T> findAll();

  Page<T> findAllActive(Pageable page);

  Page<T> findAllActive(int page, int size);

  List<T> findAllActive();

  T findOne(String id) throws EntityNotFoundException;

  T findBySlug(String slug) throws EntityNotFoundException;

  Page<T> findAllLike(T example, Pageable page);

  List<T> findAllLike(T example);

  T findOneLike(T example);

  T save(T domain);

  List<T> save(List<T> domains);

  T create(T domain) throws EntityAlreadyExistsException, EntityInvalidException;

  void delete(String id) throws EntityNotFoundException;

  void delete(T entity);

  void delete(List<T> entities);

  void softDelete(String id) throws EntityNotFoundException;

  void softDelete(T entity);

  void softDelete(List<String> ids);

  PageDto<T> toPageDto(Page<T> page);

  Long countAllLike(T entity);

  List<String> distinctColumnValues(String collection, String column);

  T update(String id, T domain) throws EntityNotFoundException;

  T patchUpdate(String id, T domain, String propChanged) throws EntityNotFoundException, EntityAlreadyExistsException;

  Boolean patchById(String id, Map<String, Object> values);

  Boolean patchBySlug(String slug, Map<String, Object> values);

  Boolean patch(T oldData, T newData);

  List<T> bulkImport(List<T> list);

  List<String> findSlugByIds(List<String> ids);

}
