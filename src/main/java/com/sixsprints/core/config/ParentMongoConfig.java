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
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new ClassToStringConverter());
    converters.add(new StringToClassConverter());
    return new MongoCustomConversions(converters);
  }

  @Bean
  public com.mongodb.MongoClient client() {
    return new com.mongodb.MongoClient(getHost(), getPort());
  }

  protected String getHost() {
    return host == null ? "localhost" : host;
  }

  protected String getDatabase() {
    return database == null ? "test-db" : host;
  }

  protected Integer getPort() {
    return port == null ? 27017 : port;
  }

}
