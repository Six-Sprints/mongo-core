package com.sixsprints.core.repository;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.ConvertingParameterAccessor;
import org.springframework.data.mongodb.repository.query.MongoQueryMethod;
import org.springframework.data.mongodb.repository.query.PartTreeMongoQuery;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.sixsprints.core.utils.InheritanceMongoUtil;

public class InheritanceAwarePartTreeMongoQuery extends PartTreeMongoQuery {

  private final Criteria inheritanceCriteria;

  public InheritanceAwarePartTreeMongoQuery(MongoQueryMethod method, MongoOperations mongoOperations,
    SpelExpressionParser expressionParser, QueryMethodEvaluationContextProvider evaluationContextProvider) {
    super(method, mongoOperations, expressionParser, evaluationContextProvider);
    inheritanceCriteria = InheritanceMongoUtil.generate(method.getEntityInformation().getJavaType());
  }

  @Override
  protected Query createQuery(ConvertingParameterAccessor accessor) {
    Query query = super.createQuery(accessor);
    addInheritanceCriteria(query);
    return query;
  }

  @Override
  protected Query createCountQuery(ConvertingParameterAccessor accessor) {
    Query query = super.createCountQuery(accessor);
    addInheritanceCriteria(query);
    return query;
  }

  private void addInheritanceCriteria(Query query) {
    if (inheritanceCriteria != null) {
      query.addCriteria(inheritanceCriteria);
    }
  }
}