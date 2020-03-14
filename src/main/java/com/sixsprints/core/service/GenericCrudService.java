package com.sixsprints.core.service;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.generic.create.GenericCreateService;
import com.sixsprints.core.generic.delete.GenericDeleteService;
import com.sixsprints.core.generic.read.GenericReadService;
import com.sixsprints.core.generic.update.GenericUpdateService;

public interface GenericCrudService<T extends AbstractMongoEntity>
  extends GenericCreateService<T>, GenericReadService<T>, GenericUpdateService<T>, GenericDeleteService<T> {

}
