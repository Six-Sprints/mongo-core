package com.sixsprints.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDbFactory;
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
import com.sixsprints.core.repository.InheritanceAwareMongoRepositoryFactoryBean;

@Configuration
@EnableMongoRepositories(repositoryFactoryBeanClass = InheritanceAwareMongoRepositoryFactoryBean.class, basePackages = "com.sixsprints.core")
public class ParentMongoConfig extends AbstractMongoClientConfiguration {

  private String host;

  private String database;

  private Integer port;

  @Bean
  public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Override
  protected String getDatabaseName() {
    return getDatabase();
  }

  @Override
  public MongoClient mongoClient() {
    StringBuilder connectionString = new StringBuilder("mongodb://").append(getHost()).append(":").append(getPort())
      .append("/")
      .append(getDatabase());
    return MongoClients.create(connectionString.toString());
  }

  @Override
  public CustomConversions customConversions() {
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

  @Bean
  public com.mongodb.MongoClient client() {
    return new com.mongodb.MongoClient(getHost(), getPort());
  }

  protected String getHost() {
    return host == null ? "localhost" : host;
  }

  protected String getDatabase() {
    return database == null ? "test-db" : database;
  }

  protected Integer getPort() {
    return port == null ? 27017 : port;
  }

}
