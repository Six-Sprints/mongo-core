
package com.sixsprints.core.generic;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.domain.CustomSequence;
import com.sixsprints.core.dto.ChangeDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.enums.AuditLogAction;
import com.sixsprints.core.enums.AuditLogSource;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.utils.BeanWrapperUtil;
import com.sixsprints.core.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Deprecated
public abstract class AbstractService<T extends AbstractMongoEntity> implements GenericService<T> {

  @Autowired
  private MongoOperations mongo;

  @Autowired
  protected MongoTemplate mongoTemplate;

  protected Javers javers = JaversBuilder.javers().build();

  protected int getNextSequence(String seqName) {
    CustomSequence counter = mongo.findAndModify(query(where("_id").is(seqName)), new Update().inc("seq", 1),
      options().returnNew(true).upsert(true), CustomSequence.class);
    return counter.getSeq();
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
  public T save(T domain) {
    if (StringUtils.isEmpty(domain.getId()) && StringUtils.isEmpty(domain.getSlug())) {
      if (prefix(domain) != null) {
        domain.setSlug(prefix(domain) + getNextSequence(collection(domain)));
      }
    }
    return repository().save(domain);
  }

  @Override
  public List<T> save(List<T> domains) {
    return repository().saveAll(domains);
  }

  @Override
  public Page<T> findAll(Pageable page) {
    Page<T> all = repository().findAll(page);
    return all;
  }

  @Override
  public Page<T> findAll(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<T> all = repository().findAll(pageable);
    return all;
  }

  @Override
  public List<T> findAll() {
    List<T> all = repository().findAll();
    return all;
  }

  @Override
  public void delete(T entity) {
    repository().delete(entity);
    log.debug("Deleted: " + entity);
  }

  @Override
  public void delete(List<T> entities) {
    repository().deleteAll(entities);
    log.debug("Deleted: " + entities);
  }

  @Override
  public void delete(String id) throws EntityNotFoundException {
    T entity = findOne(id);
    delete(entity);
  }

  @Override
  public void softDelete(T entity) {
    entity.setActive(Boolean.FALSE);
    save(entity);
    log.debug("Soft Deleted: " + entity);
  }

  protected abstract Class<T> classType();

  @Override
  public void softDelete(List<String> ids) {
    Query query = new Query(new Criteria("id").in(ids));
    Update update = new Update().set("active", Boolean.FALSE);
    mongo.updateMulti(query, update, classType());
    List<String> slugs = findSlugByIds(ids);
    for (String slug : slugs) {
      saveAuditLog(slug,
        ChangeDto.builder().action(AuditLogAction.DELETE).source(AuditLogSource.SCREEN)
          .build());
    }
  }

  @Override
  public List<String> findSlugByIds(List<String> ids) {
    return repository().findSlugByIdIn(ids).stream().map(c -> c.getSlug()).collect(Collectors.toList());
  }

  @Override
  public void softDelete(String id) throws EntityNotFoundException {
    T entity = findOne(id);
    softDelete(entity);
  }

  @Override
  public T update(String id, T domain) throws EntityNotFoundException {
    log.debug("Updating id: " + id + " with " + domain);
    T fromDb = findOne(id);
    domain.copyEntityFrom(fromDb);
    return save(domain);
  }

  @Override
  public T patchUpdate(String id, T domain, String propChanged)
    throws EntityNotFoundException, EntityAlreadyExistsException {
    log.debug("Updating id: " + id + " with " + domain);
    T oldData = findOne(id);

    Object oldValue = BeanWrapperUtil.getValue(oldData, propChanged);
    Object newValue = BeanWrapperUtil.getValue(domain, propChanged);
    ChangeDto change = ChangeDto.builder().action(AuditLogAction.UPDATE)
      .oldValue(convert(oldValue))
      .newValue(convert(newValue))
      .source(AuditLogSource.SCREEN).propChanged(propChanged)
      .build();

    BeanWrapperUtil.copyProperties(domain, oldData, ImmutableList.<String>of(propChanged));
    T fromDB = checkDuplicate(oldData);

    if (fromDB != null && !oldData.getId().equals(fromDB.getId())) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB);
      }
      delete(fromDB);
    }
    oldData = save(oldData);
    if (oldData.getActive()) {
      postSave(oldData, change);
    }
    return oldData;
  }

  private String convert(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Date) {
      Date date = (Date) value;
      return DateUtil.instance().build().dateToString(date);
    }
    return value.toString();
  }

  protected T checkDuplicate(T oldData) {
    if (StringUtils.isEmpty(oldData.getSlug())) {
      return repository().findById(oldData.getId()).orElse(null);
    }
    return repository().findBySlug(oldData.getSlug());
  }

  protected EntityAlreadyExistsException alreadyExistsException(T fromDB) {
    return EntityAlreadyExistsException.childBuilder().build();
  }

  @Override
  public List<T> findAllActive() {
    return repository().findAllByActiveTrue();
  }

  @Override
  public Page<T> findAllActive(Pageable pageable) {
    return repository().findAllByActiveTrue(pageable);
  }

  @Override
  public Page<T> findAllActive(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return findAllActive(pageable);
  }

  @Override
  public Page<T> findAllLike(T entity, Pageable page) {
    Example<T> example = Example.of(entity);
    return repository().findAll(example, page);
  }

  @Override
  public List<T> findAllLike(T entity) {
    Example<T> example = Example.of(entity);
    return repository().findAll(example);
  }

  @Override
  public T findOneLike(T entity) {
    Example<T> example = Example.of(entity);
    Optional<T> db = repository().findOne(example);
    if (db.isPresent()) {
      return db.get();
    }
    return null;
  }

  @Override
  public PageDto<T> toPageDto(Page<T> page) {
    PageDto<T> pageDto = new PageDto<>();
    pageDto.setCurrentPageSize(page.getNumberOfElements());
    pageDto.setCurrentPage(page.getNumber());
    pageDto.setTotalElements(page.getTotalElements());
    pageDto.setTotalPages(page.getTotalPages());
    pageDto.setContent(page.getContent());
    return pageDto;
  }

  protected EntityNotFoundException notFoundException(String id) throws EntityNotFoundException {
    return notFoundException("id", id);
  }

  protected EntityNotFoundException notFoundException(String key, String id) throws EntityNotFoundException {
    throw EntityNotFoundException.childBuilder().error(String.format("Entity not found with %s = %s", key, id)).build();
  }

  protected abstract GenericRepository<T> repository();

  protected String prefix(T domain) {
    return null;
  }

  protected String collection(T domain) {
    return null;
  }

  public void validatePageAndSize(Integer pageNumber, Integer pageSize) throws BaseException {
    if ((pageNumber == null) || (pageSize == null) || (pageNumber < 0) || (pageSize < 0)) {
      throw BaseException.builder().httpStatus(HttpStatus.BAD_REQUEST).error("Page number or Page size not valid")
        .build();
    }
  }

  @Override
  public Long countAllLike(T entity) {
    Example<T> example = Example.of(entity);
    return repository().count(example);
  }

  @Override
  public T findBySlug(String slug) throws EntityNotFoundException {
    T entity = repository().findBySlug(slug);
    if (entity == null) {
      throw notFoundException("slug", slug);
    }
    return entity;
  }

  @Override
  public List<String> distinctColumnValues(String collection, String column) {

    Query query = new Query();

    DistinctIterable<String> iterable = mongoTemplate.getCollection(collection).distinct(column,
      query.getQueryObject(), String.class);
    MongoCursor<String> cursor = iterable.iterator();
    List<String> list = new ArrayList<>();
    while (cursor.hasNext()) {
      list.add(cursor.next());
    }
    return list;
  }

  @Override
  public Boolean patchById(String id, Map<String, Object> values) {
    Query query = new Query(new Criteria("id").is(id));
    return makePatchRequest(id, values, query);
  }

  private Boolean makePatchRequest(String identifier, Map<String, Object> values, Query query) {
    if (values == null || values.isEmpty() || StringUtils.isEmpty(identifier)) {
      return false;
    }
    Update update = new Update();
    for (String key : values.keySet()) {
      update.set(key, values.get(key));
    }
    UpdateResult updateFirst = mongo.updateFirst(query, update, classType());
    return updateFirst.wasAcknowledged();
  }

  @Override
  public Boolean patchBySlug(String slug, Map<String, Object> values) {
    Query query = new Query(new Criteria("slug").is(slug));
    return makePatchRequest(slug, values, query);
  }

  @Override
  public Boolean patch(T oldData, T newData) {
    Map<String, Object> map = new HashMap<String, Object>();
    newData.copyEntityFrom(oldData);
    Diff diff = javers.compare(oldData, newData);
    List<ValueChange> changes = diff.getChangesByType(ValueChange.class);
    for (ValueChange change : changes) {
      map.put(change.getPropertyName(), change.getRight());
    }
    return patchById(newData.getId(), map);
  }

  @Override
  public List<T> bulkImport(List<T> list) {
    List<T> saved = Lists.newArrayList();
    if (CollectionUtils.isEmpty(list)) {
      return saved;
    }
    for (T domain : list) {
      T save = saveOneWhileBulkImport(domain);
      if (save != null) {
        saved.add(save);
      }
    }
    return saved;
  }

  protected T saveOneWhileBulkImport(T domain) {
    if (isInvalid(domain)) {
      return null;
    }
    domain = saveOrOverwrite(domain);
    return domain;
  }

  protected void postSave(T domain, ChangeDto change) {
    saveAuditLog(domain, change);
  }

  protected boolean isInvalid(T domain) {
    return false;
  }

  private T saveOrOverwrite(T domain) {
    T fromDB = checkDuplicate(domain);
    if (fromDB != null) {
      if (!fromDB.getActive()) {
        delete(fromDB);
      } else {
        Boolean active = domain.getActive();
        domain.copyEntityFrom(fromDB);
        domain.setActive(active);

        T copy = clone(fromDB);
        copyNonNullValues(domain, fromDB);

        if (checkEquals(fromDB, copy)) {
          if (skipIfEqual()) {
            return null;
          }
          return fromDB;
        }

        ChangeDto change = ChangeDto.builder().action(AuditLogAction.UPDATE)
          .source(AuditLogSource.BULK_IMPORT).build();

        fromDB = save(fromDB);
        if (fromDB.getActive()) {
          postSave(fromDB, change);
        }
        return fromDB;
      }
    }
    transformProperties(domain);

    ChangeDto change = ChangeDto.builder().action(AuditLogAction.CREATE)
      .source(AuditLogSource.BULK_IMPORT).build();

    domain = save(domain);
    if (domain.getActive()) {
      postSave(domain, change);
    }
    return domain;
  }

  protected T clone(T domain) {
    return null;
  }

  protected boolean skipIfEqual() {
    return true;
  }

  protected boolean checkEquals(T obj1, T obj2) {
    if (obj1 == null && obj2 == null) {
      return true;
    }
    return obj1 != null && obj2 != null && obj1.equals(obj2);
  }

  protected void copyNonNullValues(T source, T target) {
    BeanWrapperUtil.copyNonNullProperties(source, target);
  }

  public T create(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    if (isInvalid(domain)) {
      throw invalidException(domain);
    }
    T fromDB = checkDuplicate(domain);
    if (fromDB != null) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB);
      }
      delete(fromDB);
    }
    transformProperties(domain);
    domain = save(domain);
    if (domain.getActive()) {
      ChangeDto change = ChangeDto.builder().action(AuditLogAction.CREATE)
        .source(AuditLogSource.SCREEN).build();
      postSave(domain, change);
    }
    return domain;
  }

  protected void transformProperties(T domain) {

  }

  protected void saveAuditLog(T domain, ChangeDto dto) {

  }

  protected void saveAuditLog(String id, ChangeDto dto) {

  }

  protected EntityInvalidException invalidException(T domain) {
    return EntityInvalidException.childBuilder().build();
  }

}
