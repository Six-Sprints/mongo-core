package com.sixsprints.core.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.ApplicationTests;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.mock.domain.inheritance.Parrot;
import com.sixsprints.core.mock.domain.inheritance.Tiger;
import com.sixsprints.core.mock.service.ParrotService;
import com.sixsprints.core.mock.service.TigerService;

public class InheritanceTest extends ApplicationTests {

  @Autowired
  private ParrotService parrotService;

  @Autowired
  private TigerService tigerService;

  @Test
  public void test() throws EntityInvalidException {
    parrotService.upsertOne(
        Parrot.builder().name("Iago").canFly(true).beakColor("red").count(1).customId(1L).build());
    tigerService.upsertOne(Tiger.builder().name("Bagheera").canFly(false).runningSpeed(84).count(1)
        .customId(2L).build());
    Assertions.assertThat(parrotService.findAllList().size()).isEqualTo(1);
    Assertions.assertThat(tigerService.findAllList().size()).isEqualTo(1);
    Assertions.assertThat(tigerService.findByCanFly(true).size()).isEqualTo(0);
    Assertions.assertThat(tigerService.findByCanFly(false).size()).isEqualTo(1);
    Assertions.assertThat(
        tigerService.filterByFilterRequestDto(FilterRequestDto.builder().page(0).size(10).build())
            .getContent().size())
        .isEqualTo(1);
    Assertions.assertThat(
        tigerService.filterByFilterRequestDto(FilterRequestDto.builder().page(0).size(10).build())
            .getContent().size())
        .isEqualTo(1);

    Page<Tiger> filteredTigers =
        tigerService.filterByFilterRequestDto(FilterRequestDto.builder().page(0).size(10)
            .filterModel(ImmutableMap.<String, ColumnFilter>builder()
                .put("count",
                    SetColumnFilter.builder().values(ImmutableList.<Integer>of(1)).build())
                .build())
            .build());

    Assertions.assertThat(filteredTigers.getContent().size()).isEqualTo(1);

  }

}
