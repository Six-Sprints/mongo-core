package com.sixsprints.core.service;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.generic.update.AbstractUpdateService;

public abstract class AbstractCrudService<T extends AbstractMongoEntity> extends AbstractUpdateService<T>
  implements GenericCrudService<T> {

}
