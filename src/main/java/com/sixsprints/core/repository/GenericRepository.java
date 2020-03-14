
package com.sixsprints.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.sixsprints.core.domain.AbstractMongoEntity;

@NoRepositoryBean
public interface GenericRepository<T extends AbstractMongoEntity> extends MongoRepository<T, String> {

  List<T> findAllByActiveTrue();

  Page<T> findAllByActiveTrue(Pageable pageable);

  T findBySlug(String slug);

  List<T> findSlugByIdIn(List<String> ids);

}
