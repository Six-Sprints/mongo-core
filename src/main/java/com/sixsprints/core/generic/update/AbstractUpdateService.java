package com.sixsprints.core.generic.update;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mongodb.client.result.UpdateResult;
import com.sixsprints.core.annotation.AuditCsvImport;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.dto.IGenericExcelImport;
import com.sixsprints.core.dto.ImportLogDetailsDto;
import com.sixsprints.core.enums.ImportOperation;
import com.sixsprints.core.enums.UpdateAction;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.create.AbstractCreateService;
import com.sixsprints.core.service.ImportLogDetailsService;
import com.sixsprints.core.transformer.GenericMapper;
import com.sixsprints.core.transformer.ImportLogDetailsMapper;
import com.sixsprints.core.utils.ApplicationContext;
import com.sixsprints.core.utils.BeanWrapperUtil;
import com.sixsprints.core.utils.excel.ExcelUtil;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractUpdateService<T extends AbstractMongoEntity> extends AbstractCreateService<T>
  implements GenericUpdateService<T> {

  @Lazy
  @Autowired(required = false)
  private ImportLogDetailsService importLogDetailsService;

  @Override
  public T update(String id, T domain)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    T entity = findOne(id);
    domain.copyEntityFrom(entity);
    return update(domain);
  }

  @Override
  public T patchUpdate(String id, T domain, String propChanged)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    return patchUpdate(id, domain, List.of(propChanged));
  }

  @Override
  public T patchUpdate(String id, T domain, List<String> propsChanged)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    T entity = findOne(id);
    BeanWrapperUtil.copyProperties(domain, entity, propsChanged);
    preUpdateCheck(entity);
    patchUpdateRaw(id, entity, propsChanged);
    return entity;
  }

  @Override
  public UpdateResult patchUpdateRaw(Criteria criteria, Object value, String propChanged) {
    Update update = new Update();
    update.set(propChanged, value);
    update.set(AbstractMongoEntity.DATE_MODIFIED, System.currentTimeMillis());
    update.set(AbstractMongoEntity.LAST_MODIFIED_BY, userAuditField(ApplicationContext.getCurrentUser()));

    return mongo.updateMulti(
      Query.query(criteria),
      update,
      metaData().getClassType());
  }

  @Override
  public UpdateResult patchUpdateRaw(Criteria criteria, T domain, List<String> propsChanged) {

    List<String> propsChangedWithAudit = new ArrayList<>(propsChanged);
    domain.setDateModified(System.currentTimeMillis());
    domain.setLastModifiedBy(userAuditField(ApplicationContext.getCurrentUser()));
    propsChangedWithAudit.add(AbstractMongoEntity.DATE_MODIFIED);
    propsChangedWithAudit.add(AbstractMongoEntity.LAST_MODIFIED_BY);

    Update update = new Update();
    for (String prop : propsChangedWithAudit) {
      update.set(prop, BeanWrapperUtil.getValue(domain, prop));
    }
    return mongo.updateMulti(
      Query.query(criteria),
      update,
      metaData().getClassType());
  }

  protected String userAuditField(AbstractMongoEntity currentUser) {
    return currentUser.getSlug();
  }

  @Override
  public UpdateResult patchUpdateRaw(String id, Object value, String propChanged) {
    return patchUpdateRaw(Criteria.where(AbstractMongoEntity.ID).is(id), value, propChanged);
  }

  @Override
  public UpdateResult patchUpdateRaw(String id, T domain, List<String> propsChanged) {
    return patchUpdateRaw(Criteria.where(AbstractMongoEntity.ID).is(id), domain, propsChanged);
  }

  @Override
  @Transactional
  public List<BulkUpdateInfo<T>> bulkUpsert(List<T> list) {
    List<BulkUpdateInfo<T>> updateInfo = new ArrayList<>();
    if (CollectionUtils.isEmpty(list)) {
      return updateInfo;
    }
    for (T domain : list) {
      updateInfo.add(upsertOneWhileBulkImport(domain));
    }
    return updateInfo;
  }

  @Override
  public T upsert(T domain) throws EntityInvalidException {
    BulkUpdateInfo<T> updateInfo = upsertOneWhileBulkImport(domain);
    if (UpdateAction.INVALID.equals(updateInfo.getUpdateAction())) {
      throw invalidException(domain, updateInfo.getErrors());
    }
    return updateInfo.getData();
  }

  @Override
  public <V> void saveImportLogs(Map<ImportOperation, ImportLogDetailsDto> importResponseWrapper,
    List<ImportLogDetailsDto> collection) {

    if (!this.getClass().isAnnotationPresent(AuditCsvImport.class)) {
      log.debug("@AuditCsvImport annotation not present. Ignoring saving the csv log in the database.");
      return;
    }
    if (CollectionUtils.isEmpty(collection)) {
      return;
    }
    final String entityName = entityName();
    collection.forEach(col -> col.setEntity(entityName));
    importLogDetailsService.saveAll(ImportLogDetailsMapper.INSTANCE.toDomain(collection));

  }

  @Override
  @Transactional
  public <DTO extends IGenericExcelImport> Map<ImportOperation, ImportLogDetailsDto> importData(
    InputStream inputStream, GenericMapper<T, DTO> importMapper) throws Exception {
    List<DTO> result = importDataPreview(inputStream);
    return performImport(importMapper, result);
  }

  @Override
  @Transactional
  public <DTO extends IGenericExcelImport> Map<ImportOperation, ImportLogDetailsDto> importData(List<DTO> data,
    GenericMapper<T, DTO> importMapper) throws BaseException {
    return performImport(importMapper, data);
  }

  @Override
  public <DTO extends IGenericExcelImport> List<DTO> importDataPreview(InputStream inputStream) throws Exception {
    return importDataPreview(inputStream, new ImportParams());
  }

  @Override
  public <DTO extends IGenericExcelImport> List<DTO> importDataPreview(InputStream inputStream, ImportParams params)
    throws Exception {

    transformImportParams(params);

    @SuppressWarnings("unchecked")
    Class<DTO> classType = (Class<DTO>) metaData().getImportDataClassType();
    log.info("Import request received for {}", classType.getSimpleName());

    return ExcelUtil.importData(inputStream, params, classType);
  }

  protected void transformImportParams(ImportParams params) {

  }

  protected <DTO extends IGenericExcelImport> Map<ImportOperation, ImportLogDetailsDto> performImport(
    GenericMapper<T, DTO> importMapper, List<DTO> data) throws EntityInvalidException {

    validateImportData(data, importMapper);

    Map<ImportOperation, ImportLogDetailsDto> result = new HashMap<>();

    List<DTO> upsertList = data.stream()
      .filter(item -> ImportOperation.UPSERT.equals(item.getOperation()))
      .collect(Collectors.toList());

    List<DTO> deleteList = data.stream()
      .filter(item -> ImportOperation.DELETE.equals(item.getOperation()))
      .collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(upsertList)) {
      result.put(ImportOperation.UPSERT,
        processImportForUpsert(importMapper, upsertList));
    }

    if (!CollectionUtils.isEmpty(deleteList)) {
      result.put(ImportOperation.DELETE,
        processImportForDelete(importMapper, deleteList));
    }

    return result;

  }

  protected <DTO extends IGenericExcelImport> void validateImportData(List<DTO> data,
    GenericMapper<T, DTO> importMapper) throws EntityInvalidException {
    Map<String, List<Long>> duplicates = findDuplicateSerialNumbers(data);

    if (!duplicates.isEmpty()) {
      throw validationException(toErrorString(duplicates));
    }

    List<String> errors = new ArrayList<>();

    for (DTO dto : data) {

      Set<ConstraintViolation<DTO>> validate = validator.validate(dto);
      errors.addAll(constraintMessages(validate));
    }

    if (!errors.isEmpty()) {
      throw validationException(errors);
    }

    for (DTO dto : data) {
      List<String> errorList = checkValidity(importMapper.toDomain(dto));
      errorList = addPrefix(serialNumberError(dto.getSerialNo()), errorList);
      errors.addAll(errorList);
    }

    if (!errors.isEmpty()) {
      throw validationException(errors);
    }

  }

  protected String serialNumberError(Long serialNumber) {
    return "S.No. " + serialNumber + ": ";
  }

  private void preUpdateCheck(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    T fromDB = findDuplicate(domain);
    if (fromDB != null && !domain.getId().equals(fromDB.getId())) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB, domain);
      }
      delete(fromDB);
    }
    preUpdate(fromDB, domain);

    List<String> errors = checkValidity(domain);

    if (!CollectionUtils.isEmpty(errors)) {
      throw validationException(errors);
    }
    List<String> updateErrors = checkValidityPreUpdate(domain);

    if (!CollectionUtils.isEmpty(updateErrors)) {
      throw validationException(updateErrors);
    }
  }

  private List<String> addPrefix(String prefix, List<String> errorList) {

    List<String> errors = new ArrayList<>();
    for (String error : errorList) {
      errors.add(prefix + error);
    }
    return errors;

  }

  private List<String> toErrorString(Map<String, List<Long>> map) {
    List<String> errors = new ArrayList<>();
    for (String key : map.keySet()) {
      errors.add(duplicateRowError(map.get(key)));
    }
    return errors;
  }

  protected String duplicateRowError(List<Long> rows) {
    return "Row numbers " + rows + " are duplicate.";
  }

  private <DTO extends IGenericExcelImport> List<String> constraintMessages(
    Set<ConstraintViolation<DTO>> violations) {

    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<DTO> violation : violations) {
      String error = violation.getMessage();
      String fieldName = getLastElement(violation.getPropertyPath().iterator());
      DTO dto = violation.getRootBean();
      errors.add(serialNumberError(dto.getSerialNo()) + fieldName + " " + error);
    }

    return errors;
  }

  private String getLastElement(final Iterator<Node> itr) {
    Node lastElement = itr.next();
    while (itr.hasNext()) {
      lastElement = itr.next();
    }
    return lastElement.getName();
  }

  private <DTO extends IGenericExcelImport> Map<String, List<Long>> findDuplicateSerialNumbers(List<DTO> data) {
    Map<String, List<Long>> duplicates = new HashMap<>();
    Map<String, List<Long>> map = new HashMap<>();
    for (DTO dto : data) {
      Long serialNo = dto.getSerialNo();
      List<Long> list = map.get(dataImportKey(dto));
      if (list == null) {
        list = new ArrayList<>();
      }
      list.add(serialNo);
      map.put(dataImportKey(dto), list);
    }

    for (String key : map.keySet()) {
      if (map.get(key).size() > 1) {
        duplicates.put(key, map.get(key));
      }
    }

    return duplicates;
  }

  protected <DTO extends IGenericExcelImport> String dataImportKey(DTO dto) {
    return dto.getSerialNo() == null ? "" : dto.getSerialNo().toString();
  }

  protected <DTO extends IGenericExcelImport> ImportLogDetailsDto processImportForDelete(
    GenericMapper<T, DTO> importMapper, List<DTO> deleteList) {
    List<String> idsToDelete = deleteList.stream()
      .map(item -> findDuplicate(importMapper.toDomain(item)))
      .filter(item -> item != null)
      .map(item -> item.getId())
      .collect(Collectors.toList());
    delete(idsToDelete);
    return ImportLogDetailsDto.builder().build();
  }

  protected <DTO extends IGenericExcelImport> ImportLogDetailsDto processImportForUpsert(
    GenericMapper<T, DTO> importMapper, List<DTO> upsertList) {
    List<T> domains = importMapper.toDomain(upsertList);
    List<BulkUpdateInfo<T>> bulkUpsert = bulkUpsert(domains);

    return ImportLogDetailsDto.builder()
      .totalRowCount(upsertList.size())
      .successRowCount((int) bulkUpsert.stream()
        .filter(item -> List.of(UpdateAction.CREATE, UpdateAction.UPDATE)
          .contains(item.getUpdateAction()))
        .count())
      .build();
  }

  protected BulkUpdateInfo<T> upsertOneWhileBulkImport(T domain) {
    List<String> errors = checkValidity(domain);
    if (!CollectionUtils.isEmpty(errors)) {
      return bulkImportInfo(domain, UpdateAction.INVALID, errors);
    }
    return upsertOne(domain);
  }

  protected BulkUpdateInfo<T> upsertOne(T domain) {
    T fromDb = findDuplicate(domain);
    if (fromDb != null) {
      preUpdate(fromDb, domain);
      domain.copyEntityFrom(fromDb);

      domain.setActive(Boolean.TRUE);
      T copy = clone(fromDb);
      if (metaData().isIgnoreNullWhileBulkUpdate()) {
        copyNonNullValues(domain, fromDb);
      } else {
        fromDb = domain;
      }

      if (checkEquals(fromDb, copy)) {
        return bulkImportInfo(fromDb, UpdateAction.IGNORE, null);
      }

      List<String> errors = checkValidityPreUpdate(domain);
      if (!CollectionUtils.isEmpty(errors)) {
        return bulkImportInfo(domain, UpdateAction.INVALID, errors);
      }
      fromDb = save(fromDb);
      postUpdate(fromDb);
      return bulkImportInfo(fromDb, UpdateAction.UPDATE, null);
    }
    preCreate(domain);
    domain = save(domain);
    postCreate(domain);
    return bulkImportInfo(domain, UpdateAction.CREATE, null);
  }

  protected List<String> checkValidityPreUpdate(T domain) {
    return new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  protected T clone(T domain) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(domain);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (T) ois.readObject();
    } catch (Exception e) {
      log.warn("Clone failed. Either override the clone method or mark {} as Serializable.",
        e.getMessage());
      return null;
    }
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

  private T update(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    preUpdateCheck(domain);
    save(domain);
    postUpdate(domain);
    return domain;
  }

  private BulkUpdateInfo<T> bulkImportInfo(T fromDB, UpdateAction action, List<String> errors) {
    return BulkUpdateInfo.<T>builder().updateAction(action).errors(errors).data(fromDB).build();
  }

}
