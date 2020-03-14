package com.sixsprints.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DiffTest {

  private Javers javers = JaversBuilder.javers().build();

  @Test
  public void testDiff() {

    User user1 = User.builder().x(10).y(20).name("Karan")
      .address(User.Address.builder().city("Delhi").country("India").build()).build();

    User user2 = User.builder().x(10).name("karan")
      .address(User.Address.builder().city("Delhi").country("India").build()).build();

    Diff diff = javers.compare(user1, user2);

    List<ValueChange> changes = diff.getChangesByType(ValueChange.class);

    assertThat(changes).hasSize(2);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class User {
    Integer x;
    Integer y;
    String name;
    Address address;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Address {
      String city;
      String country;
    }
  }

}
