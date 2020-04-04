package com.sixsprints.core.generic.read;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.io.dozer.ICsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.dto.filter.BooleanColumnFilter;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.dto.filter.NumberColumnFilter;
import com.sixsprints.core.dto.filter.SearchColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.GenericAbstractService;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.AppConstants;
import com.sixsprints.core.utils.CellProcessorUtil;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.core.utils.FieldMappingUtil;
import com.sixsprints.core.utils.InheritanceMongoUtil;

public abstract class AbstractReadService<T extends AbstractMongoEntity> extends GenericAbstractService<T>
  implements GenericReadService<T> {

  private static final String IGNORE_CASE_FLAG = "i";
  private static final String SLUG = "slug";

  @Override
  public Page<T> findAll(Pageable page) {
    return repository().findAll(page);
  }

  @Override
  public Page<T> findAll(int page, int size) {
    Pageable pageable = pageable(page, size);
    return findAll(pageable);
  }

  @Override
  public List<T> findAll() {
    return repository().findAll();
  }

  @Override
  public Page<T> findAllActive(Pageable page) {
    return repository().findAllByActiveTrue(page);
  }

  @Override
  public Page<T> findAllActive(int page, int size) {
    Pageable pageable = pageable(page, size);
    return findAllActive(pageable);
  }

  @Override
  public List<T> findAllActive() {
    return repository().findAllByActiveTrue();
  }

  @Override
  public T findOne(String id) throws EntityNotFoundException {
    if (id == null) {
      throw notFoundException("null");
    }
    Optional<T> entity = repository().findById(id);
    if (!entity.isPresent()) {
      throw notFoundException(id);
    }
    return entity.get();
  }

  @Override
  public T findBySlug(String slug) throws EntityNotFoundException {
    T entity = repository().findBySlug(slug);
    if (entity == null) {
      throw notFoundException(SLUG);
    }
    return entity;
  }

  @Override
  public Page<T> findAllLike(T example, Pageable page) {
    return repository().findAll(Example.of(example), page);
  }

  @Override
  public List<T> findAllLike(T example) {
    return repository().findAll(Example.of(example));
  }

  @Override
  public T findOneLike(T example) {
    Optional<T> entity = repository().findOne(Example.of(example));
    return entity.orElse(null);
  }

  @Override
  public Page<T> filter(FilterRequestDto filterRequestDto) {
    checkFilterRequestDto(filterRequestDto);
    validatePageAndSize(filterRequestDto.getPage(), filterRequestDto.getSize());
    MetaData<T> meta = metaData();
    Sort sort = buildSort(filterRequestDto.getSortModel(), meta);
    Pageable pageable = PageRequest.of(filterRequestDto.getPage(), filterRequestDto.getSize(), sort);
    Criteria criteria = buildCriteria(filterRequestDto, meta);
    Query query = new Query(criteria);
    long total = mongo.count(query, meta.getClassType());
    query.with(pageable);
    List<T> data = mongo.find(query, meta.getClassType());
    return new PageImpl<T>(data, pageable, total);
  }

  @Override
  public List<T> filterAll(FilterRequestDto filterRequestDto) {
    checkFilterRequestDto(filterRequestDto);
    MetaData<T> meta = metaData();
    Criteria criteria = buildCriteria(filterRequestDto, meta);
    Sort sort = buildSort(filterRequestDto.getSortModel(), meta);
    Query query = new Query(criteria);
    query.with(sort);
    List<T> data = mongo.find(query, meta.getClassType());
    return data;
  }

  @Override
  public List<String> distinctColumnValues(String column, FilterRequestDto filterRequestDto) {
    MetaData<T> metaData = metaData();
    Query query = new Query();
    query.addCriteria(buildCriteria(filterRequestDto, metaData));
    DistinctIterable<String> iterable = mongo.getCollection(mongo.getCollectionName(metaData().getClassType()))
      .distinct(column, query.getQueryObject(), String.class);

    MongoCursor<String> cursor = iterable.iterator();
    List<String> list = new ArrayList<>();
    while (cursor.hasNext()) {
      String next = cursor.next();
      if (next != null)
        list.add(next);
    }
    list.remove("");
    list.add(AppConstants.BLANK_STRING);
    Collections.sort(list);
    return list;
  }

  @Override
  public <DTO> void exportData(GenericTransformer<T, DTO> transformer,
    FilterRequestDto filterRequestDto, PrintWriter writer, Locale locale) throws IOException, BaseException {

    if (filterRequestDto == null) {
      filterRequestDto = FilterRequestDto.builder().build();
    }

    ICsvDozerBeanWriter beanWriter = null;
    try {
      List<FieldDto> fields = metaData().getFields();
      String mappings[] = exportMappings(fields);

      beanWriter = new CsvDozerBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
      beanWriter.configureBeanMapping(metaData().getDtoClassType(), mappings);
      writeHeader(beanWriter, fields, mappings, locale);

      // STREAMING IN BATCHES OF 750 or overriden defaultBatchSize() method.
      filterRequestDto.setPage(0);
      filterRequestDto.setSize(defaultBatchSize());

      PageDto<DTO> pages = transformer.pageEntityToPageDtoDto(filter(filterRequestDto));

      int totalPages = pages.getTotalPages();
      CellProcessor[] exportProcessors = cellProcessors(fields);

      for (int i = 0; i < totalPages; i++) {
        List<DTO> dtos = pages.getContent();
        for (final DTO dto : dtos) {
          beanWriter.write(dto, exportProcessors);
          writer.flush();
        }
        if (i + 1 == totalPages) {
          continue;
        }
        filterRequestDto.setPage(i + 1);
        pages = transformer.pageEntityToPageDtoDto(filter(filterRequestDto));
      }

    } finally {

      if (beanWriter != null) {
        beanWriter.close();
      }
      if (writer != null) {
        writer.close();
      }
    }

  }

  protected int defaultBatchSize() {
    return 750;
  }

  private CellProcessor[] cellProcessors(List<FieldDto> fields) {
    Map<String, CellProcessor> map = exportCellProcessors(fields);
    return CellProcessorUtil.exportProcessors(fields, map);
  }

  protected Map<String, CellProcessor> exportCellProcessors(List<FieldDto> fields) {
    return new HashMap<>();
  }

  protected void writeHeader(ICsvDozerBeanWriter beanWriter, List<FieldDto> fields, String[] mappings,
    Locale locale) throws IOException {
    beanWriter.writeHeader(FieldMappingUtil.createHeaders(mappings, fields, locale));
  }

  protected String[] exportMappings(List<FieldDto> fields) {
    return FieldMappingUtil.genericMappings(fields);
  }

  protected Pageable pageable(int page, int size) {
    validatePageAndSize(page, size);
    Pageable pageable = PageRequest.of(page, size);
    return pageable;
  }

  private Sort buildSort(List<SortModel> sortModel, MetaData<T> meta) {
    Sort sort = Sort.unsorted();
    if (!CollectionUtils.isEmpty(sortModel)) {
      for (SortModel aSort : sortModel) {
        sort = sort.and(Sort.by(aSort.getSort(), aSort.getColId()));
      }
    }
    if (meta != null && meta.getDefaultSort() != null) {
      sort = sort.and(meta.getDefaultSort());
    }
    return sort;
  }

  private Criteria buildCriteria(FilterRequestDto filterRequestDto, MetaData<T> meta) {
    List<Criteria> criterias = new ArrayList<>();
    Criteria criteria = InheritanceMongoUtil.generate(meta.getClassType());
    if (criteria != null) {
      criterias.add(criteria);
    }
    if (!(filterRequestDto == null || filterRequestDto.getFilterModel() == null
      || filterRequestDto.getFilterModel().isEmpty())) {
      Map<String, ColumnFilter> filters = filterRequestDto.getFilterModel();
      for (String key : filters.keySet()) {
        addCriteria(filters.get(key), key, criterias);
      }
    }

    if (!criterias.isEmpty()) {
      return new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
    }
    return new Criteria();
  }

  private void addCriteria(ColumnFilter filter, String key, List<Criteria> criterias) {
    if (filter instanceof SetColumnFilter) {
      addSetFilter(criterias, key, (SetColumnFilter) filter);
    } else if (filter instanceof NumberColumnFilter) {
      addNumberFilter(criterias, key, (NumberColumnFilter) filter);
    } else if (filter instanceof BooleanColumnFilter) {
      addBooleanFilter(criterias, key, (BooleanColumnFilter) filter);
    } else if (filter instanceof DateColumnFilter) {
      addDateFilter(criterias, key, (DateColumnFilter) filter);
    } else if (filter instanceof SearchColumnFilter) {
      addSearchCriteria(((SearchColumnFilter) filter).getFilter(), criterias);
    } else if (filter instanceof ExactMatchColumnFilter) {
      addExactMatchCriteria(criterias, key, (ExactMatchColumnFilter) filter);
    }
  }

  private void addSetFilter(List<Criteria> criterias, String key, SetColumnFilter filter) {
    if (!CollectionUtils.isEmpty(filter.getValues())) {
      int i = 0;
      List<String> values = filter.getValues();
      int size = values.size();
      Object[] array = new String[size];

      long count = values.stream().filter(val -> StringUtils.isEmpty(val) || val.equals(AppConstants.BLANK_STRING))
        .count();
      if (count > 0) {
        array = new String[size + 1];
        array[i++] = "";
      }

      for (String val : values) {
        array[i++] = StringUtils.isEmpty(val) || val.equals(AppConstants.BLANK_STRING) ? null : val;
      }
      criterias.add(setKeyCriteria(key).in(array));
    }
  }

  private void addNumberFilter(List<Criteria> criterias, String key, NumberColumnFilter numberFilter) {
    Criteria criteria = setKeyCriteria(key);
    if (!StringUtils.isEmpty(numberFilter.getType())) {
      numberCriteria(numberFilter.getType(), numberFilter.getFilter(), numberFilter.getFilterTo(), criteria);
    } else {
      Criteria criteria1 = setKeyCriteria(key);
      Criteria criteria2 = setKeyCriteria(key);
      numberCriteria(numberFilter.getCondition1().getType(), numberFilter.getCondition1().getFilter(),
        numberFilter.getCondition1().getFilterTo(), criteria1);
      numberCriteria(numberFilter.getCondition2().getType(), numberFilter.getCondition2().getFilter(),
        numberFilter.getCondition2().getFilterTo(), criteria2);
      if (AppConstants.AND_OPERATOR.equals(numberFilter.getOperator())) {
        criteria = new Criteria().andOperator(criteria1, criteria2);
      }
      if (AppConstants.OR_OPERATOR.equals(numberFilter.getOperator())) {
        criteria = new Criteria().orOperator(criteria1, criteria2);
      }
    }
    criterias.add(criteria);
  }

  private void numberCriteria(String type, Integer filter, Integer filterTo, Criteria criteria) {
    switch (type) {
    case AppConstants.EQUALS:
      criteria.is(filter);
      break;

    case AppConstants.NOT_EQUAL:
      criteria.ne(filter);
      break;

    case AppConstants.LESS_THAN:
      criteria.lt(filter);
      break;

    case AppConstants.LESS_THAN_OR_EQUAL:
      criteria.lte(filter);
      break;

    case AppConstants.GREATER_THAN:
      criteria.gt(filter);
      break;

    case AppConstants.GREATER_THAN_OR_EQUAL:
      criteria.gte(filter);
      break;

    case AppConstants.IN_RANGE:
      criteria.lte(filterTo).gte(filter);
      break;
    }
  }

  private void addBooleanFilter(List<Criteria> criterias, String key, BooleanColumnFilter filter) {
    criterias.add(setKeyCriteria(key).is(filter.getValue()));
  }

  private void addDateFilter(List<Criteria> criterias, String key, DateColumnFilter filter) {
    Criteria criteria = setKeyCriteria(key);
    criteria = dateCriteria(filter.getType(), filter.getFilter(), filter.getFilterTo(), criteria);
    criterias.add(criteria);
  }

  private Criteria dateCriteria(String type, Long filter, Long filterTo, Criteria criteria2) {
    Criteria criteria = new Criteria(criteria2.getKey());
    switch (type) {
    case AppConstants.EQUALS:
      criteria.lte(DateUtil.instance().build().endOfDay(filter)).gte(DateUtil.instance().build().startOfDay(filter));
      break;

    case AppConstants.NOT_EQUAL:
      return new Criteria().orOperator(
        new Criteria(criteria2.getKey()).lt(DateUtil.instance().build().startOfDay(filter)),
        new Criteria(criteria2.getKey()).gt(DateUtil.instance().build().endOfDay(filter)));

    case AppConstants.LESS_THAN:
      criteria.lt(DateUtil.instance().build().startOfDay(filter));
      break;

    case AppConstants.LESS_THAN_OR_EQUAL:
      criteria.lte(DateUtil.instance().build().endOfDay(filter));
      break;

    case AppConstants.GREATER_THAN:
      criteria.gt(DateUtil.instance().build().endOfDay(filter));
      break;

    case AppConstants.GREATER_THAN_OR_EQUAL:
      criteria.gte(DateUtil.instance().build().startOfDay(filter));
      break;

    case AppConstants.IN_RANGE:
      criteria.lte(DateUtil.instance().build().endOfDay(filterTo)).gte(DateUtil.instance().build().startOfDay(filter));
      break;
    }
    return criteria;
  }

  private void addSearchCriteria(String searchKey, List<Criteria> criterias) {
    List<Criteria> searchCriteria = Lists.newArrayList();
    String quote = Pattern.quote(searchKey);

    List<FieldDto> fields = metaData().getFields();

    if (CollectionUtils.isEmpty(fields)) {
      return;
    }

    if (!fields.contains(FieldDto.builder().name(SLUG).build())) {
      searchCriteria.add(setKeyCriteria(SLUG).regex(quote, IGNORE_CASE_FLAG));
    }
    for (FieldDto field : fields) {
      if (field.getDataType().isSearchable()) {
        Criteria criteria = setKeyCriteria(field.getName()).regex(quote, IGNORE_CASE_FLAG);
        searchCriteria.add(criteria);
      }
    }
    if (!searchCriteria.isEmpty()) {
      criterias.add(new Criteria().orOperator(searchCriteria.toArray(new Criteria[searchCriteria.size()])));
    }

  }

  private void addExactMatchCriteria(List<Criteria> criterias, String key, ExactMatchColumnFilter filter) {
    criterias.add(setKeyCriteria(key).is(filter.getFilter()));
  }

  private Criteria setKeyCriteria(String key) {
    return Criteria.where(key);
  }

  private void checkFilterRequestDto(FilterRequestDto filterRequestDto) {
    if (filterRequestDto == null) {
      throw BaseRuntimeException.builder().error("page number and page size can't be null")
        .httpStatus(HttpStatus.BAD_REQUEST).build();
    }
  }
}
