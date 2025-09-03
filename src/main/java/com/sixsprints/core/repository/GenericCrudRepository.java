
package com.sixsprints.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.sixsprints.core.domain.AbstractMongoEntity;

@NoRepositoryBean
public interface GenericCrudRepository<T extends AbstractMongoEntity>
    extends MongoRepository<T, String> {

  T findBySlug(String slug);

}
