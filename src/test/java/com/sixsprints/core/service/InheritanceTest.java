package com.sixsprints.core.service;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.mock.domain.inheritance.Animal;
import com.sixsprints.core.mock.domain.inheritance.Parrot;
import com.sixsprints.core.mock.domain.inheritance.Tiger;
import com.sixsprints.core.mock.service.GenericAnimalService;
import com.sixsprints.core.mock.service.ParrotService;
import com.sixsprints.core.mock.service.TigerService;

public class InheritanceTest extends ApplicationTests {

  @Autowired
  private ParrotService parrotService;

  @Autowired
  private TigerService tigerService;

  @Autowired
  @Qualifier("animal")
  private GenericAnimalService<Animal> animalService;

  @Test
  public void test() {
    parrotService.save(Parrot.builder().name("Iago").canFly(true).beakColor("red").count(1).customId(1L).build());
    tigerService.save(Tiger.builder().name("Bagheera").canFly(false).runningSpeed(84).count(1).customId(2L).build());
    List<Animal> animals = animalService.findAll();
    Assertions.assertThat(animals.size()).isEqualTo(2);
    Assertions.assertThat(animalService.findByCanFly(false).size()).isEqualTo(1);
    Assertions.assertThat(parrotService.findAll().size()).isEqualTo(1);
    Assertions.assertThat(tigerService.findAll().size()).isEqualTo(1);
    Assertions.assertThat(tigerService.findByCanFly(true).size()).isEqualTo(0);
    Assertions.assertThat(tigerService.findByCanFly(false).size()).isEqualTo(1);
    Assertions.assertThat(tigerService.filterAll(FilterRequestDto.builder().page(0).size(10).build()).size())
      .isEqualTo(1);
    Assertions.assertThat(tigerService.filter(FilterRequestDto.builder().page(0).size(10).build()).getContent().size())
      .isEqualTo(1);

    // Added one for BLANK
    Assertions.assertThat(animalService.distinctColumnValues("name", null).size()).isEqualTo(3);
    Assertions.assertThat(parrotService.distinctColumnValues("name", null).size()).isEqualTo(2);
    Assertions.assertThat(tigerService.distinctColumnValues("name", null).size()).isEqualTo(2);

    // Added one for BLANK
    Assertions.assertThat(animalService.distinctColumnValues("count", null).size()).isEqualTo(2);
    Assertions.assertThat(parrotService.distinctColumnValues("count", null).size()).isEqualTo(2);
    Assertions.assertThat(tigerService.distinctColumnValues("count", null).size()).isEqualTo(2);

    // Added one for BLANK
    Assertions.assertThat(animalService.distinctColumnValues("customId", null).size()).isEqualTo(3);
    Assertions.assertThat(parrotService.distinctColumnValues("customId", null).size()).isEqualTo(2);
    Assertions.assertThat(tigerService.distinctColumnValues("customId", null).size()).isEqualTo(2);
    Assertions.assertThat(animalService.distinctColumnValues("customId", null).get(1).getClass()).isEqualTo(Long.class);

    List<Animal> filteredAnimals = animalService.filterAll(FilterRequestDto.builder()
      .filterModel(ImmutableMap.<String, ColumnFilter>builder()
        .put("count", SetColumnFilter.builder().values(ImmutableList.<Integer>of(1)).build())
        .build())
      .build());

    Assertions.assertThat(filteredAnimals.size()).isEqualTo(2);

  }

}
