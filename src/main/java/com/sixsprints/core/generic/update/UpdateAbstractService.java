package com.sixsprints.core.generic.update;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.generic.create.AbstractCreateService;

public abstract class UpdateAbstractService<T extends AbstractMongoEntity> extends AbstractCreateService<T>
  implements GenericUpdateService<T> {

}
