package com.sixsprints.core.generic.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.filter.BooleanColumnFilter;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.dto.filter.NumberColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.generic.GenericAbstractService;
import com.sixsprints.core.utils.AppConstants;
import com.sixsprints.core.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractReadService<T extends AbstractMongoEntity>
    extends GenericAbstractService<T> implements GenericReadService<T> {

  @Autowired
  private DateUtil dateUtil;

  @Override
  public List<T> findAllList() {
    return repository().findAll();
  }

  @Override
  public Page<T> findAll(Pageable page) {
    assertValid(page != null, "page", page);
    return repository().findAll(page);
  }

  @Override
  public Optional<T> findOneById(String id) {
    if (!StringUtils.hasText(id)) {
      return Optional.empty();
    }
    return repository().findById(id);
  }

  @Override
  public Optional<T> findOneBySlug(String slug) {
    if (!StringUtils.hasText(slug)) {
      return Optional.empty();
    }
    T entity = repository().findBySlug(slug);
    return Optional.ofNullable(entity);
  }

  @Override
  public Optional<T> findOneByCriteria(Criteria criteria) {
    assertValid(criteria != null, "criteria", criteria);
    return Optional.ofNullable(mongo.findOne(new Query(criteria), metaData().getClassType()));
  }

  @Override
  public Page<T> filterByCriteria(Criteria criteria) {
    assertValid(criteria != null, "criteria", criteria);
    return runCriteriaWithPage(metaData(), Pageable.unpaged(), criteria);
  }

  @Override
  public Page<T> filterByCriteria(Criteria criteria, Sort sort) {
    assertValid(criteria != null, "criteria", criteria);
    assertValid(sort != null, "sort", sort);
    Pageable pageable = Pageable.unpaged(sort);
    return filterByCriteria(criteria, pageable);
  }

  @Override
  public Page<T> filterByCriteria(Criteria criteria, Pageable pageable) {
    assertValid(criteria != null, "criteria", criteria);
    assertValid(pageable != null, "pageable", pageable);
    return runCriteriaWithPage(metaData(), pageable, criteria);
  }

  @Override
  @SuppressWarnings("null")
  public Page<T> filterByFilterRequestDto(FilterRequestDto filterRequestDto) {
    assertValid(filterRequestDto != null, "filterRequestDto", filterRequestDto);
    assertValid(filterRequestDto.getPage() >= 0, "filterRequestDto.page",
        filterRequestDto.getPage());
    assertValid(filterRequestDto.getSize() > 0, "filterRequestDto.size",
        filterRequestDto.getSize());
    MetaData<T> meta = metaData();
    Sort sort = buildSort(filterRequestDto.getSortModel(), meta);
    Pageable pageable =
        PageRequest.of(filterRequestDto.getPage(), filterRequestDto.getSize(), sort);
    Criteria criteria = buildCriteria(filterRequestDto, meta);
    return runCriteriaWithPage(meta, pageable, criteria);
  }

  private Page<T> runCriteriaWithPage(MetaData<T> meta, Pageable pageable, Criteria criteria) {
    Query query = new Query(criteria);
    long total = mongo.count(query, meta.getClassType());
    query.with(pageable);
    List<T> data = mongo.find(query, meta.getClassType());
    return new PageImpl<T>(data, pageable, total);
  }

  protected Sort buildSort(List<SortModel> sortModel, MetaData<T> meta) {
    Sort sort = Sort.unsorted();
    if (!CollectionUtils.isEmpty(sortModel)) {
      for (SortModel aSort : sortModel) {
        sort = sort.and(Sort.by(aSort.getSort(), aSort.getColId()));
      }
    }
    return sort;
  }

  protected Criteria buildCriteria(FilterRequestDto filterRequestDto, MetaData<T> meta) {

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

      long count = values.stream()
          .filter(val -> ObjectUtils.isEmpty(val) || val.equals(AppConstants.BLANK_STRING)).count();
      if (count > 0) {
        array = new Object[size + 2];
        array[i++] = new ArrayList<String>();
        array[i++] = "";
      }

      for (Object val : values) {
        array[i++] =
            ObjectUtils.isEmpty(val) || val.toString().equals(AppConstants.BLANK_STRING) ? null
                : val;
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

  private void addNumberFilter(List<Criteria> criterias, String key,
      NumberColumnFilter numberFilter) {
    Criteria criteria = setKeyCriteria(key);
    if (StringUtils.hasText(numberFilter.getType())) {
      numberCriteria(numberFilter.getType(), numberFilter.getFilter(), numberFilter.getFilterTo(),
          criteria);
    } else {
      Criteria criteria1 = setKeyCriteria(key);
      Criteria criteria2 = setKeyCriteria(key);
      numberCriteria(numberFilter.getCondition1().getType(),
          numberFilter.getCondition1().getFilter(), numberFilter.getCondition1().getFilterTo(),
          criteria1);
      numberCriteria(numberFilter.getCondition2().getType(),
          numberFilter.getCondition2().getFilter(), numberFilter.getCondition2().getFilterTo(),
          criteria2);
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
    criteria = dateCriteria(filter.getType(), filter.getDateFrom().getTime(), dateTo,
        filter.isExactMatch(), criteria);
    criterias.add(criteria);
  }

  private void exactDateCriteria(String type, Long filterEpoch, Long filterToEpoch,
      Criteria criteria) {

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

  private Criteria dateCriteria(String type, Long filter, Long filterTo, boolean isExactMatch,
      Criteria criteria2) {
    Criteria criteria = new Criteria(criteria2.getKey());
    if (isExactMatch) {
      exactDateCriteria(type, filter, filterTo, criteria);
      return criteria;
    }

    Long filterEOD = dateUtil.endOfDay(filter).toInstant().toEpochMilli();
    Long filterSOD = dateUtil.startOfDay(filter).toInstant().toEpochMilli();
    Long filterToEOD =
        filterTo != null ? dateUtil.endOfDay(filterTo).toInstant().toEpochMilli() : 0;

    switch (type) {
      case AppConstants.EQUALS:
        criteria.lte(filterEOD).gte(filterSOD);
        break;

      case AppConstants.NOT_EQUAL:
        return new Criteria().orOperator(new Criteria(criteria2.getKey()).lt(filterSOD),
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

  protected String transformFreeTextSearchInput(String filter) {
    return Pattern.quote(filter);
  }

  private void addExactMatchCriteria(List<Criteria> criterias, String key,
      ExactMatchColumnFilter filter) {
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

}
