package com.sixsprints.core.generic.read;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.ExportData;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.KeyLabelDto;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.filter.BooleanColumnFilter;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.dto.filter.NumberColumnFilter;
import com.sixsprints.core.dto.filter.SearchColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.GenericAbstractService;
import com.sixsprints.core.transformer.GenericMapper;
import com.sixsprints.core.utils.AppConstants;
import com.sixsprints.core.utils.BeanWrapperUtil;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.core.utils.ExcelDefaultStyles;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractReadService<T extends AbstractMongoEntity> extends GenericAbstractService<T>
  implements GenericReadService<T> {

  private static final String IGNORE_CASE_FLAG = "i";
  private static final String SLUG = "slug";

  @Autowired
  private DateUtil dateUtil;

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
  public List<KeyLabelDto> distinctColumnValues(String column, FilterRequestDto filterRequestDto) {
    MetaData<T> metaData = metaData();

    FieldDto field = findField(column, metaData);
    if (field == null) {
      log.warn("Unable to find the column {} in meta data fields. Returning empty list", column);
      return new ArrayList<>();
    }
    Class<?> classTypeFromField = getClassTypeFromField(field);
    if (classTypeFromField == null) {
      log.warn("Unable to determine the class type from field {} and column {}. Returning empty list", field, column);
      return new ArrayList<>();
    }

    String distinctColumn = StringUtils.hasText(field.getJoinColumnNameLocal()) ? field.getJoinColumnNameLocal()
      : column;
    Query query = new Query().with(Sort.by(Direction.ASC, column));
    query.addCriteria(buildCriteria(filterRequestDto, metaData));
    List<?> list = mongo.getCollection(mongo.getCollectionName(metaData().getClassType()))
      .distinct(distinctColumn, query.getQueryObject(), classTypeFromField).into(new ArrayList<>());

    List<KeyLabelDto> result = new ArrayList<>();
    result.add(KeyLabelDto.builder()
      .key("")
      .label(AppConstants.BLANK_STRING)
      .build());

    if (!CollectionUtils.isEmpty(list) && DataType.AUTO_COMPLETE.equals(field.getDataType())) {
      Query joinQuery = new Query().with(Sort.by(Direction.ASC, field.getJoinColumnName()));
      joinQuery.fields().include(field.getJoinColumnName(), SLUG).exclude("_id");
      joinQuery.addCriteria(Criteria.where(SLUG).in(list));

      List<?> joinedValues = mongo.find(joinQuery, field.getJoinCollectionClass());

      joinedValues.forEach(item -> {
        if (ObjectUtils.isNotEmpty(item)) {
          result.add(KeyLabelDto.builder()
            .key(BeanWrapperUtil.getValue(item, SLUG))
            .label(BeanWrapperUtil.getValue(item, field.getJoinColumnName()))
            .build());
        }
      });

    } else {

      list.forEach(i -> {
        if (ObjectUtils.isNotEmpty(i)) {
          result.add(KeyLabelDto.builder()
            .key(i)
            .label(i)
            .build());
        }
      });

    }
    return result;
  }

  private Class<?> getClassTypeFromField(FieldDto field) {
    if (field.getDataType() == null) {
      return null;
    }
    return field.getDataType().getClassType();
  }

  private FieldDto findField(String column, MetaData<T> metaData) {
    List<FieldDto> fields = metaData.getFields();
    if (CollectionUtils.isEmpty(fields)) {
      return null;
    }
    return fields.stream().filter(f -> f.getName().equals(column) || column.equals(f.getFilterColumnName())).findFirst()
      .orElse(null);
  }

  @Override
  public <DTO> void exportData(GenericMapper<T, DTO> mapper, FilterRequestDto filterRequestDto, OutputStream writer)
    throws IOException, BaseException {

    MetaData<T> metaData = metaData();
    if (metaData == null || metaData.getExportDataClassType() == null) {
      String error = "Consider defining metaData::exportDataClassType for " + this.getClass().getSimpleName();
      log.error(error);
      throw EntityInvalidException
        .childBuilder().error(error)
        .build();
    }
    if (filterRequestDto == null) {
      filterRequestDto = FilterRequestDto.builder().build();
    }
    List<T> data = filterAll(filterRequestDto);
    Workbook workbook = null;
    try {
      workbook = createWorkbook(mapper, data);
      workbook.write(writer);
    } finally {
      if (workbook != null) {
        workbook.close();
      }
      if (writer != null) {
        writer.close();
      }
    }
  }

  protected <DTO> Workbook createWorkbook(GenericMapper<T, DTO> mapper, List<T> data) {
    MetaData<T> metaData = metaData();
    String fileName = metaData.getExportTemplatePath();

    if (StringUtils.hasText(fileName)) {
      TemplateExportParams params = new TemplateExportParams(fileName);
      transformTemplateExportParams(params);
      return ExcelExportUtil.exportExcel(params, fetchMapDataForExcelDownload(mapper, data));
    }
    ExportParams simpleParams = new ExportParams();
    simpleParams.setSheetName(entityName());
    simpleParams.setType(ExcelType.XSSF);
    simpleParams.setStyle(ExcelDefaultStyles.class);
    simpleParams.setTitleHeight((short) 6);
    simpleParams.setHeight((short) 6);
    transformSimpleExportParams(simpleParams);
    return ExcelExportUtil.exportExcel(simpleParams, metaData.getExportDataClassType(), mapper.toDto(data));
  }

  protected void transformSimpleExportParams(ExportParams simpleParams) {

  }

  protected void transformTemplateExportParams(TemplateExportParams params) {

  }

  protected <DTO> Map<String, Object> fetchMapDataForExcelDownload(GenericMapper<T, DTO> mapper, List<T> data) {
    return objectToMap(ExportData.<DTO>builder()
      .data(mapper.toDto(data))
      .build());
  }

  protected static Map<String, Object> objectToMap(Object obj) {
    Map<String, Object> map = new HashMap<>();
    try {
      Class<?> clazz = obj.getClass();
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        String fieldName = field.getName();
        Object value = field.get(obj);
        map.put(fieldName, value);
      }
    } catch (IllegalAccessException e) {
    }
    return map;
  }

  protected Pageable pageable(int page, int size) {
    validatePageAndSize(page, size);
    Pageable pageable = PageRequest.of(page, size);
    return pageable;
  }

  protected void preFilter(FilterRequestDto filterRequestDto) {
    if (filterRequestDto == null || filterRequestDto.getFilterModel() == null
      || filterRequestDto.getFilterModel().isEmpty()) {
      return;
    }

    Map<String, ColumnFilter> filterModel = new HashMap<>(filterRequestDto.getFilterModel());
    MetaData<T> metaData = metaData();
    Map<String, String> keysToCopy = new HashMap<>();
    for (String key : filterModel.keySet()) {
      FieldDto field = findField(key, metaData);
      if (field != null && StringUtils.hasText(field.getJoinColumnNameLocal())) {
        keysToCopy.put(key, field.getJoinColumnNameLocal());
      }
    }

    if (!keysToCopy.isEmpty()) {
      keysToCopy.keySet().forEach(key -> filterModel.put(keysToCopy.get(key), filterModel.get(key)));
      keysToCopy.keySet().forEach(filterModel::remove);
    }

    filterRequestDto.setFilterModel(filterModel);

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

    preFilter(filterRequestDto);
    List<Criteria> criterias = new ArrayList<>();
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
      addSearchCriteria((SearchColumnFilter) filter, criterias);
    } else if (filter instanceof ExactMatchColumnFilter) {
      addExactMatchCriteria(criterias, key, (ExactMatchColumnFilter) filter);
    }
  }

  private void addSetFilter(List<Criteria> criterias, String key, SetColumnFilter filter) {
    if (!CollectionUtils.isEmpty(filter.getValues())) {
      int i = 0;
      List<?> values = filter.getValues();
      int size = values.size();
      Object[] array = new Object[size];

      long count = values.stream().filter(val -> ObjectUtils.isEmpty(val) || val.equals(AppConstants.BLANK_STRING))
        .count();
      if (count > 0) {
        array = new Object[size + 2];
        array[i++] = new ArrayList<String>();
        array[i++] = "";
      }

      for (Object val : values) {
        array[i++] = ObjectUtils.isEmpty(val) || val.toString().equals(AppConstants.BLANK_STRING) ? null : val;
      }
      criterias.add(setCriteriaOperator(filter, array, key));
    }
  }

  private Criteria setCriteriaOperator(SetColumnFilter filter, Object[] array, String key) {
    Criteria crit = setKeyCriteria(key);
    if (AppConstants.NOT_EQUAL.equals(filter.getType())) {
      crit = crit.nin(array);
    } else {
      crit = crit.in(array);
    }
    return crit;
  }

  private void addNumberFilter(List<Criteria> criterias, String key, NumberColumnFilter numberFilter) {
    Criteria criteria = setKeyCriteria(key);
    if (StringUtils.hasText(numberFilter.getType())) {
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
    Long dateTo = filter.getDateTo() == null ? null : filter.getDateTo().getTime();
    criteria = dateCriteria(filter.getType(), filter.getDateFrom().getTime(), dateTo, filter.isExactMatch(),
      criteria);
    criterias.add(criteria);
  }

  private void exactDateCriteria(String type, Long filterEpoch, Long filterToEpoch, Criteria criteria) {

    switch (type) {
    case AppConstants.EQUALS:
      criteria.is(filterEpoch);
      break;

    case AppConstants.NOT_EQUAL:
      criteria.ne(filterEpoch);
      break;

    case AppConstants.LESS_THAN:
      criteria.lt(filterEpoch);
      break;

    case AppConstants.LESS_THAN_OR_EQUAL:
      criteria.lte(filterEpoch);
      break;

    case AppConstants.GREATER_THAN:
      criteria.gt(filterEpoch);
      break;

    case AppConstants.GREATER_THAN_OR_EQUAL:
      criteria.gte(filterEpoch);
      break;

    case AppConstants.IN_RANGE:
      criteria.lte(filterToEpoch).gte(filterEpoch);
      break;
    }
  }

  private Criteria dateCriteria(String type, Long filter, Long filterTo, boolean isExactMatch, Criteria criteria2) {
    Criteria criteria = new Criteria(criteria2.getKey());
    if (isExactMatch) {
      exactDateCriteria(type, filter, filterTo, criteria);
      return criteria;
    }

    Long filterEOD = dateUtil.endOfDay(filter).toInstant().toEpochMilli();
    Long filterSOD = dateUtil.startOfDay(filter).toInstant().toEpochMilli();
    Long filterToEOD = filterTo != null ? dateUtil.endOfDay(filterTo).toInstant().toEpochMilli() : 0;

    switch (type) {
    case AppConstants.EQUALS:
      criteria.lte(filterEOD).gte(filterSOD);
      break;

    case AppConstants.NOT_EQUAL:
      return new Criteria().orOperator(
        new Criteria(criteria2.getKey()).lt(filterSOD),
        new Criteria(criteria2.getKey()).gt(filterEOD));

    case AppConstants.LESS_THAN:
      criteria.lt(filterSOD);
      break;

    case AppConstants.LESS_THAN_OR_EQUAL:
      criteria.lte(filterEOD);
      break;

    case AppConstants.GREATER_THAN:
      criteria.gt(filterEOD);
      break;

    case AppConstants.GREATER_THAN_OR_EQUAL:
      criteria.gte(filterSOD);
      break;

    case AppConstants.IN_RANGE:
      criteria.lte(filterToEOD).gte(filterSOD);
      break;
    }
    return criteria;
  }

  private void addSearchCriteria(SearchColumnFilter filter, List<Criteria> criterias) {

    List<Criteria> searchCriteria = new ArrayList<>();
    String quote = transformFreeTextSearchInput(filter.getFilter());
    List<FieldDto> fields = buildSearchFields(filter);

    if (CollectionUtils.isEmpty(fields)) {
      return;
    }

    for (FieldDto field : fields) {
      if (field.getDataType().isSearchable()) {
        Criteria criteria = null;
        if (StringUtils.hasText(field.getJoinCollectionName())) {
          criteria = resolveJoinValues(criterias, field, quote);
        } else {
          criteria = setKeyCriteria(field.getName()).regex(quote, IGNORE_CASE_FLAG);
        }
        if (criteria != null) {
          searchCriteria.add(criteria);
        }
      }
    }

    if (!filter.isSlugExcludedFromSearch() && !fields.contains(FieldDto.builder().name(SLUG).build())) {
      searchCriteria.add(setKeyCriteria(SLUG).regex(quote, IGNORE_CASE_FLAG));
    }

    if (!searchCriteria.isEmpty()) {
      criterias.add(new Criteria().orOperator(searchCriteria.toArray(new Criteria[searchCriteria.size()])));
    }

  }

  protected String transformFreeTextSearchInput(String filter) {
    return Pattern.quote(filter);
  }

  protected Criteria resolveJoinValues(List<Criteria> criterias, FieldDto field, String filter) {
    Set<String> joinedSlugs = new HashSet<>();
    Query joinQuery = new Query();
    joinQuery.fields().include(SLUG).exclude("_id");
    joinQuery.addCriteria(Criteria.where(field.getJoinColumnName()).regex(filter, IGNORE_CASE_FLAG));

    List<?> joinedValues = mongo.find(joinQuery, field.getJoinCollectionClass());

    joinedValues.forEach(item -> {
      if (ObjectUtils.isNotEmpty(item)) {
        Object value = BeanWrapperUtil.getValue(item, SLUG);
        if (ObjectUtils.isNotEmpty(value)) {
          joinedSlugs.add(value.toString());
        }
      }
    });

    if (!joinedSlugs.isEmpty()) {
      return setKeyCriteria(field.getJoinColumnNameLocal()).in(joinedSlugs);
    }

    return null;

  }

  protected List<FieldDto> buildSearchFields(SearchColumnFilter filter) {
    List<FieldDto> fields = new ArrayList<>();
    if (CollectionUtils.isEmpty(filter.getFields())) {
      return metaData().getFields();
    }

    for (String fieldName : filter.getFields()) {
      fields.add(FieldDto.builder().name(fieldName).dataType(DataType.TEXT).build());
    }
    return fields;
  }

  private void addExactMatchCriteria(List<Criteria> criterias, String key, ExactMatchColumnFilter filter) {
    String type = filter.getType();

    switch (type) {
    case AppConstants.EQUALS:
      criterias.add(setKeyCriteria(key).is(filter.getFilter()));
      break;

    case AppConstants.NOT_EQUAL:
      criterias.add(setKeyCriteria(key).ne(filter.getFilter()));
      break;

    case AppConstants.EXISTS:
      criterias.add(setKeyCriteria(key).exists(true));
      break;

    case AppConstants.DOES_NOT_EXIST:
      criterias.add(setKeyCriteria(key).exists(false));
      break;

    default:
      criterias.add(setKeyCriteria(key).is(filter.getFilter()));
      break;
    }
  }

  private Criteria setKeyCriteria(String key) {
    return Criteria.where(key);
  }

  private void checkFilterRequestDto(FilterRequestDto filterRequestDto) {
    if (filterRequestDto == null) {
      throw BaseRuntimeException.builder().error("Page number and page size can't be null")
        .httpStatus(HttpStatus.BAD_REQUEST).build();
    }
  }
}
