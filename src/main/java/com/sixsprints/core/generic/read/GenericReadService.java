package com.sixsprints.core.generic.read;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.KeyLabelDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericMapper;

public interface GenericReadService<T extends AbstractMongoEntity> {

  Page<T> findAll(Pageable page);

  Page<T> findAll(int page, int size);

  List<T> findAll();

  Page<T> findAllActive(Pageable page);

  Page<T> findAllActive(int page, int size);

  List<T> findAllActive();

  T findOne(String id) throws EntityNotFoundException;

  T findBySlug(String slug) throws EntityNotFoundException;

  Optional<T> findBySlugOptional(String slug);

  Page<T> findAllLike(T example, Pageable page);

  List<T> findAllLike(T example);

  T findOneLike(T example);

  Page<T> filter(FilterRequestDto filters);

  List<T> filterAll(FilterRequestDto filters);

  List<KeyLabelDto> distinctColumnValues(String column, FilterRequestDto filterRequestDto);

  <E> void exportData(GenericMapper<T, E> transformer, FilterRequestDto filterRequestDto, OutputStream writer)
    throws IOException, BaseException;

  T findOne(Criteria criteria);

  List<T> findAll(Criteria criteria);

  List<T> findAll(Criteria criteria, Sort sort);

  Page<T> findAll(Criteria criteria, Pageable page);

}
