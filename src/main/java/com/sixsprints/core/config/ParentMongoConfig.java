package com.sixsprints.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.sixsprints.core.converters.BigDecimalToDecimal128Converter;
import com.sixsprints.core.converters.ClassToStringConverter;
import com.sixsprints.core.converters.Decimal128ToBigDecimalConverter;
import com.sixsprints.core.converters.IntegerToLocalTimeConverter;
import com.sixsprints.core.converters.LocalTimeToIntegerConverter;
import com.sixsprints.core.converters.StringToClassConverter;

@Configuration
@EnableMongoRepositories(basePackages = "com.sixsprints.core")
public class ParentMongoConfig extends AbstractMongoClientConfiguration {
  
  @Value(value = "${spring.data.mongodb.uri:}")
  private String uri;

  @Value(value = "${spring.data.mongodb.database:}")
  private String database;

  @Bean
  protected MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Bean
  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(uri());
  }

  @Override
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = converters();
    return new MongoCustomConversions(converters);
  }

  protected List<Converter<?, ?>> converters() {
    List<Converter<?, ?>> converters = new ArrayList<>();

    converters.add(new ClassToStringConverter());
    converters.add(new StringToClassConverter());

    converters.add(new BigDecimalToDecimal128Converter());
    converters.add(new Decimal128ToBigDecimalConverter());

    converters.add(new LocalTimeToIntegerConverter());
    converters.add(new IntegerToLocalTimeConverter());

    return converters;
  }

  protected String uri() {
    return uri;
  }

  @Override
  protected String getDatabaseName() {
    return database;
  }

}
