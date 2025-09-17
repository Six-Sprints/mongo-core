package com.sixsprints.core.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "filterType", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = NumberColumnFilter.class, name = "NUMBER"),
    @JsonSubTypes.Type(value = SetColumnFilter.class, name = "SET"),
    @JsonSubTypes.Type(value = BooleanColumnFilter.class, name = "BOOLEAN"),
    @JsonSubTypes.Type(value = DateColumnFilter.class, name = "DATE"),
    @JsonSubTypes.Type(value = SearchColumnFilter.class, name = "TEXT"),
    @JsonSubTypes.Type(value = ExactMatchColumnFilter.class, name = "EXACT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(discriminatorProperty = "filterType",
    discriminatorMapping = {
        @DiscriminatorMapping(value = "NUMBER", schema = NumberColumnFilter.class),
        @DiscriminatorMapping(value = "SET", schema = SetColumnFilter.class),
        @DiscriminatorMapping(value = "BOOLEAN", schema = BooleanColumnFilter.class),
        @DiscriminatorMapping(value = "DATE", schema = DateColumnFilter.class),
        @DiscriminatorMapping(value = "TEXT", schema = SearchColumnFilter.class),
        @DiscriminatorMapping(value = "EXACT", schema = ExactMatchColumnFilter.class)})
@Data
public abstract class ColumnFilter {

  @NotNull
  ColumnFilterTypeEnum filterType;

}
