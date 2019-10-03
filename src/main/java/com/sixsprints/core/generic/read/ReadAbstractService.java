package com.sixsprints.core.generic.read;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.GenericAbstractService;

public abstract class ReadAbstractService<T extends AbstractMongoEntity> extends GenericAbstractService<T>
  implements GenericReadService<T> {

  @Override
  public Page<T> findAll(Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<T> findAll(int page, int size) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<T> findAllActive(Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<T> findAllActive(int page, int size) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> findAllActive() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T findOne(String id) throws EntityNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T findBySlug(String slug) throws EntityNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Page<T> findAllLike(T example, Pageable page) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> findAllLike(T example) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public T findOneLike(T example) {
    // TODO Auto-generated method stub
    return null;
  }

}
