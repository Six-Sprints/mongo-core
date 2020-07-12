package com.sixsprints.core.generic.read;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericTransformer;

public interface GenericReadService<T extends AbstractMongoEntity> {

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

  Page<T> filter(FilterRequestDto filters);

  List<T> filterAll(FilterRequestDto filters);

  List<?> distinctColumnValues(String column, FilterRequestDto filterRequestDto);

  <E> void exportData(GenericTransformer<T, E> transformer,
    FilterRequestDto filterRequestDto, PrintWriter writer, Locale locale)
    throws IOException, BaseException;

}
